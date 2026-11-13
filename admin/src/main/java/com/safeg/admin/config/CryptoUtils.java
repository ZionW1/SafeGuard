package com.safeg.admin.config;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class CryptoUtils {
    private static final String ALGORITHM = "AES";
    private static final String KEY = "1234567890123456"; // 16자 키 (예시)

    public static String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");

        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // IV와 암호문을 함께 저장 (Base64 인코딩)
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return null;
        }

        try {
            byte[] combined = Base64.getDecoder().decode(encryptedText);

            // 💡 핵심 안전장치: 16바이트(IV 크기)보다 짧으면 암호화된 데이터가 아니므로 평문으로 판단하고 그대로 리턴
            if (combined.length <= 16) {
                return encryptedText;
            }

            byte[] iv = new byte[16];
            byte[] cipherText = new byte[combined.length - 16];

            System.arraycopy(combined, 0, iv, 0, 16);
            System.arraycopy(combined, 16, cipherText, 0, cipherText.length);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decrypted = cipher.doFinal(cipherText);
            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            // 에러가 터져서 서버가 멈추는 대신, 원본 문자열을 반환하거나 로그를 찍고 넘어갑니다.
            log.warn("복호화 스킵 (평문 데이터일 가능성): " + encryptedText);
            return encryptedText;
        }
    }
}
