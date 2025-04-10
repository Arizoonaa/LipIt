# simple in-memory + file cache
import pickle
from typing import Dict
import os

embedding_cache: Dict[str, bytes] = {}  # memory cache (voice_id → pickle된 embedding)

LATENT_CACHE = {}
LATENT_DIR = "/tmp/xtts_latents"
os.makedirs(LATENT_DIR, exist_ok=True)


def load_latents_from_cache(voice_id: str):
    if voice_id in LATENT_CACHE:
        return LATENT_CACHE[voice_id]

    path = os.path.join(LATENT_DIR, f"{voice_id}.pkl")
    if os.path.exists(path):
        with open(path, "rb") as f:
            latents = pickle.load(f)
            LATENT_CACHE[voice_id] = latents
            return latents
    return None


def save_latents_to_cache(voice_id: str, latents: dict):
    LATENT_CACHE[voice_id] = latents
    path = os.path.join(LATENT_DIR, f"{voice_id}.pkl")
    with open(path, "wb") as f:
        pickle.dump(latents, f)
