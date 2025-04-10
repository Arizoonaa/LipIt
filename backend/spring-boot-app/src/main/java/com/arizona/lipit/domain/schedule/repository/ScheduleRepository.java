package com.arizona.lipit.domain.schedule.repository;

import com.arizona.lipit.domain.schedule.entity.CallSchedule;
import com.arizona.lipit.domain.schedule.entity.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<CallSchedule, Long> {

    List<CallSchedule> findAllByMemberId(Long memberId);

    Optional<CallSchedule> findByMemberIdAndScheduledDay(Long memberId, DayOfWeek scheduledDay);

    Optional<CallSchedule> findByCallScheduleIdAndMemberId(Long callScheduleId, Long memberId);

    void deleteByCallScheduleIdAndMemberId(Long callScheduleId, Long memberId);
}