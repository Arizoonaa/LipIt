package com.arizona.lipit.domain.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserInterestRequestDto {

    @Schema(description = "관심 사항 기입란", example = "IT 업계 취업 희망 개발자로 IT 관련 영어 시사에 흥미를 가짐.")
    @Size(max = 1000, message = "{interest.max_length}")
    private String interest;
}