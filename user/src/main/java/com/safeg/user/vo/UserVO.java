package com.safeg.user.vo;

import lombok.Data;

import java.io.File;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    // private Long id;
    // private String userId;
    // private String userNm;
    // private String accountNumber;
    // private String bankNm;
    // private String depositor;
    // private String email;
    // private String isDeleted;
    // private String isStopped;
    // private String nickname;
    // private String phoneNum;
    // private int pointId;
    // private String userRating;
    // private Long commonId;
    // private Long addressId;
    // private Long gradeId;
    // private String stopDate;
    // private String memo;
    // private Long fileId;
    // private String createdAt;
    // private String updatedAt;
    // private int enabled;
    // private int count;
    // private int pfmsScore;
    // private String address;
	// private String detail;
	// private String name;
	// private String receiver;
	// private String zipCode;
    // private String auth;
    // private String pay;
    // @NotBlank(message = "비밀번호는 필수 입력입니다.")
    // @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.") // ⭐️ 자리수 제한 추가
    // // ⭐️ 숫자, 문자(영문), 특수문자 포함 정규식 추가 ⭐️
    // @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_\\.])[a-zA-Z0-9!@#$%^&*?_\\.]{8,20}$",
    //         message = "비밀번호는 영문, 숫자, 특수문자(!@#$%^&*?_.)를 포함 입니다.")
    // private String password;

    // @NotBlank(message = "비밀번호 확인을 입력하세요.")
    // private String passwordConfirm;

    // // getter, setter
    // @AssertTrue(message = "이용약관에 동의해 주세요.")
    // private Boolean agree;

    // public boolean isPasswordConfirmed() {
    //     return password != null && password.equals(passwordConfirm);
    // }

    // private List<UserAuth> authList;
    // private MultipartFile image; // 첨부 파일 목록

    // private String zoneCode; // 우편번호 (예: 06130), sigunguCode
    // private String province; // 시/도 (예: 서울특별시), sido
    // private String city; // 시/군/구 (예: 강남구), sigungu
    // private String roadName; // 도로명 (예: 테헤란로), roadname
    // private String roadAddress; // 도로명 주소 전체 (예: 서울 강남구 테헤란로 123), roadAddress
    // private String jibunAddress; // 지번 주소, jibunAddress
    // private String extraAddress; // 참고 항목 (예: 역삼동), bname
    // private String detailAddress; // 사용자가 직접 입력하는 상세 주소 (예: 101동 1503호)
    // private String fullAddress;
    // private boolean isDefault; // 대표 주소 여부, -- 한 사용자의 여러 주소 중 대표 주소를 지정하고 싶을 때
    // private String isDelete; // 최종 수정 일시
    // // private String isPasswordConfirmed;


    // private String lastLoginAt;
    // private String lastLoginIp;
    // private int loginFailCount;
    // private String lockDate;
    // private String passwordUpdatedAt;
    // private String passwordUpdateIp;
    // private String passwordResetKey;
    // private String passwordResetRequestedAt;
    private Long id;
    private String userId;
    private String userNm;
    // private String password;
    // private String passwordConfirm;

    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.") // ⭐️ 자리수 제한 추가
    // ⭐️ 숫자, 문자(영문), 특수문자 포함 정규식 추가 ⭐️
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,20}$",
            message = "비밀번호는 영문, 숫자, 특수문자(!@#$%^&*?_)를 포함 입니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력하세요.")
    private String passwordConfirm;

    // getter, setter
    @AssertTrue(message = "이용약관에 동의해 주세요.")
    private Boolean agree;

    public boolean isPasswordConfirmed() {
        return password != null && password.equals(passwordConfirm);
    }
    private String accountNumber;
    private String bankNm;
    private String depositor;
    private String email;
    private String isDeleted;
    private String isStopped;
    private String nickname;
    private String phoneNum;
    private String authCode;
    private int pointId;
    private String userRating;
    private Long commonId;
    private Long addressId;
    private Long gradeId;
    private String stop_date;
    private String memo;
    private String authId;
    private Long fileId;
    private int count;
    private int pay;

    private String address;
	private String detail;
	private String name;
	private String receiver;
	private String zip_code;
    private String auth;
    private String createdAt;
    private String updatedAt;
    private int enabled;
    private String role;

    private String lastLoginAt;
    private String lastLoginIp;
    private int loginFailCount;
    private String lockDate;
    private String passwordUpdatedAt;
    private String passwordUpdateIp;
    private String passwordResetKey;
    private String passwordResetRequestedAt;

    
    private List<UserAuth> authList;
    private MultipartFile image; // 첨부 파일 목록
    private MultipartFile certImage;
    private MultipartFile IdttImage;

    private String zoneCode; // 우편번호 (예: 06130), sigunguCode
    private String province; // 시/도 (예: 서울특별시), sido
    private String city; // 시/군/구 (예: 강남구), sigungu
    private String roadName; // 도로명 (예: 테헤란로), roadname
    private String roadAddress; // 도로명 주소 전체 (예: 서울 강남구 테헤란로 123), roadAddress
    private String jibunAddress; // 지번 주소, jibunAddress
    private String extraAddress; // 참고 항목 (예: 역삼동), bname
    private String detailAddress; // 사용자가 직접 입력하는 상세 주소 (예: 101동 1503호)
    private String fullAddress;
    private boolean isDefault; // 대표 주소 여부, -- 한 사용자의 여러 주소 중 대표 주소를 지정하고 싶을 때
    

    private Long referrerNo;
    private String referrerId;

    private String guardType;

}