package com.safeg.admin.vo;

import java.time.LocalDate;

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
    private Integer leaderPay;
    private String leaderPays;
    private Integer leaderPoint;
    private String leaderCode;
    private LocalDate applyDate;
    // Getter, Setter ...
}