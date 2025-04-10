package com.ssafy.lipit_app.util

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

object CommonUtils {

    /**
     * 270초를 4분 30초 형태로 변환
     *
     * @param timestamp 변환할 Timestamp 객체
     * @return "yyyy년 MM월 dd일" 형식의 날짜 문자열
     */
    fun formatSeconds(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "${minutes}분 ${seconds}초"
    }

    /**
     * 2025-03-15 => 2025년 03월 15일로 형태 변환
     */
    fun formatDate(dateString: String): String {

        val regex = "\\d{4}-\\d{2}-\\d{2}".toRegex()
        if (!regex.matches(dateString)) {
            return "Invalid date format. Please use YYYY-MM-DD format."
        }

        val parts = dateString.split("-")

        return "${parts[0]}년 ${parts[1]}월 ${parts[2]}일"
    }

}
