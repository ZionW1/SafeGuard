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


        log.info("로그인 성공...");

        // 아이디 저장
        String rememberId = request.getParameter("remember-id"); // ✅ 아이디 저장 여부
        String username = request.getParameter("userId");            // 👩‍💼 아이디
        log.info("rememberId  : " + rememberId);
        log.info("username  : " + username);

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
        log.info("customUser : " + customUser);
        UserVO user = customUser.getUserVo();
        log.info("user : " + user);

        log.info("아이디 : " + user.getUserId());
        log.info("비밀번호 : " + user.getPassword());
        log.info("권한 : " + user.getAuthList().get(0).getAuth());
        log.info("폰 번호 : " + user.getPhoneNum());

        // 사용자 권한 가져오기
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // log.info(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        // ⭐️ 역할에 따른 리다이렉트 로직 구현 ⭐️
        // if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
        //     // ROLE_ADMIN 권한이 있다면 /admin/dashboard 로 이동
        //     response.sendRedirect("/admin/campaign01");
        // } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
        //     // ROLE_USER 권한이 있다면 /user/dashboard 로 이동
        //     response.sendRedirect("/user/dashboard");
        // } else {
        //     // 그 외 권한은 기본 페이지로 이동 (fallback)
        //     response.sendRedirect("/main");
        // }

        if (authorities != null) {
            if (authorities.stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
                response.sendRedirect("/admin/campaign01");
                return;
            } else if (authorities.stream().anyMatch(a -> "ROLE_USER".equals(a.getAuthority()))) {
                response.sendRedirect("/user/dashboard");
                return;
            }
        }
        response.sendRedirect("/main");

        // response.sendRedirect("/"); // 로그인 후 경로
        // super.onAuthenticationSuccess(request, response, authentication);
    }

}
