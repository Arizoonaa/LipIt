package com.arizona.lipit.domain.voice.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.arizona.lipit.domain.member.entity.Member;
import com.arizona.lipit.domain.voice.dto.CelebVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.RecordingVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.SelectVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.UserVoiceResponseDto;
import com.arizona.lipit.domain.voice.dto.VoiceResponseDto;
import com.arizona.lipit.domain.voice.entity.MemberVoice;
import com.arizona.lipit.domain.voice.entity.Voice;
import com.arizona.lipit.global.config.MapStructConfig;

@Mapper(config = MapStructConfig.class)
public interface VoiceMapper {

	VoiceMapper INSTANCE = Mappers.getMapper(VoiceMapper.class);

	// Voice → VoiceResponseDto 변환
	@Mapping(source = "voiceId", target = "voiceId")
	@Mapping(source = "voiceName", target = "voiceName")
	@Mapping(source = "imageUrl", target = "customImageUrl")
	@Mapping(source = "audioUrl", target = "audioUrl")
	VoiceResponseDto toVoiceResponseDto(Voice voice);

	// MemberVoice → VoiceResponseDto 변환
	@Mapping(source = "voice.voiceId", target = "voiceId")
	@Mapping(source = "voice.voiceName", target = "voiceName")
	@Mapping(source = "voice.imageUrl", target = "customImageUrl")
	@Mapping(source = "voice.audioUrl", target = "audioUrl")
	VoiceResponseDto toVoiceResponseDto(MemberVoice memberVoice);

	// MemberVoice 리스트 → VoiceResponseDto 리스트 변환
	List<VoiceResponseDto> toVoiceResponseDtoList(List<MemberVoice> memberVoices);

	// MemberVoice → UserVoiceResponseDto 변환
	@Mapping(source = "voice.voiceId", target = "voiceId")
	@Mapping(source = "voice.voiceName", target = "voiceName")
	@Mapping(source = "voice.imageUrl", target = "customImageUrl")
	@Mapping(source = "voice.audioUrl", target = "audioUrl")
	UserVoiceResponseDto toUserVoiceResponseDto(MemberVoice memberVoice);

	// Voice → UserVoiceResponseDto 변환
	@Mapping(source = "voiceId", target = "voiceId")
	@Mapping(source = "voiceName", target = "voiceName")
	@Mapping(source = "imageUrl", target = "customImageUrl")
	@Mapping(source = "audioUrl", target = "audioUrl")
	UserVoiceResponseDto toUserVoiceResponseDto(Voice voice);

	// MemberVoice 리스트 → UserVoiceResponseDto 리스트 변환
	List<UserVoiceResponseDto> toUserVoiceResponseDtoList(List<MemberVoice> memberVoices);

	// Voice + activated → CelebVoiceResponseDto 변환
	@Mapping(source = "voice.voiceId", target = "voiceId")
	@Mapping(source = "voice.voiceName", target = "voiceName")
	@Mapping(source = "voice.imageUrl", target = "customImageUrl")
	@Mapping(source = "voice.audioUrl", target = "audioUrl")
	@Mapping(source = "activated", target = "activated")
	CelebVoiceResponseDto toCelebVoiceResponseDto(Voice voice, boolean activated);

	// Member + Voice → SelectVoiceResponseDto 변환
	@Mapping(source = "member.memberId", target = "memberId")
	@Mapping(source = "voice.voiceId", target = "selectedVoiceId")
	@Mapping(source = "voice.voiceName", target = "voiceName")
	@Mapping(source = "voice.type", target = "type")
	SelectVoiceResponseDto toSelectVoiceResponseDto(Member member, Voice voice);

	// MemberVoice → RecordingVoiceResponseDto 변환
	@Mapping(source = "member.memberId", target = "memberId")
	@Mapping(source = "memberVoiceId", target = "memberVoiceId")
	@Mapping(source = "voice.voiceId", target = "voiceId")
	@Mapping(source = "voice.voiceName", target = "voiceName")
	@Mapping(source = "voice.type", target = "type")
	@Mapping(source = "voice.imageUrl", target = "imageUrl")
	@Mapping(source = "voice.audioUrl", target = "audioUrl")
	RecordingVoiceResponseDto toRecordingVoiceResponseDto(MemberVoice memberVoice);
}