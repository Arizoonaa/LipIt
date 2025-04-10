import os
import json

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
speaker_wav = os.path.abspath("./audio/benedict_2m.wav")

# 메시지 파일 로드
with open("app/test/data/messages.json", "r", encoding="utf-8") as f:
    data = json.load(f)

# 디렉토리 설정
RESULT_DIR = "./result"

# 디렉토리 없으면 생성
os.makedirs(RESULT_DIR, exist_ok=True)

# 메시지 하나씩 처리
for i, item in enumerate(data, start=1):
    text = item["content"]
    print(f"[{i}] {text}")

    # 임시 경로
    filename = f"benedict_2m_{i}.wav"
    path = os.path.join(RESULT_DIR, filename)

    if (filename in os.listdir(RESULT_DIR)) and os.path.exists(path):
        continue

    # 음성 합성
    tts.tts_to_file(
        text=text,
        speaker_wav=speaker_wav,
        language="en",
        file_path=path,
    )
