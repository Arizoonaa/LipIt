package com.arizona.lipit.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arizona.lipit.domain.member.entity.Level;

public interface LevelRepository extends JpaRepository<Level, Long> {
}
