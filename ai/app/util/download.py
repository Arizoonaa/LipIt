import os
import hashlib
import requests

WAV_CACHE_DIR = "/tmp/speaker_wavs"
os.makedirs(WAV_CACHE_DIR, exist_ok=True)


def get_cached_speaker_wav(speaker_wav_url: str) -> str:
    url_hash = hashlib.md5(speaker_wav_url.encode()).hexdigest()
    local_path = os.path.join(WAV_CACHE_DIR, f"{url_hash}.wav")

    if os.path.exists(local_path):
        return local_path

    response = requests.get(speaker_wav_url)
    if response.status_code != 200:
        raise Exception("Failed to download speaker wav")

    with open(local_path, "wb") as f:
        f.write(response.content)

    return local_path
