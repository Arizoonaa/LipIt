# app/service/synthesis.py

import os
import time

import torch
from app.core.tts_model import get_tts, reload_tts
from app.service.celeb_tts import run_celeb_tts
from app.service.clean_audio import clean_wav
from app.util.file_cache import get_cached_speaker_wav
from app.util.latents import (
    get_gpt_latent_per_chunk,
    get_latents,
    get_speaker_embedding,
)
from app.util.stream import celeb_wav_to_bytes, wav_to_bytes


def synthesize_voice(
    text: str,
    output_path: str,
    clean: bool = True,
    language: str = "en",
    length_scale: float = 0.1,
    noise_scale: float = 0.4,
    noise_scale_dp: float = 0.5,
) -> str:
    """
    TTS 음성 합성 함수

    Args:
        text (str): 생성할 텍스트
        output_path (str): 저장할 .wav 경로
        clean (bool): 후처리 여부
    Returns:
        str: 최종 저장된 파일 경로
    """

    # GPU 동기화 전후 시간 측정
    start = time.time()

    torch.cuda.synchronize()

    tts = get_tts()
    latents = get_latents()

    tts.synthesizer.tts_model.length_scale = length_scale  # 숫자가 작을수록 더 빠름
    tts.synthesizer.tts_model.noise_scale = noise_scale
    tts.synthesizer.tts_model.noise_scale_dp = noise_scale_dp

    result = tts.synthesizer.tts_model.inference(
        text=text,
        language=language,
        gpt_cond_latent=latents["gpt"],
        speaker_embedding=latents["d_vector"],
    )

    print(f"🔊 Inference 완료 시간: {time.time() - start:.2f}초")

    wav = result["wav"]

    tmp_path = output_path
    if clean:
        tmp_path = f"/tmp/tts_{os.getpid()}.wav"

    tts.synthesizer.save_wav(wav=wav, path=tmp_path)

    if clean:
        clean_wav(tmp_path, output_path)
        os.remove(tmp_path)

    return output_path


def synthesize_voice_latents(
    text: str,
    output_path: str,
    voice_id: int,
    voice_url: str,
):
    if not voice_id or not voice_url:
        raise ValueError("voice_id와 voice_url은 필수입니다.")

    language = "en"
    length_scale = 1.2
    noise_scale = 0.667
    noise_scale_dp = 0.8

    try:
        start = time.time()
        # torch.cuda.synchronize()

        # 셀럽 목소리
        if voice_id == 1:
            wav = run_celeb_tts(text)
            audio_bytes = celeb_wav_to_bytes(wav, 22050)
        else:
            # 커스텀 목소리
            tts = get_tts()
            xtts_model = tts.synthesizer.tts_model

            # xtts_model.length_scale = length_scale
            # xtts_model.noise_scale = noise_scale
            # xtts_model.noise_scale_dp = noise_scale_dp
            xtts_model.config.audio["sample_rate"] = 24000

            latents = get_latents(voice_id, voice_url)

            result = xtts_model.inference(
                text=text,
                language=language,
                gpt_cond_latent=latents["gpt"],
                speaker_embedding=latents["d_vector"],
            )

            wav = result["wav"]

            # 메모리에서 바로 WAV 변환
            audio_bytes = wav_to_bytes(wav, 24000)

            # GPU 텐서를 제거하고 PyTorch가 잡고 있는 메모리 블록도 반납
            del result
            del latents

        print(f"🔊 Inference 완료 시간: {time.time() - start:.2f}초")

        # 메모리 정리
        del wav
        torch.cuda.empty_cache()

        return audio_bytes

        # wav 파일 디스크에 저장
        # wav = result["wav"]
        # tts.synthesizer.save_wav(wav=wav, path=output_path)

        # print(f"🔊 Inference 완료 시간: {time.time() - start:.2f}초")
    except RuntimeError as e:
        if "indexSelectSmallIndex" in str(e) or "CUDA error" in str(e):
            print(f"⚠️ GPU 오류 발생: {e}")

            print(torch.cuda.is_available())  # False면 이미 context 죽음
            print(torch.cuda.memory_summary())  # 현재 상태 요약

            reload_tts()
            # 재시도 한 번만
            return synthesize_voice_latents(text, "", voice_id, voice_url)
        else:
            raise e
