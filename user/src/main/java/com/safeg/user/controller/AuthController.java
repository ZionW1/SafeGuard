package com.safeg.user.controller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safeg.user.service.AuthService;
import com.safeg.user.service.UserService;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Autowired
    UserService userService;

    @Autowired
    private ObjectMapper objectMapper; // 2. 의존성 주입
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    // 비동기 호출 메서드
    // @PostMapping("/sendCode")
    // public CompletableFuture<ResponseEntity<String>> sendCode(@RequestParam("phoneNumber") String phoneNumber) {
    //     log.info("sendCode " + phoneNumber);
    //     return authService.sendAuthCode(phoneNumber)
    //         .thenApply(success -> {
    //             if (success) {
    //                 return ResponseEntity.ok("인증번호 발송 성공");
    //             } else {
    //                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("문자 발송 실패");
    //             }
    //         }
    //     );
    // }

    @PostMapping("/sendCode")
    public CompletableFuture<ResponseEntity<String>> sendCode(@RequestParam("phoneNumber") String phoneNumber) throws Exception {
        log.info("sendCode " + phoneNumber);
        boolean phoneDuplicate = userService.phoneDuplicate(phoneNumber);
        if (phoneDuplicate) {
            Map<String, Object> response = Map.of(
                "success", phoneDuplicate,
                "message", "이미 가입된 휴대폰 번호입니다."
            );
            
            // Map 객체를 "{\"success\":false, ...}" 형태의 문자열로 변환
            String jsonResponse = objectMapper.writeValueAsString(response);
            
            return CompletableFuture.completedFuture(
                ResponseEntity.badRequest().body(jsonResponse)
            );
        }
        // }
        // if (userService.phoneDuplicate(phoneNumber)) {
        //     return ResponseEntity.badRequest().body(Map.of(
        //         "success", false, 
        //         "message", "이미 가입된 휴대폰 번호입니다."
        //     ));
        // }
        String publicIp = new RestTemplate().getForObject("http://checkip.amazonaws.com/", String.class);
        log.info("현재 서버의 실제 외부에 노출되는 IP: " + publicIp.trim());
        return authService.sendAuthCode(phoneNumber)
            .thenApply(success -> {
                if (success) {
                    return ResponseEntity.ok("인증번호 발송 성공");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("문자 발송 실패");
                }
            }
        );
    }

    @PostMapping("/verifyCode")
    public String verifyCode(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("inputCode") String code) throws Exception {
        if (authService.verifyAuthCode(phoneNumber, code)) {
            return "인증 성공";
        } else {
            return "인증 실패 또는 만료된 코드입니다.";
        }
    }
}