package com.arizona.lipit.domain.voice.repository;

import com.arizona.lipit.domain.voice.entity.Voice;
import com.arizona.lipit.domain.voice.entity.VoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoiceRepository extends JpaRepository<Voice, Long> {
    List<Voice> findByType(VoiceType type);
}