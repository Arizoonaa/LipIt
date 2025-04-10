package com.arizona.lipit.domain.voice.controller;

import com.arizona.lipit.domain.voice.dto.*;
import com.arizona.lipit.domain.voice.service.VoiceService;
import com.arizona.lipit.global.docs.voice.VoiceApiSpec;
import com.arizona.lipit.global.response.CommonResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/voices")
@RequiredArgsConstructor
public class VoiceController implements VoiceApiSpec {

    private final VoiceService voiceService;

    @Override
    @GetMapping("/celeb")
    public ResponseEntity<CommonResponse<List<CelebVoiceResponseDto>>> getCelebVoices(@RequestParam Long memberId) {
        List<CelebVoiceResponseDto> voices = voiceService.getCelebVoicesByMemberId(memberId);
        return ResponseEntity.ok(CommonResponse.ok("셀럽 음성이 성공적으로 조회되었습니다.", voices));
    }
    
    @Override
    @GetMapping("/custom")
    public ResponseEntity<CommonResponse<List<VoiceResponseDto>>> getCustomVoices(@RequestParam Long memberId) {
        List<VoiceResponseDto> voices = voiceService.getCustomVoicesByMemberId(memberId);
        return ResponseEntity.ok(CommonResponse.ok("커스텀 음성이 성공적으로 조회되었습니다.", voices));
    }

    @Override
    @GetMapping("/members/{memberId}/voice")
    public ResponseEntity<CommonResponse<List<UserVoiceResponseDto>>> getUserVoices(@PathVariable Long memberId) {
        List<UserVoiceResponseDto> voices = voiceService.getAllVoicesByMemberId(memberId);
        return ResponseEntity.ok(CommonResponse.ok("선택된 음성이 성공적으로 조회되었습니다.", voices));
    }
    
    @Override
    @PatchMapping("/members/{memberId}/voice")
    public ResponseEntity<CommonResponse<SelectVoiceResponseDto>> selectVoice(
            @PathVariable Long memberId,
            @Valid @RequestBody SelectVoiceRequestDto requestDto) {
        SelectVoiceResponseDto responseDto = voiceService.selectVoice(memberId, requestDto);
        return ResponseEntity.ok(CommonResponse.ok("음성이 성공적으로 선택되었습니다.", responseDto));
    }

    @Override
    @PostMapping("/recording")
    public ResponseEntity<CommonResponse<RecordingVoiceResponseDto>> saveRecordingVoice(
            @Valid @RequestBody RecordingVoiceRequestDto requestDto,
            @RequestParam Long memberId) {
        RecordingVoiceResponseDto responseDto = voiceService.saveRecordingVoice(requestDto, memberId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.ok("커스텀 음성이 성공적으로 저장되었습니다.", responseDto));
    }
}