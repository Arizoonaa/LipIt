from fastapi import WebSocket, WebSocketDisconnect, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.base_router import BaseRouter
from app.core.config import TTS_SERVER_URL
from app.crud.call import get_member_id_by_call_id
from app.db.session import get_db
from app.service import call
from app.schema.call import StartCallRequest, UserMessageRequest
import json, websockets

from app.service.voice import get_voice_by_call_id, get_voice_by_member_id

TTS_WS_URL = f"wss://{TTS_SERVER_URL}/ws/tts"

router = BaseRouter(prefix="/ws", tags=["WebSocket"], require_auth=False)


@router.websocket("/android")
async def android_ws_call(client_ws: WebSocket, db: AsyncSession = Depends(get_db)):
    print("TTS_WS_URL =", TTS_WS_URL)
    await client_ws.accept()
    print("📱 Android 클라이언트 연결됨")

    tts_ws = None
    call_id = None

    try:
        tts_ws = await websockets.connect(TTS_WS_URL)
        print("🎙️ TTS 서버 연결됨")

        while True:
            msg = await client_ws.receive_text()
            print("📨 받은 메시지:", msg)

            if msg.strip().upper() == "PING":
                await client_ws.send_text("PONG")
            else:

                try:
                    payload = json.loads(msg)
                    action = payload.get("action")
                    print("🔍 action =", action)
                except Exception as e:
                    print("❌ JSON 파싱 실패:", e)
                    await client_ws.close(code=1003)  # Invalid data
                    return

                if action == "start":
                    request = StartCallRequest(**payload["data"])

                    member_id = request.memberId

                    voice = await get_voice_by_member_id(db, member_id)

                    response = await call.start_call(
                        db, request, member_id, voice.voice_name, voice.type
                    )

                    call_id = response.calld

                    # 텍스트 응답 전송
                    await client_ws.send_text(
                        json.dumps(
                            {
                                "type": "text",
                                "data": {
                                    "callId": response.calld,
                                    "startTime": response.startTime,
                                    "aiMessage": response.aiMessage,
                                    "aiMessageKor": response.aiMessageKor,
                                },
                            }
                        )
                    )

                    # TTS 서버로 메시지 전송
                    await tts_ws.send(
                        json.dumps(
                            {
                                "text": response.aiMessage,
                                "voiceId": voice.voice_id,
                                "voiceUrl": voice.audio_url,
                            }
                        )
                    )

                    # 바이너리 수신 후 전송
                    await forward_tts_binary(tts_ws, client_ws)

                elif action == "message":
                    request = UserMessageRequest(**payload["data"])

                    call_id = request.callId
                    member_id = await get_member_id_by_call_id(db, call_id)
                    voice = await get_voice_by_call_id(db, call_id)

                    response = await call.add_message_to_call(
                        db, call_id, request, member_id, voice.voice_name, voice.type
                    )

                    await client_ws.send_text(
                        json.dumps(
                            {
                                "type": "text" if response.endTime is None else "end",
                                "data": {
                                    "aiMessage": response.aiMessage,
                                    "aiMessageKor": response.aiMessageKor,
                                    "endTime": response.endTime,
                                    "reportCreated": response.reportCreated,
                                },
                            }
                        )
                    )

                    # TTS 서버로 메시지 전송
                    await tts_ws.send(
                        json.dumps(
                            {
                                "text": response.aiMessage,
                                "voiceId": voice.voice_id,
                                "voiceUrl": voice.audio_url,
                            }
                        )
                    )

                    await forward_tts_binary(tts_ws, client_ws)

                elif action == "end":
                    response = await call.end_call(db, call_id)
                    await client_ws.send_text(
                        json.dumps(
                            {
                                "type": "end",
                                "data": {
                                    "endTime": response.endTime,
                                    "duration": response.duration,
                                    "reportCreated": response.reportCreated,
                                },
                            }
                        )
                    )
                    break

    except WebSocketDisconnect:
        print("❌ Android 연결 종료됨")

    except Exception as e:
        print("❌ 예외 발생:", e)

    finally:
        try:
            if tts_ws:
                await tts_ws.close()
            if client_ws.client_state.name != "DISCONNECTED":
                await client_ws.close()
        except Exception as e:
            print("⚠️ 종료 중 예외:", e)


async def forward_tts_binary(tts_ws, client_ws):
    while True:
        data = await tts_ws.recv()
        if isinstance(data, bytes):
            await client_ws.send_bytes(data)
        elif isinstance(data, str) and data.strip() == "END":
            print("✅ TTS 응답 완료")
            break
