//package com.ssafy.lipit_app.ui.screens.edit_call.add_voice.before
//
//// AddVoiceIntent.kt
//sealed class AddVoiceIntent {
//    object StartRecording : AddVoiceIntent()
//    object StopRecording : AddVoiceIntent()
//    object NextSentence : AddVoiceIntent()
//    data class SetVoiceName(val name: String) : AddVoiceIntent()
//    object SubmitVoice : AddVoiceIntent()
//    object NavigateToMain : AddVoiceIntent()
//}
//
//// AddVoiceState.kt
//data class AddVoiceState(
//    val recordingStatus: RecordingStatus = RecordingStatus.WAITING, // 녹음 상태
//    val currentSentenceIndex: Int = 0, // 현재 녹음하는 문장의 인덱스
//    val sentenceList: List<String> = sampleSentences, // 사용자가 읽을 전체 문장
//    val recognizedText: String = "", // STT로 인식된 텍스트
//    val accuracy: Float = 0f, // 문장 정확도 (0.0 ~ 1.0)
//    val recordedFiles: List<String> = emptyList(), // 각 문장별 녹음 파일 경로
//    val isAllSentencesRecorded: Boolean = false, // 모든 문장 녹음 완료 여부
//    val voiceName: String = "", // 저장할 음성 이름
//    val isUploading: Boolean = false, // 업로드 중 여부
//    val uploadSuccess: Boolean = false, // 업로드 성공 여부
//    val errorMessage: String? = null // 오류 메시지
//)
//
//enum class RecordingStatus {
//    WAITING,    // 녹음 대기 중
//    RECORDING,  // 녹음 중
//    ANALYZING,  // 음성 분석 중 (새로 추가)
//    COMPLETED,  // 녹음 및 분석 완료 (정확도 충분)
//    FAILED      // 녹음 분석 실패 (정확도 부족) (새로 추가)
//}