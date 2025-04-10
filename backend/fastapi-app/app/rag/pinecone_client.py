# app/graph/pinecone_client.py
from pinecone import Pinecone, ServerlessSpec

from app.core.config import INDEX_NAME, PINECONE_API_KEY


pc = Pinecone(api_key=PINECONE_API_KEY)
_index = None


def get_index():
    global _index
    if _index is None:
        try:
            _index = pc.Index(INDEX_NAME)
            # 테스트로 dummy query를 넣어도 OK
            _ = _index.describe_index_stats()
        except Exception:
            print(f"🔧 Index '{INDEX_NAME}' not found. Creating it...")
            pc.create_index(
                name=INDEX_NAME,
                dimension=384,
                metric="cosine",
                spec=ServerlessSpec(cloud="aws", region="us-east-1"),
            )
            _index = pc.Index(INDEX_NAME)
    return _index
