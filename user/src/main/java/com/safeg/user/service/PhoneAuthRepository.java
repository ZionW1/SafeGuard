package com.safeg.user.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.safeg.user.vo.PhoneAuth;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PhoneAuthRepository extends JpaRepository<PhoneAuth, Long> {
    Optional<PhoneAuth> findByPhoneNumberAndAuthCodeAndUsedFalseAndExpiresAtAfter(
        String phoneNumber, String authCode, LocalDateTime now);
}