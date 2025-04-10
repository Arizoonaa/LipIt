package com.arizona.lipit.domain.schedule.service;

import com.arizona.lipit.domain.schedule.dto.CallStatusDto;
import com.arizona.lipit.domain.schedule.dto.ScheduleDeleteResponseDto;
import com.arizona.lipit.domain.schedule.dto.ScheduleRequestDto;
import com.arizona.lipit.domain.schedule.dto.ScheduleResponseDto;
import com.arizona.lipit.domain.schedule.entity.CallSchedule;
import com.arizona.lipit.domain.schedule.entity.DayOfWeek;
import com.arizona.lipit.domain.schedule.entity.TopicCategory;
import com.arizona.lipit.domain.schedule.mapper.ScheduleMapper;
import com.arizona.lipit.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getAllSchedulesByMemberId(Long memberId) {
        List<CallSchedule> schedules = scheduleRepository.findAllByMemberId(memberId);
        return scheduleMapper.toDtoList(schedules);
    }

    @Transactional
    public ScheduleResponseDto createSchedule(Long memberId, ScheduleRequestDto requestDto) {
        // String -> Enum 변환
        DayOfWeek scheduledDay = scheduleMapper.toDayOfWeek(requestDto.getScheduledDay());
        TopicCategory topicCategory = scheduleMapper.toTopicCategory(requestDto.getTopicCategory());

        // 중복 일정 확인
        scheduleRepository.findByMemberIdAndScheduledDay(memberId, scheduledDay)
                .ifPresent(schedule -> {
                    throw new IllegalStateException("해당 요일에 이미 일정이 존재합니다.");
                });

        // 엔티티 생성 및 저장
        CallSchedule callSchedule = scheduleMapper.toEntity(requestDto, memberId, scheduledDay, topicCategory);
        CallSchedule savedSchedule = scheduleRepository.save(callSchedule);

        return scheduleMapper.toDto(savedSchedule);
    }

    @Transactional
    public ScheduleResponseDto updateSchedule(Long callScheduleId, Long memberId, ScheduleRequestDto requestDto) {
        // 사용자의 일정인지 확인
        CallSchedule callSchedule = scheduleRepository.findByCallScheduleIdAndMemberId(callScheduleId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자의 일정을 찾을 수 없습니다."));

        // 수정하려는 요일에 이미 다른 일정이 있는지 확인
        DayOfWeek newScheduledDay = scheduleMapper.toDayOfWeek(requestDto.getScheduledDay());
        if (newScheduledDay != null && !callSchedule.getScheduledDay().equals(newScheduledDay)) {
            scheduleRepository.findByMemberIdAndScheduledDay(memberId, newScheduledDay)
                    .ifPresent(schedule -> {
                        throw new IllegalStateException("해당 요일에 이미 다른 일정이 존재합니다.");
                    });
        }

        // 엔티티 업데이트
        TopicCategory topicCategory = scheduleMapper.toTopicCategory(requestDto.getTopicCategory());
        callSchedule.setScheduledDay(newScheduledDay != null ? newScheduledDay : callSchedule.getScheduledDay());
        callSchedule.setScheduledTime(requestDto.getScheduledTime() != null ? requestDto.getScheduledTime() : callSchedule.getScheduledTime());
        callSchedule.setTopicCategory(topicCategory);

        return scheduleMapper.toDto(callSchedule);
    }

    @Transactional
    public ScheduleDeleteResponseDto deleteSchedule(Long callScheduleId, Long memberId) {
        // 사용자의 일정인지 확인
        CallSchedule callSchedule = scheduleRepository.findByCallScheduleIdAndMemberId(callScheduleId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자의 일정을 찾을 수 없습니다."));

        // 일정 삭제
        scheduleRepository.deleteByCallScheduleIdAndMemberId(callScheduleId, memberId);

        return ScheduleDeleteResponseDto.builder()
                .success(true)
                .build();
    }

    @Transactional(readOnly = true)
    public CallStatusDto getCallStatusByScheduleId(Long callScheduleId) {
        CallSchedule callSchedule = scheduleRepository.findById(callScheduleId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 통화 일정입니다."));
        
        return CallStatusDto.builder()
                .missedCount(callSchedule.getMissedCount())
                .isCalled(callSchedule.getIsCalled())
                .build();
    }

    @Transactional
    public CallStatusDto increaseMissedCountByScheduleId(Long callScheduleId) {
        CallSchedule callSchedule = scheduleRepository.findById(callScheduleId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 통화 일정입니다."));
        
        // 부재중 개수 증가
        callSchedule.setMissedCount(callSchedule.getMissedCount() + 1);
        
        return CallStatusDto.builder()
                .missedCount(callSchedule.getMissedCount())
                .isCalled(callSchedule.getIsCalled())
                .build();
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDto getTodaySchedule(Long memberId, DayOfWeek dayOfWeek) {
        CallSchedule callSchedule = scheduleRepository.findByMemberIdAndScheduledDay(memberId, dayOfWeek)
                .orElseThrow(() -> new EntityNotFoundException("일정이 존재하지 않습니다."));
        
        return ScheduleResponseDto.builder()
                .callScheduleId(callSchedule.getCallScheduleId())
                .scheduledDay(callSchedule.getScheduledDay().name())
                .scheduledTime(callSchedule.getScheduledTime())
                .topicCategory(callSchedule.getTopicCategory() != null ? callSchedule.getTopicCategory().name() : null)
                .build();
    }
}