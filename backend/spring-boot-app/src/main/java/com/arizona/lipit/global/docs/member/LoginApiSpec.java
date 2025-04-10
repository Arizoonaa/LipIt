package com.arizona.lipit.global.docs.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.arizona.lipit.domain.member.dto.LoginRequestDto;
import com.arizona.lipit.domain.member.dto.LoginResponseDto;
import com.arizona.lipit.global.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "로그인", description = "로그인 관련 API")
public interface LoginApiSpec {

	@Operation(summary = "로그인", description = """
		💡 로그인을 진행합니다.
		""")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그인에 성공했습니다."),
		@ApiResponse(responseCode = "400", description = "`[LOGIN-001]` 잘못된 비밀번호입니다.", content = @Content()),
		@ApiResponse(responseCode = "404", description = "`[LOGIN-002]` 해당 이메일을 가진 사용자가 없습니다.", content = @Content()),
		@ApiResponse(responseCode = "500",
			description = "`[LOGIN-003]` 로그인 처리 중 서버 오류가 발생했습니다.",
			content = @Content())
	})
	ResponseEntity<CommonResponse<LoginResponseDto>> authenticateUser(
		@Valid @RequestBody LoginRequestDto request
	);
}
