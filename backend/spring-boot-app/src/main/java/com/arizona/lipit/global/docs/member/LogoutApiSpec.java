package com.arizona.lipit.global.docs.member;

import org.springframework.http.ResponseEntity;

import com.arizona.lipit.domain.member.dto.SuccessResponseDto;
import com.arizona.lipit.global.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "로그아웃", description = "로그아웃 관련 API")
public interface LogoutApiSpec {

	@Operation(summary = "로그아웃", description = """
		💡 로그아웃을 진행합니다.
		
		---
		
		[ 참고 ]
		- 로그아웃 시 Authorization 헤더에 Access Token을 포함해야 함
			- `Authorization: Bearer {accessToken}` 형식
		""")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그아웃에 성공했습니다."),
		@ApiResponse(responseCode = "401",
			description = """
				`[AUTH-001]` 인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요.
				
				`[AUTH-002]` 유효하지 않은 Access Token입니다.
				
				`[AUTH-003]` 만료된 Access Token입니다.
				
				`[AUTH-004]` Access Token이 누락되었습니다.
				""",
			content = @Content()),
		@ApiResponse(responseCode = "500", description = "`[LOGOUT-001]` 로그아웃 처리 중 서버 오류가 발생했습니다.", content = @Content())
	})
	ResponseEntity<CommonResponse<SuccessResponseDto>> logoutUser(HttpServletRequest request);
}
