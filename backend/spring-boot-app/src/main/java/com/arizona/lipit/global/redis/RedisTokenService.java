package com.arizona.lipit.global.redis;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

	private final StringRedisTemplate redisTemplate;

	/**
	 * 리프레시 토큰 저장
	 */
	public void saveRefreshToken(String email, String refreshToken, long expiration) {
		redisTemplate.opsForValue().set(email, refreshToken, expiration, TimeUnit.MILLISECONDS);
	}

	/**
	 * 리프레시 토큰 조회
	 */
	public Optional<String> getRefreshToken(String email) {
		return Optional.ofNullable(redisTemplate.opsForValue().get(email));
	}

	/**
	 * 리프레시 토큰 삭제 (로그아웃)
	 */
	public void deleteRefreshToken(String email) {
		redisTemplate.delete(email);
	}
}
