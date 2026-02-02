package com.safeg.user.vo;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
//@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilesVO {

    private int no;
    private Long id;
    
    private String image; // 첨부 파일명
    private String imageId;
    
    
    private String status;    // 파일구분 (BANNER, CAMPAIGN등)
    private Long statusId;    // 파일의 글 번호
    private String isDelete;
    
    private Long fileId; //bigint not null auto_increment comment '파일 고유 식별자',
    private String fileType; //varchar(50) not null comment '파일 용도 구분자 (PROFILE_IMG, BOARD_ATTACH 등)',
    private Long targetId; //bigint null comment '파일이 속한 대상의 ID (예: 게시글 ID, 회원 ID)',
    private String targetType; //varchar(50) null comment 'target_id가 참조하는 테이블명 (예: USER, BOARD)',
    private String originalName; //varchar(255) not null comment '원본 파일 이름',
    private String savedName; //varchar(255) not null comment '서버 저장 파일 이름 (UUID)',
    private String filePath; //varchar(500) not null comment '파일 저장 경로',
    private Long fileSize; //bigint not null comment '파일 크기 (바이트)',
    private String fileExtension; //varchar(10) null comment '파일 확장자',
    private String mimeType; //varchar(100) null comment '파일 mime 타입',
    private int downloadCount; //int default 0 null comment '다운로드 횟수',
    private String isDeleted; //char(1) default 'N' not null comment '삭제 여부 (Y/N)',
    private String createdAt; //datetime default current_timestamp not null comment '생성 일시',
    private Long createdBy; //bigint null comment '파일 업로드 사용자 ID',
    private String updatedAt; //datetime default current_timestamp on update current_timestamp not null comment '최종 수정 일시',
    private Long updatedBy; //bigint null comment '파일 정보 수정한 사용자 ID',

    private MultipartFile file; // 파일 데이터

    private String userId;
    // 이미지: image/jpeg, image/png, image/gif, image/webp
    // 오디오: audio/mpeg, audio/wav
    // 비디오: video/mp4, video/x-msvideo
    // 문서: application/pdf, application/msword, text/plain
    // 웹 페이지: text/html, application/javascript
    private String args;
    public FilesVO() {
        this.imageId=UUID.randomUUID().toString();
    }

}
// 