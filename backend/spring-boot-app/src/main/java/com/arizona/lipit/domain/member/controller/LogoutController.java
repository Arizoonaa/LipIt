package com.arizona.lipit.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arizona.lipit.domain.member.dto.SuccessResponseDto;
import com.arizona.lipit.domain.member.service.AuthService;
import com.arizona.lipit.global.docs.member.LogoutApiSpec;
import com.arizona.lipit.global.response.CommonResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/logout")
public class LogoutController implements LogoutApiSpec {

	private final AuthService authService;

	@PostMapping
	public ResponseEntity<CommonResponse<SuccessResponseDto>> logoutUser(HttpServletRequest request) {
		// 클라이언트로부터 Authorization 헤더에서 토큰 추출
		String authHeader = request.getHeader("Authorization");
		authService.logoutUser(authHeader);
		return ResponseEntity.ok(CommonResponse.ok("로그아웃에 성공했습니다.", SuccessResponseDto.success()));
	}
}
