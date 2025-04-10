from TTS.utils.synthesizer import Synthesizer


# Synthesizer 한 번만 초기화
def get_cached_synthesizer():
    if not hasattr(get_cached_synthesizer, "synth"):
        print("🚀 VITS 모델 초기화 중...")
        get_cached_synthesizer.synth = Synthesizer(
            tts_checkpoint="model/checkpoint_340000.pth",
            tts_config_path="model/config.json",
        )
        print("✅ VITS 모델 캐싱 완료")
    return get_cached_synthesizer.synth
