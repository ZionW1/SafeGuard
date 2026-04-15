package com.safeg.admin.security;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.safeg.admin.vo.CustomUser;
import com.safeg.admin.vo.UserVO;
import com.safeg.admin.vo.Users;

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
        log.info("username : " + username);
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

        // 사용자 권한 가져오기
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        if (authorities != null) {
            // 1. 관리자 권한 확인
            if (authorities.stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
                log.info("관리자 로그인 성공: {}", username);
                
                // 2. 특정 아이디(semiAdmin)인 경우
                if ("semiAdmin".equals(username)) {
                    log.info("semiAdmin 전용 페이지로 이동");
                    response.sendRedirect("/admin/campaign09");
                } 
                // 3. 일반 관리자인 경우
                else {
                    log.info("일반 관리자 페이지로 이동");
                    response.sendRedirect("/admin/campaign09");
                }
                return; // 리다이렉트 후 종료
            } 
            
            // 4. 일반 사용자 권한 확인
            else if (authorities.stream().anyMatch(a -> "ROLE_USER".equals(a.getAuthority()))) {
                log.info("일반 사용자 메인으로 이동");
                response.sendRedirect("/");
                return;
            }
        }
    }
}
