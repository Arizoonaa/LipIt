from langgraph.graph import StateGraph
from app.graph.node.parse_report import parse_report_node
from app.graph.node.llm import llm_node
from app.graph.node.prompt_report import prompt_report_node
from app.graph.schema.state import CallState


def build_report_graph():
    builder = StateGraph(CallState)

    builder.add_node("report_prompt", prompt_report_node)
    builder.add_node("llm", llm_node)
    builder.add_node("parse_report", parse_report_node)

    builder.set_entry_point("report_prompt")
    builder.add_edge("report_prompt", "llm")
    builder.add_edge("llm", "parse_report")
    builder.set_finish_point("parse_report")

    return builder.compile()
