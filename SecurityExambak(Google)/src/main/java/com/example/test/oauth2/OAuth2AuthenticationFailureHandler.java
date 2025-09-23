package com.example.test.oauth2;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler{

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		
		String errMsg = "소셜 로그인 실패!";
		
		// AuthenticationException : 시큐리티에서 정의한 인증 과정 중에 발생하는 모든 예외의 기본 킬래스
		// 현재 예외의 원인이 된 그 예외 자체를 리턴해주는 메서드
        if (exception.getCause() != null) {
        	errMsg += " 원인: " + exception.getCause().getMessage();
        }
        request.getSession().setAttribute("socialErrorMessage", errMsg);

        // 로그인 페이지로 리다이렉트
        response.sendRedirect("/login?social_error=true");
		
	}

	
}
