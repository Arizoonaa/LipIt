# app/main.py

from fastapi import FastAPI
from fastapi.staticfiles import StaticFiles
import os
import time

from app.api.v1.ws_call import router
from app.core.synthesizer import get_cached_synthesizer
from app.core.tts_model import load_tts


# ========== Monkey Patch 적용 ==========
def monkey_patch_check_arguments():
    from TTS.api import TTS
    from TTS.tts.models import xtts
    from TTS.tts.models.xtts import Xtts
    from TTS.utils.synthesizer import Synthesizer
    import torch

    # 1. TTS._check_arguments 패치
    def _patched_check_arguments(
        self, speaker=None, language=None, speaker_wav=None, emotion=None, **kwargs
    ):
        if self.is_multi_speaker and (
            speaker is None and speaker_wav is None and "d_vector" not in kwargs
        ):
            raise ValueError(
                "Model is multi-speaker but no speaker, speaker_wav, or d_vector provided."
            )
        if self.is_multi_lingual and language is None:
            raise ValueError("Model is multi-lingual but no language provided.")
        if (
            not self.is_multi_speaker
            and speaker is not None
            and "voice_dir" not in kwargs
        ):
            raise ValueError("Model is not multi-speaker but speaker is provided.")
        if not self.is_multi_lingual and language is not None:
            raise ValueError("Model is not multi-lingual but language is provided.")
        if emotion is not None:
            raise ValueError("Emotion can only be used with Coqui Studio models.")

    TTS._check_arguments = _patched_check_arguments
    print("✅ Monkey patched: TTS._check_arguments")

    # 2. XTTS 내부 d_vector 허용 패치
    def _patched_get_conditioning_latents(self, file_path, load_sr, d_vector=None):
        if d_vector is not None:
            print("🎙️ Using d_vector instead of speaker_wav")
            speaker_embedding = d_vector.to(self.device)
            # 3D로 바꿔야 GPT에서 concat할 때 오류 안 남
            gpt_cond_latent = torch.zeros(
                (1, 1, 1024), dtype=speaker_embedding.dtype, device=self.device
            )
            return gpt_cond_latent, speaker_embedding
        return xtts.original_get_conditioning_latents(self, file_path, load_sr)

    def _patched_full_inference(
        self, text, speaker_wav, language, d_vector=None, **kwargs
    ):
        gpt_cond_latent, speaker_embedding = self.get_conditioning_latents(
            speaker_wav,
            24000,
            d_vector=d_vector,
        )

        # HuggingFace generate 오류 방지
        if "config" in kwargs:
            kwargs.pop("config")

        outputs = self.inference(
            text,
            language,
            gpt_cond_latent,
            speaker_embedding,
            **kwargs,
        )
        return outputs["wav"]  # wav만 리턴

    def _patched_synthesize(self, text, speaker_wav, language, **kwargs):
        if "d_vector" not in kwargs and speaker_wav is None:
            raise ValueError(
                "[patched] Multi-speaker XTTS 모델은 `speaker_wav` 또는 `d_vector` 중 하나가 필요합니다."
            )
        return self.full_inference(text, speaker_wav, language, **kwargs)

    if not hasattr(Xtts, "_original_synthesize"):
        xtts.original_get_conditioning_latents = Xtts.get_conditioning_latents

    Xtts.get_conditioning_latents = _patched_get_conditioning_latents
    Xtts.full_inference = _patched_full_inference
    Xtts.synthesize = _patched_synthesize
    print("✅ Monkey patched: XTTS")

    # 3. Synthesizer.tts 패치 (최종 예외 방지)
    def _patched_synthesizer_tts(
        self,
        text,
        speaker_name=None,
        language_name=None,
        speaker_wav=None,
        d_vector=None,
        split_sentences=True,
        **kwargs,
    ):
        if self.tts_model is None:
            raise RuntimeError("❌ tts_model이 None입니다. checkpoint 로딩 실패")

        # ✅ 단일 화자 (VITS) 모델이면 Synthesizer 원래 로직을 쓰자
        if not getattr(self.tts_model, "is_multi_speaker", False):
            # Synthesizer 원래 방식 호출 (patch 안 함)
            return Synthesizer._original_tts(self, text)

        # ✅ XTTS (멀티 화자) 모델
        if speaker_wav is None and d_vector is None:
            raise ValueError("XTTS는 speaker_wav 또는 d_vector가 필요합니다.")

        return self.tts_model.synthesize(
            text,
            speaker_wav,
            language_name,
            d_vector=d_vector,
            **kwargs,
        )

    # 한번만 패치
    if not hasattr(Synthesizer, "_original_tts"):
        Synthesizer._original_tts = Synthesizer.tts
        Synthesizer.tts = _patched_synthesizer_tts
        print("✅ Monkey patched: Synthesizer.tts (VITS + XTTS 호환)")


# ========== Lifespan ==========
async def lifespan(app: FastAPI):
    start = time.time()
    monkey_patch_check_arguments()
    load_tts()
    get_cached_synthesizer()  # 모델 메모리에 로딩
    print(f"✅ TTS 모델 로딩 완료! (소요 시간: {time.time() - start:.2f}초)")

    print("🟢 서버 준비 완료 ✅ 이제 WebSocket 연결 가능")
    yield


# ========== FastAPI 앱 ==========
app = FastAPI(lifespan=lifespan)

# API 라우터 등록
app.include_router(router)
