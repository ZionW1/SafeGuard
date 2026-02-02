package com.safeg.user.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddressVO {
    private String addressId; // 주소 고유 ID
    private String userId;
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
	private String createdAt; // 삭제여부
	private String updatedAt; // 생성 일시
    private String isDelete; // 최종 수정 일시
}