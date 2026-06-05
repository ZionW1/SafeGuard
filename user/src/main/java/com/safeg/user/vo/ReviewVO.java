package com.safeg.user.vo;

import org.springframework.web.multipart.MultipartFile;

import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewVO {
    // 1. 실제 테이블 컬럼과 1:1 매칭되는 필드
    private Long reviewId;       // id (PK)
    private Long campaignId;     // campaign_id (FK)
    private String userId;       // user_id (FK)
    private String title;        // title
    private String content;      // content
    private String address;      // 주소
    private String thumbnailName; // thumnail_name (DB에 저장될 파일명 문자열)
    private String createdAt;    // created_at
    private String updatedAt;    // updated_at

    // 💡 [화면 받아오기용] HTML Form 태그에서 <input type="file" name="reviewFile">로 던진 이미지를 파싱하는 용도
    private MultipartFile thumbnail; 

    // 💡 [화면 뿌려주기용] MyBatis Select 쿼리문에서 JOIN을 통해 끌고 와 화면에 출력할 용도
    private String reviewerName;  // 회원 테이블(users)에서 JOIN으로 가져올 이름
    private String campaignTitle; // 캠페인 테이블(campaign)에서 JOIN으로 가져올 제목

    private String leaderCode;
    private String placeAddr;
    private int campaignPay;
    private String leaderId;
    private String userNm;
    private String id;
    private String role;
    private String guardType;
    private String typeNm;
    private String timeSegment;
}