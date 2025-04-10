# app/graph/util/chunking.py


# 예시: message 단위로 3개씩 묶는 sliding chunk
def chunk_messages(messages, chunk_size=3):
    return [
        "\n".join(f"{m.type}: {m.content}" for m in messages[i : i + chunk_size])
        for i in range(0, len(messages), chunk_size)
    ]
