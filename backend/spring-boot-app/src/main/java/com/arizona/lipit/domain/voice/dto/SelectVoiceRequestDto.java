package com.arizona.lipit.domain.voice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SelectVoiceRequestDto {
    @Schema(description = "선택할 음성 ID", example = "1")
    private Long voiceId;
} 