package com.safeg.user.vo;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CampaignVO {
    private Long id;
    private String campaignTitle;
    private String statusName;
    private int applicantsNum;
    private int recruitmentNum;

    // private int recruitmentNum;
    private Long campaignId;
	private String companyNm;
	private String companyPh;
	private String campTitle;
	private String leaderCode;
    private String typeCode;
    private String statusCode;
    private String placeAddr;
    private String mission;
    private LocalDate appPeriodStr;
    private LocalDate appPeriodEnd;
    private LocalDate eventPeriodStr;
	private LocalDate eventPeriodEnd;
	private LocalDate resultDate;
	private String createdAt;
	private String updatedAt;
    private String isDelete;
    private int recruitNum;
	private int applNum;
    private Long fileId;
	private int whiteSal;
	private int greenSal;
	private int yellowSal;
	private int orangeSal;
	private int redSal;
	private int blueSal;
	private int brownSal;
    private String userNm;
    private String isActive;
    private LocalDate periodEnd;
    private LocalDate periodStr;


    private LocalDate period_str;
    private LocalDate period_end;


    private String leaderName; // 인솔자 이름
    private String leaderPhone; // 인솔자 연락처
    private String leaderNo; // 인솔자 번호
    
    private MultipartFile fileName;    // 파일명
    private String phoneNum;
    private MultipartFile image; // 첨부 파일 목록
    
    private Long codeId;
    private String codeGroup;
    private String code;
    private String codeName;
    private String description;
    private int sortOrder;
    private transient boolean isApplyPossible; // 이 캠페인이 현재 사용자에게 신청 가능한지 여부 (DB 저장 X)

    //private List<CampaignFilesVO> campaignFilesList; //첨부 파일 정보
    private String filePath; //varchar(500) not null comment '파일 저장 경로',
    private String savedName; //varchar(255) not null comment '저장 파일
}
