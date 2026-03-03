package com.safeg.user.security;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler{
    private static final Logger logger = LoggerFactory.getLogger(LoginFailureHandler.class); 

    /**
     * 로그인 실패 시 호출되는 메소드
     * 로그인 실패 횟수 체크, 보안 처리
     * (로그인 실패 5회 누적 시, 본인인증 등 요청)
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        // 로그인 실패 횟수 조회 & 증가 (예: redis, db 등)
        String username = request.getParameter("userId");
        // int failCount = loginFailService.incrementFailCount(username);

        String errorMessage = "아이디나 비밀번호가 맞지 않습니다.";
        if (exception instanceof UsernameNotFoundException) {
            errorMessage = "존재하지 않는 아이디입니다.";
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "아이디나 비밀번호가 맞지 않습니다.";
        } 
        else if (exception instanceof LockedException) {
            errorMessage = "계정이 잠겨 있습니다. 관리자에게 문의하세요.";
        } else if (exception instanceof DisabledException) {
            errorMessage = "계정이 비활성화 되어 있습니다.";
        } 
        // else if (exception instanceof AccountExpiredException) {
        //     errorMessage = "계정이 만료되었습니다.";
        // } 
        else if (exception instanceof CredentialsExpiredException) {
            errorMessage = "비밀번호가 만료되어 변경이 필요합니다.";
        } else if (exception instanceof AuthenticationServiceException) {
            errorMessage = "인증 서비스에 문제가 발생했습니다. 잠시 후 다시 시도하세요.";
        }
        logger.warn("로그인 실패 - 아이디: {}, 사유: {}", username, exception.getMessage());
        logger.warn("로그인 실패 - 아이디: {}, 사유: {}", username, errorMessage);

        // if (failCount >= 5) {
        //     // 5회 이상 실패하면 추가 처리(예: 본인 인증 요청)
        //     errorMessage = "로그인 실패 5회 누적, 본인 인증이 필요합니다.";
        // }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":\"" + errorMessage + "\"}");
    }
    
}
