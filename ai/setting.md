
# AI 사용 기록

1. [HALLO](#HALLO)
2. [Coqui-TTS](#Coqui-TTS)

## HALLO

- [HALLO 모델을 실행하기 위한 전체 소스코드 및 환경 설정](https://github.com/fudan-generative-vision/hallo)

- [모델 가중치(*.pth, *.ckpt 등)](
    https://huggingface.co/fudan-generative-ai/hallo
)

- 설치 방법
    ```
    # 0. (필수) 패키지를 설치할 가상환경을 생성하고 활성화 진행

    # 프로젝트 파일 Clone
    git clone https://github.com/fudan-generative-vision/hallo.git

    # 설치
    pip install -r requirements.txt
    pip install .

    # ffmpeg 설치
    conda install -c conda-forge ffmpeg

    # 대용량 git clone 을 위해 git lfs 설치
    conda install -c conda-forge git-lfs

    # huggingface 파일 다운로드를 위한 설치 (버전 지정 필수)
    pip install huggingface_hub==0.20.2

    git clone https://huggingface.co/fudan-generative-ai/hallo pretrained_models
    ```

- 설치 & 압축 해제 후 폴더 구조

    <img width=350 src="https://velog.velcdn.com/images/bmlsj/post/c3b8be9f-6c28-4eda-b55e-aac6c3cf0014/image.png">


- 립싱크 영상 생성 명령어
  - 테스트용 이미지, 음성 파일 준비 후 아래와 같이 입력
    ```
    python scripts/inference.py --source_image examples/reference_images/1.jpg --driving_audio examples/driving_audios/1.wav
    ```


<br>

## 2. Coqui-TTS
- [Github](https://github.com/idiap/coqui-ai-TTS)
- [Documentation](https://coqui-tts.readthedocs.io/en/latest/)


- 설치 방법
    ```
    # 0. (필수) 패키지 설치할 가상 환경 생성 후 실행: Python 버전 3.9 로 설정 필수 !

    # 1. PyTorch + CUDA 설치 (꼭 conda로)
    conda install pytorch=1.10.0 torchaudio=0.10.0 cudatoolkit=11.1 -c pytorch -c nvidia

    # 2. TTS 최신버전 설치
    python -m pip install git+https://github.com/coqui-ai/TTS.git@main

    # 3. XTTS clone 설치
    conda install -c conda-forge git-lfs
    git clone https://huggingface.co/coqui/XTTS-v2


    # Error: 만약 torch 버전 에러가 날 경우 아래와 같이 버전 지정해서 재설치 시도
    pip uninstall torch torchaudio -y
    pip install torch==2.1.0+cu118 torchaudio==2.1.0+cu118 -f https://download.pytorch.org/whl/torch_stable.html
    ```

- 설치과정 완료 후 패키지 구조

    <img width=700 src="https://velog.velcdn.com/images/bmlsj/post/01514274-90cb-49b5-be89-d36d432c01df/image.png">


### 사용 방법1: TTS 단순 API 사용
```
from TTS.api import TTS

# XTTS 모델 수동 로딩
tts = TTS(
    model_path="XTTS-v2",
    config_path="XTTS-v2/config.json",   # 파일 경로
    gpu=True
)

# 텍스트 → 음성 파일 생성
tts.tts_to_file(
    text="Hi! I'm Miji. I'm really smart and cool. I'm the best in the world",
    speaker_wav="voice_0.wav",  # 음성 샘플 경로
    language="en",              # 언어 코드
    file_path="xtts_success.wav"
)
```

### 사용 방법2: 파인튜닝 방법

<img src="">
<img src="">


- metadata.csv 파일 구성 예시

```
AJ002_002|in a draw in the bureau of an upstairs room of my current home,|In a draw in the Bureau of an upstairs room of my current home,
AJ002_003|there is a locked cedarwood box which i inherited as a youth from my grandfather.|there is a locked Cedarwood box which I inherited as a youth from my grandfather.
AJ002_004|this is, one might say, my box of secrets.|This is, one might say, my box of secrets.
```


    - row 구조 : 파일명|원문|원문
    - 파일명에서 *.wav 확장제 제외
    - 각 구분은 파이프(|) 특수기호 사용
    - 숫자, 특수문자는 영문 표기로 변경

```
# 실행 위치 이동
cd coqui/coqui-ai-TTS

# 파인튜닝 실행 명령어 => config.json 파일 내용과 학습 데이터가 잘 준비되어 있어야함
CUDA_VISIBLE_DEVICES=0 python TTS/bin/train_tts.py --config_path /home/j-j12d102/sarang/coqui/my_voice_data/config.json
```


- config.json 파일 작성 예시

    ```
    {
        "output_path": "/home/j-j12d102/sarang/coqui/outputs_benedict_vits",
        "model": "vits",
        "batch_size": 16,
        "eval_batch_size": 16,
        "add_blank": true,
        "compute_linear_spec": true,
        "min_audio_len": 1,
        "max_audio_len": 500000,
        "min_text_len": 1,
        "max_text_len": 1000,
        "datasets": [
            {
                "formatter": "ljspeech",
                "dataset_name": "my_voice_data_benedict_vits",
                "path": "/home/j-j12d102/sarang/coqui/my_voice_data_benedict_vits",
                "meta_file_train": "metadata.csv",
                "language": "en"
            }
        ],
        "restore_path": "/root/.local/share/tts/tts_models--en--ljspeech--vits/model.pth",
        "model_args": {
            "hidden_channels": 192
        }
    }
    ```
    - VITS 모델을 사용하는 경우 위와 같이 작성
    - 모델별로 config.json 구성 내용이 다르기 때문에 주의
    - output_path : 결과물이 저장될 위치
    - datasets
        - formatter : metadata.csv 파일 구성 방식
        - dataset_name : 학습 시킬 wavs 와 metadata.csv 파일이 있는 폴더 명
        - path : wavs, metadata.csv 파일이 있는 위치 경로 지정
    - restore_path : 파인튜닝 대상 모델 경로
