package com.arizona.lipit.global.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.arizona.lipit.global.exception.CustomException;
import com.arizona.lipit.global.exception.ErrorCode;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 이 필터는 Spring Security에서 HTTP 요청이 들어올 때마다 실행됨
 * OncePerRequestFilter를 상속받았기 때문에 모든 요청마다 한 번씩 실행됨
 * 클라이언트가 API 요청을 보낼 때, JWT 인증을 검사하고 인증 객체를 설정하는 역할을 수행
 * [클라이언트 요청] → [JwtAuthenticationFilter] → [Spring Security 인증 검사] → [Controller 실행]
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		String path = request.getRequestURI();

		// Swagger, API Docs, 정적 자원, 인증 관련 경로는 JWT 검사에서 제외
		if (isExcludePath(path)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = resolveToken(request); // 헤더에서 토큰 추출

		System.out.println("📌 token: " + token);

		if (token == null || token.trim().isEmpty()) {
			request.setAttribute("exception", new CustomException(ErrorCode.ACCESS_TOKEN_MISSING));
			throw new AuthenticationException("Missing token") {
			}; // 강제 위임
		}

		if (jwtProvider.validateAccessToken(token)) {
			Authentication authentication = jwtProvider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);  // "Bearer " 제거 후 반환
		}
		return null;
	}

	private boolean isExcludePath(String path) {
		return path.startsWith("/spring/api/swagger-ui")
			|| path.startsWith("/spring/api/v3/api-docs")
			|| path.startsWith("/spring/api/swagger-resources")
			|| path.startsWith("/spring/api/webjars")
			|| path.startsWith("/spring/api/auth/login")
			|| path.startsWith("/spring/api/auth/signup");
	}
}
