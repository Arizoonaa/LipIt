import aiohttp


async def fetch_audio_as_bytes(audio_url: str) -> bytes:
    async with aiohttp.ClientSession() as session:
        async with session.get(audio_url) as resp:
            return await resp.read()
