package com.arizona.lipit.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import okhttp3.OkHttpClient;

@Configuration
public class OkHttpConfig {

	@Bean
	public OkHttpClient okHttpClient() {
		return new OkHttpClient.Builder()
			.retryOnConnectionFailure(true) // 자동 재시도 활성화
			.build();
	}
}
