package com.arizona.lipit.domain.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ScheduleRequestDto {

    @Schema(description = "요일 선택지", example = "MONDAY")
    @NotBlank(message = "{scheduledDay.required}")
    private String scheduledDay;

    @Schema(description = "시간 선택지", example = "14:30:00")
    @NotBlank(message = "{scheduledTime.required}")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$",
            message = "{scheduledTime.invalid}")
    private String scheduledTime;

    @Schema(description = "카테고리 선택지", example = "SPORTS")
    private String topicCategory;
}