package com.ssafy.lipit_app.ui.screens.edit_call.add_voice

import android.net.Uri

// 모든 UI 상태 데이터를 담는 State 클래스
data class AddVoiceState(
    val recordingStatus: RecordingStatus = RecordingStatus.WAITING, // 녹음 상태
    val currentSentenceIndex: Int = 0, // 현재 녹음하는 문장의 인덱스
    val sentenceList: List<String> = sampleSentences, // 사용자가 읽을 전체 문장
    val recognizedText: String = "", // STT로 인식된 텍스트
    val accuracy: Float = 0f, // 문장 정확도 (0.0 ~ 1.0)
    val recordedFiles: List<String> = emptyList(), // 각 문장별 녹음 파일 경로
    val isAllSentencesRecorded: Boolean = false, // 모든 문장 녹음 완료 여부

    val selectedImageUri: Uri? = null,  // 추가: 선택된 이미지 URI
    val voiceName: String = "", // 저장할 음성 이름

    val isUploading: Boolean = false, // 업로드 중 여부
    val uploadSuccess: Boolean = false, // 업로드 성공 여부

    // Error Dialog 처리용
    val errorMessage: String? = null,      // 오류 메시지
    val showErrorPopup: Boolean = false,   // 에러 팝업 표시 여부
)

// 녹음 상태를 표현하는 열거형
enum class RecordingStatus {
    WAITING,    // 녹음 대기 중
    RECORDING,  // 녹음 중
    ANALYZING,  // 음성 분석 중
    COMPLETED,  // 녹음 및 분석 완료 (정확도 충분)
    FAILED      // 녹음 분석 실패 (정확도 부족)
}

// 샘플 문장 목록 - 이 파일에 함께 정의
val sampleSentences = listOf(
//    "Hi",
   "Hello, how are you doing today?",
   "I really enjoy reading books in the morning.",
    "The quick brown fox jumps over the lazy dog.",
    "Could you please repeat that one more time?",
    "It's a beautiful day to learn something new.",
    "Technology keeps changing the way we live.",
    "Sometimes silence speaks louder than words.",
    "What's your favorite kind of music to listen to?",
    "I can’t believe how fast time flies these days.",
    "Practice makes perfect, so never give up!"
)