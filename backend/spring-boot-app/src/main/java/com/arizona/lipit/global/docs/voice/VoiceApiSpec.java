package com.arizona.lipit.global.docs.voice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.arizona.lipit.domain.voice.dto.*;
import com.arizona.lipit.global.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "음성", description = "음성 관련 API")
public interface VoiceApiSpec {

    @Operation(summary = "셀럽 음성 조회", description = "💡 회원의 레벨에 따라 사용 가능한 셀럽 음성을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "셀럽 음성이 성공적으로 조회되었습니다."),
        @ApiResponse(responseCode = "400", description = "`[MEMBER-001]` 유효하지 않은 회원 ID입니다.", content = @Content),
        @ApiResponse(responseCode = "404", description = """
            `[MEMBER-002]` 해당 ID를 가진 사용자를 찾을 수 없습니다.
            `[VOICE-001]` 셀럽 음성을 찾을 수 없습니다.
            """, content = @Content)
    })
    ResponseEntity<CommonResponse<List<CelebVoiceResponseDto>>> getCelebVoices(
        @Parameter(description = "회원 ID") @RequestParam Long memberId
    );

    @Operation(summary = "커스텀 음성 조회", description = "💡 회원이 등록한 커스텀 음성을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "커스텀 음성이 성공적으로 조회되었습니다."),
        @ApiResponse(responseCode = "400", description = "`[MEMBER-001]` 유효하지 않은 회원 ID입니다.", content = @Content),
        @ApiResponse(responseCode = "404", description = """
            `[MEMBER-002]` 해당 ID를 가진 사용자를 찾을 수 없습니다.
            `[VOICE-002]` 커스텀 음성을 찾을 수 없습니다.
            """, content = @Content)
    })
    ResponseEntity<CommonResponse<List<VoiceResponseDto>>> getCustomVoices(
        @Parameter(description = "회원 ID") @RequestParam Long memberId
    );

    @Operation(summary = "선택된 음성 조회", description = "💡 회원이 현재 선택한 음성을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "선택된 음성이 성공적으로 조회되었습니다."),
        @ApiResponse(responseCode = "400", description = "`[MEMBER-001]` 유효하지 않은 회원 ID입니다.", content = @Content),
        @ApiResponse(responseCode = "404", description = "`[MEMBER-002]` 해당 ID를 가진 사용자를 찾을 수 없습니다.", content = @Content)
    })
    ResponseEntity<CommonResponse<List<UserVoiceResponseDto>>> getUserVoices(
        @Parameter(description = "회원 ID") @PathVariable Long memberId
    );

    @Operation(summary = "음성 선택", description = "💡 사용자가 사용할 음성을 선택합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "음성이 성공적으로 선택되었습니다."),
        @ApiResponse(responseCode = "400", description = """
            `[MEMBER-001]` 유효하지 않은 회원 ID입니다.
            `[VOICE-003]` 유효하지 않은 음성 요청입니다.
            """, content = @Content),
        @ApiResponse(responseCode = "401", description = "`[VOICE-004]` 음성에 대한 접근 권한이 없습니다.", content = @Content),
        @ApiResponse(responseCode = "404", description = """
            `[MEMBER-002]` 해당 ID를 가진 사용자를 찾을 수 없습니다.
            `[VOICE-005]` 요청한 음성을 찾을 수 없습니다.
            """, content = @Content)
    })
    ResponseEntity<CommonResponse<SelectVoiceResponseDto>> selectVoice(
        @Parameter(description = "회원 ID") @PathVariable Long memberId,
        @Valid @RequestBody SelectVoiceRequestDto requestDto
    );

    @Operation(summary = "커스텀 음성 저장", description = "💡 사용자의 커스텀 음성을 저장합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "음성이 성공적으로 저장되었습니다."),
        @ApiResponse(responseCode = "400", description = """
            `[MEMBER-001]` 유효하지 않은 회원 ID입니다.
            `[VOICE-006]` 음성 정보 형식이 올바르지 않습니다.
            `[VOICE-007]` 유효하지 않은 URL 형식입니다.
            """, content = @Content),
        @ApiResponse(responseCode = "404", description = "`[MEMBER-002]` 해당 ID를 가진 사용자를 찾을 수 없습니다.", content = @Content),
        @ApiResponse(responseCode = "409", description = "`[VOICE-008]` 이미 존재하는 음성 이름입니다.", content = @Content)
    })
    ResponseEntity<CommonResponse<RecordingVoiceResponseDto>> saveRecordingVoice(
        @Valid @RequestBody RecordingVoiceRequestDto requestDto,
        @Parameter(description = "회원 ID") @RequestParam Long memberId
    );
}