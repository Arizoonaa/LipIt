# 1. 의존성 빌드용 (패키지 설치만 수행)
FROM python:3.12-slim AS builder

# 작업 디렉토리
WORKDIR /install

# 시스템 패키지 설치 (C 확장 빌드용)
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    gcc \
    libffi-dev \
    libssl-dev \
    curl \
    && rm -rf /var/lib/apt/lists/*

# requirements.txt 복사 및 pip 설치
COPY requirements.txt .

# pip 업그레이드 + 바이너리 우선 설치
RUN pip install --upgrade pip \
    && pip install --prefer-binary --no-cache-dir --prefix=/install -r requirements.txt


# 2. 실행 전용 이미지 (패키지만 가져오고 깔끔하게)
FROM python:3.12-slim

# 작업 디렉토리
WORKDIR /app

# builder에서 설치된 패키지만 복사
COPY --from=builder /install /usr/local

# FastAPI 애플리케이션 소스 복사
COPY . .

# pycache, 테스트 캐시 정리 (optional)
RUN find . -type d -name "__pycache__" -exec rm -r {} + || true

# FastAPI 서버 실행
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
