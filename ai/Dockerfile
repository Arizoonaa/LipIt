FROM python:3.10-slim

WORKDIR /app

# 시스템 패키지 설치
RUN apt-get update && apt-get install -y \
    espeak \
    ffmpeg \
    libsndfile1 \
    git \
    && rm -rf /var/lib/apt/lists/*

# requirements.txt 복사 먼저 (캐시 활용)
COPY requirements.txt .

# torch (GPU용) 설치 - CUDA 11.8 기준
RUN pip install --upgrade pip && \
    pip install torch==2.0.0+cu118 torchaudio==2.0.0+cu118 --index-url https://download.pytorch.org/whl/cu118 && \
    pip install -r requirements.txt

# 모델 미리 다운받기 (xtts_v2로 변경)
# 약관 동의
ENV COQUI_TOS_AGREED=1
ENV TTS_CACHE_DIR=/root/.local/share/tts

# 모델 다운로드
# RUN python -c "from TTS.api import TTS; TTS(model_name='tts_models/multilingual/multi-dataset/xtts_v2')"

# 모델 캐시 디렉토리 설정
ENV TTS_CACHE_DIR=/app/tts_models
ENV COQUI_TOS_AGREED=1

# Pickle 관련 에러를 피하기 위해 캐시 다운로드만 먼저
RUN python -c "from torch.serialization import add_safe_globals; \
                from TTS.tts.configs.xtts_config import XttsConfig; \
                from TTS.tts.models.xtts import XttsAudioConfig; \
                add_safe_globals([XttsConfig, XttsAudioConfig]); \
                from TTS.api import TTS; TTS(model_name='tts_models/multilingual/multi-dataset/xtts_v2', gpu=False)"

# 소스 복사
COPY . .

# FastAPI 서버 실행
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000", "--reload"]
