# app/graph/node/prompt_meaningful_messages.py


async def prompt_meaningful_messages_node(state: dict) -> dict:
    history = state.get("messages", [])

    MEANINGLESS_HUMAN_UTTERANCES = {
        "bye",
        "goodbye",
        "ok",
        "okay",
        "thanks",
        "thank you",
        "good",
        "alright",
        "see you",
    }

    # 대화에서 의미 있는 "human + ai" 쌍만 추출
    dialogue_pairs = []
    current_ai = None

    def is_meaningful(text: str) -> bool:
        stripped = text.strip().lower()
        return stripped not in MEANINGLESS_HUMAN_UTTERANCES and len(stripped) > 2

    for msg in history:
        if hasattr(msg, "type"):
            role = msg.type
            content = msg.content
        elif msg.__class__.__name__ == "HumanMessage":
            role = "human"
            content = msg.content
        else:
            role = "ai"
            content = msg.content

        if role == "ai":
            current_ai = content.strip()
        elif role == "human":
            if is_meaningful(content):
                dialogue_pairs.append({"ai": current_ai, "human": content.strip()})
            current_ai = None

    # 프롬프트
    base_suffix = """
**Meaningful Messages with Tags and Summary Facts (meaningful_messages)**

You will be given a list of I → you exchanges from a phone conversation.

Your task is to extract up to 5 meaningful responses, but only if they actually exist in the conversation.
If no meaningful facts are stated, return an empty list.

For each meaningful response:
- Rephrase it into a full sentence that includes the original I-question if relevant.
- Speak **directly to the user** (use "you").
- Clearly describe what you (the user) said (e.g., your name, hobby, location, favorite song/lyric, etc.).
- DO NOT include rhetorical or vague questions such as "Don't you know...?" or "You know what...?"
- DO NOT invent or assume anything that was not clearly said.

Each item must include:
- "content": A full sentence summarizing what you said, optionally including what I asked. Use natural language and second-person "you".
- "tags": 3–5 specific and relevant English keywords
- "summary_facts": concise facts you stated (e.g., "Your name is Sarah", "You enjoy painting", "Your favorite lyric is from Black or White")

⚠️ DO NOT include vague replies like "okay", "thanks", "bye", or rhetorical questions like "Do you know my favorite song?"
⚠️ DO NOT generate any message unless it was clearly said in the conversation.
⚠️ Avoid generic tags like "sentence", "talk", or "English".
✅ Convert numeric digits to words in English only, but keep digits in Korean.

IMPORTANT:
Use the extracted facts to help answer future questions accurately. For example, if the user later asks, "Do you remember my favorite song?", you should be able to recall it using this data.

Return only valid JSON in the structure shown below.
The example is for format only — do not generate anything unless it was explicitly said.

{
  "meaningful_messages": [
    {
      "content": "You said you enjoy painting in your free time.",
      "tags": ["hobby", "painting", "art"],
      "summary_facts": ["You enjoy painting"]
    }
  ]
}
"""

    examples = "\n\n".join(
        [
            f"ai: {pair['ai']}\nhuman: {pair['human']}"
            for pair in dialogue_pairs
            if pair["ai"] and pair["human"]
        ]
    )

    system_prompt = f"{base_suffix}\n\nConversation:\n{examples}"
    state["chat_prompt"] = [{"role": "system", "content": system_prompt}]
    return state
