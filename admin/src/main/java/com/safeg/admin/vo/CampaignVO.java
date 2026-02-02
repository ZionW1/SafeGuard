package com.safeg.admin.vo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CampaignVO {
    private Long id;
    private String campaignTitle;
    private String statusName;
    private int applicantsNum;
    private int recruitmentNum;
    
    private Long campaignId;
	private String companyNm;
	private String companyPh;
	private String campTitle;
	private String leaderCode;
    private String typeCode;
    private String statusCode;
    private String placeAddr;
    private String mission;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate AppPeriodStr;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate AppPeriodEnd;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventPeriodStr;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate eventPeriodEnd;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate resultDate;
	private String createdAt;
	private String updatedAt;
    private String isDeleted;
    private int recruitNum;
	private int applNum;
    private Long fileId;
    private int campaignPay;
    private int leaderPoint;
    private int leaderPay;
    





    private String userNm;
    private String leaderName; // 인솔자 이름
    private String leaderPhone; // 인솔자 연락처
    private String leaderId; // 인솔자 연락처
    private Long leaderNo; // 인솔자 번호
    
    private MultipartFile fileName;    // 파일명
    private String phoneNum;
    private MultipartFile image; // 첨부 파일 목록

    private Long codeId;
    private String codeGroup;
    private String code;
    private String codeName;
    private String description;
    private int sortOrder;
    //private List<CampaignFilesVO> campaignFilesList; //첨부 파일 정보
    
}
