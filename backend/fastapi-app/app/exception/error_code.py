from enum import Enum


class ErrorCode:
    def __init__(self, code: str, message: str):
        self.code = code
        self.message = message

    def __str__(self):
        return f"[{self.code}] {self.message}"


# 실제 에러 코드 정의
class Error:
    # Auth
    AUTH_UNAUTHORIZED = ErrorCode(
        "AUTH-001", "인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요."
    )
    AUTH_INVALID_TOKEN = ErrorCode("AUTH-002", "유효하지 않은 Access Token입니다.")
    AUTH_EXPIRED_TOKEN = ErrorCode("AUTH-003", "만료된 Access Token입니다.")
    AUTH_TOKEN_MISSING = ErrorCode("AUTH-004", "Access Token이 누락되었습니다.")

    # Call
    CALL_NOT_FOUND = ErrorCode("CALL-001", "해당 통화 기록을 찾을 수 없습니다.")
    CALL_ALREADY_ENDED = ErrorCode("CALL-002", "이미 종료된 통화입니다.")
    CALL_INTERNAL_ERROR = ErrorCode(
        "CALL-003", "전화 기능 처리 중 서버 오류가 발생했습니다."
    )
    CALL_AI_FAILED = ErrorCode("CALL-004", "AI 처리 중 서버 오류가 발생했습니다.")
    CALL_TTS_FAILED = ErrorCode("CALL-005", "TTS 처리 중 서버 오류가 발생했습니다.")

    # News
    NEWS_QUERY_ERROR = ErrorCode("NEWS-001", "뉴스 조회 중 서버 오류가 발생했습니다.")
    NEWS_UPDATE_ERROR = ErrorCode(
        "NEWS-002", "뉴스 업데이트 중 서버 오류가 발생했습니다."
    )
    NEWS_CRAWL_ERROR = ErrorCode("NEWS-003", "뉴스 크롤링 중 서버 오류가 발생했습니다.")

    # Audio
    AUDIO_UPLOAD_ERROR = ErrorCode(
        "AUDIO-001", "파일 업로드 중 서버 오류가 발생했습니다."
    )

    # RAG
    RAG_SEARCH_ERROR = ErrorCode("RAG-001", "RAG 검색 중 서버 오류가 발생했습니다.")

    # Report
    REPORT_NOT_FOUND = ErrorCode("REPORT-001", "보고서를 찾을 수 없습니다.")
    REPORT_INTERNAL_ERROR = ErrorCode(
        "REPORT-002", "보고서 처리 중 서버 오류가 발생했습니다."
    )

    # Daily Sentence
    DAILY_SENTENCE_NOT_FOUND = ErrorCode("DAILY-001", "오늘의 문장을 찾을 수 없습니다.")
    DAILY_SENTENCE_INTERNAL_ERROR = ErrorCode(
        "DAILY-002", "오늘의 문장 처리 중 오류가 발생했습니다."
    )

    # Member
    MEMBER_NOT_FOUND = ErrorCode("MEMBER-001", "사용자를 찾을 수 없습니다.")
    MEMBER_NO_SELECTED_VOICE = ErrorCode("MEMBER-002", "선택된 음성이 없습니다.")

    # Voice
    VOICE_NOT_FOUND = ErrorCode("VOICE-001", "해당 음성을 찾을 수 없습니다.")
    VOICE_AUDIO_URL_NOT_FOUND = ErrorCode("VOICE-002", "해당 음성의 URL이 없습니다.")

    # S3
    S3_PRESIGNED_ERROR = ErrorCode(
        "S3-001", "S3 Presigned URL 발급 중 오류가 발생했습니다."
    )
