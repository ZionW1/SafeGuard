package com.safeg.user.vo;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCampaignVO {
    private Long id;
    private String createdAt;
    private String updatedAt;
    private String invitation_url;
    private String isSelected;
    private Long campaignId;
    private Long userNo;
    private String userId;
    private String status;
    private int extendPeriod;
    private boolean eventActive;
    private int applicantsNum;
    private LocalDate appliedStrDate;
    private LocalDate appliedEndDate;
    
    private String userNm;
    private String phoneNum;
    private String campaignTitle;
    private String companyPh;
    private String companyNm;
    private int recruitmentNum;
    private String typeNm;
    private LocalDate appPeriodStr;
    private LocalDate appPeriodEnd;
    private LocalDate eventPeriodStr;
    private LocalDate eventPeriodEnd;
    private LocalDate resultDate;
    private String placeAddr;
    private String statusNm;
    private LocalDate applyDate;
    // @DateTimeFormat(pattern = "yyyy-MM-dd")
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    // private LocalDate applyDate;
    private String leaderCode;
    private int campaignPay;
    private int leaderPoint;
    private int referrerPoint;
    private int totalPoint;
    private int count;
    private int pointSum;
    private int pay;

    private String authId;


    private Long pointId;
    private int amount;
    private String pointType;
    private Long sourceId;
    private LocalDate missionDate;

    private int totalAmount;
    private int referrerCount;
    private int leaderPointCount;


    private int campaignPayTotal;

    private int pointFull;
    private String filePath;

    public String getHyphenPhone() {
        // 1. 데이터가 없거나 길이가 짧은 경우 그대로 반환 (NPE 방어)
        if (this.phoneNum == null || this.phoneNum.length() < 10) {
            return this.phoneNum; 
        }
        
        // 2. 11자리인 경우 (01012345678 -> 010-1234-5678)
        if (this.phoneNum.length() == 11) {
            return this.phoneNum.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
        }
        
        // 3. 10자리인 경우 (021234567 -> 02-123-4567 등 예외 처리)
        if (this.phoneNum.length() == 10) {
            return this.phoneNum.replaceFirst("(\\d{2,3})(\\d{3,4})(\\d{4})", "$1-$2-$3");
        }
    
        return this.phoneNum;
    }
}