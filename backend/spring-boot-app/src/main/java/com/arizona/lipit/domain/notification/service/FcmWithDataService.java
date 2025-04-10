package com.arizona.lipit.domain.notification.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.http.HttpHeaders;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.arizona.lipit.domain.notification.dto.NotificationRequestDto;
import com.arizona.lipit.global.exception.CustomException;
import com.arizona.lipit.global.exception.ErrorCode;
import com.arizona.lipit.global.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmWithDataService {

	private final ObjectMapper objectMapper;
	private final OkHttpClient client;

	/**
	 * 📌 FCM AccessToken 가져오기
	 * FCM에 push 요청을 보낼 때 인증을 위해 Header에 포함시킬 AccessToken 생성
	 */
	private String getAccessToken() throws IOException {
		GoogleCredentials googleCredentials = GoogleCredentials
			.fromStream(new ClassPathResource(Constants.FIREBASE_KEY_FILE).getInputStream())
			.createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging")); // 올바른 권한 설정

		googleCredentials.refreshIfExpired();
		String token = googleCredentials.getAccessToken().getTokenValue();

		if (token == null || token.isEmpty()) {
			throw new IllegalStateException("🔥 FCM Access Token을 가져오지 못했습니다!");
		}

		return token;
	}

	/**
	 * 📌 FCM 메시지 전송 (비동기)
	 */
	@Async("taskExecutor") // 비동기 실행
	public CompletableFuture<Map<String, String>> sendToUser(String targetToken,
		NotificationRequestDto requestDto) throws
		IOException {
		if (targetToken != null && !targetToken.trim().isEmpty()) {  // 공백 및 개행 문자 제거
			String cleanedToken = targetToken.trim();  // FCM 토큰 정리
			log.info("📨 정리된 FCM 토큰: {}", cleanedToken);

			return sendMessage(cleanedToken, requestDto);
		} else {
			throw new CustomException(ErrorCode.INVALID_FCM_TOKEN);
		}
	}

	/**
	 * 📌 FCM 메시지 전송 (비동기)
	 */
	@Async("taskExecutor") // 비동기 실행
	public CompletableFuture<Integer> broadCastToAllUsers(List<String> targetTokens,
		NotificationRequestDto requestDto) throws IOException {
		for (String targetToken : targetTokens) {
			if (targetToken != null && !targetToken.trim().isEmpty()) {  // 공백 및 개행 문자 제거
				String cleanedToken = targetToken.trim();  // FCM 토큰 정리
				sendMessage(cleanedToken, requestDto);
			}
		}

		return CompletableFuture.completedFuture(targetTokens.size());
	}

	/**
	 * 📌 FCM 메시지 생성 (Data 메시지 형식)
	 */
	private CompletableFuture<Map<String, String>> sendMessage(String targetToken,
		NotificationRequestDto requestDto) throws
		JsonProcessingException, IOException {
		// Data Message 생성
		Map<String, String> data = new HashMap<>();
		data.put("type", requestDto.getType());
		data.put("title", requestDto.getTitle());
		data.put("body", requestDto.getBody());
		data.put("sendTime", String.valueOf(System.currentTimeMillis()));

		// FCM 메시지 포맷 (올바른 형식)
		Map<String, Object> messageMap = new HashMap<>();
		messageMap.put("message", Map.of(
			"token", targetToken,  // FCM 디바이스 토큰
			"data", data                // 데이터 메시지
		));

		String message = objectMapper.writeValueAsString(messageMap);

		sendDataMessage(message); // 비동기 호출

		return CompletableFuture.completedFuture(data);
	}

	/**
	 * 📌 FCM 메시지 전송 요청
	 */
	private boolean sendDataMessage(String message) throws IOException {
		RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
		String accessToken = getAccessToken();

		Request request = new Request.Builder()
			.url(Constants.API_URL)
			.post(requestBody)
			.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)  // FCM 토큰 적용
			.addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
			.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				log.error("❌ FCM 전송 실패! 응답 코드: {}, 응답 메시지: {}", response.code(), response.body());
				return false;
			} else {
				log.info("✅ FCM 전송 성공!");
				return true;
			}
		}
	}
}
