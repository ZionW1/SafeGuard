package com.safeg.admin.vo;

import lombok.Data;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVOpy {
    private Long id;
    private String userId;
    private String userNm;
    private String password;
    private String accountNumber;
    private String bankNm;
    private String depositor;
    private String email;
    private String isDeleted;
    private String isStopped;
    private String nickname;
    private String phoneNum;
    private int pointId;
    private String userRating;
    private Long commonId;
    private Long addressId;
    private Long gradeId;
    private String stopDate;
    private String memo;
    private Long fileId;
    private String createdAt;
    private String updatedAt;
    private int enabled;
    
    private String address;
	private String detail;
	private String name;
	private String receiver;
	private String zipCode;
    private String auth;
    

    private List<UserAuth> authList;
    private MultipartFile image; // 첨부 파일 목록


    public String authListgetAuth() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'authListgetAuth'");
    }
}