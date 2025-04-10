package com.arizona.lipit.domain.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CallStatusDto {
    @Schema(description = "부재중 개수", example = "0")
    private Integer missedCount;
    
    @Schema(description = "통화 수신 여부", example = "false")
    private Boolean isCalled;
} 