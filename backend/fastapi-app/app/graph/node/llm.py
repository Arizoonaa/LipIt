# app/graph/node/llm.py
from app.graph.llm_client import llm


async def llm_node(state: dict) -> dict:
    """
    GPT 호출로 response.content 저장
    - 입력: state["chat_prompt"]
    - 출력: state["raw_llm_response"]
    """
    chat_prompt = state["chat_prompt"]

    response = await llm.ainvoke(chat_prompt)

    state["raw_llm_response"] = response.content
    return state
