package com.safeg.user.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LogControlInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // HTML을 반환하는 정상적인 응답일 때만 실행
        if (modelAndView != null && !modelAndView.getViewName().startsWith("redirect:")) {
            
            // 실제 운영 환경 여부 (나중에 환경 변수로 관리하면 더 좋습니다)
            // true이면 console.log() 안보임.
            boolean isProd = true; 

            if (isProd) {
                String script = "<script>" +
                                "(function() {" +
                                "  console.log = function() {};" +
                                "  console.info = function() {};" +
                                "  console.warn = function() {};" +
                                "  console.debug = function() {};" +
                                "})();" +
                                "</script>";
                
                // HTML 템플릿으로 스크립트 전달
                modelAndView.addObject("logControlScript", script);
            }
        }
    }
}