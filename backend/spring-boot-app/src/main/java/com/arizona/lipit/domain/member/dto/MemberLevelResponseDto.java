package com.arizona.lipit.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberLevelResponseDto {

	@Schema(description = "등급")
	private int level;

	@Schema(description = "UI용 뱃지 아이콘 파일 경로")
	private String badgeIcon;

	@Schema(description = "누적 통화 시간 (%)")
	private int totalCallDurationPercentage;

	@Schema(description = "리포트 개수 (%)")
	private int totalReportCountPercentage;
}
