package com.arizona.lipit.domain.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScheduleResponseDto {
    @Schema(description = "전화 일정 ID", example = "1")
    private Long callScheduleId;

    @Schema(description = "사용자 선택 요일", example = "MONDAY")
    private String scheduledDay;

    @Schema(description = "사용자 선택 시간", example = "14:30:00")
    private String scheduledTime;

    @Schema(description = "사용자 선택 카테고리", example = "SPORTS")
    private String topicCategory;
}