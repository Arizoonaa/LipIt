import io
import soundfile as sf  # TTS 내부적으로 사용하는 포맷 저장용
import numpy as np
import torch


def wav_to_bytes(wav, sample_rate: int = 24000):
    print(wav.shape, wav.dtype, wav.min(), wav.max())

    buffer = io.BytesIO()
    sf.write(buffer, wav, samplerate=24000, format="WAV", subtype="PCM_16")
    buffer.seek(0)  # ★ 중요: 커서를 맨 앞으로 옮겨줘야 제대로 읽힘
    return buffer.read()


def celeb_wav_to_bytes(wav, sample_rate: int = 22050) -> bytes:
    # torch.Tensor → np.ndarray
    if isinstance(wav, torch.Tensor):
        wav = wav.squeeze().cpu().numpy()

    elif isinstance(wav, list):
        wav = np.array(wav, dtype=np.float32)

    # shape 정리
    if isinstance(wav, np.ndarray):
        if wav.ndim == 3:
            wav = np.squeeze(wav)
        elif wav.ndim == 2 and wav.shape[0] == 1:
            wav = wav.squeeze(0)

        wav = np.clip(wav, -1.0, 1.0)  # 정규화 추가

    # sample_rate 정수 보장
    sample_rate = int(sample_rate)

    # WAV bytes 변환
    buffer = io.BytesIO()
    sf.write(buffer, wav, samplerate=sample_rate, format="WAV", subtype="PCM_16")
    buffer.seek(0)
    return buffer.read()
