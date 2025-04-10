package com.arizona.lipit.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arizona.lipit.domain.member.dto.LoginRequestDto;
import com.arizona.lipit.domain.member.dto.LoginResponseDto;
import com.arizona.lipit.domain.member.service.AuthService;
import com.arizona.lipit.global.docs.member.LoginApiSpec;
import com.arizona.lipit.global.response.CommonResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth/login")
@RequiredArgsConstructor
public class LoginController implements LoginApiSpec {

	private final AuthService authService;

	@PostMapping
	public ResponseEntity<CommonResponse<LoginResponseDto>> authenticateUser(
		@Valid @RequestBody LoginRequestDto requestDto
	) {
		LoginResponseDto responseDto = authService.authenticateUser(requestDto);
		return ResponseEntity.ok(CommonResponse.created("로그인에 성공했습니다.", responseDto));
	}
}

