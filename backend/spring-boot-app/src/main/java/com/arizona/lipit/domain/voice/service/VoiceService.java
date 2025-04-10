package com.arizona.lipit.domain.voice.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import com.arizona.lipit.domain.member.entity.Level;
import com.arizona.lipit.domain.member.entity.Member;
import com.arizona.lipit.domain.member.repository.MemberRepository;
import com.arizona.lipit.domain.voice.dto.CelebVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.RecordingVoiceRequestDto;
import com.arizona.lipit.domain.voice.dto.RecordingVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.SelectVoiceRequestDto;
import com.arizona.lipit.domain.voice.dto.SelectVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.UserVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.VoiceResponseDto;
import com.arizona.lipit.domain.voice.entity.MemberVoice;
import com.arizona.lipit.domain.voice.entity.Voice;
import com.arizona.lipit.domain.voice.entity.VoiceType;
import com.arizona.lipit.domain.voice.mapper.VoiceMapper;
import com.arizona.lipit.domain.voice.repository.MemberVoiceRepository;
import com.arizona.lipit.domain.voice.repository.VoiceRepository;
import com.arizona.lipit.global.exception.CustomException;
import com.arizona.lipit.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
public class VoiceService {

	private final MemberRepository memberRepository;
	private final MemberVoiceRepository memberVoiceRepository;
	private final VoiceRepository voiceRepository;
	private final VoiceMapper voiceMapper;

	@Cacheable(value = "celebVoices", key = "'all'")
	public List<Voice> getCelebVoices() {
		List<Voice> voices = voiceRepository.findByType(VoiceType.CELEB);
		System.out.println("Fetching from database: " + voices.size() + " voices"); // 캐시 동작 확인용
		return voices;
	}

	public List<CelebVoiceResponseDto> getCelebVoicesByMemberId(Long memberId) {
		validateMemberId(memberId);
		Member member = findMemberById(memberId);
		List<Voice> celebVoices = getCelebVoices();
		return mapToCelebVoiceResponseDtos(celebVoices, member.getLevel());
	}

	@Transactional(readOnly = true)
	public List<VoiceResponseDto> getCustomVoicesByMemberId(Long memberId) {
		validateMemberId(memberId);
		findMemberById(memberId);
		return findAndMapCustomVoices(memberId);
	}

	@Transactional(readOnly = true)
	public List<UserVoiceResponseDto> getAllVoicesByMemberId(Long memberId) {
		validateMemberId(memberId);
		Member member = findMemberById(memberId);

		if (member.getSelectedVoiceId() == null) {
			return List.of();
		}

		Voice selectedVoice = findVoiceById(member.getSelectedVoiceId());
		return List.of(voiceMapper.toUserVoiceResponseDto(selectedVoice));
	}

	@Transactional
	public SelectVoiceResponseDto selectVoice(Long memberId, SelectVoiceRequestDto requestDto) {
		validateVoiceRequest(requestDto);
		Member member = findMemberById(memberId);
		Voice selectedVoice = findVoiceById(requestDto.getVoiceId());

		validateVoiceOwnership(memberId, selectedVoice);

		member.setSelectedVoiceId(selectedVoice.getVoiceId());
		memberRepository.save(member);

		return voiceMapper.toSelectVoiceResponseDto(member, selectedVoice);
	}

	@Transactional
	public RecordingVoiceResponseDto saveRecordingVoice(RecordingVoiceRequestDto requestDto, Long memberId) {
		validateRecordingRequest(requestDto, memberId);
		Member member = findMemberById(memberId);
		validateDuplicateVoiceName(memberId, requestDto.getVoiceName());

		Voice savedVoice = saveNewVoice(requestDto);
		MemberVoice savedMemberVoice = saveMemberVoice(member, savedVoice);

		return voiceMapper.toRecordingVoiceResponseDto(savedMemberVoice);
	}

	@Transactional
	public void saveDefaultCelebVoice(Long memberId) {
		Member member = findMemberById(memberId);
		Voice defaultCelebVoice = findVoiceById(1L);
		MemberVoice savedMemberVoice = saveMemberVoice(member, defaultCelebVoice);
	}

	// Private helper methods
	private void validateMemberId(Long memberId) {
		if (memberId == null) {
			throw new CustomException(ErrorCode.INVALID_MEMBER_ID);
		}
	}

	private Member findMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_ID_NOT_FOUND));
	}

	private List<VoiceResponseDto> findAndMapCustomVoices(Long memberId) {
		List<MemberVoice> memberVoices = memberVoiceRepository.findCustomVoicesByMemberId(memberId);
		if (memberVoices.isEmpty()) {
			throw new CustomException(ErrorCode.VOICE_NOT_FOUND);
		}
		return voiceMapper.toVoiceResponseDtoList(memberVoices);
	}

	private Voice findVoiceById(Long voiceId) {
		return voiceRepository.findById(voiceId)
			.orElseThrow(() -> new CustomException(ErrorCode.VOICE_NOT_EXIST));
	}

	private void validateRecordingRequest(RecordingVoiceRequestDto requestDto, Long memberId) {
		if (requestDto == null || memberId == null) {
			throw new CustomException(ErrorCode.INVALID_REQUEST, "잘못된 요청입니다. 입력 값을 확인해주세요.");
		}

		if (isInvalidRequiredFields(requestDto)) {
			throw new CustomException(ErrorCode.INVALID_DATA, "요청 데이터를 처리할 수 없습니다. 입력 값을 확인해주세요.");
		}

		validateUrls(requestDto);
	}

	private boolean isInvalidRequiredFields(RecordingVoiceRequestDto requestDto) {
		return requestDto.getVoiceName() == null || requestDto.getVoiceName().trim().isEmpty() ||
			requestDto.getAudioUrl() == null || requestDto.getAudioUrl().trim().isEmpty();
	}

	private void validateUrls(RecordingVoiceRequestDto requestDto) {
		try {
			new URL(requestDto.getAudioUrl());
			if (requestDto.getImageUrl() != null && !requestDto.getImageUrl().trim().isEmpty()) {
				new URL(requestDto.getImageUrl());
			}
		} catch (MalformedURLException e) {
			throw new CustomException(ErrorCode.URL_RESOURCE_NOT_FOUND, "제공된 URL에서 리소스를 찾을 수 없습니다.");
		}
	}

	private void validateDuplicateVoiceName(Long memberId, String voiceName) {
		boolean isDuplicate = memberVoiceRepository.findAllVoicesByMemberId(memberId).stream()
			.anyMatch(mv -> mv.getVoice().getVoiceName().trim().equals(voiceName.trim()));

		if (isDuplicate) {
			throw new CustomException(ErrorCode.DATA_CONFLICT, "이미 존재하는 데이터입니다. 중복을 확인해주세요.");
		}
	}

	private void validateVoiceRequest(SelectVoiceRequestDto requestDto) {
		if (requestDto.getVoiceId() == null || requestDto.getVoiceId() <= 0) {
			throw new CustomException(ErrorCode.INVALID_REQUEST, "잘못된 요청입니다. 입력 값을 확인해주세요.");
		}
	}

	private void validateVoiceOwnership(Long memberId, Voice voice) {
		if (voice.getType() == VoiceType.CUSTOM) {
			boolean isOwnVoice = memberVoiceRepository.findAllVoicesByMemberId(memberId).stream()
				.anyMatch(mv -> mv.getVoice().getVoiceId().equals(voice.getVoiceId()));
			
			if (!isOwnVoice) {
				throw new CustomException(ErrorCode.FORBIDDEN_ACCESS, "해당 리소스에 접근할 권한이 없습니다.");
			}
		}
	}

	private Voice saveNewVoice(RecordingVoiceRequestDto requestDto) {
		Voice voice = Voice.builder()
			.voiceName(requestDto.getVoiceName().trim())
			.audioUrl(requestDto.getAudioUrl().trim())
			.imageUrl(requestDto.getImageUrl() != null ? requestDto.getImageUrl().trim() : null)
			.type(VoiceType.CUSTOM)
			.build();

		return voiceRepository.save(voice);
	}

	private MemberVoice saveMemberVoice(Member member, Voice voice) {
		MemberVoice memberVoice = MemberVoice.builder()
			.member(member)
			.voice(voice)
			.build();

		return memberVoiceRepository.save(memberVoice);
	}

	private List<CelebVoiceResponseDto> mapToCelebVoiceResponseDtos(List<Voice> celebVoices, Level memberLevel) {
		int currentLevel = memberLevel != null ? memberLevel.getLevel() : 0;

		return celebVoices.stream()
			.map(voice -> {
				boolean activated = determineVoiceActivation(voice.getVoiceName(), currentLevel);
				return voiceMapper.toCelebVoiceResponseDto(voice, activated);
			})
			.collect(Collectors.toList());
	}

	private boolean determineVoiceActivation(String voiceName, int currentLevel) {
		return switch (voiceName.toLowerCase()) {
			case "benedict" -> currentLevel >= 1;
			case "ariana" -> currentLevel >= 2;
			case "leonardo" -> currentLevel >= 3;
			case "taylor" -> currentLevel >= 4;
			case "jennie" -> currentLevel >= 5;
			default -> false;
		};
	}
}