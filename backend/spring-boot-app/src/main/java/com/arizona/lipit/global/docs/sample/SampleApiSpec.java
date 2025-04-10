package com.arizona.lipit.global.docs.sample;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.arizona.lipit.domain.member.dto.MemberDto;
import com.arizona.lipit.domain.member.dto.SignupRequestDto;
import com.arizona.lipit.global.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "", description = "")
public interface SampleApiSpec {

	@Operation(summary = "", description = """
		💡 
		""")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "400", description = "`[]` ", content = @Content()),
		@ApiResponse(responseCode = "500", description = "`[]` ", content = @Content())
	})
	ResponseEntity<CommonResponse<MemberDto>> createSample(@Valid @RequestBody SignupRequestDto request);
}
