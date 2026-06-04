package com.safeg.user.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
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
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request
                                , HttpServletResponse response
                                , Authentication authentication) throws ServletException, IOException {
        
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

        // 3. 페이지 리다이렉트 처리 (중복 방지)
        setDefaultTargetUrl("/"); // 기본 이동 페이지 지정
        
        // 💡 세션 대기실에서 아까 컨트롤러가 넣어둔 목적지 주소를 꺼냅니다.
    String redirectUrl = (String) request.getSession().getAttribute("prevPage");
    log.info("🔓 로그인 성공! 세션에서 꺼낸 목적지 주소: {}", redirectUrl);

    if (redirectUrl != null && !redirectUrl.isEmpty()) {
        // 사용한 세션 주소는 깔끔하게 지워주고
        request.getSession().removeAttribute("prevPage");
        // 💡 에러 없이 정상적으로 리뷰 등록 페이지로 리다이렉트 시킵니다!
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    } else {
        // 목적지가 없었다면 안전하게 메인 페이지로 이동
        getRedirectStrategy().sendRedirect(request, response, "/");
    }
    }
}