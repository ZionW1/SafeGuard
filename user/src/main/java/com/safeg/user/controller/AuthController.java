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
import com.safeg.user.util.EncryptionUtil;

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
        String hashedPhone = EncryptionUtil.hash(phoneNumber);

        boolean phoneDuplicate = userService.phoneDuplicate(hashedPhone, null);
        if (phoneDuplicate) {
            Map<String, Object> response = Map.of(
            "success", false, 
            "duplicate", true, // 차라리 이 필드를 추가하는 게 명확합니다.
            "message", "이미 가입된 휴대폰 번호입니다."
            );
            
            // Map 객체를 "{\"success\":false, ...}" 형태의 문자열로 변환
            String jsonResponse = objectMapper.writeValueAsString(response);
            
            return CompletableFuture.completedFuture(
                ResponseEntity.badRequest().body(jsonResponse)
            );
        }

        String publicIp = new RestTemplate().getForObject("http://checkip.amazonaws.com/", String.class);
        log.info("현재 서버의 실제 외부에 노출되는 IP: " + publicIp.trim());
        return authService.sendAuthCode(phoneNumber)
    .thenApply(success -> {
        if (success) {
            // 1. 성공 시에도 JSON 문자열을 만들어 리턴합니다.
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "인증번호 발송 성공"
            );
            try {
                return ResponseEntity.ok(objectMapper.writeValueAsString(response));
            } catch (JsonProcessingException e) {
                return ResponseEntity.status(500).body("JSON 변환 에러");
            }
        } else {
            // 2. 실패 시에도 가급적 JSON 구조를 맞춰주는 것이 좋습니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(  "{\"success\":false, \"message\":\"문자 발송 실패\"}");
        }
    });
    }

    @PostMapping("/sendCodeFindId")
    public CompletableFuture<ResponseEntity<String>> sendCodeFindId(@RequestParam("phoneNumber") String phoneNumber) throws Exception {
        log.info("sendCode " + phoneNumber);

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