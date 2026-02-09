package com.safeg.admin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safeg.admin.vo.SmsVO;

import lombok.RequiredArgsConstructor;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService{

    // properties 파일에서 값을 읽어옵니다.
    @Value("${aligo.api.key}")
    private String apiKey;

    @Value("${aligo.api.userid}")
    private String userId;

    @Value("${aligo.api.sender}")
    private String sender;

    private final SmsRepository smsLogRepository;

    @Async("smsExecutor") // 위에서 만든 빈 이름을 지정합니다.
    @Override
    public void sendTestSms(String receiver, String message) throws Exception{
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 파라미터 구성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("key", apiKey);
        body.add("userid", userId);
        body.add("sender", sender);
        body.add("receiver", receiver);
        body.add("msg", message);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        // 발송 및 응답 확인
        String response = restTemplate.postForObject("https://apis.aligo.in/send/", entity, String.class);
        System.out.println("결과: " + response);
    }

    @Retryable(
        value = { RuntimeException.class }, 
        maxAttempts = 3,              // 최대 3번 시도
        backoff = @Backoff(delay = 2000) // 실패 시 2초 쉬고 다시
    )
    @Override
    public String sendSms(String receiver, String message) {
        // 1. 알리고 API 호출 (기존 코드)
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 파라미터 구성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("key", apiKey);
        body.add("userid", userId);
        body.add("sender", sender);
        body.add("receiver", receiver);
        body.add("msg", message);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        // 발송 및 응답 확인
        String response = restTemplate.postForObject("https://apis.aligo.in/send/", entity, String.class);
        System.out.println("결과: " + response);

        // String response = restTemplate.postForObject(URL, entity, String.class);
        
        // JSON 파싱 (Jackson 등 활용)
        // ... (생략) ...
        ObjectMapper mapper = new ObjectMapper();

        String resultCode = "";
        String msgId = "";

        try {
            // 2. String 응답을 JSON 객체로 읽기
            JsonNode root = mapper.readTree(response);
            
            // 3. 필요한 필드만 추출
            resultCode = root.path("result_code").asText();
            msgId = root.path("msg_id").asText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 2. DB에 결과 저장
        SmsVO log = SmsVO.builder()
                .receiverNum(receiver) // 메서드 파라미터 이름 확인
                .content(message)
                .resultCode(resultCode) // 위에서 파싱한 값
                .msgId(msgId)           // 위에서 파싱한 값
                .build();
        smsLogRepository.save(log);

        return response;
    }

    @Recover // 3번 다 실패했을 때 실행되는 메서드
    public String recover(RuntimeException e, String receiver, String message) {
        System.out.println("결국 발송 실패: " + receiver);
        return "{\"result_code\":\"-99\", \"message\":\"최종 실패\"}";
    }
}