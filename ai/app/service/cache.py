import os
import pickle

import torch

from app.core.tts_model import get_tts
from app.util.download import get_cached_speaker_wav

LATENT_DIR = "/tmp/xtts_latents"
os.makedirs(LATENT_DIR, exist_ok=True)
LATENT_CACHE = {}


def get_cached_embedding(voice_id: str, voice_url: str):
    if voice_id in LATENT_CACHE:
        return LATENT_CACHE[voice_id]

    cache_path = os.path.join(LATENT_DIR, f"{voice_id}.pkl")
    if os.path.exists(cache_path):
        with open(cache_path, "rb") as f:
            speaker_embedding = pickle.load(f)
            if not isinstance(speaker_embedding, torch.Tensor):
                raise TypeError(
                    "❌ 캐시된 speaker_embedding이 torch.Tensor가 아닙니다!"
                )
            LATENT_CACHE[voice_id] = speaker_embedding
            return speaker_embedding

    voice_path = get_cached_speaker_wav(voice_url)
    xtts = get_tts().synthesizer.tts_model
    _, speaker_embedding = xtts.get_conditioning_latents(
        file_path=voice_path, load_sr=24000
    )

    if not isinstance(speaker_embedding, torch.Tensor):
        raise TypeError("❌ 새로 추출한 speaker_embedding이 torch.Tensor가 아닙니다!")

    LATENT_CACHE[voice_id] = speaker_embedding
    with open(cache_path, "wb") as f:
        pickle.dump(speaker_embedding, f)

    return speaker_embedding
