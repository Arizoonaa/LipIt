# app/graph/utils/parser.py
import json
import logging

logger = logging.getLogger(__name__)


def parse_gpt_json_response(response_content: str, expected_keys: list[str]) -> dict:
    """
    GPT 응답을 JSON으로 파싱하고 예상되는 키만 추출.
    실패 시 fallback 값 포함한 딕셔너리 반환
    """
    try:
        parsed = json.loads(response_content.strip())
        return {key: parsed.get(key, None) for key in expected_keys}
    except json.JSONDecodeError:
        logger.warning("🚫 JSON Decode Error in LLM response")
        logger.warning(f"💬 GPT Raw Response: {response_content}")
        return {key: None for key in expected_keys}
