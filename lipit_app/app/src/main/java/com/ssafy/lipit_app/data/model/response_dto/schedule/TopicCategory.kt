package com.ssafy.lipit_app.data.model.response_dto.schedule

/**
 * 사용법 예시
 * [영어 -> 한글]
 * val category = TopicCategory.fromEnglish("MOVIE_BOOK")
 * val korean = category?.koreanName ?: "기타"
 *
 * [한글 -> 영어]
 * val category = TopicCategory.fromKorean("영화/도서")
 * val englishName = category?.name ?: "ETC"
 */
enum class TopicCategory(val koreanName: String) {
    MOVIE_BOOK("영화/책"),
    TRIP("여행"),
    MUSIC("음악"),
    GAME("게임"),
    FOOD("음식"),
    SPORTS("스포츠"),
    HEALTH("건강");

    companion object {
        // 영어 → enum
        fun fromEnglish(name: String): TopicCategory? =
            values().find { it.name == name }

        // 한글 → enum
        fun fromKorean(korean: String): TopicCategory? =
            values().find { it.koreanName == korean }
    }
}
