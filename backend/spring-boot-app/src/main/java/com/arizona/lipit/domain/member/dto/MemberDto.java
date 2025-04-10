package com.arizona.lipit.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {
	@Schema(description = "사용자 ID", example = "1")
	private Long memberId;

	@Schema(description = "사용자 이메일", example = "test@test.com")
	private String email;

	@Schema(description = "사용자 이름", example = "test")
	private String name;

	@Schema(description = "사용자 성별", example = "FEMALE")
	private Gender gender;

	@Schema(description = "사용자 관심 사항", example = "저는 개발자로 취업 준비를 하고 있어요. 그림 그리기가 취미입니다!")
	private String interest;

	@Schema(description = "사용자가 선택한 음성 ID", example = "1")
	private Long selectedVoiceId;

	@Schema(description = "사용자 등급", example = "1")
	private Long levelId;

	@Schema(description = "FCM 디바이스 토큰", example = "")
	private String fcmToken;
}
