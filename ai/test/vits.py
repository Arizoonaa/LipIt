from TTS.utils.synthesizer import Synthesizer
import time

start = time.time()

# 체크포인트와 config 경로
test_ckpt = "model/checkpoint_340000.pth"
test_config = "model/config.json"

# 텍스트 설정
text = "I sincerely hope this test is successful, because I want to go home."
out_path = "out.wav"

# 신디사이저 초기화 및 실행
synthesizer = Synthesizer(
    tts_checkpoint=test_ckpt,
    tts_config_path=test_config,
)

loading_time = time.time()

print("✅ VITS 모델 로딩 완료! (소요 시간: {:.2f}초)".format(loading_time - start))

wav = synthesizer.tts(text)
synthesizer.save_wav(wav, out_path)

print("✅ TTS 완료! (소요 시간: {:.2f}초)".format(time.time() - loading_time))
