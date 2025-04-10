from fastapi import APIRouter, WebSocket, WebSocketDisconnect
import websockets

from app.core.base_router import BaseRouter
from app.core.config import TTS_SERVER_URL

TTS_WS_URL = f"wss://{TTS_SERVER_URL}/ws/tts"

router = BaseRouter(prefix="/ws", tags=["WebSocket"], require_auth=False)


@router.websocket("/android/test")
async def android_ws(client_ws: WebSocket):
    print("TTS_WS_URL =", TTS_WS_URL)
    await client_ws.accept()
    print("📱 Android 클라이언트 연결됨")

    tts_ws = None  # finally 블록에서 접근 위해 선언

    try:
        tts_ws = await websockets.connect(TTS_WS_URL)
        print("🎙️ TTS 서버 연결됨")

        while True:
            text = await client_ws.receive_text()
            print("📨 텍스트 수신:", text)

            await tts_ws.send(text)

            # 바이트 응답 수신
            while True:
                data = await tts_ws.recv()

                if isinstance(data, bytes):
                    await client_ws.send_bytes(data)
                elif isinstance(data, str) and data.strip() == "END":
                    print("✅ TTS 응답 완료")
                    break

    except WebSocketDisconnect:
        print("❌ Android 연결 종료됨")

    except Exception as e:
        print("❌ 예외 발생:", e)

    finally:
        if not client_ws.client_state.name == "DISCONNECTED":
            try:
                await client_ws.close()
            except Exception as e:
                print("⚠️ client_ws.close 중 예외:", e)

        if tts_ws and not tts_ws.close:
            try:
                await tts_ws.close()
            except Exception as e:
                print("⚠️ tts_ws.close 중 예외:", e)
