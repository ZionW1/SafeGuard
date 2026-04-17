package com.safeg.admin.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import com.safeg.admin.service.AligoSmsService;
import com.safeg.admin.service.CampaignService;
import com.safeg.admin.service.UserService;
import com.safeg.admin.vo.CampaignVO;
import com.safeg.admin.vo.UserVO;

import ch.qos.logback.core.model.Model;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {
    
    private final CampaignService campaignService;
    private final UserService userService;
    private final AligoSmsService aligoSmsService; // @Autowired 대신 생성자 주입 권장 (이미 RequiredArgsConstructor가 있음)

    // 1. 새벽 2시 스케줄러 (정상)
    @Scheduled(cron = "0 0 2 * * *")
    public void campaignScheduler() {
        log.info("CampaignScheduler - 만료된 캠페인 처리 시작.");
        try {
            campaignService.updateExpiredCampaigns();
            log.info("CampaignScheduler - 만료된 캠페인 처리 완료.");
        } catch (Exception e) {
            log.error("CampaignScheduler - 만료된 캠페인 처리 중 오류: {}", e.getMessage(), e);
        }
    }

    // 2. 오전 10시 알림톡 스케줄러 (수정 완료)
    @Scheduled(cron = "0 0 10 * * *")
    public void closeCampaignScheduler() { // 👈 Model model 파라미터 제거!
        log.info("CloseCampaignScheduler - 알림톡 발송 스케줄러 시작.");
        
        try {
            List<CampaignVO> closedCampaign = campaignService.closedCampaign();
            log.info("대상 캠페인 수: {}", (closedCampaign != null ? closedCampaign.size() : 0));

            if (closedCampaign == null || closedCampaign.isEmpty()) return;

            for (CampaignVO campaignsVO : closedCampaign) {
                try {
                    // 안전한 문자열 조립 (String.valueOf는 null일 경우 "null" 문자열을 반환하여 에러를 막음)
                    String appPeriod = String.valueOf(campaignsVO.getAppPeriodStr()) + " ~ " + String.valueOf(campaignsVO.getAppPeriodEnd());
                    String eventPeriod = String.valueOf(campaignsVO.getEventPeriodStr()) + " ~ " + String.valueOf(campaignsVO.getEventPeriodEnd());

                    aligoSmsService.rosterCheckAsync(campaignsVO.getLeaderPhone(), campaignsVO.getTypeNm(), campaignsVO.getCampaignTitle(), campaignsVO.getRecruitmentNum(), appPeriod, eventPeriod, "https://행집.com/apply/userCampaignApply/" + campaignsVO.getCampaignId(), campaignsVO.getCompanyPh());
                } catch (Exception e) {
                    log.error("캠페인 ID {} 발송 중 개별 오류: {}", campaignsVO.getCampaignId(), e.getMessage());
                }
            }
            log.info("CloseCampaignScheduler - 알림톡 발송 요청 완료.");
        } catch (Exception e) {
            log.error("CloseCampaignScheduler - 전체 로직 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}