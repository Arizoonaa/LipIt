# app/graph/call_graph.py
from langgraph.graph import StateGraph
from app.graph.node import (
    memory_node,
    prompt_start_call_node,
    prompt_meaningful_messages_node,
    prompt_ai_response_node,
    parse_ai_response_node,
    parse_meaningful_messages_node,
    llm_node,
    rag_node,
    tts_node,
)

from app.graph.schema.state import CallState


def build_start_call_graph():
    builder = StateGraph(CallState)
    builder.add_node("start_prompt", prompt_start_call_node)
    builder.add_node("llm", llm_node)
    builder.add_node("parse", parse_ai_response_node)
    builder.add_node("tts", tts_node)
    builder.add_node("memory", memory_node)

    builder.set_entry_point("start_prompt")
    builder.add_edge("start_prompt", "llm")
    builder.add_edge("llm", "parse")
    builder.add_edge("parse", "tts")
    builder.add_edge("tts", "memory")
    builder.set_finish_point("memory")
    return builder.compile()


def build_add_message_graph():
    builder = StateGraph(CallState)
    builder.add_node("memory", memory_node)
    builder.add_node("rag", rag_node)
    builder.add_node("prompt", prompt_ai_response_node)
    builder.add_node("llm", llm_node)
    builder.add_node("parse", parse_ai_response_node)
    builder.add_node("tts", tts_node)
    builder.add_node("memory2", memory_node)

    builder.set_entry_point("memory")
    builder.add_edge("memory", "rag")
    builder.add_edge("rag", "prompt")
    builder.add_edge("prompt", "llm")
    builder.add_edge("llm", "parse")
    builder.add_edge("parse", "tts")
    builder.add_edge("tts", "memory2")
    builder.set_finish_point("memory2")
    return builder.compile()


def build_meaningful_messages_graph():
    builder = StateGraph(CallState)
    builder.add_node("memory", memory_node)
    builder.add_node("meaningful_messages_prompt", prompt_meaningful_messages_node)
    builder.add_node("llm", llm_node)
    builder.add_node("parse", parse_meaningful_messages_node)

    builder.set_entry_point("memory")
    builder.add_edge("memory", "meaningful_messages_prompt")
    builder.add_edge("meaningful_messages_prompt", "llm")
    builder.add_edge("llm", "parse")
    builder.set_finish_point("parse")
    return builder.compile()
