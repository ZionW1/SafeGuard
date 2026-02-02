package com.safeg.user.controller;
import com.safeg.user.service.BannerService;
import com.safeg.user.service.CampaignService;
import com.safeg.user.service.MainService;
import com.safeg.user.service.UserService;
import com.safeg.user.vo.CommonData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice // ⭐ 이 어노테이션이 핵심! 모든 컨트롤러에 적용됩니다.
@RequiredArgsConstructor // Lombok을 사용하여 final 필드를 가진 생성자 자동 생성 (생성자 주입)
@Slf4j // ⭐ 로깅을 위한 Lombok 어노테이션
public class CommonDataAdvice {

    @Autowired
    private final UserService userService; // ⭐ final 키워드와 함께 의존성 주입 (생성자 주입)
    @Autowired
    private final MainService mainService; // ⭐ final 키워드와 함께 의존성 주입 (생성자 주입)

    @ModelAttribute("commonData")
    public CommonData getCommonData() {
        CommonData data = new CommonData();
        try {
            int totalUsers = userService.totalUser();
            int totalCampaign = mainService.totalCampaign();
            
            data.setTotalUser(totalUsers);
            data.setTotalCampaign(totalCampaign);
            
            log.info("CommonData successfully prepared: {}", data);
        } catch (Exception e) {
            log.error("Error preparing CommonData. Returning default values: {}", e.getMessage(), e);
            // 에러 발생 시에도 null이 아닌, 기본값을 가진 객체를 반환하여 템플릿 오류 방지
            data.setTotalUser(0); 
            data.setTotalCampaign(0);
            // data.setTotalReview(0);
        }
        return data;
    }
}