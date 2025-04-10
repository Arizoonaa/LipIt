package com.arizona.lipit.domain.member.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arizona.lipit.domain.member.dto.MemberDto;
import com.arizona.lipit.domain.member.dto.SignupRequestDto;
import com.arizona.lipit.domain.member.entity.Level;
import com.arizona.lipit.domain.member.entity.Member;
import com.arizona.lipit.domain.member.mapper.MemberMapper;
import com.arizona.lipit.domain.member.repository.LevelRepository;
import com.arizona.lipit.domain.member.repository.MemberRepository;
import com.arizona.lipit.domain.voice.service.VoiceService;
import com.arizona.lipit.global.exception.CustomException;
import com.arizona.lipit.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignupService {

	private final MemberRepository memberRepository;
	private final MemberMapper memberMapper;

	private final LevelRepository levelRepository;

	private final VoiceService voiceService;

	private final PasswordEncoder passwordEncoder;

	/**
	 * 회원 가입
	 */
	@Transactional
	public MemberDto saveMember(SignupRequestDto request) {
		// 이메일 중복 체크
		if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}

		// 비밀번호 일치 여부 체크
		if (!request.getPassword1().equals(request.getPassword2())) {
			throw new CustomException(ErrorCode.PASSWORDS_DO_NOT_MATCH);
		}

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(request.getPassword1());

		// 기본 등급
		Level defaultLevel = levelRepository.findById(1L).get();

		// 새로운 사용자 생성 및 저장
		// 비밀번호 암호화 후 Member 엔티티 생성
		Member member = Member.builder()
			.email(request.getEmail())
			.password(encodedPassword)
			.name(request.getName())
			.gender(request.getGender())
			.level(defaultLevel)
			.selectedVoiceId(1L)
			.build();

		// DB 저장
		memberRepository.save(member);

		// 기본 셀럽 음성 추가
		voiceService.saveDefaultCelebVoice(member.getMemberId());

		// 엔티티 → DTO 변환 후 반환
		return memberMapper.toDto(member);
	}
}
