package com.arizona.lipit.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignupRequestDto {
	@Schema(description = "사용자 이메일", example = "test@test.com")
	@NotBlank(message = "{email.required}")
	@Email(message = "{email.invalid}")
	private String email;

	@Schema(description = "비밀번호", example = "test1234")
	@NotBlank(message = "{password.required}")
	@Size(min = 6, message = "{password.min_length}")
	private String password1;

	@Schema(description = "검증 비밀번호", example = "test1234")
	@NotEmpty(message = "{password.confirm_required}")
	private String password2;

	@Schema(description = "사용자 이름", example = "test")
	@NotBlank(message = "{username.required}")
	private String name;

	@Schema(description = "사용자 성별", example = "FEMALE")
	private Gender gender;
}
