package com.safeg.admin.vo;

public class AligoResponse {
    private int result_code;    // 1이면 성공, 나머지는 실패
    private String message;     // 결과 메시지
    private String msg_id;      // 발송 고유 ID
    private int success_cnt;    // 성공 건수
    // Getter, Setter 생략
}