# app/graph/node/end_prompt.py
from app.graph.util.json_prompt_builder import build_json_response_prompt


def prompt_end_call_node(state: dict) -> dict:
    suffix = "You are ending a phone call. Wrap up the conversation in a friendly and natural way."
    system_prompt = build_json_response_prompt(suffix=suffix)
    user_prompt = "End the phone conversation naturally."
    state["chat_prompt"] = [
        {"role": "system", "content": system_prompt},
        {"role": "user", "content": user_prompt},
    ]
    state["user_input"] = user_prompt
    return state
