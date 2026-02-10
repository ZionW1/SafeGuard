package com.safeg.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.safeg.admin.config.AsyncConfig;
import com.safeg.admin.controller.aController;
import com.safeg.admin.vo.PhoneAuth;

import lombok.extern.slf4j.Slf4j;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService{

    private final PhoneAuthRepository phoneAuthRepository;
    private final AligoSmsService smsService;

    public AuthServiceImpl(PhoneAuthRepository phoneAuthRepository, AligoSmsService smsService) {
        this.phoneAuthRepository = phoneAuthRepository;
        this.smsService = smsService;
    }


    /**
     * 문자 전송 실제 호출 메서드 - 재시도 가능하도록 분리
     * 예외 발생 시 재시도 동작함
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     */
    @Transactional
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000)
    )
    public void sendSmsWithRetry(String phoneNumber, String authCode) throws JsonMappingException, JsonProcessingException {
        boolean sent = smsService.sendAuthSms(phoneNumber, authCode);
        if (!sent) {
            throw new RuntimeException("문자 전송 실패 - 재시도 필요");
        }
    }

    // 인증번호 생성 및 SMS 발송, DB 저장
    @Async
    @Transactional
    public CompletableFuture<Boolean> sendAuthCode(String phoneNumber) {
        log.info("인증 프로세스 시작: " + phoneNumber);
        String authCode = generateAuthCode();

        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. 문자 전송 시도
                sendSmsWithRetry(phoneNumber, authCode);
                
                // 2. 전송 성공 시에만 DB 저장 (유효시간 3분)
                PhoneAuth phoneAuth = new PhoneAuth();
                phoneAuth.setPhoneNumber(phoneNumber);
                phoneAuth.setAuthCode(authCode);
                phoneAuth.setExpiresAt(LocalDateTime.now().plusMinutes(3));
                phoneAuthRepository.save(phoneAuth);
                
                log.info("인증번호 발송 및 DB 저장 완료: " + phoneNumber);
                return true;
                
            } catch (Exception e) {
                // 여기서 찍히는 e의 내용을 봐야 왜 실패했는지 알 수 있습니다!
                log.error("인증 프로세스 중 치명적 에러 발생: ", e); 
                return false;
            }
        }, AsyncConfig.smsExecutor()); // 아까 설정한 smsExecutor를 꼭 넣어주세요!
    }

    // 인증번호 검증
    @Transactional
    public boolean verifyAuthCode(String phoneNumber, String inputCode) {
        LocalDateTime now = LocalDateTime.now();
        var maybeAuth = phoneAuthRepository.findByPhoneNumberAndAuthCodeAndUsedFalseAndExpiresAtAfter(phoneNumber, inputCode, now);
        if (maybeAuth.isPresent()) {
            PhoneAuth auth = maybeAuth.get();
            auth.setUsed(true);
            phoneAuthRepository.save(auth);
            return true;
        }
        return false;
    }

    private String generateAuthCode() {
        int code = 100000 + (int)(Math.random() * 900000);
        return String.valueOf(code);
    }
}

