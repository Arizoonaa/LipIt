from typing import Union
from app.api.v1.endpoint.call import (
    start_call_endpoint,
    add_message_to_call_endpoint,
    end_call_endpoint,
)
from app.core.base_router import BaseRouter
from app.schema.call import (
    AIMessageResponse,
    EndCallResponse,
    StartCallRequest,
    StartCallResponse,
    UserMessageRequest,
)
from app.schema.common import APIResponse
from app.exception.error_code import Error

router = BaseRouter(prefix="/api/calls", tags=["전화"])

router.api_doc(
    path="",
    endpoint=start_call_endpoint,
    methods=["POST"],
    request_model=StartCallRequest,
    response_model=APIResponse[StartCallResponse],
    request_example={
        "memberId": 1,
        "topic": "SPORTS",
    },
    success_model=StartCallResponse,
    success_example={
        "callId": 1,
        "startTime": "2025-03-14T14:20:30.123Z",
        "aiMessage": "Hello! How are you doing? I heard you're a big sports enthusiast. What's your favorite sport to watch or play?",
        "aiMessageKor": "안녕하세요! 어떻게 지내고 계신가요? 스포츠 애호가라고 들었는데요. 가장 좋아하는 스포츠는 무엇인가요?",
    },
    errors={
        500: {
            "message": Error.CALL_INTERNAL_ERROR.message,
            "code": Error.CALL_INTERNAL_ERROR.code,
        }
    },
    summary="📞 통화 시작",
    description="AI 음성 ID와 대화 주제를 기반으로 새로운 통화를 생성하고 첫 메시지를 반환합니다.",
)

router.api_doc(
    path="/{callId}/messages",
    endpoint=add_message_to_call_endpoint,
    methods=["PATCH"],
    request_model=UserMessageRequest,
    response_model=APIResponse[AIMessageResponse],
    request_example={"callId": 1, "userMessage": "I don't like sports that much."},
    success_model=AIMessageResponse,
    success_example={
        200: {
            "summary": "일반 대화 응답",
            "description": "통화가 계속되는 경우의 AI 응답 예시",
            "value": {
                "aiMessage": "That's totally fine! Sports aren't for everyone. So, besides listening to music, what else do you enjoy doing in your free time?",
                "aiMessageKor": "괜찮습니다! 스포츠가 모든 사람을 위한 것은 아닙니다. 그렇다면 음악을 듣는 것 외에도 여가 시간에 즐기는 것은 무엇인가요?",
            },
        },
        201: {
            "summary": "통화 종료 응답",
            "description": "사용자의 발화로 통화가 종료될 때의 응답 예시",
            "value": {
                "aiMessage": "Alright, sounds good! If you have any more questions in the future, feel free to reach out. Have a great day!",
                "aiMessageKor": "좋아요, 좋아요! 앞으로 더 궁금한 점이 있으면 언제든지 질문해 주세요. 좋은 하루 되세요!",
                "endTime": "2025-03-14T14:25:45.678Z",
                "duration": 315,
                "reportCreated": True,
            },
        },
    },
    errors={
        404: {
            "message": Error.CALL_NOT_FOUND.message,
            "code": Error.CALL_NOT_FOUND.code,
        },
        500: {
            "message": Error.CALL_INTERNAL_ERROR.message,
            "code": Error.CALL_INTERNAL_ERROR.code,
        },
    },
    summary="📞 메시지 추가",
    description="사용자의 메시지를 저장하고, AI의 응답 메시지를 messages 리스트에 추가합니다.",
)

router.api_doc(
    path="/{callId}/end",
    endpoint=end_call_endpoint,
    methods=["PATCH"],
    request_model=None,
    response_model=APIResponse[EndCallResponse],
    success_model=EndCallResponse,
    success_example={
        "endTime": "2025-03-14T14:25:45.678Z",
        "duration": 315,
        "reportCreated": True,
    },
    errors={
        400: {
            "message": Error.CALL_ALREADY_ENDED.message,
            "code": Error.CALL_ALREADY_ENDED.code,
        },
        404: {
            "message": Error.CALL_NOT_FOUND.message,
            "code": Error.CALL_NOT_FOUND.code,
        },
        500: {
            "message": Error.CALL_INTERNAL_ERROR.message,
            "code": Error.CALL_INTERNAL_ERROR.code,
        },
    },
    summary="📞 통화 종료",
    description="사용자가 통화 종료 버튼을 클릭하여 통화를 종료합니다.",
)
