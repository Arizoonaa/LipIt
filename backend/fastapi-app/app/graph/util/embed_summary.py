# app/graph/util/embed_summary.py

from typing import List
import asyncio
from app.rag.embedding import get_embedding_model


async def summarize_contexts_by_embedding(
    contexts: List[str], top_k: int = 3
) -> List[str]:
    """
    여러 context 중 의미 있는 핵심 문장만 추리는 임베딩 기반 요약 함수.
    유사한 문장은 하나로 통합되도록 top_k개만 반환.
    """
    if not contexts:
        return []

    model = get_embedding_model()

    # dict인 경우 content 필드만 추출
    pure_texts = [c["content"] if isinstance(c, dict) else c for c in contexts]

    embeddings = await asyncio.to_thread(model.embed_documents, pure_texts)

    # 중심에 가까운 벡터 추출 (simple 방식: 평균 벡터와의 거리로 판단)
    import numpy as np

    avg_embedding = np.mean(embeddings, axis=0)
    scores = [
        np.dot(e, avg_embedding) / (np.linalg.norm(e) * np.linalg.norm(avg_embedding))
        for e in embeddings
    ]

    # 가장 중심적인 top_k 문장 추출
    sorted_indices = sorted(range(len(scores)), key=lambda i: scores[i], reverse=True)
    selected = [contexts[i] for i in sorted_indices[:top_k]]

    return selected
