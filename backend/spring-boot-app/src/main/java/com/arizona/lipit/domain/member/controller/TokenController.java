// package com.arizona.lipit.domain.auth.controller;
//
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import lombok.RequiredArgsConstructor;
//
// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/auth/token")
// public class TokenController {
//
// 	private final AuthService authService;
//
// 	@PostMapping("/refresh")
// 	public ResponseEntity<TokenResponse> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
// 		TokenResponse tokenResponse = authService.refreshToken(refreshToken);
// 		return ResponseEntity.ok(tokenResponse);
// 	}
// }
