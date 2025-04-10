package com.arizona.lipit.domain.onboarding.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arizona.lipit.domain.member.dto.MemberDto;
import com.arizona.lipit.domain.member.entity.Member;
import com.arizona.lipit.domain.member.repository.MemberRepository;
import com.arizona.lipit.domain.onboarding.dto.UserInterestRequestDto;
import com.arizona.lipit.domain.onboarding.service.UserInterestService;
import com.arizona.lipit.global.docs.onboarding.OnboardingApiSpec;
import com.arizona.lipit.global.exception.CustomException;
import com.arizona.lipit.global.exception.ErrorCode;
import com.arizona.lipit.global.response.CommonResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/onboarding")
public class OnboardingController implements OnboardingApiSpec {

	// CallScheduleService 주입 제거
	private final UserInterestService userInterestService;
	private final MemberRepository memberRepository;

	// 일정 관련 엔드포인트 제거
	// @PostMapping("/alarm") 메서드 전체 삭제

	@PostMapping("/interesting")
	public ResponseEntity<CommonResponse<MemberDto>> createUserInterest(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody UserInterestRequestDto requestDto) {

		String email = userDetails.getUsername(); // 이메일 추출
		Long memberId = memberRepository.findByEmail(email)
			.map(Member::getMemberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		MemberDto memberDto = userInterestService.saveUserInterest(memberId, requestDto);

		return ResponseEntity.ok(CommonResponse.created("관심 사항이 성공적으로 저장되었습니다.", memberDto));
	}
}