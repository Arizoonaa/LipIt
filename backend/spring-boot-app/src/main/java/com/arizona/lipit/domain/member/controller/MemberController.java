package com.arizona.lipit.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arizona.lipit.domain.member.dto.MemberLevelResponseDto;
import com.arizona.lipit.domain.member.service.MemberService;
import com.arizona.lipit.global.docs.member.MemberApiSpec;
import com.arizona.lipit.global.response.CommonResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController implements MemberApiSpec {

	private final MemberService memberService;

	@PostMapping("/{memberId}/level")
	public ResponseEntity<CommonResponse<MemberLevelResponseDto>> getMemberLevel(@PathVariable Long memberId) {
		MemberLevelResponseDto response = memberService.getMemberLevel(memberId);
		return ResponseEntity.ok(
			CommonResponse.ok("회원 등급이 성공적으로 조회되었습니다.", response)
		);
	}
}
