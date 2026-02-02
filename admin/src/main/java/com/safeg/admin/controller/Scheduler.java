package com.safeg.admin.controller;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.safeg.admin.service.CampaignService;
import com.safeg.admin.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component // 스프링 빈으로 등록
@RequiredArgsConstructor
public class Scheduler {
    
    private final CampaignService campaignService;
    
    private final UserService userService;

    // ⭐️ 매일 새벽 2시에 실행되도록 설정 ⭐️
    // Cron 표현식: 초 분 시 일 월 요일
    // 0 0 2 * * *  -> 매일 새벽 2시 0분 0초에 실행
    @Scheduled(cron = "0 0 2 * * *")
    public void CampaignScheduler() {
        log.info("CampaignScheduler - 만료된 캠페인 처리 스케줄러 시작.");
        try {
            campaignService.updateExpiredCampaigns();
            log.info("CampaignScheduler - 만료된 캠페인 처리 스케줄러 완료.");
        } catch (Exception e) {
            log.error("CampaignScheduler - 만료된 캠페인 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 0 0 L * ?") // ✨ 이 부분이 월말 처리의 핵심이야! ✨
    public void resetPayOnMonthEnd() throws Exception{
        LocalDate today = LocalDate.now();
        
        System.out.println("이번 달의 마지막 날(" + today + ")! Pay 컬럼을 0으로 초기화하는 작업을 시작할게!");
        
        userService.resetAllUserPay();
        // --- 여기에 'pay' 컬럼을 0으로 업데이트하는 로직을 넣어줘! ---
        // 예시: 모든 사용자 또는 특정 조건의 pay를 0으로 설정하는 메서드 호출
        // payService.resetAllUserPayToZero();
        // 또는 특정 Repository를 통해 직접 DB 업데이트
        // yourRepository.updatePayToZeroForAllUsers();
        // -------------------------------------------------------------
        
        System.out.println("Pay 컬럼 0 초기화 작업 완료!");
    }
}
