package com.arizona.lipit.global.docs.schedule;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.arizona.lipit.domain.schedule.dto.CallStatusDto;
import com.arizona.lipit.domain.schedule.dto.ScheduleDeleteResponseDto;
import com.arizona.lipit.domain.schedule.dto.ScheduleRequestDto;
import com.arizona.lipit.domain.schedule.dto.ScheduleResponseDto;
import com.arizona.lipit.global.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "일정", description = "전화 일정 관련 API")
public interface ScheduleApiSpec {

    @Operation(summary = "일정 조회", description = """
        💡 사용자의 모든 일정을 조회합니다.
        """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일정이 성공적으로 조회되었습니다."),
            @ApiResponse(responseCode = "401",
                    description = """
                `[AUTH-001]` 인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요.
                
                `[AUTH-002]` 유효하지 않은 Access Token입니다.
                
                `[AUTH-003]` 만료된 Access Token입니다.
                
                `[AUTH-004]` Access Token이 누락되었습니다.
                """,
                    content = @Content()),
            @ApiResponse(responseCode = "500",
                    description = "`[SCHEDULE-001]` 일정 조회 중 서버 오류가 발생했습니다.",
                    content = @Content())
    })
    ResponseEntity<CommonResponse<List<ScheduleResponseDto>>> getAllSchedules(
            @RequestParam @Parameter(description = "사용자 ID") Long memberId);

    @Operation(summary = "오늘의 일정 조회", description = """
        💡 사용자의 오늘 요일에 해당하는 일정을 조회합니다.
        """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일정이 성공적으로 조회되었습니다."),
            @ApiResponse(responseCode = "401",
                    description = """
                `[AUTH-001]` 인증되지 않은 사용자입니다. 로그인 후 다시 시도하세요.
                
                `[AUTH-002]` 유효하지 않은 토큰입니다.
                
                `[AUTH-003]` 만료된 Access Token입니다.
                
                `[AUTH-004]` Access Token이 누락되었습니다.
                """,
                    content = @Content()),
            @ApiResponse(responseCode = "404",
                    description = "`[ALALM-004]` 일정이 존재하지 않습니다.",
                    content = @Content()),
            @ApiResponse(responseCode = "500",
                    description = "서버 내부 오류가 발생했습니다.",
                    content = @Content())
    })
    ResponseEntity<CommonResponse<ScheduleResponseDto>> getTodaySchedule(
            @RequestParam @Parameter(description = "사용자 ID") Long memberId,
            @RequestParam @Parameter(description = "오늘자 요일") String callScheduleDay);

    @Operation(summary = "일정 생성", description = """
        💡 새로운 일정을 생성합니다.
        """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "일정이 성공적으로 저장되었습니다."),
            @ApiResponse(responseCode = "400",
                    description = """
                `[SCHEDULE-002]` 해당 요일에 이미 일정이 존재합니다.
                
                `[SCHEDULE-003]` 유효하지 않은 요일 형식입니다.
                
                `[SCHEDULE-004]` 유효하지 않은 시간 형식입니다.
                """,
                    content = @Content()),
            @ApiResponse(responseCode = "404",
                    description = "`[MEMBER-001]` 해당 사용자를 찾을 수 없습니다.",
                    content = @Content()),
            @ApiResponse(responseCode = "500",
                    description = "`[SCHEDULE-005]` 일정 생성 중 서버 오류가 발생했습니다.",
                    content = @Content())
    })
    ResponseEntity<CommonResponse<ScheduleResponseDto>> createSchedule(
            @RequestParam @Parameter(description = "사용자 ID") Long memberId,
            @Valid @RequestBody ScheduleRequestDto requestDto);

    @Operation(summary = "일정 수정", description = """
        💡 기존 일정을 수정합니다.
        """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일정이 성공적으로 수정되었습니다."),
            @ApiResponse(responseCode = "400",
                    description = """
                `[SCHEDULE-003]` 유효하지 않은 요일 형식입니다.
                
                `[SCHEDULE-004]` 유효하지 않은 시간 형식입니다.
                
                `[SCHEDULE-006]` 해당 요일에 이미 다른 일정이 존재합니다.
                """,
                    content = @Content()),
            @ApiResponse(responseCode = "404",
                    description = """
                `[SCHEDULE-007]` 해당 일정을 찾을 수 없습니다.
                
                `[MEMBER-001]` 해당 사용자를 찾을 수 없습니다.
                """,
                    content = @Content()),
            @ApiResponse(responseCode = "403",
                    description = "`[SCHEDULE-008]` 해당 일정에 대한 권한이 없습니다.",
                    content = @Content()),
            @ApiResponse(responseCode = "500",
                    description = "`[SCHEDULE-009]` 일정 수정 중 서버 오류가 발생했습니다.",
                    content = @Content())
    })
    ResponseEntity<CommonResponse<ScheduleResponseDto>> updateSchedule(
            @RequestParam @Parameter(description = "사용자 ID") Long memberId,
            @PathVariable @Parameter(description = "수정할 일정 ID") Long callScheduleId,
            @Valid @RequestBody ScheduleRequestDto requestDto);

    @Operation(summary = "일정 삭제", description = """
        💡 일정을 삭제합니다.
        """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "일정이 성공적으로 삭제되었습니다."),
            @ApiResponse(responseCode = "404",
                    description = """
                `[SCHEDULE-007]` 해당 일정을 찾을 수 없습니다.
                
                `[MEMBER-001]` 해당 사용자를 찾을 수 없습니다.
                """,
                    content = @Content()),
            @ApiResponse(responseCode = "403",
                    description = "`[SCHEDULE-008]` 해당 일정에 대한 권한이 없습니다.",
                    content = @Content()),
            @ApiResponse(responseCode = "500",
                    description = "`[SCHEDULE-010]` 일정 삭제 중 서버 오류가 발생했습니다.",
                    content = @Content())
    })
    ResponseEntity<CommonResponse<ScheduleDeleteResponseDto>> deleteSchedule(
            @RequestParam @Parameter(description = "사용자 ID") Long memberId,
            @PathVariable @Parameter(description = "삭제할 일정 ID") Long callScheduleId);

    @Operation(summary = "부재중 개수 조회", description = """
        💡 특정 일정의 부재중 개수를 조회합니다.
        """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "부재중 개수가 성공적으로 조회되었습니다."),
            @ApiResponse(responseCode = "404",
                    description = "`[CALL-004]` 존재하지 않는 통화 일정입니다.",
                    content = @Content())
    })
    ResponseEntity<CommonResponse<CallStatusDto>> getMissedCount(
            @PathVariable @Parameter(description = "일정 ID") Long callScheduleId);

    @Operation(summary = "전화 수신 거절", description = """
        💡 특정 일정의 부재중 개수를 1 증가시키고 통화 상태를 반환합니다.
        """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전화 수신이 거절되었습니다."),
            @ApiResponse(responseCode = "404",
                    description = "`[CALL-004]` 존재하지 않는 통화 일정입니다.",
                    content = @Content())
    })
    ResponseEntity<CommonResponse<CallStatusDto>> rejectCall(
            @PathVariable @Parameter(description = "일정 ID") Long callScheduleId);
}