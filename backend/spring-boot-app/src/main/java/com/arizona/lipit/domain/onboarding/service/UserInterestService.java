package com.arizona.lipit.domain.onboarding.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arizona.lipit.domain.member.dto.MemberDto;
import com.arizona.lipit.domain.member.entity.Member;
import com.arizona.lipit.domain.member.mapper.MemberMapper;
import com.arizona.lipit.domain.member.repository.MemberRepository;
import com.arizona.lipit.domain.onboarding.dto.UserInterestRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInterestService {  // 클래스명 수정
	private final MemberRepository memberRepository;

	@Transactional
	public MemberDto saveUserInterest(Long memberId, UserInterestRequestDto requestDto) {  // 메서드명 수정
		// 회원 조회
		Member member = memberRepository.findById(memberId).orElse(null);

		if (member != null) {
			// 관심사항 업데이트
			member.setInterest(requestDto.getInterest());
			member = memberRepository.save(member);

			// MemberMapper를 사용하여 DTO 변환
			return MemberMapper.INSTANCE.toDto(member);
		}

		return null;
	}

	public MemberDto getUserInterest(Long memberId) {  // 메서드명 수정
		// 회원 조회
		Member member = memberRepository.findById(memberId).orElse(null);

		if (member != null) {
			// MemberMapper를 사용하여 DTO 변환
			return MemberMapper.INSTANCE.toDto(member);
		}

		return null;
	}
}