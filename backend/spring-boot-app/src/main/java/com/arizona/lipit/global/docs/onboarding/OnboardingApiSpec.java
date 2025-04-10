package com.arizona.lipit.global.docs.onboarding;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;

import com.arizona.lipit.domain.member.dto.MemberDto;
import com.arizona.lipit.domain.onboarding.dto.UserInterestRequestDto;
import com.arizona.lipit.global.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[온보딩] 사용자 정보 설정", description = "사용자 온보딩 관련 API")
public interface OnboardingApiSpec {
	@Operation(summary = "관심 사항 저장", description = """
		💡 사용자의 관심 사항 정보를 저장합니다.
		""")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "관심 사항이 성공적으로 저장되었습니다."),
		@ApiResponse(responseCode = "400", description = "`[ONBOARDING-001]` 잘못된 요청 형식입니다.", content = @Content()),
		@ApiResponse(responseCode = "401", description = "`[ONBOARDING-002]` 인증되지 않은 사용자입니다.", content = @Content()),
		@ApiResponse(responseCode = "500", description = "`[ONBOARDING-003]` 서버 오류가 발생했습니다.", content = @Content())
	})
	ResponseEntity<CommonResponse<MemberDto>> createUserInterest(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody UserInterestRequestDto requestDto
	);
}