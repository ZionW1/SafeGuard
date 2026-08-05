package com.safeg.user.vo;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampLeaderVO {
    private Long no; // 상위 캠페인 ID
    private Long campaignId; // 상위 캠페인 ID
    private String leaderId;
    private String leaderNo;
    private String leaderNm;
    private String leaderPh;
    private int leaderPay;
    private String leaderPays;
    private int leaderPoint;
    private String leaderCode;
    private String phoneHash;
    // Getter, Setter ...
}