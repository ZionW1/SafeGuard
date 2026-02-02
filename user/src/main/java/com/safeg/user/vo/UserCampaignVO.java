package com.safeg.user.vo;

import java.time.LocalDate;

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
    private String leaderCode;
    private int campaignPay;
    private int leaderPoint;
    private int referrerPoint;
    private int totalPoint;
    private int count;
    private int pointSum;
    private int pay;




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
}