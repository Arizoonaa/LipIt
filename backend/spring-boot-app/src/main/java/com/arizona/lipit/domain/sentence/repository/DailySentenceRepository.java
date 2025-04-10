package com.arizona.lipit.domain.sentence.repository;

import com.arizona.lipit.domain.sentence.entity.DailySentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DailySentenceRepository extends JpaRepository<DailySentence, Long> {
    
    // PostgreSQL의 RANDOM() 함수 사용
    @Query(value = "SELECT * FROM daily_sentence ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    DailySentence findRandomSentence();
}