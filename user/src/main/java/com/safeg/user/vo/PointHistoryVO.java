package com.safeg.user.vo;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointHistoryVO {
    private Long pointId;
    private Long userNo;
    private List<Long> userNos;
    private List<String> userIds;

    private String userId;
    private Long campaignId;
    private int amount;
    private String pointType;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String category;
    private String settlementStatus;
    private String afterBalance;
    private Long sourceNo;
    private String missionDate;

    private int totalAmount;
    private int referrerCount;
    private int applicantsNum;
    private int leaderPointCount;
    private String reasonInfo;
    private String userNm;
    private String id;
    private int pointCnt;

    private int totalPoint;

    private int referralCount;
    private int referralAmount;
    private int attendAmount;
    private int attendCount;
    private int workAmount;
    private int workCount;
    private int leaderAmount;
    private int leaderCount;

    private String campaignTitle;
    private String statusNm;
    private LocalDate applyDate;
    private String timeSegment;
    private int wageChk;
    private int workHour;

    private String ucChoice;

    public boolean empty() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'empty'");
    }
}
