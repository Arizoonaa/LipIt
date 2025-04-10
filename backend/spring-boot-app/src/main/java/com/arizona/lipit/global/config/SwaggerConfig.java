package com.arizona.lipit.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	private Info apiInfo() {
		return new Info()
			.title("[Lip It] REST API")
			.description("SSAFY AI 영상 도메인 특화 프로젝트 **Lip It 서비스의 API 명세서**입니다.")
			.version("v1.0")
			.contact(new Contact().name("Team Arizona")
				.email("www.arizona.com")
				.url("suhmiji@gmail.com"))
			.license(new License()
				.name("License of API")
				.url("API license URL"));
	}

	private SecurityScheme createApiKeyScheme() {
		return new SecurityScheme().type(SecurityScheme.Type.HTTP)
			.bearerFormat("JWT")
			.scheme("bearer");
	}

	@Bean
	public OpenAPI openApi() {
		return new OpenAPI()
			.addServersItem(new Server().url("/spring/api"))
			.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
			.components(new Components().addSecuritySchemes("Bearer Authentication", createApiKeyScheme()))
			.info(apiInfo());
	}

	@Bean
	public GroupedOpenApi userApi() {
		return GroupedOpenApi.builder()
			.group("1. 회원 관리")
			.pathsToMatch("/auth/**", "/members/**")
			.build();
	}

	@Bean
	public GroupedOpenApi onboardingApi() {
		return GroupedOpenApi.builder()
			.group("2. 온보딩")
			.pathsToMatch("/onboarding/**")
			.build();
	}

	@Bean
	public GroupedOpenApi voiceApi() {
		return GroupedOpenApi.builder()
			.group("3. 음성 관리")
			.pathsToMatch("/voices/**", "/users/voices/**")
			.build();
	}

	@Bean
	public GroupedOpenApi scheduleApi() {
		return GroupedOpenApi.builder()
			.group("4. 일정 관리")
			.pathsToMatch("/schedule/**")
			.build();
	}

	@Bean
	public GroupedOpenApi fcmTestApi() {
		return GroupedOpenApi.builder()
			.group("5. FCM 테스트")
			.pathsToMatch("/notifications/**")
			.build();
	}
}
