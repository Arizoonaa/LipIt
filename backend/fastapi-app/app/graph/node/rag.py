# app/graph/node/rag.py
from app.rag.search import search_relevant_call_memory
from dateutil.parser import parse


async def rag_node(state: dict) -> dict:
    member_id = state["member_id"]
    user_input = state["input"]

    matches = await search_relevant_call_memory(
        member_id=member_id, query=user_input, top_k=3
    )

    # 유사도 필터링 + created_at 기준 정렬
    filtered = []
    for m in matches:
        if m["score"] >= 0.5:
            item = {
                "content": m["content"],
                "score": m["score"],
                "created_at": m["created_at"],
            }
            if "summary_facts" in m:
                item["summary_facts"] = m["summary_facts"]
            filtered.append(item)

    # 최신 순 정렬
    # 문자열을 datetime으로 바꿔 정렬
    filtered = sorted(filtered, key=lambda x: parse(x["created_at"]), reverse=True)

    state["retrieved_context"] = filtered[:3]
    return state

    # member_id = state["member_id"]
    # user_input = state["input"]

    # matches = await search_relevant_call_memory(
    #     member_id=member_id, query=user_input, top_k=10
    # )
    # raw_texts = [m["content"] for m in matches if m.get("score", 0) >= 0.5]

    # # 1. chunking
    # chunked = chunk_contexts(raw_texts)  # 줄 단위 분리
    # deduped = list(set(chunked))  # 중복 제거

    # # 2. 요약 (유사 문장 압축)
    # summary_contexts = await summarize_contexts_by_embedding(deduped)

    # # 3. 결과 저장
    # state["retrieved_context"] = raw_texts
    # return state
