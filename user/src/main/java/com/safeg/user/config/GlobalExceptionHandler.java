package com.safeg.user.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    // 💡 NullPointerException이 발생하면 이 함수가 가로챕니다.
    @ExceptionHandler(NullPointerException.class)
    public String handleNullPointerException(NullPointerException ex) {
        
        // 에러 로그 출력 (어디서 터졌는지 확인용)
        System.err.println("세션 만료 또는 유저 정보 누락으로 인한 NPE 발생: " + ex.getMessage());
        
        // 로그인 화면으로 강제 이동시킵니다.
        return "redirect:/"; // 로그인 페이지 URL로 변경해주세요.
    }
}
