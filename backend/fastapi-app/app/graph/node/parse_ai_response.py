# app/graph/node/parse_ai_response_node.py
from app.graph.util.parser import parse_gpt_json_response


async def parse_ai_response_node(state: dict) -> dict:
    raw = state.get("raw_llm_response", "")
    result = parse_gpt_json_response(raw, ["en", "ko", "should_end_call"])

    state["ai_response"] = result["en"] or raw
    state["ai_response_kor"] = result["ko"] or ""
    state["should_end_call"] = (
        result["should_end_call"] if result["should_end_call"] is not None else False
    )
    return state
