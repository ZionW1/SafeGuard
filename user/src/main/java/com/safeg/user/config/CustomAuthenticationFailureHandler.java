package com.safeg.user.config;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String errorMessage = "아이디나 비밀번호가 맞지 않습니다.";

        if (exception instanceof UsernameNotFoundException) {
            errorMessage = "존재하지 않는 아이디입니다.";
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "비밀번호가 틀렸습니다.";
        }

        // JSON 응답
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
    }
}