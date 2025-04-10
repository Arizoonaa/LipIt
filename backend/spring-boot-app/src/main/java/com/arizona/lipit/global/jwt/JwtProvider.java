package com.arizona.lipit.global.jwt;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.arizona.lipit.global.exception.CustomException;
import com.arizona.lipit.global.exception.ErrorCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

@Component
@Getter
public class JwtProvider {

	private final SecretKey secretKey;

	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;

	/**
	 * -- Getter --
	 *  리프레시 토큰 만료 시간 반환
	 */
	@Getter
	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	// 고정된 시크릿 키를 환경변수로 주입받고, hmacShaKeyFor 로 변환
	public JwtProvider(@Value("${jwt.secret}") String secret) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	// JWT 토큰에서 인증 객체 추출
	public Authentication getAuthentication(String token) {
		Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
		String email = claims.getSubject();

		UserDetails userDetails = new User(email, "", new ArrayList<>());
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	/**
	 * 액세스 토큰 생성
	 */
	public String generateAccessToken(String email) {
		return generateToken(email, accessTokenExpiration);
	}

	/**
	 * 리프레시 토큰 생성
	 */
	public String generateRefreshToken(String email) {
		return generateToken(email, refreshTokenExpiration);
	}

	/**
	 * 토큰 생성 공통 메서드
	 */
	private String generateToken(String email, long expiration) {
		return Jwts.builder()
			.setSubject(email)
			.setIssuedAt(new Date()) // 토큰 발급 시간
			.setExpiration(new Date(System.currentTimeMillis() + expiration)) // 만료 시간 설정
			.signWith(secretKey, SignatureAlgorithm.HS256)   // SecretKey를 사용하여 서명
			.compact();
	}

	/**
	 * Access Token 검증 (만료 시 Refresh Token을 사용해야 함)
	 */
	public boolean validateAccessToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (ExpiredJwtException e) {
			throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED);
		} catch (MalformedJwtException | UnsupportedJwtException e) {
			throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
		} catch (IllegalArgumentException e) {
			throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
		}
	}

	/**
	 * Refresh Token 검증 (만료 시 재로그인 필요)
	 */
	public void validateRefreshToken(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token);
		} catch (ExpiredJwtException e) {
			throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
		} catch (MalformedJwtException | UnsupportedJwtException e) {
			throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
		} catch (IllegalArgumentException e) {
			throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
		}
	}

	/**
	 * 토큰에서 이메일 추출
	 */
	public String getEmailFromToken(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
	}
}
