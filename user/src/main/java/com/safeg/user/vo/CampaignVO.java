package com.safeg.user.vo;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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
    // private LocalDate appPeriodStr;
    // private LocalDate appPeriodEnd;
    private String isDelete;
	private int whiteSal;
	private int greenSal;
	private int yellowSal;
	private int orangeSal;
	private int redSal;
	private int blueSal;
	private int brownSal;
    private String isActive;
    private LocalDate periodEnd;
    private LocalDate periodStr;


    private LocalDate period_str;
    private LocalDate period_end;

    private transient boolean isApplyPossible; // 이 캠페인이 현재 사용자에게 신청 가능한지 여부 (DB 저장 X)

    //private List<CampaignFilesVO> campaignFilesList; //첨부 파일 정보
    private String filePath; //varchar(500) not null comment '파일 저장 경로',
    private String savedName; //varchar(255) not null comment '저장 파일

    private boolean showPoint;

    private Long id;
    private String campaignTitle;
    private String statusName;
    private Integer applicantsNum;
    private Integer recruitmentNum;
    private List<String> userNos;
    private List<String> userIds;
    private Long campaignId;
	private String companyNm;
	private String companyPh;
	private String campTitle;
	private String leaderCode;
    private String typeCode;
    private String typeNm;
    private String statusCode;
    private String placeAddr;
    private String mission;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate appPeriodStr;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate appPeriodEnd;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventPeriodStr;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate eventPeriodEnd;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate resultDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventStrDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventEndDate;

	private String createdAt;
	private String updatedAt;
    private String isDeleted;
    private int recruitNum;
	private int applNum;
    private Long fileId;
    private int campaignPay;
    private int leaderPoint;
    private int leaderPay;
    private String leaderPays;

    private String applyDateS;

    private String ucChoice;

    private List<CampLeaderVO> leaderList;

//     // Getter, Setter
//     public Long getCampaignId() { return campaignId; }
//     public void setCampaignId(Long campaignId) { this.campaignId = campaignId; }

//     public List<CampaignLeaderVO> getLeaderList() { return leaderList; }
//     public void setLeaderList(List<CampaignLeaderVO> leaderList) { this.leaderList = leaderList; }
// }


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
    private String leaderNm;
    private String workHour;
    private String wageChk;
    private String timeSegment;

    //private List<CampaignFilesVO> campaignFilesList; //첨부 파일 정보

}
