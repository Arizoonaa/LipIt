package com.ssafy.lipit_app.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.enums.ReadyState

/**
 * WebSocketHeartbeat
 *
 * - 일정 주기로 WebSocket 서버에 "PING" 메시지를 보내 연결이 유지되고 있는지 확인합니다.
 * - 서버는 "PONG" 메시지로 응답하며, TTSViewModel에서 해당 응답을 처리합니다.
 * - 서버 연결이 끊겼을 경우 자동으로 중단됩니다.
 * (주후를 보는 내 마음도 하투비투 ❣️)
 */
class WebSocketHeartbeat(
    private val wsClient: WebSocketClient,
    private val pingMessage: String = "PING",
    private val intervalMillis: Long = 10_000L // 10초 간격
) {
    private val handler = Handler(Looper.getMainLooper())

    /** PING 전송 작업 */
    private val pingRunnable = object : Runnable {
        override fun run() {
            if (!wsClient.isOpen) { // OOM 방지
                Log.w("Heartbeat", "WebSocket 연결 안 됨 - PING 생략")
                return // 다음 루프를 돌리지 않음
            }
            
            try {
                if (wsClient.isOpen && wsClient.readyState == ReadyState.OPEN) {
                    wsClient.send(pingMessage)
                    Log.d("Heartbeat", "💓 PING 전송")
                } else {
                    Log.w("Heartbeat", "⚠️ WebSocket 연결 안 됨 - PING 생략")
                }
            } catch (e: Exception) {
                Log.e("Heartbeat", "❌ Ping 전송 실패: ${e.message}", e)
            } finally {
                handler.postDelayed(this, intervalMillis)
            }
        }
    }

    /** 하트비트 시작 */
    fun start() {
        handler.post(pingRunnable)
    }

    /** 하트비트 정지 */
    fun stop() {
        handler.removeCallbacks(pingRunnable)
    }
}
