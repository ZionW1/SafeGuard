package com.safeg.user.service;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safeg.user.vo.UserCampaignVO;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AligoSmsService {

    @Value("${aligo.api.key}")
    private String apiKey;

    @Value("${aligo.api.userid}")
    private String userId;

    @Value("${aligo.api.sender}")
    private String sender;

    private final String ALIGO_URL = "https://apis.aligo.in/send/";
    private final String KAKAO_URL = "https://kakaoapi.aligo.in/akv10/alimtalk/send/";

    // public boolean sendAuthSms(String phoneNumber, String authCode) throws JsonMappingException, JsonProcessingException {
    //     log.info("AligoSmsService sendAuthSms + " + phoneNumber + ", " + authCode);
    //     RestTemplate restTemplate = new RestTemplate();

    //     Map<String, String> params = new HashMap<>();
    //     params.put("user_id", userId);
    //     params.put("key", apiKey);
    //     params.put("sender", sender);
    //     params.put("receiver", phoneNumber);
    //     params.put("msg", "인증번호는 [" + authCode + "] 입니다. 안전하게 사용하세요.");
    //     params.put("testmode_yn", "Y");  // "Y"로 하면 테스트 모드 (문자 전송 안됨)

    //     // HttpHeaders headers = new HttpHeaders();
    //     // headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    //     // HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

    //     // try {
    //     //     ResponseEntity<String> response = restTemplate.postForEntity(ALIGO_URL, request, String.class);
    //     //     String body = response.getBody();

    //     //     // "result_code":1 이면 성공 (응답은 JSON 형태)
    //     //     return body != null && body.contains("\"result_code\":\"1\"");
    //     // } catch (Exception e) {
    //     //     e.printStackTrace();
    //     //     return false;
    //     // }

    //     MultiValueMap<String, String> params1 = new LinkedMultiValueMap<>();
    //     params1.add("user_id", "your_id");
    //     params1.add("key", "your_key");

    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    //     HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params1, headers);

    //     ResponseEntity<String> response = restTemplate.postForEntity(ALIGO_URL, request, String.class);

    //     if (response.getStatusCode() == HttpStatus.OK) {
    //         ObjectMapper objectMapper = new ObjectMapper();
    //         JsonNode root = objectMapper.readTree(response.getBody());
    //         String resultCode = root.path("result_code").asText();
    //         String message = root.path("message").asText();
    //         // result_code가 "1"이면 성공, 아니면 실패 처리
    //     }
    //     log.info("알리고 전체 응답: " + response.getBody());
    //     String body = response.getBody();
        
    //     return body != null && body.contains("\"result_code\":\"1\"");
    // }

    public boolean sendAuthSms(String phoneNumber, String authCode) throws JsonMappingException, JsonProcessingException {
        log.info("AligoSmsService 전송 시도: {} , 코드: {}", phoneNumber, authCode);
        
        RestTemplate restTemplate = new RestTemplate();

        // 1. 반드시 MultiValueMap을 사용해야 하며, @Value로 받은 변수를 넣어야 합니다.
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("key", apiKey);            // @Value 변수 사용
        body.add("user_id", userId);        // @Value 변수 사용
        body.add("sender", sender);         // @Value 변수 사용
        body.add("receiver", phoneNumber);
        body.add("msg", "[SafeG] 인증번호는 [" + authCode + "] 입니다. 3분 이내에 입력해주세요.");
        
        // 🚨 테스트 완료 후 문자가 실제로 오게 하려면 이 줄을 주석 처리하거나 "N"으로 바꾸세요!
        // body.add("testmode_yn", "Y"); 

        // 2. 헤더 설정 (Form Data 형식)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 3. 요청 객체 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            // 4. 전송
            ResponseEntity<String> response = restTemplate.postForEntity(ALIGO_URL, request, String.class);
            log.info("알리고 전체 응답: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                
                // 알리고는 성공 시 result_code가 정수 1 또는 문자열 "1"로 옵니다.
                String resultCode = root.path("result_code").asText();
                return "1".equals(resultCode);
            }
        } catch (Exception e) {
            log.error("알리고 통신 중 에러 발생: ", e);
        }
        
        return false;
    }

    public boolean sendSmsApply(Long campaignId, List<UserCampaignVO> userCampaignVO) {
        // TODO Auto-generated method stub
        log.info("AligoSmsService 캠페인 : {} , 날짜 : {}", campaignId, userCampaignVO);
        
        RestTemplate restTemplate = new RestTemplate();

        // 1. 반드시 MultiValueMap을 사용해야 하며, @Value로 받은 변수를 넣어야 합니다.
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("key", apiKey);            // @Value 변수 사용
        body.add("user_id", userId);        // @Value 변수 사용
        body.add("sender", sender);         // @Value 변수 사용
        body.add("receiver", "01038966824");
        body.add("msg", "http://행집.com/apply/userCampaignApply/" + campaignId + "/" + userCampaignVO.get(0).getApplyDate());
        
        // 🚨 테스트 완료 후 문자가 실제로 오게 하려면 이 줄을 주석 처리하거나 "N"으로 바꾸세요!
        // body.add("testmode_yn", "Y"); 

        // 2. 헤더 설정 (Form Data 형식)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 3. 요청 객체 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            // 4. 전송
            ResponseEntity<String> response = restTemplate.postForEntity(ALIGO_URL, request, String.class);
            log.info("알리고 전체 응답: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                
                // 알리고는 성공 시 result_code가 정수 1 또는 문자열 "1"로 옵니다.
                String resultCode = root.path("result_code").asText();
                return "1".equals(resultCode);
            }
        } catch (Exception e) {
            log.error("알리고 통신 중 에러 발생: ", e);
        }
        return false;
    }

    public boolean sendEventNotice(String receiver, String eventName, String count, String period, String link) {
        RestTemplate restTemplate = new RestTemplate();
    
        // 1. 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    
        // 2. 파라미터 설정 (알림톡 전용 키값 사용)
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("apikey", apiKey);      // SMS는 'key'였으나 알림톡은 'apikey'
        params.add("userid", userId);      // SMS는 'user_id'였으나 알림톡은 'userid'
        params.add("senderkey", "알리고에서_발급받은_발신키"); // 발신번호가 아니라 '발신키'입니다!
        params.add("tpl_code", "UG_4123"); 
        params.add("sender", sender);      // 등록된 발신번호
        params.add("receiver_1", receiver);
        params.add("subject_1", "행사 명단 발표 안내");
        
        // 템플릿과 100% 일치해야 함 (공백, 줄바꿈 주의)
        String message = String.format("[SafeGuard] %s 명단이 발표되었습니다!\n\n" +
                "[확정인원] : %s명\n" +
                "[행사기간] : %s\n\n" +
                "[당첨자 명단] :\n%s\n\n" +
                "※ 꼭! 알고계세요!\n" +
                "- 모집인원 출석 시 모바일 명단을 꼭! 확인하세요!\n" +
                "- 명단의 '출석체크' 버튼을 상황에 맞게 반드시 눌러주세요\n\n" +
                "[담당자 연결] : 010-XXXX-XXXX", eventName, count, period, link);
        
        params.add("message_1", message);
        
        // 버튼이 있다면 추가 (템플릿 신청 시 버튼을 넣었다면 필수)
        // params.add("button_1", "{\"button\":[{\"name\":\"명단 확인하기\",\"linkType\":\"WL\",\"linkMo\":\"" + link + "\",\"linkPc\":\"" + link + "\"}]}");
    
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
    
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(KAKAO_URL, request, String.class);
            log.info("알림톡 전송 응답: {}", response.getBody());
    
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = new ObjectMapper().readTree(response.getBody());
                // 알림톡도 성공 시 result_code는 "1"
                return "1".equals(root.path("result_code").asText());
            }
        } catch (Exception e) {
            log.error("알림톡 전송 중 오류 발생", e);
        }
        return false;
    }
    
    @Async("taskExecutor") // 비동기 처리는 여기서!
    public CompletableFuture<Boolean> sendEventNoticeAsync(String receiver, String eventName, String count, String period, String link) {
        try {
            // 실제 발송 로직 호출
            boolean result = sendEventNotice(receiver, eventName, count, period, link);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("비동기 전송 중 에러: ", e);
            return CompletableFuture.completedFuture(false);
        }
    }
}