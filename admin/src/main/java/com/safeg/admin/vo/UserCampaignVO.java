package com.safeg.admin.vo;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private String isLeader;
    private LocalDate appliedStrDate;
    private LocalDate appliedEndDate;
    private int pfmcScore;
    private String role;
    private String userNm;
    private String phoneNum;
    private String campaignTitle;
    private String typeNm;
    private LocalDate appPeriodStr;
    private LocalDate appPeriodEnd;
    private LocalDate eventPeriodStr;
    private LocalDate eventPeriodEnd;
    private LocalDate resultDate;
    private String placeAddr;
    private String statusNm;
    private LocalDate applyDate;
    private LocalDate beforeDate;
    private String timeSegment;
    private String leadApply;

    private int leaderPay;

    private int recruitmentNum;
    private int limitCount;

    private List<Long> userNos;

    // private int applicantsNum;

    public boolean isEmpty() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isEmpty'");
    }
}