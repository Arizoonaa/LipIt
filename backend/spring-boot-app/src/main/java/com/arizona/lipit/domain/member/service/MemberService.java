package com.arizona.lipit.domain.member.service;

import static com.arizona.lipit.global.exception.ErrorCode.*;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arizona.lipit.domain.member.dto.MemberDto;
import com.arizona.lipit.domain.member.dto.MemberLevelResponseDto;
import com.arizona.lipit.domain.member.entity.Level;
import com.arizona.lipit.domain.member.entity.Member;
import com.arizona.lipit.domain.member.mapper.MemberMapper;
import com.arizona.lipit.domain.member.repository.LevelRepository;
import com.arizona.lipit.domain.member.repository.MemberRepository;
import com.arizona.lipit.global.exception.CustomException;
import com.arizona.lipit.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final MemberMapper memberMapper;

	private final LevelRepository levelRepository;

	public MemberDto getMemberById(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_ID_NOT_FOUND));

		return memberMapper.toDto(member);
	}

	@Transactional
	public MemberLevelResponseDto getMemberLevel(Long memberId) {

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		int userDuration = member.getTotalCallDuration();
		int userReport = member.getTotalReportCount();

		// 모든 레벨 불러오기 (level 오름차순 정렬)
		List<Level> allLevels = levelRepository.findAll(Sort.by(Sort.Direction.ASC, "level"));

		// 현재 레벨 판별 (기본값: 1레벨)
		Level matchedLevel = allLevels.get(0);

		for (Level level : allLevels) {
			if (userDuration >= level.getMinCallDuration() && userReport >= level.getMinReportCount()) {
				matchedLevel = level;
			} else {
				break;
			}
		}

		// 다음 레벨 정보
		int nextLevelIndex = allLevels.indexOf(matchedLevel) + 1;
		Level nextLevel = (nextLevelIndex < allLevels.size()) ? allLevels.get(nextLevelIndex) : null;

		// 퍼센트 계산
		int durationPercent = 100;
		int reportPercent = 100;

		if (nextLevel != null) {
			int durationGap = nextLevel.getMinCallDuration() - matchedLevel.getMinCallDuration();
			int reportGap = nextLevel.getMinReportCount() - matchedLevel.getMinReportCount();

			int durationProgress = userDuration - matchedLevel.getMinCallDuration();
			int reportProgress = userReport - matchedLevel.getMinReportCount();

			durationPercent = (int)(Math.min(100.0, Math.max(0.0, (durationProgress * 100.0 / durationGap))));
			reportPercent = (int)(Math.min(100.0, Math.max(0.0, (reportProgress * 100.0 / reportGap))));
		}

		// 등급 업데이트
		if (!member.getLevel().getLevelId().equals(matchedLevel.getLevelId())) {
			member.setLevel(matchedLevel);
			memberRepository.save(member);
		}

		return MemberLevelResponseDto.builder()
			.level(matchedLevel.getLevel())
			.badgeIcon(matchedLevel.getBadgeIcon())
			.totalCallDurationPercentage(durationPercent)
			.totalReportCountPercentage(reportPercent)
			.build();
	}
}
