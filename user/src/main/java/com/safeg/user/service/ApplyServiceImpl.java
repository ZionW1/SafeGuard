package com.safeg.user.service;

import java.beans.Transient;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safeg.user.mapper.ApplyMapper;
import com.safeg.user.mapper.UserMapper;
import com.safeg.user.vo.PointHistoryVO;
import com.safeg.user.vo.UserCampaignVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApplyServiceImpl implements ApplyService {

    @Autowired
    ApplyMapper applyMapper;

    @Autowired
    UserMapper userMapper;

    @Override
    public List<UserCampaignVO> userCampaignApply(String id, LocalDate applyDate) throws Exception {
        // TODO Auto-generated method stub
        List<UserCampaignVO> userCampaignApply = applyMapper.userCampaignApply(id, applyDate);
        return userCampaignApply;
    }

    @Override
    @Transactional
    public String updateStatus(Long userNo, Long campaignId, LocalDate applyDate, String statusValue) throws Exception{
        // TODO Auto-generated method stub
        // log.info("userNo : " + userNo + ", campaignId : " + campaignId + ", applyDate : " + applyDate +  ", statusValue : " + statusValue );
        String statusInfo = applyMapper.statusInfo(userNo, campaignId, applyDate);
        log.info("statusInfo.getStatus() = " + statusInfo);

        int result = applyMapper.updateStatus(userNo, campaignId, applyDate, statusValue);
        
        String statusInfoAfter = applyMapper.statusInfo(userNo, campaignId, applyDate);
        log.info("statusInfo.getStatus() = " + statusInfoAfter);


        if(result >= 1){
            if(statusInfo.equals("1") || statusInfo.equals("3")){
                int resultPay = 0;
        
                if("2".equals(statusInfoAfter)){
                    Long referrerId = userMapper.getReferrerNoById(userNo); // ⭐ userNo가 퇴근하는 사람의 users.id!
                    log.info("referrerId" + referrerId);

                    if (referrerId != null && referrerId != 0) { // 추천인이 있다면 (referrer_no가 유효하다면)
                        int pointAmount = 5000; // ⭐ 지급할 추천인 포인트 (이 값은 설정 등으로 관리하는 게 좋아!)
                        
                        PointHistoryVO pointHistory = new PointHistoryVO();
                        pointHistory.setUserId(referrerId);             // 포인트를 받은 사람: 추천인
                        pointHistory.setAmount(pointAmount);            // 지급된 포인트 양
                        pointHistory.setPointType("REFERRAL_REWARD");   // 포인트 종류: 추천인 보상
                        pointHistory.setSourceId(userNo);               // 이 포인트를 받게 된 원천: 퇴근한 사용자(피추천인)의 userNo
                        pointHistory.setMissionDate(applyDate);   // 경호 업무 완료 날짜 (오늘 날짜)

                        // pointHistoryMapper는 point_history 테이블에 데이터를 삽입하는 매퍼야.
                        applyMapper.insertPointHistory(pointHistory);
                    }
                    if("1".equals(statusInfo)){
                        resultPay = applyMapper.updatePay(userNo, campaignId);
                        log.info("resultPay = " + resultPay);
                    }else if("3".equals(statusInfo)){
                        resultPay = applyMapper.updateLatenessPay(userNo, campaignId);
                        log.info("resultPay = " + resultPay);
                    }
                    
                    /*
                    INSERT INTO point_history (user_id, amount, point_type, source_id, mission_date, created_at)
                    VALUES (
                        [조회된 referrer_no],                 -- 포인트를 받은 추천인의 users.id
                        500,                                 -- 지급된 포인트 양
                        'REFERRAL_REWARD',                   -- 포인트 종류: 추천인 보상
                        [경호 업무를 완료한 피추천인의 id],     -- 이 포인트를 받게 된 원천 (누구를 추천해서 받았는지)
                        CURDATE(),                           -- 오늘 날짜 또는 경호 업무 완료 날짜
                        NOW()
                    );
                    */
                }
                
                return statusInfoAfter;
                // applyMapper.updatePay(userNo, campaignId, statusValue);
            }else{
                return statusInfoAfter;
            }
        }else{
            return "0";
        }
    }

    @Override
    public String statusInfo(Long userNo, Long campaignId, LocalDate applyDate) throws Exception {
        // TODO Auto-generated method stub
        String userStatusInfo = applyMapper.statusInfo(userNo, campaignId, applyDate);

        return userStatusInfo;
    }

    @Override
    public int initStatus(Long userNo, Long campaignId, LocalDate applyDate) throws Exception {
        // TODO Auto-generated method stub
        int initStatus = applyMapper.initStatus(userNo, campaignId, applyDate);
        
        return initStatus;
    }

    @Override
    public void lateYn(Long userNo, Long campaignId, LocalDate applyDate) throws Exception {
        // TODO Auto-generated method stub
        applyMapper.lateYn(userNo, campaignId, applyDate);
    }

    @Override
    public void pointFull(Long userNo, Long campaignId, LocalDate applyDate) throws Exception {
        // TODO Auto-generated method stub
        applyMapper.pointFull(userNo);
    }
}