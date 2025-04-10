package com.arizona.lipit.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {
	@Schema(description = "사용자 ID")
	private Long memberId;

	@Schema(description = "사용자 이메일")
	private String email;

	@Schema(description = "사용자 이름")
	private String name;

	@Schema(description = "액세스 토큰")
	private String accessToken;

	@Schema(description = "리프레시 토큰")
	private String refreshToken;

	@Schema(description = "FCM 토큰")
	private String fcmToken;
}
