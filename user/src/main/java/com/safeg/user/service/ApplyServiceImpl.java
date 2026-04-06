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
        log.info("userNo : " + userNo + ", campaignId : " + campaignId + ", applyDate : " + applyDate +  ", statusValue : " + statusValue  + ", applyDateToString : " + applyDate.toString().substring(0, 7));
        String fullAttendDate = applyDate.toString().substring(0, 7);
        log.info("fullAttendDate : " + fullAttendDate);
        String statusInfo = applyMapper.statusInfo(userNo, campaignId, applyDate);
        String applyDateString = applyDate.toString();
        int result = applyMapper.updateStatus(userNo, campaignId, applyDate, statusValue);
        log.info("출결 상태 업데이트 결과 : " + result);
        int fullAttendCount = applyMapper.fullAttendCount(userNo, fullAttendDate);
        log.info("이번 달 무단결근 횟수 : " + fullAttendCount);
        int point = applyMapper.pointAmount(campaignId);
        log.info("이번 캠페인 근무 포인트 : " + point);

        if (result >= 1) {
            log.info("출결 상태 업데이트 성공: userNo={}, campaignId={}, applyDate={}, newStatus={}", userNo, campaignId, applyDate, statusValue);  
            String statusInfoAfter = applyMapper.statusInfo(userNo, campaignId, applyDate);
            log.info("statusInfoAfter : " + statusInfoAfter);  

            // 1. 추천인 보상 (지급 대상: 추천인)
            if ("2".equals(statusInfoAfter) && ("1".equals(statusInfo) || "3".equals(statusInfo))) {
                log.info(statusInfoAfter + "로 변경되었으므로, 추천인 보상 지급 로직을 실행합니다.");
                Long referrerId = userMapper.getReferrerNoById(userNo);
                if (referrerId != null && referrerId != 0) {
                    PointHistoryVO refPoint = new PointHistoryVO();
                    refPoint.setUserId(referrerId); // 돈 받는 사람: 추천인
                    refPoint.setAmount(5000);
                    refPoint.setCategory("REFERRAL");
                    refPoint.setSourceId(userNo); // 원인 제공자: 본인
                    refPoint.setMissionDate(applyDateString);
                    refPoint.setCampaignId(campaignId);
                    refPoint.setSettlementStatus("READY");
                    applyMapper.insertPointHistory(refPoint);
                }

                // 2. 본인 포인트 (지급 대상: 본인)
                PointHistoryVO myPoint = new PointHistoryVO();
                myPoint.setUserId(userNo); // 돈 받는 사람: 본인
                myPoint.setSourceId(userNo);
                myPoint.setMissionDate(applyDateString);
                myPoint.setSettlementStatus("READY");
                
                // 만근 포인트
                if (fullAttendCount == 0) {
                    // 1. 이번 달 무단결근이 0회라면 -> 만근 포인트 지급 시도
                    // (단, 이미 이번 달에 지급받았는지 체크하는 로직이 있으면 중복 방지에 좋습니다.)
                    myPoint.setAmount(10000);
                    myPoint.setCategory("FULL_ATTEND");
                    myPoint.setCampaignId(campaignId);
                    applyMapper.insertPointHistory(myPoint);
                } else {
                    // 2. 이번 달 무단결근이 1회라도 있다면 -> 기존 만근 포인트 회수(0원 처리)
                    // 이전에 결근했을 때 미처 처리하지 못했더라도, 오늘 출근 처리 시점에 확실히 잡아냅니다.
                    myPoint.setAmount(0);
                    myPoint.setCategory("FULL_ATTEND");
                    myPoint.setCampaignId(campaignId);
                    applyMapper.updateAttendPoint(myPoint); 
                }

                // 일반 근무 포인트
                myPoint.setAmount(point);
                myPoint.setCategory("WORK");
                myPoint.setCampaignId(campaignId);
                applyMapper.insertPointHistory(myPoint);
            }

            if ("5".equals(statusInfoAfter)) { // AWOL 무단결근
                PointHistoryVO awol = new PointHistoryVO();
                awol.setUserId(userNo); // 돈 받는 사람: 본인
                awol.setSourceId(userNo);
                awol.setMissionDate(applyDateString);
                awol.setSettlementStatus("READY");
                awol.setAmount(0);
                awol.setCategory("FULL_ATTEND");
                awol.setCampaignId(campaignId);
                log.info("awol : " + awol.toString());
                    applyMapper.updateAttendPoint(awol);
            }

            // 3. 팀장 보너스 (지급 대상: 팀장)
            if ("9".equals(statusInfoAfter)) { // 혹은 statusInfo 확인
                // 1. 인솔자 기본 포인트 적립
                saveLeaderPoint(userNo, campaignId, applyDate, "WORK", applyMapper.leaderAmount(campaignId));

                // 2. 인솔자 추가 포인트 적립
                saveLeaderPoint(userNo, campaignId, applyDate, "LEADER_EXTRA", 5000);

                // 1. 이번 달 무단결근이 0회라면 -> 만근 포인트 지급 시도
                // (단, 이미 이번 달에 지급받았는지 체크하는 로직이 있으면 중복 방지에 좋습니다.)
                PointHistoryVO leaderAttendPoint = new PointHistoryVO();
                leaderAttendPoint.setUserId(userNo); // 돈 받는 사람: 본인
                leaderAttendPoint.setSourceId(userNo);
                leaderAttendPoint.setMissionDate(applyDateString);
                leaderAttendPoint.setSettlementStatus("READY");
                
                // 만근 포인트
                if (fullAttendCount == 0) {
                    // 1. 이번 달 무단결근이 0회라면 -> 만근 포인트 지급 시도
                    // (단, 이미 이번 달에 지급받았는지 체크하는 로직이 있으면 중복 방지에 좋습니다.)
                    leaderAttendPoint.setAmount(10000);
                    leaderAttendPoint.setCategory("FULL_ATTEND");
                    leaderAttendPoint.setCampaignId(campaignId);
                    applyMapper.insertPointHistory(leaderAttendPoint);
                } else {
                    // 2. 이번 달 무단결근이 1회라도 있다면 -> 기존 만근 포인트 회수(0원 처리)
                    // 이전에 결근했을 때 미처 처리하지 못했더라도, 오늘 출근 처리 시점에 확실히 잡아냅니다.
                    leaderAttendPoint.setAmount(0);
                    leaderAttendPoint.setCategory("FULL_ATTEND");
                    leaderAttendPoint.setCampaignId(campaignId);
                    applyMapper.updateAttendPoint(leaderAttendPoint); 
                }
            }

            return statusInfoAfter;
        } else {
            throw new Exception("출결 상태 업데이트 실패");
        }
    }

    private void saveLeaderPoint(Long userId, Long campId, LocalDate date, String category, int amount) throws Exception {
        String dateString = date.toString();

        PointHistoryVO vo = new PointHistoryVO();
        vo.setUserId(userId);
        vo.setAmount(amount);
        vo.setCategory(category);
        vo.setSourceId(userId);
        vo.setCampaignId(campId);
        vo.setMissionDate(dateString);
        vo.setSettlementStatus("READY");
        applyMapper.insertPointHistory(vo);
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

    public List<UserCampaignVO> getApplyDate(String campaignId) throws Exception {
        List<UserCampaignVO> getApplyDate = applyMapper.getApplyDate(campaignId);

        return getApplyDate;
    }
}