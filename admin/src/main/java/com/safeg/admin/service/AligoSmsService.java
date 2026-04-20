package com.safeg.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;

import java.util.HashMap;
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
    RestTemplate restTemplate = new RestTemplate();

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

    public boolean sendSmsApply(String phoneNumber, String authCode) {
        // TODO Auto-generated method stub
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

    public boolean sendEventNotice(String receiver, String eventName, String count, String period, String link) {
        // 1. 템플릿 본문 구성 (승인된 내용과 글자 하나 안 틀리고 똑같아야 함)
        String url = "https://alimtalk-api.aligo.in/akv10/alimtalk/send/";
    
        // 🚨 템플릿 문구 구성 (주의: 승인받은 문구와 띄어쓰기, 줄바꿈이 완벽히 일치해야 함)
        String message = String.format(
            "[%s] %s 모집 공고 등록 안내\n\n" +
            "안녕하세요, 요청하신 %s의 모집 공고가 시스템에 등록되어 안내드립니다.\n\n" +
            "아래 집행 내역을 확인해 주시기 바랍니다.\n" +
            "이상이 없다면 설정된 모집 기간부터 모집이 시작되며, 시작 이후에는 내용 변경이 어려우니 일정, 인원, 안내사항을 반드시 검토해 주세요.\n\n" +
            "■ 집행 내역 안내\n\n" +
            "[모집인원] : %s명\n\n" +
            "[모집기간] : %s\n\n" +
            "[행사기간] : %s\n\n" +
            "[모집글 확인하기]\n" +
            "%s\n\n" +
            "[담당자 문의]\n" +
            "%s",
            eventName, eventName, count, link
        );
    
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("apikey", "YOUR_API_KEY");        // 알리고 API 키
        params.add("userid", "YOUR_ID");             // 알리고 아이디
        params.add("senderkey", "YOUR_SENDER_KEY");  // 발신 프로필 키
        params.add("tpl_code", "YOUR_TPL_CODE");     // 승인된 템플릿 코드 (예: TF_0001)
        params.add("sender", sender);         // 발신자 번호
        params.add("receiver_1", receiver);          // 수신자 번호
        params.add("subject_1", "모집 공고 등록 안내"); // 알리고 관리자용 제목
        params.add("message_1", message);            // 치환 완료된 전체 문구
    
        // 버튼이 있다면 추가 (없으면 생략)
        // params.add("button_1", "{\"button\":[{\"name\":\"확인하기\",\"linkType\":\"WL\",\"linkMo\":\""+link+"\",\"linkPc\":\""+link+"\"}]}");
    
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, params, Map.class);
            Map<String, Object> body = response.getBody();
            log.info("알림톡 발송 결과: {}", body);
            return "1".equals(String.valueOf(body.get("result_code")));
        } catch (Exception e) {
            log.error("알림톡 전송 중 시스템 에러 발생", e);
            return false;
        }
    }
    

    @Async("taskExecutor")
    public CompletableFuture<Boolean> registrationAsync(
            String receiver, String type, String eventName, int count, String appPeriod, String eventPeriod, String link, String leaderPhone) {
        try {
            // 실제 알림톡 발송 로직 호출
            boolean result = registrationTalk(receiver, type, eventName, count, appPeriod, eventPeriod, link, leaderPhone);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("비동기 알림톡 전송 중 에러 발생: ", e);
            return CompletableFuture.completedFuture(false);
        }
    }

    private boolean registrationTalk(String receiver, String type, String eventName, int count, String appPeriod, String eventPeriod, String link, String leaderPhone) {
        log.info("sendAlimtalk : receiver : " + receiver + " type : " + type + " eventName : " + eventName + " appPeriod : " + appPeriod + " eventPeriod : " + eventPeriod + " link : " + link + "leaderPhone : " +leaderPhone);
        String url = "https://kakaoapi.aligo.in/akv10/alimtalk/send/";
        String buttonJson = "{\"button\": [{\"name\": \"채널 추가\", \"linkType\": \"AC\"}]}";

    
        String conutString = String.valueOf(count);
        // 2. 새로운 템플릿 문구 구성
        String template = "[%s] %s 모집 공고 등록 안내\n" +
                "\n" +
                "안녕하세요, 요청하신 %s의 모집 공고가 시스템에 등록되어 안내드립니다.\n" +
                "\n" +
                "아래 집행 내역을 확인해 주시기 바랍니다.\n" +
                "이상이 없다면 설정된 모집 기간부터 모집이 시작되며, 시작 이후에는 내용 변경이 어려우니 일정, 인원, 안내사항을 반드시 검토해 주세요.\n" +
                "\n" +
                "■ 집행 내역 안내\n" +
                "\n" +
                "[모집인원] : %s명\n" + // %d 대신 %s를 쓰면 타입 에러(IllegalFormatConversionException)를 방지할 수 있습니다.
                "\n" +
                "[모집기간] : %s\n" +
                "\n" +
                "[행사기간] : %s\n" +
                "\n" +
                "[모집글 확인하기]\n" +
                "%s\n" +
                "\n" +
                "[담당자 문의]\n" +
                "%s"; // 템플릿에 포함된 텍스트

        // 3. 메시지 생성
        String message = String.format(template, 
            type,          // [%s] 행사구분
            eventName,     // %s 행사명 (제목)
            eventName,     // %s 행사명 (본문)
            count,         // %s 모집인원
            appPeriod,     // %s 모집기간
            eventPeriod,   // %s 행사기간
            link,          // %s 모집링크
            leaderPhone     // %s 담당자연락처
        );
    
        log.info("apiKey : " + apiKey + " userId : " + userId + " sender : " + sender + " message : " + message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("apikey", apiKey);
        params.add("userid", userId);
        params.add("senderkey", "ff7f69c328188f85aa26867582ce55a57358b4a3");
        params.add("tpl_code", "UG_4122"); // 예: TF_0001
        params.add("sender", sender); 
        params.add("receiver_1", receiver);
        params.add("subject_1", "모집 공고 등록 안내");
        params.add("message_1", message);
        params.add("button_1", buttonJson);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);


        try {
            // RestTemplate 등을 이용해 POST 요청 (이미 bean 등록되어 있다고 가정)
            // ResponseEntity<Map> response = restTemplate.postForEntity(url, params, Map.class);
            // String resultCode = String.valueOf(response.getBody().get("result_code"));
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            log.info("알리고 전체 응답: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                
                // 알리고는 성공 시 result_code가 정수 1 또는 문자열 "1"로 옵니다.
                String resultCode = root.path("result_code").asText();
                return "1".equals(resultCode);
            }
            
            // return "1".equals(resultCode);
        } catch (Exception e) {
            log.error("알림톡 API 통신 실패", e);
            return false;
        }
        return false;
    }

    @Async("taskExecutor")
    public CompletableFuture<Boolean> rosterCheckAsync(
            String receiver, String type, String eventName, int count, String appPeriod, String eventPeriod, String link, String companyPh) {
        log.info("rosterCheckAsync");
        try {
            // 실제 알림톡 발송 로직 호출
            boolean result = rosterChecktalk(receiver, type, eventName, count, appPeriod, eventPeriod, link, companyPh);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("비동기 알림톡 전송 중 에러 발생: ", e);
            return CompletableFuture.completedFuture(false);
        }
    }

    private boolean rosterChecktalk(String receiver, String type, String eventName, int count, String appPeriod, String eventPeriod, String link, String companyPh) {
        log.info("sendAlimtalk : receiver : " + receiver + " type : " + type + " eventName : " + eventName + " appPeriod : " + appPeriod + " eventPeriod : " + eventPeriod + " link : " + link + " companyPh : " + companyPh);
        String url = "https://kakaoapi.aligo.in/akv10/alimtalk/send/";
    

        // 예시: 템플릿을 통째로 복사해서 가져온 경우
        String template = "[%s] 운영 인솔자 지정 및 업무 안내\n" +
        "\n" +
        "안녕하세요. 관리자에 의해 [%s]의 운영 인솔자로 지정되셨기에 관련 명단과 업무 내용을 안내드립니다.\n" +
        "\n" +
        "확정된 명단을 확인하시어 배정된 행사의 현장 운영에 참고해 주시기 바랍니다.\n" +
        "\n" +
        "1. 행사 확정 정보\n" +
        "\n" +
        "확정인원 : %d명\n" +
        "\n" +
        "행사기간 : %s\n" +
        "\n" +
        "[당첨자 명단 확인]\n" +
        "%s\n" +
        "\n" +
        "※ 인솔자 업무 안내\n" +
        "\n" +
        "현장 인원 출석 시 모바일 명단을 반드시 대조해 주세요.\n" +
        "\n" +
        "명단의 '출석체크' 버튼을 현장 상황에 맞게 꼭 눌러주시기 바랍니다.\n" +
        "\n" +
        "[담당자 문의]\n" +
        "%s";

        String message = String.format(template, type, eventName, count, eventPeriod, link, companyPh);
        String buttonJson = "{\"button\": [{\"name\": \"채널 추가\", \"linkType\": \"AC\"}]}";
        // 예시: 템플릿을 통째로 복사해서 가져온 경우

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("apikey", apiKey);
        params.add("userid", userId);
        params.add("senderkey", "ff7f69c328188f85aa26867582ce55a57358b4a3");
        params.add("tpl_code", "UG_4123"); // 예: TF_0001
        params.add("sender", sender); 
        params.add("receiver_1", receiver);
        params.add("subject_1", "인솔자 명단 안내");
        params.add("message_1", message);
        params.add("button_1", buttonJson);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);


        try {
            // RestTemplate 등을 이용해 POST 요청 (이미 bean 등록되어 있다고 가정)
            // ResponseEntity<Map> response = restTemplate.postForEntity(url, params, Map.class);
            // String resultCode = String.valueOf(response.getBody().get("result_code"));
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            log.info("알리고 전체 응답: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                
                // 알리고는 성공 시 result_code가 정수 1 또는 문자열 "1"로 옵니다.
                String resultCode = root.path("result_code").asText();
                return "1".equals(resultCode);
            }
            
            // return "1".equals(resultCode);
        } catch (Exception e) {
            log.error("알림톡 API 통신 실패", e);
            return false;
        }
        return false;
    }
}