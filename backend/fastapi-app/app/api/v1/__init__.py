from app.api.v1.endpoint.s3_router import router as audio_router
from app.api.v1.endpoint.call_router import router as call_router
from app.api.v1.endpoint.rag_router import router as rag_router
from app.api.v1.endpoint.report_router import router as report_router
from app.api.v1.endpoint.daily_sentence_router import router as daily_sentence_router
from app.api.v1.endpoint.ws_android import router as ws_android_router
from app.api.v1.endpoint.ws_android_call import router as ws_android_call_router


def get_routers():
    return [
        daily_sentence_router,
        call_router,
        rag_router,
        audio_router,
        report_router,
        ws_android_router,
        ws_android_call_router,
    ]
