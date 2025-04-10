from pydub import AudioSegment
from pydub.effects import normalize, low_pass_filter


def clean_wav(input_path: str, output_path: str, cutoff_hz: int = 8000):
    audio = AudioSegment.from_wav(input_path)
    audio = normalize(audio)  # 전체 볼륨 정규화
    audio = low_pass_filter(audio, cutoff_hz)  # 8kHz 이상 고주파 제거 (기계음 줄이기)
    audio.export(output_path, format="wav")
