import os
import hashlib
import requests
import soundfile as sf
import subprocess

SPEAKER_CACHE_DIR = "./speaker_cache"  # 필요시 변경
os.makedirs(SPEAKER_CACHE_DIR, exist_ok=True)


def is_valid_wav(
    filepath: str,
    min_duration_sec: float = 0.5,
    required_samplerate: int = 24000,
    required_channels: int = 1,
) -> bool:
    try:
        with sf.SoundFile(filepath) as f:
            samplerate = f.samplerate
            channels = f.channels
            frames = f.frames

            duration_sec = frames / float(samplerate)

            if duration_sec < min_duration_sec:
                print(
                    f"❌ WAV too short: {duration_sec:.2f}s (min: {min_duration_sec}s)"
                )
                return False

            if samplerate != required_samplerate:
                print(
                    f"❌ Wrong samplerate: {samplerate} (expected: {required_samplerate})"
                )
                return False

            if channels != required_channels:
                print(
                    f"❌ Wrong number of channels: {channels} (expected: {required_channels})"
                )
                return False

            return True

    except Exception as e:
        print(f"❌ WAV 검증 실패: {e}")
        return False


def convert_wav_to_24k_mono(input_path: str, output_path: str):
    try:
        subprocess.run(
            [
                "ffmpeg",
                "-y",
                "-i",
                input_path,
                "-ar",
                "24000",
                "-ac",
                "1",  # 샘플레이트 24k, mono
                output_path,
            ],
            check=True,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
        )
    except Exception as e:
        raise Exception(f"❌ WAV 변환 실패: {e}")


def get_cached_speaker_wav(speaker_wav_url: str) -> str:
    url_hash = hashlib.md5(speaker_wav_url.encode()).hexdigest()
    original_path = os.path.join(SPEAKER_CACHE_DIR, f"{url_hash}.wav")
    converted_path = os.path.join(SPEAKER_CACHE_DIR, f"{url_hash}_24k.wav")

    # 캐시된 24k 버전이 이미 유효하다면 바로 반환
    if os.path.exists(converted_path) and is_valid_wav(converted_path):
        return converted_path

    # 원본이 존재한다면 유효한지 확인하고 필요시 변환
    if os.path.exists(original_path):
        if not is_valid_wav(original_path, required_samplerate=24000):
            print("⚠️ 캐시된 WAV 파일이 유효하지 않음. 변환 시도")
        else:
            print("⚠️ 캐시된 WAV는 유효하지만 샘플레이트가 다를 수 있음. 변환 시도")

        convert_wav_to_24k_mono(original_path, converted_path)

        if is_valid_wav(converted_path):
            return converted_path
        else:
            raise Exception("❌ WAV 변환 후에도 유효하지 않음")

    # 파일 없으면 다운로드
    response = requests.get(speaker_wav_url)
    if response.status_code != 200:
        raise Exception("❌ Failed to download speaker wav")

    with open(original_path, "wb") as f:
        f.write(response.content)

    # 변환 + 유효성 검사
    convert_wav_to_24k_mono(original_path, converted_path)

    if is_valid_wav(converted_path):
        return converted_path
    else:
        raise Exception("❌ 다운로드된 WAV 파일이 손상됨 또는 너무 짧음")
