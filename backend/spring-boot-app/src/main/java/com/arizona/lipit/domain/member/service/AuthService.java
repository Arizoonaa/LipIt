package com.arizona.lipit.domain.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arizona.lipit.domain.member.dto.LoginRequestDto;
import com.arizona.lipit.domain.member.dto.LoginResponseDto;
import com.arizona.lipit.domain.member.entity.Member;
import com.arizona.lipit.domain.member.repository.MemberRepository;
import com.arizona.lipit.global.exception.CustomException;
import com.arizona.lipit.global.exception.ErrorCode;
import com.arizona.lipit.global.jwt.JwtProvider;
import com.arizona.lipit.global.redis.RedisTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final MemberRepository memberRepository;
	private final JwtProvider jwtProvider;
	private final PasswordEncoder passwordEncoder;
	private final RedisTokenService redisTokenService;

	/**
	 * 사용자 로그인 및 JWT 발급
	 */
	@Transactional
	public LoginResponseDto authenticateUser(LoginRequestDto request) {
		Member member = memberRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
			throw new CustomException(ErrorCode.INVALID_PASSWORD);
		}

		// JWT 발급
		String accessToken = jwtProvider.generateAccessToken(member.getEmail());
		String refreshToken = jwtProvider.generateRefreshToken(member.getEmail());

		// Redis에 리프레시 토큰 저장 (만료 시간 설정)
		redisTokenService.saveRefreshToken(member.getEmail(), refreshToken, jwtProvider.getRefreshTokenExpiration());

		// FCM 토큰 저장
		String fcmToken = request.getFcmToken();
		if (fcmToken != null) {
			member.setFcmToken(fcmToken);
		}

		return LoginResponseDto.builder()
			.memberId(member.getMemberId())
			.email(member.getEmail())
			.name(member.getName())
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.fcmToken(member.getFcmToken())
			.build();
	}

	/**
	 * 사용자 로그아웃 (리프레시 토큰 블랙리스트 등록)
	 */
	@Transactional
	public void logoutUser(String authHeader) {
		if (authHeader == null) {
			throw new CustomException(ErrorCode.ACCESS_TOKEN_MISSING);
		}

		if (authHeader.startsWith("Bearer ")) {
			String accessToken = authHeader.substring(7);
			String email = jwtProvider.getEmailFromToken(accessToken);

			// Redis에서 리프레시 토큰 삭제
			redisTokenService.deleteRefreshToken(email);
		} else {
			throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
		}
	}

	/**
	 * 리프레시 토큰을 사용하여 새로운 액세스 토큰 발급
	 */
	// @Transactional
	// public TokenResponse refreshAccessToken(String refreshToken) {
	// 	// 리프레시 토큰 검증
	// 	if (!jwtProvider.validateToken(refreshToken)) {
	// 		throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
	// 	}
	//
	// 	// Redis에서 저장된 리프레시 토큰 가져오기
	// 	String email = jwtProvider.getEmailFromToken(refreshToken);
	// 	String storedRefreshToken = redisTokenService.getRefreshToken(email)
	// 		.orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED));
	//
	// 	if (!refreshToken.equals(storedRefreshToken)) {
	// 		throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
	// 	}
	//
	// 	// 새로운 액세스 토큰 발급
	// 	String newAccessToken = jwtProvider.generateAccessToken(email);
	// 	return new TokenResponse(newAccessToken, refreshToken);
	// }
}
