# app/graph/node/parse_report.py
from app.graph.util.parser import parse_gpt_json_response
import logging

logger = logging.getLogger(__name__)


async def parse_report_node(state: dict) -> dict:
    """
    GPT 응답(JSON)을 파싱하여 summary, feedback, native_expressions 필드에 저장
    """
    response_content = state.get("raw_llm_response", "")

    keys = ["summary", "feedback", "english_level", "native_expressions"]

    parsed = parse_gpt_json_response(response_content, expected_keys=keys)

    state["summary"] = parsed["summary"] or "요약 실패"
    state["feedback"] = parsed["feedback"] or "피드백 실패"
    state["english_level"] = parsed["english_level"] or "중"  # 기본값은 중으로 설정
    state["native_expressions"] = parsed["native_expressions"] or []

    return state
