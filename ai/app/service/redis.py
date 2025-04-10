# from app.core.tts_model import get_tts
# import redis
# import pickle
# import hashlib

# redis_client = redis.Redis(host="redis", port=6379, decode_responses=False)


# def get_cached_embedding(voice_id: str, speaker_wav_url: str):
#     tts = get_tts()

#     key = f"embedding:{voice_id}"

#     cached = redis_client.get(key)
#     if cached:
#         return pickle.loads(cached)

#     # wav 다운로드 (캐싱 또는 직접 다운로드 방식)
#     # speaker_wav_path = get_cached_speaker_wav(speaker_wav_url)

#     embedding = tts.get_speaker_embedding(speaker_wav=speaker_wav_url)

#     redis_client.set(key, pickle.dumps(embedding), ex=86400)  # 1일 TTL

#     return embedding
