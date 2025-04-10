package com.arizona.lipit.global.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.arizona.lipit.global.exception.CustomException;
import com.arizona.lipit.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

	public String getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
		}

		Object principalObj = authentication.getPrincipal();
		if (principalObj instanceof UserDetails userDetails) {
			return userDetails.getUsername(); // 이메일만 반환
		}

		throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
	}
}
