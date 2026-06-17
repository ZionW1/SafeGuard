package com.safeg.user.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import com.safeg.user.vo.CustomUser;
import com.safeg.user.vo.UserVO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 로그인 성공 처리 이벤트 핸들러
 */
@Slf4j
@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request
                                , HttpServletResponse response
                                , Authentication authentication) throws ServletException, IOException {
        clearAuthenticationAttributes(request);
        // 1. 아이디 저장 기능 (쿠키)
        String rememberId = request.getParameter("remember-id"); // 아이디 저장 여부
        String username = request.getParameter("userId");        // 아이디

        if( rememberId != null && rememberId.equals("on") ) {
            Cookie cookie = new Cookie("remember-id", username);
            cookie.setMaxAge(60 * 60 * 24 * 7); // 7일 유효
            cookie.setPath("/");
            response.addCookie(cookie);
        } else {
            Cookie cookie = new Cookie("remember-id", username);
            cookie.setMaxAge(0); // 즉시 삭제
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        // 2. 인증된 사용자 정보 활용 (로그 찍기용 등으로 활용 가능)
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        UserVO user = customUser.getUserVo();
        log.info("로그인 성공 유저: {}", user.getUserId());

        log.info("🔓 [로그인 성공 핸들러 작동] 유저: {}", authentication.getName());

        String redirectUrl = (String) request.getSession().getAttribute("prevPage");
        log.info("🎯 세션에서 꺼낸 목적지 주소 확인: {}", redirectUrl);
    
        // 목적지가 없다면 기본 메인 페이지('/') 설정
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            redirectUrl = "/";
        } else {
            request.getSession().removeAttribute("prevPage"); // 사용 후 세션 삭제
        }
    
        // 💡 [핵심 수정] fetch 요청에 일반 html이나 문자로 응답을 주기 위해 헤더 설정
        response.setContentType("text/plain;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        // 🚀 프론트엔드 자바스크립트에게 가야 할 최종 목적지 주소를 글자(Body)로 넘겨줍니다.
        response.getWriter().write(redirectUrl);
        response.getWriter().flush();
    }
}