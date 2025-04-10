package com.arizona.lipit.global.docs.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.arizona.lipit.domain.member.dto.MemberLevelResponseDto;
import com.arizona.lipit.global.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "회원 관리", description = "회원 관리 API")
public interface MemberApiSpec {

	@Operation(summary = "회원 등급 조회", description = """
		💡 회원의 통화 시간과 리포트 개수를 기준으로 등급 및 달성 퍼센트를 조회합니다.
		""")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "회원 등급이 성공적으로 조회되었습니다."),
		@ApiResponse(responseCode = "400", description = "`[MEMBER-002]` 사용자 ID가 누락되었습니다.", content = @Content()),
		@ApiResponse(responseCode = "401", description = "`[AUTH-001]` 인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요.", content = @Content()),
		@ApiResponse(responseCode = "401", description = "`[AUTH-002]` 유효하지 않은 토큰입니다.", content = @Content()),
		@ApiResponse(responseCode = "403", description = "`[AUTH-009]` 해당 리소스에 접근할 권한이 없습니다.", content = @Content()),
		@ApiResponse(responseCode = "404", description = "`[MEMBER-001]` 해당 ID를 가진 사용자를 찾을 수 없습니다.", content = @Content())
	})
	ResponseEntity<CommonResponse<MemberLevelResponseDto>> getMemberLevel(
		@Parameter(description = "회원 ID", required = true)
		@PathVariable("memberId") Long memberId
	);
}
