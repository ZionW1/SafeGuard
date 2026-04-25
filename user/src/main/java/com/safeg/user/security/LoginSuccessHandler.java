package com.safeg.user.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.safeg.user.vo.CustomUser;
import com.safeg.user.vo.UserVO;
import com.safeg.user.vo.Users;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 로그인 성공 처리 이벤트 핸들러
 * 
 */
@Slf4j
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    
    /**
     * 로그인 성공 시 호출되는 메소드
     * 🍪 아이디 저장 쿠키 생성
     * 🔐 로그인 후 이전 페이지로 리다이렉트
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request
                                , HttpServletResponse response
                                , Authentication authentication) throws ServletException, IOException {
        // 아이디 저장
        String rememberId = request.getParameter("remember-id"); // ✅ 아이디 저장 여부
        String username = request.getParameter("userId");            // 👩‍💼 아이디

        // 아이디 저장 체크 ✅
        if( rememberId != null && rememberId.equals("on") ) {
            Cookie cookie = new Cookie("remember-id", username);  // 쿠키에 아이디 등록
            cookie.setMaxAge(60 * 60 * 24 * 7);                        // 유효기간 : 7일
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        // 아이디 저장 체크 ❌
        else {
            Cookie cookie = new Cookie("remember-id", username);  // 쿠키에 아이디 등록
            cookie.setMaxAge(0);                                // 유효기간 : 0 (삭제)
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        // 인증된 사용자 정보

        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        UserVO user = customUser.getUserVo();

        // response.sendRedirect("/"); // 로그인 후 경로
        super.onAuthenticationSuccess(request, response, authentication);
    }

}
