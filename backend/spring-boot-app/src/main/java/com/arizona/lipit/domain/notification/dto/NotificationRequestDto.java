package com.arizona.lipit.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDto {

	@Schema(description = "사용자 ID", example = "1")
	private Long memberId;

	@Schema(description = "알림 유형", example = "CALL_REMINDER")
	private String type;

	@Schema(description = "알림 제목", example = "전화 10분 전")
	private String title;

	@Schema(description = "알림 본문", example = "통화 시간이 10분 후에 시작됩니다. 준비하세요!")
	private String body;

	@Schema(description = "(선택) 오늘의 문장 ID")
	private Long sentenceId;
}
