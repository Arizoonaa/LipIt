# app/graph/util/context_formatter.py


def convert_context_to_memory_lines(contexts: list[dict]) -> list[str]:
    """
    RAG로 검색된 context를 그대로 memory로 사용 (이미 자연어 형태로 저장됨)
    """
    return [ctx["content"] for ctx in contexts if ctx.get("content")]
