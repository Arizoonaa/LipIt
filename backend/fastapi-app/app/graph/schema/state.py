# app/schema/state.py
from mailbox import BabylMessage
from typing import List, Optional, TypedDict, Union

from app.model.news import News


class CallState(TypedDict, total=False):
    call_id: int
    member_id: int
    input: str  # LLM에 넘길 전체 prompt
    user_input: str  # 실제 사용자 메시지
    topic: Optional[str]
    news: Optional[News]
    messages: List[Union[BabylMessage, dict]]  # LangChain 메시지 or dict
    ai_response: str
    ai_response_kor: str
    ai_audio_url: str
    chat_prompt: List[dict]  # 역할 분리 프롬프트
    retrieved_context: List[dict]  # RAG 결과
    should_end_call: bool  # 대화 종료 여부
    is_timeout: bool
    raw_llm_response: str
    duration: int
    summary: str
    feedback: str
    native_expressions: List[dict]
    meaningful_messages: List[dict]
    voice_name: str
    voice_type: str
