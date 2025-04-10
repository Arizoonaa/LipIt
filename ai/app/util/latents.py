import os
from app.core.tts_model import get_tts
from app.service.cache import get_cached_embedding
from app.util.cache import load_latents_from_cache, save_latents_to_cache
from app.util.file_cache import get_cached_speaker_wav


def get_latents(voice_id: str, voice_url: str):
    speaker_wav = get_cached_speaker_wav(voice_url)
    if not speaker_wav or not os.path.exists(speaker_wav):
        raise ValueError(f"❌ speaker_wav 로드 실패: {speaker_wav}")

    xtts_model = get_tts().synthesizer.tts_model
    gpt_latent, speaker_embedding = xtts_model.get_conditioning_latents(
        file_path=speaker_wav, load_sr=24000
    )

    return {"gpt": gpt_latent, "d_vector": speaker_embedding}


def get_speaker_embedding(voice_id: str, voice_url: str):
    """
    speaker_embedding을 voice_id 기준으로 캐싱하여 반환
    """
    embedding = get_cached_embedding(voice_id, voice_url)  # 캐시 or 추출
    return embedding


def get_gpt_latent_per_chunk(voice_url: str):
    """
    chunk마다 별도로 생성해야 하는 gpt_latent 반환 (문장 길이 영향 받음)
    """
    xtts_model = get_tts().synthesizer.tts_model

    voice_path = get_cached_speaker_wav(voice_url)

    gpt_latent, _ = xtts_model.get_conditioning_latents(
        file_path=voice_path, load_sr=24000
    )

    return gpt_latent


def get_latents_per_chunk(chunk_text: str, voice_url: str):
    speaker_wav = get_cached_speaker_wav(voice_url)
    if not speaker_wav or not os.path.exists(speaker_wav):
        raise ValueError(f"❌ speaker_wav 로드 실패: {speaker_wav}")

    xtts_model = get_tts().synthesizer.tts_model
    gpt_latent, speaker_embedding = xtts_model.get_conditioning_latents(
        file_path=speaker_wav,
        text=chunk_text,
        load_sr=24000,
    )
    return {"gpt": gpt_latent, "d_vector": speaker_embedding}
