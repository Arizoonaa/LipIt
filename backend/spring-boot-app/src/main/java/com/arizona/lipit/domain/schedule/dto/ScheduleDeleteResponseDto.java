package com.arizona.lipit.domain.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScheduleDeleteResponseDto {
    @Schema(description = "삭제 성공 여부", example = "true")
    private boolean success;
}