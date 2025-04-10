# app/util/text_chunker.py
import re


import re


def split_text_to_chunks(
    text: str, max_chars: int = 150, min_chars: int = 30
) -> list[str]:
    """
    TTS 성능을 고려한 텍스트 chunk 분리기
    - max_chars: 한 chunk당 최대 문자 수 (ex. 100 ≒ 약 4~5초 음성)
    - min_chars: 너무 짧은 chunk는 이전 chunk와 병합
    """

    # 문장 단위로 분리
    sentences = re.split(r"(?<=[.!?;:-])\s+", text.strip())
    chunks = []

    for sentence in sentences:
        sentence = sentence.strip()
        if not sentence:
            continue
        if len(sentence) <= max_chars:
            chunks.append(sentence)
        else:
            # 긴 문장은 단어 기준으로 자르기
            words = sentence.split()
            current = ""
            for word in words:
                if len(current) + len(word) + 1 > max_chars:
                    chunks.append(current.strip())
                    current = word
                else:
                    current += " " + word if current else word
            if current:
                chunks.append(current.strip())

    # 너무 짧은 chunk는 이전 것과 합치기
    merged = []
    buffer = ""
    for chunk in chunks:
        if len(buffer) + len(chunk) + 1 < min_chars:
            buffer += " " + chunk if buffer else chunk
        else:
            if buffer:
                merged.append(buffer.strip())
            buffer = chunk
    if buffer:
        merged.append(buffer.strip())

    return merged
