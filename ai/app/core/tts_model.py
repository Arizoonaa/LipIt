# app/core/tts_model.py

import asyncio
import torch
from TTS.api import TTS
from TTS.tts.models.xtts import Xtts

device = "cuda" if torch.cuda.is_available() else "cpu"

tts_lock = asyncio.Lock()

tts = None
cached_latents = None


def get_tts():
    return tts


def get_latents():
    return cached_latents


def load_tts():
    global tts, cached_latents

    if tts is None:
        tts = TTS("tts_models/multilingual/multi-dataset/xtts_v2").to(device)


def reload_tts():
    global tts
    print("🔄 TTS 모델 재초기화 중...")
    torch.cuda.empty_cache()
    tts = TTS(model_name="tts_models/multilingual/multi-dataset/xtts_v2")
    print("✅ 모델 재로드 완료")
