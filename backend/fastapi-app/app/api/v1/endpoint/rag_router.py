# app/api/v1/endpoint/rag_router.py
from app.api.v1.endpoint.rag import test_rag_memory_search
from app.core.base_router import BaseRouter
from app.exception.error_code import Error
from app.schema.common import APIResponse
from app.schema.rag import RAGTestRequest, RAGTestResponse

router = BaseRouter(prefix="/api/rag", tags=["RAG(테스트용)"])

router.api_doc(
    path="/test",
    endpoint=test_rag_memory_search,
    methods=["POST"],
    request_model=RAGTestRequest,
    response_model=APIResponse[RAGTestResponse],
    success_model=RAGTestResponse,
    success_example={
        "relatedCalls": [
            {
                "callId": "12",
                "content": "user: I love sports!\nai: That's great! What's your favorite sport?",
                "score": 0.895,
            },
            {
                "callId": "8",
                "content": "user: I watch baseball a lot.\nai: Oh nice, which team do you support?",
                "score": 0.841,
            },
        ]
    },
    errors={
        500: {
            "message": Error.CALL_INTERNAL_ERROR.message,
            "code": Error.CALL_INTERNAL_ERROR.code,
        }
    },
    summary="🔍 유사 대화 검색 테스트",
    description="사용자 메시지를 기반으로 이전 통화 기록 중 유사한 대화를 검색합니다.",
)
