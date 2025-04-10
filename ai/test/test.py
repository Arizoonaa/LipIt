import os

from TTS.api import TTS
import torch

# 디바이스 설정
device = "cuda" if torch.cuda.is_available() else "cpu"
print("✅ Device:", device)

# 모델 로드 (자동으로 다운로드됨, 캐시 사용)
tts = TTS(
    model_name="tts_models/multilingual/multi-dataset/xtts_v2", gpu=(device == "cuda")
)

# 화자 음성 파일 경로
speaker_wav = os.path.abspath("./audio/benedict.wav")

# 디렉토리 설정
RESULT_DIR = "./result"

# 디렉토리 없으면 생성
os.makedirs(RESULT_DIR, exist_ok=True)

celebs = [
    "Benedict Cumberbatch",
    "Ariana Grande",
    "Leonardo DiCaprio",
    "Taylor Swift",
    "Jennie",
]

filename = f"benedict0.wav"
path = os.path.join(RESULT_DIR, filename)

# text = f"Hi, I'm {celebs[2]}. Let's start learning English with Lip It."
text = "Now that sarang has introduced you to Lip It, let's give me a call and have a chat!"

# 음성 합성
tts.tts_to_file(
    text=text,
    speaker_wav=speaker_wav,
    language="en",
    file_path=path,
)
