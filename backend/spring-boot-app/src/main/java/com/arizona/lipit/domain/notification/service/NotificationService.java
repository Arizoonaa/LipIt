package com.arizona.lipit.domain.notification.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.arizona.lipit.domain.member.service.MemberService;
import com.arizona.lipit.domain.notification.dto.NotificationRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final FcmWithDataService fcmWithDataService;
	private final MemberService memberService;

	public Map<String, String> sendNotificationToUser(NotificationRequestDto requestDto) throws
		IOException,
		ExecutionException,
		InterruptedException {
		String targetToken = memberService.getMemberById(requestDto.getMemberId()).getFcmToken();

		return fcmWithDataService.sendToUser(targetToken, requestDto).get();
	}
}
