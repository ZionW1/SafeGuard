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
            return "C:/safe_guard/upload/";
        } else {
            return "/Users/pieck/Documents/upload/";
        }
    }

    public static final String CAMPAIGN_IMAGE_PATH = "/uploads/campaigns/";
    public static final String USER_PROFILE_IMAGE_PATH = "/uploads/profiles/";
    
    private int totalUser;
    private int totalCampaign;
    private int totalReview;
    private int totalDonation;
}
