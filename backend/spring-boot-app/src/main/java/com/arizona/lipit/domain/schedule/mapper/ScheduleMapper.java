package com.arizona.lipit.domain.schedule.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.arizona.lipit.domain.schedule.dto.ScheduleRequestDto;
import com.arizona.lipit.domain.schedule.dto.ScheduleResponseDto;
import com.arizona.lipit.domain.schedule.entity.CallSchedule;
import com.arizona.lipit.domain.schedule.entity.DayOfWeek;
import com.arizona.lipit.domain.schedule.entity.TopicCategory;
import com.arizona.lipit.global.config.MapStructConfig;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface ScheduleMapper {

    ScheduleMapper INSTANCE = Mappers.getMapper(ScheduleMapper.class);

    // CallSchedule → ScheduleResponseDto 변환
    @Mapping(target = "callScheduleId", source = "callScheduleId")
    @Mapping(target = "scheduledDay", expression = "java(callSchedule.getScheduledDay().name())")
    @Mapping(target = "scheduledTime", source = "scheduledTime")
    @Mapping(target = "topicCategory", expression = "java(callSchedule.getTopicCategory() != null ? callSchedule.getTopicCategory().name() : null)")
    ScheduleResponseDto toDto(CallSchedule callSchedule);

    // 여러 일정을 DTO 리스트로 변환
    List<ScheduleResponseDto> toDtoList(List<CallSchedule> callSchedules);

    // 요청 DTO와 추가 정보로 Entity 생성
    @Mapping(target = "callScheduleId", ignore = true)
    @Mapping(target = "missedCount", constant = "0")
    @Mapping(target = "isCalled", constant = "false")
    CallSchedule toEntity(ScheduleRequestDto requestDto, Long memberId, DayOfWeek scheduledDay, TopicCategory topicCategory);

    // String을 Enum으로 변환하는 기본 메서드
    default DayOfWeek toDayOfWeek(String day) {
        return day != null ? DayOfWeek.valueOf(day) : null;
    }

    default TopicCategory toTopicCategory(String category) {
        return category != null ? TopicCategory.valueOf(category) : null;
    }
}