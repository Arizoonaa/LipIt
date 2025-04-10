package com.arizona.lipit.domain.voice.repository;

import com.arizona.lipit.domain.voice.entity.MemberVoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberVoiceRepository extends JpaRepository<MemberVoice, Long> {
    
    @Query("SELECT mv FROM MemberVoice mv JOIN FETCH mv.voice v WHERE mv.member.memberId = :memberId AND v.type = com.arizona.lipit.domain.voice.entity.VoiceType.CELEB")
    List<MemberVoice> findCelebVoicesByMemberId(@Param("memberId") Long memberId);
    
    @Query("SELECT mv FROM MemberVoice mv JOIN FETCH mv.voice v WHERE mv.member.memberId = :memberId AND v.type = com.arizona.lipit.domain.voice.entity.VoiceType.CUSTOM")
    List<MemberVoice> findCustomVoicesByMemberId(@Param("memberId") Long memberId);
    
    @Query("SELECT mv FROM MemberVoice mv JOIN FETCH mv.voice v WHERE mv.member.memberId = :memberId")
    List<MemberVoice> findAllVoicesByMemberId(@Param("memberId") Long memberId);
    
    @Query("SELECT mv FROM MemberVoice mv JOIN FETCH mv.voice v " +
           "WHERE mv.member.memberId = :memberId AND mv.voice.voiceId = CAST(mv.member.selectedVoiceId AS long)")
    Optional<MemberVoice> findSelectedVoiceByMemberId(@Param("memberId") Long memberId);
}
//조인연산 때문에 관계 매핑(@OneToMany, @ManyToOne 등) 대신 JPQL (@Query) 사용
