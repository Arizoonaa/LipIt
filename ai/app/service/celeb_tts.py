import time
import torch
from TTS.utils.synthesizer import Synthesizer

from app.core.synthesizer import get_cached_synthesizer


def run_celeb_tts(text: str, sample_rate: int = 24000):
    start = time.time()

    try:
        synthesizer = get_cached_synthesizer()  # 캐싱된 Synthesizer 사용
    except Exception as e:
        print("❌ Synthesizer 로딩 실패:", e)
        raise e

    loading_time = time.time()

    try:
        with torch.no_grad():  # 추론 최적화
            wav = synthesizer.tts(text)
    except Exception as e:
        print("❌ TTS 예외 발생:", e)
        raise e

    return wav
