package com.arizona.lipit.domain.voice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordingVoiceResponseDto {
	@Schema(description = "회원 ID", example = "1")
	private Long memberId;

	@Schema(description = "회원 음성 ID", example = "2")
	private Long memberVoiceId;

	@Schema(description = "음성 ID", example = "3")
	private Long voiceId;

	@Schema(description = "음성 이름", example = "내 녹음 음성")
	private String voiceName;

	@Schema(description = "음성 타입", example = "CUSTOM")
	private String type;

	@Schema(description = "음성 이미지 URL", example = "https://dlxayir1dj7sa.cloudfront.net/voice-image/voice_image_default.png")
	private String imageUrl;

	@Schema(description = "음성 오디오 URL", example = "https://example.com/audio.mp3")
	private String audioUrl;
}
//없어도 됨(확인용)