# app/rag/store.py
import asyncio
from app.rag.embedding import get_embedding
from app.rag.pinecone_client import get_index
from app.util.datetime_utils import iso_now_kst


async def store_meaningful_messages(call_id: int, member_id: int, messages: list[dict]):
    index = get_index()
    for i, item in enumerate(messages):
        content = item["content"]
        tags = item["tags"]
        summary_facts = item["summary_facts"]
        embedding = await get_embedding(content)
        await asyncio.to_thread(
            lambda: index.upsert(
                [
                    {
                        "id": f"call-{call_id}-chunk-{i}",
                        "values": embedding,
                        "metadata": {
                            "member_id": str(member_id),
                            "content": content,
                            "tags": tags,
                            "summary_facts": summary_facts,
                            "created_at": iso_now_kst(),  # UTC 시간 기준
                        },
                    }
                ]
            )
        )
