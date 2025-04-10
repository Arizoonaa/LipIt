# app/graph/node/parse_meaningful_messages.py
from app.graph.util.parser import parse_gpt_json_response


async def parse_meaningful_messages_node(state: dict) -> dict:
    raw = state.get("raw_llm_response", "")
    result = parse_gpt_json_response(raw, ["meaningful_messages"])

    state["meaningful_messages"] = result["meaningful_messages"] or []
    return state
