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
public class SelectVoiceResponseDto {
    @Schema(description = "회원 ID", example = "1")
    private Long memberId;
    
    @Schema(description = "선택된 음성 ID", example = "2")
    private Long selectedVoiceId;
    
    @Schema(description = "음성 이름", example = "스윙스")
    private String voiceName;
    
    @Schema(description = "음성 타입", example = "CELEB")
    private String type;
} 