package com.arizona.lipit.domain.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.arizona.lipit.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByEmail(String email);
	
	@Query("SELECT m FROM Member m WHERE m.fcmToken IS NOT NULL AND m.fcmToken <> ''")
	List<Member> findAllByFcmTokenIsNotNull();
}
