# app/graph/node/prompt_report.py
from langchain_core.messages import HumanMessage, SystemMessage
import logging

logger = logging.getLogger(__name__)


def prompt_report_node(state: dict) -> dict:
    messages = state["messages"]
    # 사용자 이름 가져오기 (없으면 기본값 사용)
    member_name = state.get("member_name", "사용자")

    # 음성 이름 가져오기 (없으면 기본값 사용)
    voice_name = state.get("voice_name", "English Tutor")

    # 디버깅을 위해 로그에 정보 기록
    logger.info(
        f"📝 리포트를 위한 사용자 이름: '{member_name}', 음성 이름: '{voice_name}'"
    )

    # 전체 대화 텍스트 - AI를 음성 이름으로 표시
    conversation_text = "\n".join(
        [
            f"{voice_name if m.type == 'ai' else '당신'}: {m.content}"
            for m in messages
            if hasattr(m, "content") and m.content
        ]
    )

    # 사용자 메시지만 따로 추출
    user_sentences_list = [
        m.content.strip()
        for m in messages
        if m.type == "human" and hasattr(m, "content") and m.content.strip()
    ]

    # 사용자 문장을 문자열로 구성 (프롬프트에 삽입)
    user_sentences_block = "\n".join(f"- {s}" for s in user_sentences_list)

    # 시스템 프롬프트 생성
    system_prompt = f"""
You are an assistant generating a structured summary report of a phone conversation between a user named "{member_name}" and an AI voice assistant named "{voice_name}". 

📌 USER NAME: {member_name}
📌 VOICE NAME: {voice_name}
📌 Use the actual name "{member_name}" throughout the report.
📌 Use the voice name "{voice_name}" instead of "English Tutor" when referring to the AI assistant.
📌 Your task is to analyze the conversation and return a polished, natural, and helpful Korean report.
📌 IMPORTANT: Keep numeric digits as they are in Korean text (예: "2개", "3시간", "5가지").

1. 📖 Summary (summary)

- 이번 대화의 **처음부터 끝까지** 균형 있게 요약해 주세요 (존댓말).
- **80자 이내**로 작성하세요. 이 글자수는 엄격한 제한입니다.
- 대화의 시작, 중간, 마무리 부분이 모두 요약에 포함되어야 합니다.
- 핵심 주제와 흐름의 변화를 간략하게 표현하세요.
- "{member_name}"님과 "{voice_name}" 간의 주요 대화 내용을 포함하세요.
- 한국어 텍스트에서는 숫자를 그대로 유지하세요 (예: "2일", "10분").

2. 💡 Feedback (feedback)

- 피드백은 통화 내용에서 사용자가 말한 내용 중 어색한 표현에 대해 수정하는 방식으로 주세요.
- "{member_name}님"의 영어 사용에 대해 **교사처럼 친절한 말투**로 한국어 피드백을 작성해주세요.
- "{member_name}님은 ~" 형태로 이름을 사용하여 직접 말해 주세요.
- "사용자님" 대신 반드시 "{member_name}님"으로 표현해주세요.
- 영어 실력, 표현, 문장 구성, 발음, 유창성 등에 대해 구체적으로 코멘트 해 주세요.
- 반드시 **한 가지 이상 구체적인 팁**을 주세요. 예: "'I like' 대신 'I'm into'라고 말하면 더 자연스럽습니다."
- 영어 단어는 **영문 스펠링**만 사용하세요.
- 100자 내외, 친절하고 긍정적으로 작성하세요.
- 숫자는 그대로 유지하세요 (예: "3개의 예시").


3. 🎯 **English Level (english_level)**

- 사용자의 영어 회화 실력을 평가하여 "상", "중", "하" 중 하나로 평가해주세요.
- 각 수준의 기준은 다음과 같습니다:
  - "상": 복잡한 주제에 대해 자연스럽게 대화 가능, 문법/어휘 오류가 거의 없음
  - "중": 일상적인 주제로 의사소통 가능, 간헐적인 문법/어휘 오류가 있음
  - "하": 기본적인 의사 표현만 가능, 빈번한 문법/어휘 오류가 있음


4. ✍️ **Native Expressions (native_expressions)**

From the user's original sentences below, extract **up to 3**. For each:

- "my_sentence": original sentence by user.
- "native_sentence": a more natural, native-style English version. Convert any numbers to text (e.g., "2 apples" → "two apples").
- "keyword": one **key native idiom or phrase** from your version.
- "keyword_kor": basic **dictionary-style** Korean translation of the keyword (e.g., "wrap up" → "마무리하다").

---


--- USER SENTENCES START ---
{user_sentences_block}
--- USER SENTENCES END ---


📢 IMPORTANT INSTRUCTIONS:

- Only use exact user inputs for "my_sentence"
- ALWAYS use the name "{member_name}" when referring to the user
- ALWAYS use "{voice_name}" when referring to the AI assistant
- NEVER use generic terms like "사용자" or "Tutor" or "English Tutor"
- Replace ALL instances of "사용자님" with "{member_name}님"
- Make sure to personalize the report using the actual names
- In the summary, include BOTH what the user said AND how {voice_name} responded
- Create a balanced summary that shows the interaction between both participants
- Use simple JSON formatting — no markdown, no commentary
- Convert all numbers to text words in English only (e.g., "2 hours" → "two hours"), but keep numbers as digits in Korean (e.g., "5분")


Return your answer in **strict JSON format**:

{{
  "summary": "...",
  "feedback": "...",
  "english_level": "상" | "중" | "하",
  "native_expressions": [
    {{
      "my_sentence": "...",
      "native_sentence": "...",
      "keyword": "...",
      "keyword_kor": "..."
    }},
    ...
  ],
}}
""".strip()

    prompt = [
        SystemMessage(content=system_prompt.strip()),
        HumanMessage(content=conversation_text),
    ]

    # 디버깅을 위해 실제 프롬프트 출력
    print(
        f"🔍 프롬프트에 포함된 사용자 이름: '{member_name}', 음성 이름: '{voice_name}'"
    )

    state["chat_prompt"] = prompt
    return state
