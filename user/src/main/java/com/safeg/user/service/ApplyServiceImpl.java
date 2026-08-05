package com.safeg.user.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safeg.user.mapper.ApplyMapper;
import com.safeg.user.mapper.UserMapper;
import com.safeg.user.vo.CampaignVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.PointHistoryVO;
import com.safeg.user.vo.UserCampaignVO;
import com.safeg.user.vo.UserVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApplyServiceImpl implements ApplyService {

    final ApplyMapper applyMapper;

    @Autowired
    UserMapper userMapper;

    ApplyServiceImpl(ApplyMapper applyMapper) {
        this.applyMapper = applyMapper;
    }

    @Override
    public List<UserCampaignVO> userCampaignApply(String id, LocalDate applyDate, String timeSegment) throws Exception {
        // TODO Auto-generated method stub
        List<UserCampaignVO> userCampaignApply = applyMapper.userCampaignApply(id, applyDate, timeSegment);
        return userCampaignApply;
    }

    @Override
    @Transactional
    public String updateStatus(Long userNo, Long campaignId, LocalDate applyDate, String statusValue, int workHour) throws Exception{
        // TODO Auto-generated method stub
        log.info("userNo : " + userNo + ", campaignId : " + campaignId + ", applyDate : " + applyDate +  ", statusValue : " + statusValue  + ", applyDateToString : " + applyDate.toString().substring(0, 7));
        String fullAttendDate = applyDate.toString().substring(0, 7);
        log.info("fullAttendDate : " + fullAttendDate);
        String statusInfo = applyMapper.statusInfo(userNo, campaignId, applyDate);
        String applyDateString = applyDate.toString();
        int result = applyMapper.updateStatus(userNo, campaignId, applyDate, statusValue);
        log.info("출결 상태 업데이트 결과 : " + result);

        if (result >= 1) {
            log.info("출결 상태 업데이트 성공: userNo={}, campaignId={}, applyDate={}, newStatus={}", userNo, campaignId, applyDate, statusValue);
            String statusInfoAfter = applyMapper.statusInfo(userNo, campaignId, applyDate);
            log.info("statusInfoAfter : " + statusInfoAfter);

            // 1. 추천인 보상 (지급 대상: 추천인)
            if ("2".equals(statusInfoAfter) && ("1".equals(statusInfo) || "3".equals(statusInfo))) {

            }

            return statusInfoAfter;
        } else {
            throw new Exception("출결 상태 업데이트 실패");
        }
    }

    private void saveLeaderPoint(Long userId, Long campId, LocalDate date, String category, int amount) throws Exception {
        String dateString = date.toString();

        PointHistoryVO vo = new PointHistoryVO();
        vo.setUserNo(userId);
        vo.setAmount(amount);
        vo.setCategory(category);
        vo.setSourceNo(userId);
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
    public String getLeaderStatus(Long userNo, Long campaignId, LocalDate applyDate) throws Exception {        // TODO Auto-generated method stub
        String userStatusInfo = applyMapper.getLeaderStatus(userNo, campaignId, applyDate);
        return userStatusInfo;
    }

    @Override
    public int initStatus(Long userNo, Long campaignId, LocalDate applyDate, String division) throws Exception {
        // TODO Auto-generated method stub
        int initStatus = 0;
        initStatus = applyMapper.initStatus(userNo, campaignId, applyDate, division);
        if(initStatus >= 1) {
            log.info("출결 상태 초기화 성공: userNo={}, campaignId={}, applyDate={}, division={}", userNo, campaignId, applyDate, division);
            applyMapper.deletePoint(userNo, campaignId, applyDate, division);
        } else {
            throw new Exception("출결 상태 초기화 실패");
        }
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

    @Override
    @Transactional
    public int rosterRemove(Long userNo, Long campaignId, LocalDate applyDate) throws Exception {
        // TODO Auto-generated method stub
        int initStatus = applyMapper.rosterRemove(userNo, campaignId, applyDate);
        int applyList = applyMapper.applyList(userNo, campaignId);

        if(applyList == 0) {
            applyMapper.applicantsMinus(campaignId);
        }

        return initStatus;
    }

    @Override
    public Long pointSelect(PointHistoryVO pointHistoryVO) throws Exception {
        log.info("pointSelect called with PointHistoryVO: {}", pointHistoryVO);
        Long pointId = applyMapper.pointSelect(pointHistoryVO);
        log.info("pointSelect returned pointId: {}", pointId);
        return pointId;
    }

    @Override
    public int pointInsert(PointHistoryVO pointHistoryVO) throws Exception {
        log.info("pointInsert called with PointHistoryVO: {}", pointHistoryVO);
        log.info("pointInsert called with userNo: {}, userId: {}, campaignId: {}, localDate: {}", pointHistoryVO.getUserNo(), pointHistoryVO.getUserId(), pointHistoryVO.getCampaignId(), pointHistoryVO.getMissionDate());

        int result = applyMapper.insertPointHistory(pointHistoryVO);
        return result; // 성공적으로 처리되었음을 나타내는 예시 반환값
    }

    @Override
    public int pointUpdate(PointHistoryVO pointHistoryVO) throws Exception {
        log.info("pointUpdate called with PointHistoryVO: {}", pointHistoryVO);
        int result = applyMapper.pointUpdate(pointHistoryVO);
        log.info("pointUpdate result: {}", result);
        return result;
    }

    @Override
    public UserCampaignVO getWorkInfo(String campaignId, LocalDate applyDate) throws Exception {
        UserCampaignVO getWorkInfo = applyMapper.getWorkInfo(campaignId, applyDate);

        return getWorkInfo;
    }

    @Override
    public PointHistoryVO getUserSgnf(int campaignId, List<Long> userNo, String missionDate) throws Exception {
        PointHistoryVO getUserSgnf = applyMapper.getUserSgnf(campaignId, userNo, missionDate);

        return getUserSgnf;
    }

    public List<UserCampaignVO> getUserInfo(UserCampaignVO dto) throws Exception {
        // TODO Auto-generated method stub
        List<UserCampaignVO> getUserInfo = applyMapper.getUserInfo(dto);

        return getUserInfo;
    }

    @Override
    public int pointInsert(List<UserCampaignVO> getUserInfo) throws Exception {
        log.info("pointInsert : " + getUserInfo);
        // TODO Auto-generated method stub
        int result = 0;
        for (UserCampaignVO dto : getUserInfo) {
            Long campaignId = dto.getCampaignId();
            Long userNo = dto.getUserNo();
            String userId = dto.getUserId();

            if (dto.getApplyDate() == null) {
                throw new IllegalArgumentException("🚨 유저번호 [" + userNo + "]의 신청 날짜가 누락되어 작업을 전면 취소합니다.");
            }

            String applyDate = dto.getApplyDate().toString();
            String theMonth = applyDate.toString().substring(0, 7);
            String wageChk = dto.getWageChk();
            String status = dto.getStatus();
            int workHour = dto.getWorkHour();

            int theMonthCnt = applyMapper.fullAttendCount(userNo, theMonth);

            log.info("theMonth : : " + theMonth);
            log.info("theMonthCnt : : " + theMonthCnt);

            PointHistoryVO myPoint = new PointHistoryVO();
            myPoint.setCampaignId(campaignId);
            myPoint.setUserNo(userNo);
            myPoint.setUserId(userId);
            myPoint.setSourceNo(userNo);
            myPoint.setMissionDate(applyDate);
            myPoint.setSettlementStatus("READY");

            // --------------------------------------------------------
            // 🏃‍♂️ 1. 일반 근무 유저 처리
            // --------------------------------------------------------
            if(status.equals("2")) {
                log.info("status 2 : " + status);
                // 일반 근무 포인트
                if(wageChk.equals("02")) {
                    myPoint.setAmount(dto.getCampaignPay() * workHour);
                } else if(wageChk.equals("01")) {
                    myPoint.setAmount(dto.getCampaignPay());
                }else {
                    myPoint.setAmount(dto.getCampaignPay());
                }
                myPoint.setCategory("WORK");

                log.info("myPoint : " + myPoint);
                result = applyMapper.insertPointHistory(myPoint);
            // --------------------------------------------------------
            // 👑 2. 인솔자 처리
            // --------------------------------------------------------
            } else if (status.equals("9")) {
                log.info("status 9 : " + status);
                // 👑 인솔자 기본 급여 적립
                if (dto.getCampaignPay() > 0) {
                    myPoint.setAmount(dto.getCampaignPay());
                    myPoint.setCategory("WORK");
                    applyMapper.insertPointHistory(myPoint);
                } else {
                    log.warn("인솔자 급여가 0이하입니다. 캠페인 ID: {}", campaignId);
                }

                // 담당한 일반 유저(status=2) 수에 따른 인솔 수수료 추가 적립
                if (dto.getLeaderPoint() > 0) {
                    for (UserCampaignVO info : getUserInfo) {
                        PointHistoryVO extraPoint = new PointHistoryVO();
                        if(info.getStatus().equals("2")) {
                            extraPoint.setCampaignId(campaignId);
                            extraPoint.setUserNo(userNo); // 돈 받는 사람: 본인
                            extraPoint.setUserId(userId); // 돈 받는 사람: 본인
                            extraPoint.setMissionDate(applyDate);
                            extraPoint.setSettlementStatus("READY");
                            extraPoint.setAmount(dto.getLeaderPoint());
                            extraPoint.setSourceNo(info.getUserNo());
                            extraPoint.setCategory("LEADER_EXTRA");
                            applyMapper.insertPointHistory(extraPoint);
                        }
                    }
                } else {
                    log.warn("인솔자 포인트가 0이하입니다. 캠페인 ID: {}", campaignId);
                }
            } else {
                log.info("퇴근 상태가 없음.");
                throw new IllegalStateException("🚨 유저번호 [" + userId + "]의 퇴근 상태(" + status + ")가 부적절하여 전체 적립을 취소합니다.");
            }

            // --------------------------------------------------------
            // 🏅 3. [공통 혜택] 만근 포인트 적립 (전체 대상)
            // --------------------------------------------------------
            PointHistoryVO attendPoint = new PointHistoryVO();
            attendPoint.setCampaignId(campaignId);
            attendPoint.setUserNo(userNo);
            attendPoint.setUserId(userId);
            attendPoint.setSourceNo(userNo);
            attendPoint.setMissionDate(applyDate);
            attendPoint.setSettlementStatus("READY");

            if (theMonthCnt == 0) {
                // 1. 이번 달 무단결근이 0회라면 -> 만근 포인트 지급 시도
                // (단, 이미 이번 달에 지급받았는지 체크하는 로직이 있으면 중복 방지에 좋습니다.)
                attendPoint.setAmount(10000);
                attendPoint.setCategory("FULL_ATTEND");

                log.info("attendPoint : " + attendPoint);
                applyMapper.insertPointHistory(attendPoint);
            } else {
                // 2. 이번 달 무단결근이 1회라도 있다면 -> 기존 만근 포인트 회수(0원 처리)
                // 이전에 결근했을 때 미처 처리하지 못했더라도, 오늘 출근 처리 시점에 확실히 잡아냅니다.
                attendPoint.setAmount(0);
                attendPoint.setCategory("FULL_ATTEND");
                applyMapper.updateAttendPoint(attendPoint);
                log.info("해당 월은 만근포인트 없음");
            }

            // --------------------------------------------------------
            // 🤝 4. [공통 혜택] 추천인 포인트 적립 (전체 대상)
            // --------------------------------------------------------
            log.info("userNo + " + userNo);

            UserVO refInfo = userMapper.getReferrerNoById(userNo);
            log.info("refInfo + " + refInfo);
            if (refInfo != null && refInfo.getReferrerNo() != null) {
                PointHistoryVO refPoint = new PointHistoryVO();
                refPoint.setUserNo(refInfo.getReferrerNo()); // 돈 받는 사람: 추천인
                refPoint.setUserId(refInfo.getReferrerId()); // 돈 받는 사람: 추천인
                refPoint.setAmount(5000);
                refPoint.setCategory("REFERRAL");
                refPoint.setSourceNo(userNo); // 원인 제공자: 본인
                refPoint.setMissionDate(applyDate);
                refPoint.setCampaignId(campaignId);
                refPoint.setSettlementStatus("READY");
                log.info("refPoint : " + refPoint.toString());
                int i = applyMapper.insertPointHistory(refPoint);
                log.info("추천인 포인트 적립 결과 : " + i);
            }
            result++;
            log.info("sult +  + " + result);
        }

        return result;
    }

    @Override
    public String updateLeader(Long userNo, Long campaignId, LocalDate applyDate, String status, int workHour)
            throws Exception {
        // TODO Auto-generated method stub
        int updateLeader = applyMapper.updateLeader(userNo, campaignId, applyDate, status);

        return String.valueOf(updateLeader);
    }

    public List<UserVO> userInfoList(Long campaignId, Option option) throws Exception{
        log.info("campaignId : {}, option : {}", campaignId, option);

        List<UserVO> userInfoList = applyMapper.userInfoList(campaignId, option);
        log.info("userInfoList : " + userInfoList);

        return userInfoList;
    }

    public int getPointInfo(String campaignId, LocalDate finalDate) throws Exception {
        int pointInfo = applyMapper.getPointInfo(campaignId, finalDate);

        return pointInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 에러 발생 시 자동 롤백
    public UserCampaignVO overlapTitle(CampaignVO dto) throws Exception {
        log.info("overlapTitle" + dto.getLeaderPay());
        Long campaignId = dto.getCampaignId();
        List<String> userNoList = dto.getUserNos();
        List<String> userIdList = dto.getUserIds();

        int userNoSize = userNoList.size();
        int currentCount = applyMapper.countApplicants(dto.getCampaignId());
        int maxRecruitment = dto.getRecruitmentNum(); // 모집 인원 (예: 4)
        log.info("overlapTitle currentCount : " + currentCount);
        log.info("overlapTitle maxRecruitment : " + maxRecruitment);
        log.info("overlapTitle userNoSize : " + userNoSize);

        if (userNoSize > maxRecruitment) {
            throw new IllegalArgumentException("이미 모집 인원(" + maxRecruitment + "명)이 마감되었습니다.");
        }

        // 1단계: [검증] 체크된 모든 유저들을 먼저 '전부' 검사합니다.
        for (String userNo : userNoList) {
            log.info("userNo : " + userNo);
            log.info("dto : " + dto);

            UserCampaignVO overlapTitle = applyMapper.overlapTitle(dto, userNo);
            // 공백이나 null이 아니라는 것은 무언가 중복된 캠페인 타이틀이 조회되었다는 뜻!
            if (overlapTitle != null) {
                // log.info("중복 발견 - 유저 번호: {}, 중복 캠페인: {}", userNo, overlapTitle);

                return overlapTitle;
                // 컨트롤러의 .catch(error => alert(error.message)) 로 전달될 에러 메시지
                // throw new IllegalArgumentException("이미 [" + overlapTitle + "] 캠페인 일정이 있는 유저가 포함되어 있습니다.\n기간을 확인해 주세요.");

            }
        }
        return null;
    }

    public int userApply(CampaignVO dto) throws Exception {
        log.info("userApply : ");
        // 2단계: [등록] 위의 for문(검증)을 에러 없이 '완전히' 통과했다면 중복이 없는 것입니다.
        // 이제 안전하게 하나씩 insert를 진행합니다.
        Long campaignId = dto.getCampaignId();
        List<String> userNoList = dto.getUserNos();
        List<String> userIdList = dto.getUserIds();

        LocalDate startDate = dto.getEventPeriodStr(); // 예: "2026-01-19" -> LocalDate
        LocalDate endDate = dto.getEventPeriodEnd();   // 예: "2026-01-21" -> LocalDate

        log.info("campId : " + campaignId);
        log.info("userNoList : " + userNoList);
        log.info("userIdList : " + userIdList);
        log.info("startDate : " + startDate);
        log.info("endDate : " + endDate);

        List<LocalDate> datesInRange = Stream.iterate(startDate, date -> date.plusDays(1))
                                            // startDate와 endDate 모두 포함
                                            .limit(endDate.toEpochDay() - startDate.toEpochDay() + 1)
                                            .collect(Collectors.toList());
        log.info("datesInRange : " + datesInRange);

        List<Map<String, Object>> insertList = new ArrayList<>();
        // datesInRange는 이미 자바단에 계산되어 있는 [2026-07-06, 2026-07-07...] 리스트
        log.info("equals(dto.getApplyDateS() : ");
        if ("ALL".equals(dto.getApplyDateS())) {
            // 1. ALL(전체)일 때는 [유저 수 × 날짜 수] 만큼 조합해서 리스트를 만듭니다. (2중 for문)
            for (int i = 0; i < userNoList.size(); i++) {
                for (LocalDate date : datesInRange) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("userNo", userNoList.get(i));
                    row.put("userId", userIdList.get(i));
                    row.put("applyDate", date); // 각 날짜별로 한 행씩 생성
                    insertList.add(row);
                }
            }
        } else {
            // 2. 단일 날짜일 때는 [유저 수] 만큼만 행을 만듭니다. (1중 for문)
            for (int i = 0; i < userNoList.size(); i++) {
                Map<String, Object> row = new HashMap<>();
                row.put("userNo", userNoList.get(i));
                row.put("userId", userIdList.get(i));
                row.put("applyDate", dto.getApplyDateS()); // 사용자가 고른 단일 날짜 고정
                insertList.add(row);
            }
        }

        // 4. 각 날짜별로 DB에 삽입할 DTO 객체 생성
        // Map<String, Object> paramMap = new HashMap<>();
        // paramMap.put("campaignId", dto.getCampaignId());
        // paramMap.put("timeSegment", dto.getTimeSegment());
        // paramMap.put("eventPeriodStr", dto.getEventPeriodStr());
        // paramMap.put("eventPeriodEnd", dto.getEventPeriodEnd());

        // paramMap.put("userNoList", dto.getUserNos()); // [43, 34]
        // paramMap.put("userIdList", dto.getUserIds()); // [admin123, test01]
        // paramMap.put("datesInRange", datesInRange);    // [2026-06-26]
        // paramMap.put("applyDate", dto.getApplyDateS());

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("campaignId", dto.getCampaignId());
        paramMap.put("timeSegment", dto.getTimeSegment());
        paramMap.put("eventPeriodStr", dto.getEventPeriodStr());
        paramMap.put("eventPeriodEnd", dto.getEventPeriodEnd());
        paramMap.put("insertList", insertList); // 🔥 2중 구조를 하나로 푼 이 리스트만 던집니다!

        log.info("완성된 인서트 리스트 개수: " + insertList.size()); // 유저2명 x 날짜4개면 8이 찍혀야 정상

        log.info("paramMpa" + paramMap);

        int result = applyMapper.userApply(paramMap);
        log.info("result + " + result);

        if(result > 0) {
            applyMapper.updateApplicantsNum(campaignId);
        }

        log.info("총 등록된 날짜별 데이터 건수: " + result);

        // 모든 작업이 성공적으로 끝나면 성공 메시지 반환
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 에러 발생 시 자동 롤백
    public int userCancel(CampaignVO dto) throws Exception {
        log.info("overlapTitle campaignVO : " + dto);

        Long campaignId = dto.getCampaignId();
        List<String> userNoList = dto.getUserNos();
        LocalDate startDate = dto.getEventPeriodStr(); // 예: "2026-01-19" -> LocalDate
        LocalDate endDate = dto.getEventPeriodEnd();   // 예: "2026-01-21" -> LocalDate

        List<LocalDate> datesInRange = Stream.iterate(startDate, date -> date.plusDays(1))
            // startDate와 endDate 모두 포함
            .limit(endDate.toEpochDay() - startDate.toEpochDay() + 1)
            .collect(Collectors.toList());

        log.info("userNoList : " + userNoList);
        log.info("datesInRange " + datesInRange);
        // 4. 각 날짜별로 DB에 삽입할 DTO 객체 생성
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("campaignId", dto.getCampaignId());
        paramMap.put("timeSegment", dto.getTimeSegment());
        // paramMap.put("eventPeriodStr", dto.getEventPeriodStr());
        // paramMap.put("eventPeriodEnd", dto.getEventPeriodEnd());

        paramMap.put("userNoList", dto.getUserNos()); // [43, 34]
        // paramMap.put("userIdList", dto.getUserIds()); // [admin123, test01]
        paramMap.put("datesInRange", datesInRange);    // [2026-06-26]

        log.info("paramMap + " + paramMap);

        int result = applyMapper.userCancel(paramMap);
        log.info("result + " + result);

        if(result > 0) {
            applyMapper.updateApplicantsNum(campaignId);
        }

        log.info("총 등록된 날짜별 데이터 건수: " + result);

        // 모든 작업이 성공적으로 끝나면 성공 메시지 반환
        return result;
    }
}