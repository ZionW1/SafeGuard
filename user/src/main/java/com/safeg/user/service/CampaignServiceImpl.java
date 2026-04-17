package com.safeg.user.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safeg.user.mapper.CampaignMapper;
import com.safeg.user.vo.CampaignVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.UserCampaignVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CampaignServiceImpl implements CampaignService{

    @Autowired
    CampaignMapper campaignMapper;

    @Autowired
    private AligoSmsService aligoSmsService;

    @Override
    public CampaignVO campaignSelect(Long id) throws Exception{
        // TODO Auto-generated method stub
        CampaignVO campaignSelect = campaignMapper.campaignSelect(id);

        return campaignSelect;
    }

    @Override
    public String campaignApply(UserCampaignVO userCampaignVO) throws Exception {
        // TODO Auto-generated method stub
        LocalDate startDate;
        LocalDate endDate;
        int result = 0;
        int result1 = 0;
        log.info("userCampaignVO : " + userCampaignVO);

        log.info("getEventPeriodEnd : " +userCampaignVO.getUserId());

        log.info("getEventPeriodEnd :" +userCampaignVO.getEventPeriodStr());
        log.info("getEventPeriodEnd :" +userCampaignVO.getEventPeriodEnd());

        try {
            startDate = userCampaignVO.getEventPeriodStr(); // 예: "2026-01-19" -> LocalDate
            endDate = userCampaignVO.getEventPeriodEnd();   // 예: "2026-01-21" -> LocalDate
        } catch (DateTimeParseException e) {
            // 날짜 형식 파싱에 실패하면 예외 처리
            throw new IllegalArgumentException("캠페인 기간 날짜 형식 오류: " + e.getMessage());
        }

        int overlapCount = campaignMapper.scheduleOverlap(userCampaignVO);
        log.info("overlapCount : " + overlapCount);
        if (overlapCount > 0) {
            // 중복된 일정이 있음
            return "OVERLAP";
        }
        // 3. 시작 날짜부터 종료 날짜까지의 모든 날짜(LocalDate) 리스트 생성
        List<LocalDate> datesInRange = Stream.iterate(startDate, date -> date.plusDays(1))
                                            // startDate와 endDate 모두 포함
                                            .limit(endDate.toEpochDay() - startDate.toEpochDay() + 1)
                                            .collect(Collectors.toList());
        log.info("datesInRange " + datesInRange);
        // 4. 각 날짜별로 DB에 삽입할 DTO 객체 생성
        List<UserCampaignVO> dailyEntriesToInsert = new ArrayList<>();
        for (LocalDate date : datesInRange) {
            UserCampaignVO dailyEntry = new UserCampaignVO();
            dailyEntry.setCampaignId(userCampaignVO.getCampaignId());
            dailyEntry.setUserId(userCampaignVO.getUserId());
            dailyEntry.setUserNo(userCampaignVO.getUserNo());
            dailyEntry.setApplicantsNum(userCampaignVO.getApplicantsNum());
            dailyEntry.setEventPeriodStr(userCampaignVO.getEventPeriodStr());
            dailyEntry.setEventPeriodEnd(userCampaignVO.getEventPeriodEnd());
            
            dailyEntry.setApplyDate(date);
            dailyEntriesToInsert.add(dailyEntry);
        }

        // 5. 매퍼를 통해 DB에 배치 삽입 (또는 하나씩 삽입)
        if (!dailyEntriesToInsert.isEmpty()) {
            log.info("dailyEntriesToInsert " + dailyEntriesToInsert);

            result = campaignMapper.campaignApply(dailyEntriesToInsert);
            // applyMapper.insertUserCampaignPeriod(dailyEntriesToInsert); // 아래 Mapper 메서드 참조
        }
        if(result >= 1) {
            log.info("updateApplicants : " + userCampaignVO.getApplicantsNum());
            result1 = campaignMapper.updateApplicants(userCampaignVO);
        }
        // int result = campaignMapper.campaignApply(userCampaignVO);

        CampaignVO campaignsVO = campaignMapper.notActiveCampaign(userCampaignVO.getCampaignId());

        log.info("notActiveCampaign : " + campaignsVO);
        if (campaignsVO != null) {
            try {
                updateCampaign(campaignsVO.getCampaignId());

                // 안전한 문자열 조립 (String.valueOf는 null일 경우 "null" 문자열을 반환하여 에러를 막음)
                String appPeriod = String.valueOf(campaignsVO.getAppPeriodStr()) + " ~ " + String.valueOf(campaignsVO.getAppPeriodEnd());
                String eventPeriod = String.valueOf(campaignsVO.getEventPeriodStr()) + " ~ " + String.valueOf(campaignsVO.getEventPeriodEnd());
                
                aligoSmsService.rosterCheckAsync(campaignsVO.getLeaderPhone(), campaignsVO.getTypeCode(), campaignsVO.getCampaignTitle(), campaignsVO.getRecruitmentNum(), appPeriod, eventPeriod, "https://행집.com/apply/userCampaignApply/" + campaignsVO.getCampaignId(), campaignsVO.getCompanyPh());
            } catch (Exception e) {
                log.error("캠페인 ID {} 발송 중 개별 오류: {}", campaignsVO.getCampaignId(), e.getMessage());
            }
        }
        return (result1 > 0) ? "SUCCESS" : "FAIL";
    }

    public String overlapTitle(UserCampaignVO userCampaignVO) throws Exception {
        log.info("overlapTitle userCampaignVO : " + userCampaignVO);
        String duplicateTitle = campaignMapper.overlapTitle(userCampaignVO);
        log.info("조회된 중복 타이틀: [" + duplicateTitle + "]"); // 대괄호를 붙여서 공백이나 빈값 확인
    
        // 값이 정말로 있을 때만(null이 아니고, 비어있지 않고, 공백만 있는게 아닐 때) 리턴
        if (duplicateTitle != null && !duplicateTitle.trim().isEmpty()) {
            return duplicateTitle;
        }
        
        // 중복이 없으면 명확하게 null을 반환해서 컨트롤러가 if문에 걸리지 않게 함
        return null;
    }

    @Override
    public List<CampaignVO> allView(Option option) throws Exception {
        // TODO Auto-generated method stub
        List<CampaignVO> allView = campaignMapper.allView(option);
        return allView;
    }

    @Override
    public List<UserCampaignVO> userCampaignApply(String id) throws Exception {
        // TODO Auto-generated method stub
        List<UserCampaignVO> userCampaignApply = campaignMapper.userCampaignApply(id);
        return userCampaignApply;
    }

    @Override
    public List<UserCampaignVO> appliedCampaign(String userId) throws Exception {
        // TODO Auto-generated method stub
        List<UserCampaignVO> appliedCampaign = campaignMapper.appliedCampaign(userId);
        return appliedCampaign;
    }


    @Override
    public List<CampaignVO> closedCampaign() throws Exception {
        // TODO Auto-generated method stub
        List<CampaignVO> closedCampaign = campaignMapper.closedCampaign();

        return closedCampaign;
    }

    @Override
    public int updateCampaign(Long campaignId) throws Exception {
        // TODO Auto-generated method stub
        int result = campaignMapper.updateCampaign(campaignId);
        return result;
    }

    @Override
    public List<UserCampaignVO> campaignApplied(String userId, Long campaignId) throws Exception {
        // TODO Auto-generated method stub
        List<UserCampaignVO> campaignApplied = campaignMapper.campaignApplied(userId, campaignId);

        return campaignApplied;
    }
    
    public List<CampaignVO> searchCampaign(String schCamp) throws Exception {
        // TODO Auto-generated method stub
        List<CampaignVO> searchCampaign = campaignMapper.searchCampaign(schCamp);

        return searchCampaign;
    }

    @Override
    public List<CampaignVO> campaignProgress() throws Exception {
        // TODO Auto-generated method stub
        List<CampaignVO> campaignProgress = campaignMapper.campaignProgress();

        return campaignProgress;
    }

    @Override
    public List<CampaignVO> campaignGuard() throws Exception {
        // TODO Auto-generated method stub
        List<CampaignVO> campaignGuard = campaignMapper.campaignGuard();

        return campaignGuard;
    }

    @Override
    public List<CampaignVO> campaignFulfill() throws Exception {
        // TODO Auto-generated method stub
        List<CampaignVO> campaignFulfill = campaignMapper.campaignFulfill();

        return campaignFulfill;    
    }
}
