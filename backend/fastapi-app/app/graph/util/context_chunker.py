import re
from typing import List


def chunk_contexts(raw_contexts: List[str]) -> List[str]:
    chunks = []
    for ctx in raw_contexts:
        # 'ai: ~\nhuman: ~' 구조라면 분리
        parts = re.split(r"\n+", ctx)
        chunks.extend(part.strip() for part in parts if part.strip())
    return chunks
