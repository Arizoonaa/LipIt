package com.arizona.lipit.domain.notification.service;

import com.arizona.lipit.domain.member.repository.MemberRepository;
import com.arizona.lipit.domain.notification.dto.NotificationRequestDto;
import com.arizona.lipit.domain.sentence.entity.DailySentence;
import com.arizona.lipit.domain.sentence.repository.DailySentenceRepository;
import com.arizona.lipit.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;
    private final MemberRepository memberRepository;
    private final DailySentenceRepository dailySentenceRepository;

    @Scheduled(cron = "0 0 7 * * *")  // 매일 아침 7시
    public void sendDailySentence() {
        try {
            // 1. 오늘의 문장 조회 결과 확인
            log.info("📝 오늘의 문장 조회 시작");
            DailySentence todaySentence = dailySentenceRepository.findRandomSentence();
            if (todaySentence == null) {
                log.error("❌ 오늘의 문장이 존재하지 않습니다.");
                return;
            }
            log.info("✅ 선택된 오늘의 문장 - ID: {}, 내용: {}, 한글: {}", 
                todaySentence.getDailySentenceId(), 
                todaySentence.getContent(), 
                todaySentence.getContentKorean());

            // 2. FCM 토큰 있는 사용자 확인
            List<Member> targetMembers = memberRepository.findAllByFcmTokenIsNotNull();
            log.info("📱 FCM 토큰 보유 회원 수: {}", targetMembers.size());
            
            // 각 회원의 FCM 토큰 출력
            targetMembers.forEach(member -> {
                log.info("회원 ID: {}, FCM 토큰: {}", member.getMemberId(), member.getFcmToken());
            });

            if (targetMembers.isEmpty()) {
                log.warn("⚠️ FCM 토큰을 가진 회원이 없습니다.");
                return;
            }

            // 3. 알림 전송
            targetMembers.forEach(member -> {
                log.info("🔔 회원 ID: {}에게 알림 전송 시도", member.getMemberId());
                NotificationRequestDto notificationRequest = NotificationRequestDto.builder()
                    .memberId(member.getMemberId())
                    .type("DAILY_SENTENCE")
                    .title("오늘의 문장")
                    .body(todaySentence.getContent() + "\n" + todaySentence.getContentKorean())
                    .sentenceId(todaySentence.getDailySentenceId())
                    .build();

                try {
                    Map<String, String> result = notificationService.sendNotificationToUser(notificationRequest);
                    log.info("✅ 회원 ID: {}에게 알림 전송 성공. 결과: {}", member.getMemberId(), result);
                } catch (Exception e) {
                    log.error("❌ 회원 ID: {}에게 알림 전송 실패. 에러: {}", member.getMemberId(), e.getMessage(), e);
                }
            });
            
            log.info("✅ 오늘의 문장 알림 전송 프로세스 완료");
        } catch (Exception e) {
            log.error("❌ 오늘의 문장 알림 스케줄링 실패: {}", e.getMessage(), e);
        }
    }
}