package com.arizona.lipit.domain.voice.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "voice")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long voiceId;

	@Column(nullable = false)
	private String voiceName;

	@Column
	private String audioUrl;

	@Column
	@Builder.Default
	private String imageUrl = "https://dlxayir1dj7sa.cloudfront.net/voice-image/voice_image_default.png";

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private VoiceType type;

	@CreationTimestamp
	private Timestamp createdAt;

	@UpdateTimestamp
	private Timestamp updatedAt;

	// 저장 전에 기본값 설정
	@PrePersist
	public void prePersist() {
		if (this.imageUrl == null) {
			this.imageUrl = "https://dlxayir1dj7sa.cloudfront.net/voice-image/voice_image_default.png";
		}
		if (this.audioUrl == null) {
			this.audioUrl = ""; // 필요하면 여기도 기본값
		}
	}
}