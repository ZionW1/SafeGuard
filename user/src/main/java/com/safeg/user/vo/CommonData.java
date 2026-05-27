package com.safeg.user.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonData {
    public static final String CAMPAIGN_IMAGE_PATH = "/uploads/campaigns/";
    public static final String USER_PROFILE_IMAGE_PATH = "/uploads/profiles/";
    
    private int totalUser;
    private int totalGuard;
    private int totalCampaign;
    private int totalReview;
    private int totalDonation;

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
}
