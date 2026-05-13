package com.safeg.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonData {
    public static String getUploadPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "C:/SafeGuard_Project/upload/";
        } else if (os.contains("linux")) {
            // AWS EC2 리눅스 경로 추가
            return "/home/ubuntu/upload/";
        } else {
            // 맥 경로 (개발용)
            return "/Users/pieck/Documents/upload/";
        }
    }

    // 이 아래 상수들은 getUploadPath() 뒤에 붙는 상세 경로로 쓰일 겁니다.
    public static final String CAMPAIGN_IMAGE_PATH = "uploads/campaigns/";
    public static final String USER_PROFILE_IMAGE_PATH = "uploads/profiles/";
    
    private int totalUser;
    private int totalCampaign;
    private int totalReview;
    private int totalDonation;
}