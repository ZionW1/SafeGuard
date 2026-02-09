package com.safeg.admin.vo;

import java.time.LocalDateTime;

import jakarta.persistence.Id;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class SmsVO {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String receiverNum;  // 수신 번호
    private String content;      // 문자 내용
    private String resultCode;   // 결과 코드 (1: 성공, 나머지: 실패)
    private String msgId;        // 알리고 메시지 ID
    private LocalDateTime sentAt; // 발송 시간

    @Builder
    public SmsVO(String receiverNum, String content, String resultCode, String msgId) {
        this.receiverNum = receiverNum;
        this.content = content;
        this.resultCode = resultCode;
        this.msgId = msgId;
        this.sentAt = LocalDateTime.now();
    }

}