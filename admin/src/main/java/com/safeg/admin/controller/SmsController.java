package com.safeg.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safeg.admin.service.SmsService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SmsController {

    @Autowired
    private SmsService smsService;

    // 테스트를 위해 브라우저에서 바로 호출 가능하게 GetMapping 사용
    // 예: http://localhost:8080/send-sms?to=01045558079
    @GetMapping("/send-sms")
    public String sendSms(@RequestParam("to") String to) throws Exception {
        String message = "알리고 API를 통한 Spring 비동기 발송 테스트입니다.";
        
        // 서비스의 비동기 메서드 호출
        smsService.sendTestSms(to, message);
        log.info("문자 발송 요청이 완료되었습니다");
        return "문자 발송 요청이 완료되었습니다! (백그라운드에서 처리 중)";
    }
}