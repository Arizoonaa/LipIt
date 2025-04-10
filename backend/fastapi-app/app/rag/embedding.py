# app/graph/embedding.py
import asyncio
from langchain_huggingface import HuggingFaceEmbeddings

_embedding_model = None  # 전역 변수로 모델 저장


def get_embedding_model():
    global _embedding_model
    if _embedding_model is None:
        _embedding_model = HuggingFaceEmbeddings(
            model_name="BAAI/bge-small-en-v1.5",
            model_kwargs={"device": "cpu"},
        )
    return _embedding_model


async def get_embedding(text: str) -> list:
    """텍스트 임베딩 생성 (HuggingFace BGE 모델 기반)"""
    model = get_embedding_model()
    return await asyncio.to_thread(model.embed_query, text)
