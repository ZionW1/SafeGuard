package com.safeg.admin.vo;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InquiryVO {
    private Long inquiryId;
    private String companyNm;
    private String userNm;
    private String userId;
    private String phoneNum;
    private String title;
    private String content;
    private String type;
    private String costume;
    private String address;
    private Integer salary;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate payDate;
    private String memo;
    private String gatheringTime;
    private String supportType;
    private String status;
    private String agree;
    private String campaignTitle;

    private String answer;
    private String answerYn;
    private String answerAdmin;
    private LocalDate answerDate;
    private String answerTitle;
    private String answerContent;

    private LocalDate createdAt;
    private LocalDate updatedAt;
}
