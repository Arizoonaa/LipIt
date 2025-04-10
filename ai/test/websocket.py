import asyncio
import websockets


async def test_tts_ws():
    uri = "wss://webmaster-motel-findarticles-aspect.trycloudflare.com/ws/tts"
    text = "Hi there! Are you a sports fan?"

    async with websockets.connect(uri) as websocket:
        print("✅ TTS 서버 WebSocket 연결됨")
        await websocket.send(text)
        print("📤 텍스트 전송 완료")

        count = 0
        while True:
            try:
                data = await websocket.recv()
                if isinstance(data, bytes):
                    print(f"📥 받은 chunk {count + 1} - 크기: {len(data)} bytes")
                    with open(f"chunk_{count}.wav", "wb") as f:
                        f.write(data)
                    count += 1
                else:
                    print("📛 예상치 못한 데이터:", data)
                    break
            except websockets.exceptions.ConnectionClosed:
                print("🔌 연결 종료됨")
                break


asyncio.run(test_tts_ws())
