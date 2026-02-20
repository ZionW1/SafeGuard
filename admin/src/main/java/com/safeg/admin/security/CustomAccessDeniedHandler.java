package com.safeg.admin.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler{
    
    @Override
    // public void handle(HttpServletRequest request, 
    //         HttpServletResponse response,
    //         AccessDeniedException accessDeniedException) throws IOException, ServletException {
    //     log.info("접근 권한이 없습니다.");
    //     log.error("접근 권한 거부: ", accessDeniedException);
    //     response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
    //     // if (isDevMode()) {
    //     //     response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    //     //     response.getWriter().write("접근 거부 사유: " + accessDeniedException.getMessage());
    //     //     response.getWriter().flush();
    //     // } else {
    //     //     request.getRequestDispatcher("/error/403").forward(request, response);
    //     // }
    //     request.getRequestDispatcher("/error/403").forward(request, response);
    // }
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("접근 권한이 없습니다.");
        response.getWriter().flush();
    }
}
