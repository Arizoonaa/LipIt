# app/file/delete.py
import asyncio
import os
import logging

logger = logging.getLogger(__name__)


async def delete_file_later(path: str, delay: int = 5):
    logger.info(f"🕒 Scheduled deletion of {path} in {delay} seconds")
    await asyncio.sleep(delay)
    if os.path.exists(path):
        os.remove(path)
        logger.info(f"✅ Deleted file: {path}")
    else:
        logger.warning(f"⚠️ File already gone: {path}")
