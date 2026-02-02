package com.safeg.user.vo;

import java.time.LocalDate;

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
    private Long userId;
    private int amount;
    private String pointType;
    private Long sourceId;
    private LocalDate missionDate;

    private int totalAmount;
    private int referrerCount;
    private int leaderPointCount;

    private String userNm;

    private int totalPoint;

}
