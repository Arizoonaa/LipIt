# compare_voice_similarity.py

from scipy.spatial.distance import cosine

import torch
from TTS.api import TTS
from TTS.tts.models.xtts import Xtts
import torchaudio

device = "cuda" if torch.cuda.is_available() else "cpu"


def compare_voices(wav1_path, wav2_path):
    tts = TTS("tts_models/multilingual/multi-dataset/xtts_v2").to(device)

    waveform1, sr1 = torchaudio.load(wav1_path)
    waveform2, sr2 = torchaudio.load(wav2_path)

    emb1 = tts.synthesizer.tts_model.get_speaker_embedding(waveform1, sr=sr1)
    emb2 = tts.synthesizer.tts_model.get_speaker_embedding(waveform2, sr=sr2)

    similarity = 1 - cosine(emb1.squeeze(), emb2.squeeze())
    print(f"🧠 두 음성 유사도: {similarity:.3f}")


if __name__ == "__main__":
    import sys

    wav1 = sys.argv[1]
    wav2 = sys.argv[2]
    compare_voices(wav1, wav2)


# > 0.95	    매우 유사 — 거의 같은 사람
# 0.85 ~ 0.95	꽤 유사 — 같은 성별, 억양 비슷
# 0.70 ~ 0.85	다름 — 다른 사람일 확률 높음
# < 0.70	    확실히 다름
