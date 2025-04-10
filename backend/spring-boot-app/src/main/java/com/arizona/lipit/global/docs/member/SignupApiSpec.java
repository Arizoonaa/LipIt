package com.arizona.lipit.global.docs.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.arizona.lipit.domain.member.dto.MemberDto;
import com.arizona.lipit.domain.member.dto.SignupRequestDto;
import com.arizona.lipit.global.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "회원 가입", description = "회원 가입 관련 API")
public interface SignupApiSpec {

	@Operation(summary = "회원 가입", description = """
		💡 회원 가입을 진행합니다.
		""")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "회원 가입이 완료되었습니다."),
		@ApiResponse(responseCode = "400",
			description = """
				`[SIGNUP-001]` 이미 존재하는 이메일입니다.
				
				`[SIGNUP-002]` 비밀번호가 일치하지 않습니다.
				""",
			content = @Content()),
		@ApiResponse(responseCode = "500",
			description = "`[SIGNUP-003]` 회원 가입 처리 중 서버 내부 오류가 발생했습니다.",
			content = @Content())
	})
	ResponseEntity<CommonResponse<MemberDto>> createMember(@Valid @RequestBody SignupRequestDto request);
}
