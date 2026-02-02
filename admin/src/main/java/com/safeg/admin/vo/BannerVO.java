package com.safeg.admin.vo;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
// @NoArgsConstructor

public class BannerVO {
    private String id; // 배너 고유 ID
    private Long bannerId; // 배너 고유 ID

    private String filePath; // 파일경로
    private Long fileSize; // 용량
    private String image; // 첨부 파일 목록
    private String createdAt; // 생성일
    private String updatedAt; // 수정일
    private String imageId; // 이미지 고유 ID
    private String isDeleted; // 삭제 여부
    private String url; // 링크 URL
    private String title; // 제목

    private MultipartFile file; // 파일 데이터

    public BannerVO() {
        this.imageId=UUID.randomUUID().toString();
    }
}
