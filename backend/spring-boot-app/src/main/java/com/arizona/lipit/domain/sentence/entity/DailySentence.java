package com.arizona.lipit.domain.sentence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "daily_sentence")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailySentence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_sentence_id")
    private Long dailySentenceId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "content_korean", nullable = false)
    private String contentKorean;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;
}