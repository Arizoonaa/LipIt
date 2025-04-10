package com.ssafy.lipit_app.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import java.util.regex.Pattern

object KeywordHighlightUtil {

    fun highlightKeywordInSentence(sentence: String, keyword: String): AnnotatedString {
        val baseKeyword = keyword.lowercase().trim()
        val builder = AnnotatedString.Builder(sentence)

        val patterns = generateSmartPatterns(baseKeyword)

        patterns.forEach { patternStr ->
            val pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(sentence)

            while (matcher.find()) {
                builder.addStyle(
                    style = SpanStyle(
                        background = Color(0xFFD09FE6).copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold
                    ),
                    start = matcher.start(),
                    end = matcher.end()
                )
            }
        }

        return builder.toAnnotatedString()
    }

    private fun generateSmartPatterns(keyword: String): List<String> {
        val tokens = keyword.trim().lowercase().split("\\s+".toRegex())

        return when {
            tokens.size == 1 -> {
                val k = Pattern.quote(tokens[0])
                listOf(
                    "\\b$k\\b",
                    "\\b$k(s|es|ed|ing)?\\b",
                    "\\b(is|am|are|was|were|has|have|had)\\s+$k\\b",
                    "\\b(isn't|aren't|wasn't|weren't|hasn't|haven't|hadn't)\\s+$k\\b",
                    "\\b(i'm|we're|they're|it's|he's|she's)\\s+$k\\b",
                    "\\b$k\\s+(into|about|with|for|to)\\b",
                    "\\b(into|about|with|for|to)\\s+$k\\b"
                )
            }

            tokens.size == 2 -> {
                // ✅ be into 같은 경우: be -> 다양한 축약형과 변형 고려
                val part1 = tokens[0]
                val part2 = tokens[1]

                val part1Patterns = if (part1 == "be") {
                    listOf(
                        "\\b(i\\s*'?m|you\\s*'?re|he\\s*'?s|she\\s*'?s|we\\s*'?re|they\\s*'?re|is|am|are|was|were)\\b"
                    )
                } else {
                    listOf("\\b${Pattern.quote(part1)}\\b")
                }

                val part2Pattern = "\\b${Pattern.quote(part2)}\\b"

                // 결과: be 변형 + 두 번째 단어
                part1Patterns + part2Pattern
            }

            else -> {
                val first = Pattern.quote(tokens.first())
                val last = tokens.drop(1).map { Pattern.quote(it) }
                val flexibleMiddle = last.joinToString(separator = "\\s+") {
                    "(?:\\w+\\s+)?$it"
                }
                listOf("\\b$first(?:\\s+\\w+){0,2}\\s+$flexibleMiddle\\b")
            }
        }
    }

}
