package com.arizona.lipit.domain.schedule.service;

import com.arizona.lipit.domain.schedule.entity.CallSchedule;
import com.arizona.lipit.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleResetService {

    private final ScheduleRepository scheduleRepository;

    /**
     * 매주 월요일 00:00에 모든 스케줄의 부재중 개수를 초기화합니다.
     */
    @Scheduled(cron = "0 0 0 * * MON")
    @Transactional
    public void resetMissedCounts() {
        log.info("부재중 개수 초기화 작업 시작");
        List<CallSchedule> allSchedules = scheduleRepository.findAll();
        
        for (CallSchedule schedule : allSchedules) {
            schedule.setMissedCount(0);
        }
        
        log.info("{}개 스케줄의 부재중 개수가 초기화되었습니다.", allSchedules.size());
    }
} 