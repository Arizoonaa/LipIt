package com.arizona.lipit.global.docs.notification;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.arizona.lipit.domain.notification.dto.NotificationRequestDto;
import com.arizona.lipit.global.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "FCM 테스트", description = "FCM 테스트 관련 API")
public interface NotificationTestApiSpec {

	@Operation(summary = "FCM 알림 전송", description = """
		💡 안드로이드 기기에 FCM 알림을 전송할 수 있습니다.
		
		---
		
		**[ 알림 유형 ]**
		- **CALL_REMINDER**: 예약 전화 10분 전 알림
		- **CALL_START**: 전화 알림
		- **MISSED_CALL**: 부재중 알림
		- **REPORT_COMPLETE**: 리포트 발행 알림
		- **DAILY_SENTENCE**: 오늘의 문장 알림
		""")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "알림이 성공적으로 전송되었습니다.")
	})
	ResponseEntity<CommonResponse<Map<String, String>>> sendTestNotification(
		@RequestBody NotificationRequestDto requestDto
	) throws IOException, ExecutionException, InterruptedException;
}
