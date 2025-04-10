package com.arizona.lipit.domain.member.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Level {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long levelId;

	@Column(nullable = false)
	private int level;

	@Column(nullable = false)
	private int minCallDuration;

	@Column(nullable = false)
	private int minReportCount;

	@Column(nullable = false)
	private String badgeIcon;

	@CreationTimestamp
	private Timestamp createdAt;

	@UpdateTimestamp
	private Timestamp updatedAt;
}
