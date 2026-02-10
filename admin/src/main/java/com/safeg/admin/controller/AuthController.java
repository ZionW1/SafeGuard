package com.safeg.admin.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.safeg.admin.service.AuthService;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

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
    public CompletableFuture<ResponseEntity<String>> sendCode(@RequestParam("phoneNumber") String phoneNumber) {
        log.info("sendCode " + phoneNumber);
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