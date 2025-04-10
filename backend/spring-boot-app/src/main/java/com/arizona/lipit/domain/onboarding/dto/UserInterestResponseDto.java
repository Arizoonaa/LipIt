package com.arizona.lipit.domain.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInterestResponseDto {

    @Schema(description = "사용자 ID", example = "1")
    private Long memberId;

    @Schema(description = "사용자 관심 사항", example = "IT 업계 취업 희망 개발자로 IT 관련 영어 시사에 흥미를 가짐.")
    private String interest;
}