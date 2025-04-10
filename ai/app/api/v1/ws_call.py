# app/api/v1/tts.py

import asyncio
from concurrent.futures import ThreadPoolExecutor
import json
import time
import traceback

from fastapi import (
    APIRouter,
    WebSocket,
    WebSocketDisconnect,
)
from fastapi.responses import HTMLResponse, FileResponse

from app.service.synthesis import (
    synthesize_voice_latents,
)
from app.util.text_chunker import split_text_to_chunks
from app.core.tts_model import tts_lock

router = APIRouter()

executor = ThreadPoolExecutor(max_workers=4)  # 동시에 4개까지 TTS 실행 가능


@router.websocket("/ws/tts")
async def tts_websocket(ws: WebSocket):
    try:
        await ws.accept()
        print("✅ WebSocket 연결 수립됨")

        while True:
            try:
                message = await ws.receive()
                print("📥 Raw 메시지:", message)
            except WebSocketDisconnect:
                print("🔌 클라이언트가 연결 끊음")
                break
            except RuntimeError as e:
                print(f"❌ receive 중 에러: {e}")
                break

            if message["type"] == "websocket.disconnect":
                print("🔌 WebSocket 연결 종료 감지됨")
                break

            if message["type"] == "websocket.receive" and "text" in message:
                if message["text"] == "PING":
                    await ws.send_text("PONG")
                    continue

                try:
                    payload = json.loads(message["text"])
                    full_text = payload.get("text", "")
                    voice_id = payload.get("voiceId")
                    voice_url = payload.get("voiceUrl")
                except Exception as e:
                    print(f"❌ JSON 파싱 오류: {e}")
                    try:
                        await ws.send_text("END")
                    except:
                        pass
                    continue

                chunks = split_text_to_chunks(full_text)
                start = time.time()

                # 함수는 루프 바깥에서 정의
                async def process_and_send_chunk(chunk, idx):
                    try:
                        async with tts_lock:
                            audio_bytes = (
                                await asyncio.get_event_loop().run_in_executor(
                                    executor,
                                    synthesize_voice_latents,
                                    chunk,
                                    "",
                                    voice_id,
                                    voice_url,
                                )
                            )
                        await ws.send_bytes(audio_bytes)
                        print(f"✅ chunk {idx + 1} 전송 완료: {chunk}")
                    except (WebSocketDisconnect, RuntimeError) as e:
                        print(
                            f"❌ chunk {idx + 1} 전송 실패 (연결 끊김 또는 비정상 종료): {e}"
                        )
                        raise  # 전체 종료로 빠져나오게 함
                    except Exception as e:
                        print(f"❌ chunk {idx + 1} 전송 중 예외: {e}")

                # 병렬 실행 + END는 딱 한 번
                try:
                    tasks = [
                        process_and_send_chunk(chunk, idx)
                        for idx, chunk in enumerate(chunks)
                    ]
                    await asyncio.gather(*tasks)

                    # END 신호도 try-except로 감싸기
                    try:
                        await ws.send_text("END")
                    except Exception as e:
                        print(f"⚠️ END 전송 실패 (이미 닫힘): {e}")

                except Exception as e:
                    print(f"❌ 전체 처리 중 오류: {e}")
                    try:
                        await ws.send_text("END")
                    except:
                        pass

                print(f"⭐ 전체 전송 완료 시간: {time.time() - start:.2f}초")
    except WebSocketDisconnect as e:
        print(f"🔌 WebSocket 연결 종료 (코드: {e.code})")
    except Exception as e:
        print("❌ 서버 측 예외 발생:")
        traceback.print_exc()


@router.websocket("/ws/tts/embedding")
async def tts_websocket(ws: WebSocket):
    try:
        print("⚡ WebSocket 핸들러 진입 시도")
        await ws.accept()
        print("✅ WebSocket 연결 수립됨")
    except Exception as e:
        print("❌ WebSocket 진입 중 예외 발생:", e)
        return

    try:
        while True:
            try:
                message = await ws.receive()
                print("📥 Raw 메시지:", message)
            except WebSocketDisconnect:
                print("🔌 클라이언트가 연결 끊음")
                break
            except RuntimeError as e:
                print(f"❌ receive 중 에러: {e}")
                break

            if message["type"] == "websocket.disconnect":
                print("🔌 WebSocket 연결 종료 감지됨")
                break

            if message["type"] == "websocket.receive" and "text" in message:
                try:
                    payload = json.loads(message["text"])
                    full_text = payload.get("text", "")
                    voice_id = payload.get("voiceId", None)
                    voice_url = payload.get("voiceUrl", None)
                except Exception as e:
                    print("❌ JSON 파싱 오류:", e)
                    await ws.send_text("END")
                    continue

                print("📨 받은 텍스트:", full_text)
                print("🔗 받은 voiceId:", voice_id)
                print("🔗 받은 voiceUrl:", voice_url)

                chunks = split_text_to_chunks(full_text)
                start = time.time()

                # 함수는 루프 바깥에서 정의
                async def process_and_send_chunk(chunk, idx):
                    try:
                        async with tts_lock:
                            audio_bytes = (
                                await asyncio.get_event_loop().run_in_executor(
                                    executor,
                                    synthesize_voice_latents,
                                    chunk,
                                    "",
                                    voice_id,
                                    voice_url,
                                )
                            )
                        await ws.send_bytes(audio_bytes)
                        print(f"✅ chunk {idx + 1} 전송 완료: {chunk}")
                    except Exception as e:
                        print(f"❌ chunk {idx + 1} 오류: {e}")

                # 병렬 실행 + END는 딱 한 번
                tasks = [
                    process_and_send_chunk(chunk, idx)
                    for idx, chunk in enumerate(chunks)
                ]
                await asyncio.gather(*tasks)

                await ws.send_text("END")  # 전체 전송 끝났을 때 한 번만

                print(f"⭐ 전체 전송 완료 시간: {time.time() - start:.2f}초")

            elif "bytes" in message:
                print("📦 받은 바이트:", len(message["bytes"]))

    except WebSocketDisconnect as e:
        print(f"🔌 WebSocket 연결 종료 (코드: {e.code})")
    except Exception as e:
        print("❌ 서버 측 예외 발생:")
        traceback.print_exc()


@router.get("/ws/test", response_class=HTMLResponse)
def get():
    return """
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>WebSocket TTS 테스트 (embedding 기반)</title>
  </head>
  <body>
    <h2>WebSocket TTS 테스트</h2>
    <input id="text" type="text" value="Hello world" />
    <button onclick="sendTTS()">전송</button>
    <br /><br />
    <audio id="audio" controls autoplay></audio>

    <script>
      let ws;
      let audioQueue = [];
      let pendingPayload = null;

      function connectWebSocket() {
        if (ws && ws.readyState === WebSocket.OPEN) return;

        ws = new WebSocket("wss://lipit.store/ws/tts/embedding");

        ws.onopen = () => {
          console.log("✅ WebSocket 연결됨");
          if (pendingPayload) {
            console.log("📤 연결 후 전송:", pendingPayload);
            ws.send(pendingPayload);
            pendingPayload = null;
          }
        };

        ws.onmessage = (event) => {
          if (typeof event.data === "string") {
            if (event.data === "END") {
              console.log("✅ 전송 완료");
              return;
            }
            console.log("📨 메시지 수신됨:", event.data);
            return;
          }

          console.log("📥 오디오 수신됨");

          const blob = new Blob([event.data], { type: "audio/wav" });
          const url = URL.createObjectURL(blob);
          audioQueue.push(url);
          if (audioQueue.length === 1) playNext();
        };

        ws.onclose = () => {
          console.warn("❌ WebSocket 연결 종료됨");
          ws = null;
        };
      }

      function sendTTS() {
        const text = document.getElementById("text").value;

        const payload = JSON.stringify({
          text: text,
          voiceId: 1, // 테스트용 voice_id
          voiceUrl: "https://dlxayir1dj7sa.cloudfront.net/voice-audio/6_1744134882854.mp3"
        });

        if (!ws || ws.readyState !== WebSocket.OPEN) {
          console.log("🕒 WebSocket 연결 중 or 없음... 대기열에 저장");
          pendingPayload = payload;
          connectWebSocket();
          return;
        }

        console.log("📤 전송:", payload);
        ws.send(payload);
      }

      function playNext() {
        if (audioQueue.length === 0) return;

        const url = audioQueue[0];
        const audio = document.getElementById("audio");

        console.log("🎵 재생 시작:", url);
        audio.src = url;
        audio.load();
        audio.play().catch((e) => console.warn("🎧 재생 실패:", e));

        audio.onended = () => {
          console.log("🎵 재생 완료");
          audioQueue.shift();
          playNext();
        };
      }

      window.addEventListener("DOMContentLoaded", () => {
        setTimeout(() => {
          connectWebSocket();
        }, 1000);
      });
    </script>
  </body>
</html>
"""
