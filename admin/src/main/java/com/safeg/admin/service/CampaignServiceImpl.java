package com.safeg.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.safeg.admin.mapper.CampaignMapper;
import com.safeg.admin.vo.CampaignVO;
import com.safeg.admin.vo.FilesVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
//import com.safeg.admin.vo.k.Files;
import com.safeg.admin.vo.UserVO;
import com.safeg.admin.vo.UserCampaignVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CampaignServiceImpl implements CampaignService{

    @Autowired
    private CampaignMapper campaignMapper;

    @Autowired 
    FileService fileService;

    @Autowired
    AligoSmsService aligoSmsService;

    @Override
    public List<CampaignVO> campaignList(Option option, Page page) throws Exception {
        // TODO Auto-generated method stub
        log.info(":::::::::: CampaignsServiceImpl.list() ::::::::::");
        
        int total = campaignCount(option, page);
        log.info(":::::::::: total :::::::::: " + total);
        page.setTotal(total);
        
        List<CampaignVO> list = campaignMapper.campaignList(option, page);
        
        return list;
    }

    @Override
    public int campaignCount(Option option, Page page) throws Exception {
        return campaignMapper.campaignCount(option, page);
    }
    
    @Override
    @Transactional // 두 작업이 하나의 트랜잭션으로 묶이도록!
    public int campaignInsert(CampaignVO campaignsVO) throws Exception {
        // 1. 캠페인 기본 정보 삽입
        int result = campaignMapper.campaignInsert(campaignsVO);
    
        LocalDate startDate;
        LocalDate endDate;
    
        log.info("등록 처리 impl : " + campaignsVO);
        
        MultipartFile file = campaignsVO.getImage();
        if (campaignsVO.getCampaignId() == null) {
            throw new RuntimeException("캠페인 등록 실패: 캠페인 ID를 가져올 수 없습니다.");
        }
    
        System.out.println("캠페인 '" + campaignsVO.getCampaignTitle() + "' 등록 완료 및 인솔자 연결 완료!");
    
        // 파일 업로드 로직
        if(file != null && !file.isEmpty()){
            FilesVO uploadFile = new FilesVO();
            uploadFile.setFile(file);
            uploadFile.setFileSize(file.getSize());
            uploadFile.setFileType("campaign_File");
            uploadFile.setTargetType("campaign");
            uploadFile.setTargetId(campaignsVO.getCampaignId());
            uploadFile.setMimeType("");
            uploadFile.setId(campaignsVO.getCampaignId());
            uploadFile.setStatusId(campaignsVO.getCampaignId());
            uploadFile.setStatus("campaign");
            log.info("등록 처리 uploadFile : " + uploadFile);
    
            fileService.upload(uploadFile);
        }
    
        // 날짜 파싱
        try {
            startDate = campaignsVO.getEventPeriodStr();
            endDate = campaignsVO.getEventPeriodEnd();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("캠페인 기간 날짜 형식 오류: " + e.getMessage());
        }
    
        // 3. 날짜 리스트 생성 (시작일~종료일이 같아도 최소 1개의 날짜 생성됨)
        List<LocalDate> datesInRange = Stream.iterate(startDate, date -> date.plusDays(1))
                                            .limit(endDate.toEpochDay() - startDate.toEpochDay() + 1)
                                            .collect(Collectors.toList());
        log.info("datesInRange " + datesInRange);
        
        // 4. 각 날짜별로 DB에 삽입할 DTO 객체 생성
        List<UserCampaignVO> dailyEntriesToInsert = new ArrayList<>();

        // ✨ LeaderId가 Null이 아니고 비어있지 않을 때만 진입하도록 수정!
        if (campaignsVO.getLeaderId() != null && !campaignsVO.getLeaderId().isEmpty()) {
            
            for (LocalDate date : datesInRange) {
                UserCampaignVO dailyEntry = new UserCampaignVO();
                dailyEntry.setCampaignId(campaignsVO.getCampaignId());
                
                // 올바른 LeaderId와 LeaderNo 세팅
                dailyEntry.setUserId(campaignsVO.getLeaderId()); 
                dailyEntry.setUserNo(campaignsVO.getLeaderNo());
                
                // 급여가 0원인 4번 캠페인은 'N', 급여가 있는 5번 캠페인은 'Y'로 정상 분기
                if(campaignsVO.getLeaderPay() == 0){
                    dailyEntry.setLeadApply("N");
                } else {
                    dailyEntry.setLeadApply("Y");
                }
                
                dailyEntry.setApplicantsNum(campaignsVO.getApplicantsNum());
                dailyEntry.setEventPeriodStr(campaignsVO.getEventPeriodStr());
                dailyEntry.setEventPeriodEnd(campaignsVO.getEventPeriodEnd());
                dailyEntry.setTimeSegment(campaignsVO.getTimeSegment());
                dailyEntry.setApplyDate(date);
                
                dailyEntriesToInsert.add(dailyEntry);
            }
        }
    
        // 5. 매퍼를 통해 DB에 배치 삽입
        if (!dailyEntriesToInsert.isEmpty()) {
            log.info("dailyEntriesToInsert 정보: " + dailyEntriesToInsert);
            result = campaignMapper.insertCampaignLeaderApply(dailyEntriesToInsert);
        } else {
            log.warn("🚨 경고: 삽입할 인솔자 날짜별 리스트가 비어있습니다. (인솔자 정보 누락 의심)");
        }
    
        // 코드 타입명 바인딩
        if ("01".equals(campaignsVO.getTypeCode())) {
            campaignsVO.setTypeNm("경호");
        } else if ("02".equals(campaignsVO.getTypeCode())) {
            campaignsVO.setTypeNm("진행");
        } else if ("03".equals(campaignsVO.getTypeCode())) {
            campaignsVO.setTypeNm("수행");
        }
    
        // 알리고 알림톡/SMS 발송
        String AppPeriod = campaignsVO.getAppPeriodStr().toString() + " ~ "+ campaignsVO.getAppPeriodEnd().toString();
        String EventPeriod = campaignsVO.getEventPeriodStr().toString() + " ~ "+ campaignsVO.getEventPeriodEnd().toString();
        // aligoSmsService.registrationAsync(campaignsVO.getCompanyPh(), campaignsVO.getTypeNm(), campaignsVO.getCampaignTitle(), campaignsVO.getRecruitmentNum(), AppPeriod, EventPeriod, "https://행집.com/campaign/" + campaignsVO.getCampaignId(), campaignsVO.getLeaderPhone());
    
        return result;
    }

    @Override
    public CampaignVO campaignSelect(String id) throws Exception {
        // TODO Auto-generated method stub
        String status = "1";
        log.info("campaignSelectDetail");
        CampaignVO campaignDetail = campaignMapper.campaignSelect(id);
        return campaignDetail;
    }

    

    @Override
    @Transactional // 두 작업이 하나의 트랜잭션으로 묶이도록!
    public int campaignUpdate(CampaignVO campaignVO) throws Exception {
        log.info("수정 처리 impl : " + campaignVO);
        int result = 0;

        // 기존 데이터 세팅 조회
        UserCampaignVO oldCampaign = campaignMapper.applySelect(campaignVO.getCampaignId());

        int oldNum = oldCampaign.getRecruitmentNum(); // 기존 모집인
        int newNum = campaignVO.getRecruitmentNum(); // 입력 모집인
        int appNum = oldCampaign.getApplicantsNum(); // 기존 신청자
        LocalDate oldAppEnd = oldCampaign.getAppPeriodEnd(); // 기존 신청 시작일

        LocalDate oldStartDate = oldCampaign.getEventPeriodStr(); // 기존 행사 시작 일
        LocalDate oldEndDate = oldCampaign.getEventPeriodEnd(); // 기존 행사 마지막 날
        String isLeader = oldCampaign.getIsLeader(); // 인솔자 Y/N
        String leadApply = oldCampaign.getLeadApply(); // 인솔자 신청 Y/N

        Long campaignId = campaignVO.getCampaignId();
        String timeSegment = campaignVO.getTimeSegment();

        int vacantSeats = newNum - appNum; // 확대 시 빈자리 계산

        // 기존/신규 날짜 리스트 생성
        List<LocalDate> oldDates = oldStartDate.datesUntil(oldEndDate.plusDays(1)).collect(Collectors.toList()); // 기존 날짜 List
        log.info("A oldDates :" + oldDates);
        List<LocalDate> newDates = campaignVO.getEventPeriodStr().datesUntil(campaignVO.getEventPeriodEnd().plusDays(1)).collect(Collectors.toList()); // 입력 날짜 List
        
        int oldSize = oldDates.size(); // 기존 날짜 크기
        int newSize = newDates.size(); // 입력 날짜 크기
        
        LocalDate sourceDate = null;

        UserCampaignVO param = new UserCampaignVO();
        param.setCampaignId(campaignId);
        param.setTimeSegment(timeSegment);

        // ================================================================
        // 파트 1: [정원 변동 관리] - 최상단에서 완벽하게 처리
        // ================================================================
        if (oldNum != newNum) {
            if (oldNum < newNum) { // 정원 확대 시
                if (vacantSeats > 0) {
                    sourceDate = (oldSize > 0) ? newDates.get(oldSize - 1) : null;
                    log.info("A sourceDate {}", sourceDate);

                    List<Long> targetUserNos = campaignMapper.targetUserNos(campaignId, timeSegment, vacantSeats, sourceDate);
                    log.info("정원 확대로 인한 부활 시작! 빈자리: {}명, 시간대: {}", vacantSeats, timeSegment);
                    
                    // 💡 1단계: 해당 시간대(timeSegment)에 탈락한 유저만 선착순 조회
                    // (이를 위해 campaignMapper.targetUserNos 호출 시 파라미터에 timeSegment를 함께 넘겨주거나 객체에 담아 보냅니다)
                    log.info("정원 확대로 인한 기존 신청자 부활 시작! 추가 가능 인원: {}명, 기존 일정 일수: {}일", vacantSeats, oldSize);
                    
                    // 💡 2단계: 날짜별로 돌면서 '그 날짜'의 '그 시간대' 데이터만 정확히 부활
                    if (targetUserNos != null && !targetUserNos.isEmpty()) {
                        log.info("정원 확대로 인한 기존 신청자 부활 시작! 대상 유저: {}", targetUserNos);
                        
                        param.setLimitCount(vacantSeats);
                        param.setUserNos(targetUserNos); // 유저 리스트 세팅

                        for (int i = 0; i < oldSize; i++) {
                            log.info("[부활 진행] 날짜: {}, 제한 인원(LIMIT): {}명", oldDates.get(i), vacantSeats);

                            param.setApplyDate(oldDates.get(i));
                            campaignMapper.reApplyUsers(param);
                            campaignMapper.updateApplicantsNum(campaignId);
                        }
                    }
                }
                campaignMapper.updateIsDeleted(campaignId, "N", "Y");
            }
            else { // 정원 축소 시
                if (appNum >= newNum) { 
                    // [상황 A] 진짜로 정원이 넘치거나 딱 꽉 찼을 때
                    log.info("정원 축소 상태 진입 - 새 정원: " + newNum);
                    campaignMapper.updateIsDeleted(campaignId, "N", "N");
                    int exceedCount = appNum - newNum;
                    if (exceedCount > 0) {
                        log.info("정원 초과로 인한 신청자 탈락 처리! 인원: {}명", exceedCount);
                        campaignMapper.updateUcIsDeleted(campaignId, exceedCount);
                        campaignMapper.updateApplicantsNum(campaignId);
                    } else {

                    }
                } else { // [상황 B] 정원을 줄였지만 여전히 정원에 여유가 있을 때
                    log.info("정원에 여유가 있음. 현재 인원: {}, 새 정원: {}", appNum, newNum);
                    
                    // 여유 자리가 있으므로 캠페인은 계속 모집 중('Y') 상태여야 함
                    campaignMapper.updateIsDeleted(campaignId, "N", "Y");
                    
                    if (vacantSeats > 0) {
                        // 억울하게 잘려 있는 사람 목록을 가져와서 다시 'N'으로 부활시킨다!
                        sourceDate = (oldSize > 0) ? newDates.get(oldSize - 1) : null;
                        List<Long> targetUserNos = campaignMapper.targetUserNos(campaignId, timeSegment, vacantSeats, sourceDate);

                        if (targetUserNos != null && !targetUserNos.isEmpty()) {
                            log.info("정원 축소/유지 중 빈자리 발견으로 인한 기존 탈락자 복구('N'): {}", targetUserNos);
                            param.setLimitCount(vacantSeats);
                            param.setUserNos(targetUserNos); // 유저 리스트 세팅

                            // 💡 [리팩토링] 외부로 빠진 param 오브젝트 재활용
                            for (int i = 0; i < oldSize; i++) {
                                param.setApplyDate(oldDates.get(i));
                                campaignMapper.reApplyUsers(param);
                                campaignMapper.updateApplicantsNum(campaignId);
                            }
                        }
                    }
                }
            }
        }
    
        // ================================================================
        // 파트 2: [인솔자 및 마감일 변경 처리]
        // ================================================================
        if (oldCampaign != null && campaignVO.getLeaderPay() != oldCampaign.getLeaderPay()) {
            String leadApplyStatus = (campaignVO.getLeaderPay() == 0) ? "N" : "Y";
            campaignMapper.updateLeadApply(campaignId, leadApplyStatus);
        }

        if (oldCampaign != null && !campaignVO.getLeaderId().equals(oldCampaign.getUserId())) {
            campaignMapper.leaderUpdate(campaignId, oldCampaign.getUserNo(), campaignVO.getLeaderNo(), campaignVO.getLeaderId());
        }

        boolean isPeriodChanged = !oldAppEnd.equals(campaignVO.getAppPeriodEnd());
        boolean isPeriodShortened = campaignVO.getAppPeriodEnd().isBefore(oldAppEnd); // 마감일이 앞으로 당겨짐(단축)
        boolean isCapacityIncreased = newNum > oldNum;
        boolean increased = newNum > appNum;

        log.info("isPeriodChanged : " + isPeriodChanged);
        log.info("isPeriodShortened : " + isPeriodShortened);
        log.info("isCapacityIncreased : " + isCapacityIncreased);

        // ---------------------------------------------------------------
        // 조건 A: 마감일이 늘어났거나, 정원이 늘어나서 빈자리가 생겼을 때 -> 활성화('Y')
        // && !isPeriodShortened
        // ---------------------------------------------------------------
        if (isCapacityIncreased || (isPeriodChanged)) {
            // 단, 기간이 남아있고 인원도 널널할 때만 켜야 하므로 안전장치 조건을 추가해 줍니다.
            if (appNum < newNum && !campaignVO.getAppPeriodEnd().isBefore(LocalDate.now())) {
                log.info("정원 확대 또는 기간 연장으로 인한 캠페인 활성화('Y')");
                campaignMapper.updateIsActive(campaignId, "Y");
            } else if (appNum >= newNum || isPeriodChanged) {
                log.info("캠페인 상태 변경 시도 -> ID: {}, 상태: {}", campaignId, "N");
                log.info("정원 초과 또는 기간 만료(단축 포함)로 인한 캠페인 마감 처리('N')");
                campaignMapper.updateIsActive(campaignId, "N");
            }
        }
        
        // ---------------------------------------------------------------
        // 조건 B: 질문하신 내용! 마감일이 줄어들었거나 정원이 줄어들었는데, 
        // 현재 인원이 새 정원 이상이거나 오늘 날짜 기준 마감일이 지나버렸다면 -> 마감('N')
        // 현재 날짜로 바꾸고 싶으면 isPeriodShortened 대신 campaignsVO.getAppPeriodEnd().isBefore(LocalDate.now())) {
        // ---------------------------------------------------------------
        
    
        // 원본 캠페인 마스터 테이블 업데이트
        result = campaignMapper.campaignUpdate(campaignVO);
        // ================================================================
        // 파트 3: [날짜 일수 조정 처리]
        // ================================================================
        // 조건 1: 수정한 날짜 크기(일수)가 같을 때 -> 실제 날짜 값이 변했을 때만 변경
        if (newSize == oldSize && (!oldStartDate.equals(campaignVO.getEventPeriodStr()) || !oldEndDate.equals(campaignVO.getEventPeriodEnd()))) {
            for (int i = 0; i < oldSize; i++) {
                log.info("조건 1: 수정한 날짜 크기(일수)가 같을 때 -> 실제 날짜 값이 변했을 때만 변경");
                campaignMapper.updateApplyDate(campaignId, campaignVO.getEventPeriodStr(), campaignVO.getEventPeriodEnd(), oldDates.get(i), newDates.get(i));
            }
        }
        // 조건 2: 수정한 날짜 크기(일수)가 줄어들었을 때 (3일 -> 2일)
        else if (newSize < oldSize) {
            for (int i = 0; i < newSize; i++) {
                campaignMapper.updateApplyDate(campaignId, campaignVO.getEventPeriodStr(), campaignVO.getEventPeriodEnd(), oldDates.get(i), newDates.get(i));
            }
            for (int i = newSize; i < oldSize; i++) {
                campaignMapper.deleteApplyDate(campaignId, oldDates.get(i));
            }
        }
        // 조건 3: 수정한 날짜 크기(일수)가 늘어났을 때 (2일 -> 3일)
        else if (newSize > oldSize) {
            // 1. 기존 일정 분량 우선 이동
            for (int i = 0; i < oldSize; i++) {
                campaignMapper.updateApplyDate(campaignId, campaignVO.getEventPeriodStr(), campaignVO.getEventPeriodEnd(), oldDates.get(i), newDates.get(i));
            }
            
            // 💡 [핵심 추가] 현재 이 캠페인에 정상적으로 참여 중인('N') 유저들의 번호를 싹 긁어옵니다.
            // (이 유저들은 날짜가 늘어났으니 새로 늘어난 날짜에도 'N' 상태로 복구되거나 복사되어야 합니다.)
            List<UserCampaignVO> activeUser = campaignMapper.getActiveUserNos(campaignId, timeSegment); 
            
            // is_deleted의 값이 N
            List<Long> activeUserNos = activeUser.stream()
                                    .map(UserCampaignVO::getUserNo) // 혹은 user -> user.getUserNo()
                                    .collect(Collectors.toList());
            
            log.info("activeUserNos" + activeUserNos);

            sourceDate = newDates.get(oldSize - 1); 
            log.info("sourceDate" + sourceDate);
            // 2. 늘어난 날짜 데이터를 처리하는 루프
            for (int i = oldSize; i < newSize; i++) {
                LocalDate targetDate = newDates.get(i);
                log.info("targetDate" + targetDate);
                // ---------------------------------------------------------------
                // 새로 늘어난 날짜(targetDate)에 과거 탈락 기록('Y')이 있는 유저들을 먼저 'N'으로 부활!
                // ---------------------------------------------------------------
                if (activeUserNos != null && !activeUserNos.isEmpty()) {
                    param.setApplyDate(targetDate);
                    param.setUserNos(activeUserNos); 
                    log.info("[부활 진행] 대상 날짜: {}, 유저목록: {}", targetDate, activeUserNos);
                    campaignMapper.reApplyUsers(param); 
                }

                // 수정 후 apply_date
                // 3. 기존에 데이터가 아예 없던 유저들은 새로 복사(INSERT) 처리 진행
                // 모집인보다 신청인이 더 많을때
                if (increased) {
                    UserCampaignVO reSearchCampaign = campaignMapper.applySelect(campaignVO.getCampaignId());
                    
                    // 수정할 날짜 갯수.
                    List<LocalDate> reSearchDate = reSearchCampaign.getEventPeriodStr().datesUntil(reSearchCampaign.getEventPeriodEnd().plusDays(1)).collect(Collectors.toList()); // 기존 날짜 List

                    if(reSearchDate.size() > 0) {
                        for(int j = 0 ; j < activeUser.size() ; j ++) {
                            log.info("activeUser.size() for 문" + activeUser.get(j).getUserNo());
                            UserCampaignVO applyDateInfo = campaignMapper.applyDateInfo(campaignId, activeUser.get(j).getUserNo(), reSearchDate.get(j), timeSegment); 
                            log.info("applyDateInfo for 문" + applyDateInfo);
                            log.info("newSize for 문" + newSize);
                            log.info("oldDates for 문" + oldDates);
                            log.info("oldDates for 문" + oldDates.size());


                            log.info("C reSearchDate :" + reSearchDate.get(newSize-oldDates.size()));

                            // int dates = newDates - oldDates;

                            if(applyDateInfo != null ) {
                                log.info("C oldDates :" + oldDates);
                                log.info("정원 확대로 기존 날짜 데이터를 새로운 날짜로 복사");
                                UserCampaignVO paramInfo = new UserCampaignVO();
            
                                log.info("oldDates.get(0) A : " + oldDates.get(0));
                                log.info("reSearchDate.get(i) A : " + reSearchDate.get(i));
                                log.info("applyDateInfo.getApplyDate() A : " + applyDateInfo.getApplyDate());

                                paramInfo.setCampaignId(campaignId);
            
                                paramInfo.setApplyDate(reSearchDate.get(i));
                                paramInfo.setBeforeDate(oldDates.get(0));
                                paramInfo.setEventPeriodStr(campaignVO.getEventPeriodStr());
                                paramInfo.setEventPeriodEnd(campaignVO.getEventPeriodEnd());
                                paramInfo.setTimeSegment(timeSegment);

                                log.info("paramInfo A: " + paramInfo);
                                // sourceDate - 복사 대상이 되는 원본 날짜
                                // targetDate - 새로 생성될 날짜
                                paramInfo.setUserNo(activeUser.get(j).getUserNo());
        
                                if ("Y".equals(activeUser.get(j).getLeadApply())) {
                                    paramInfo.setIsLeader("Y");
                                    paramInfo.setLeadApply(activeUser.get(j).getLeadApply());
                                    paramInfo.setStatus("8");
                                    log.info("paramInfo B: " + paramInfo);
                                    // increased - 증가
                                    campaignMapper.copyApplyDateInc(paramInfo);
                                } else {
                                    paramInfo.setIsLeader("N");
                                    paramInfo.setLeadApply(activeUser.get(j).getLeadApply());
                                    paramInfo.setStatus("0");
                                    log.info("paramInfo C: " + paramInfo);
                                    // increased - 증가
                                    campaignMapper.copyApplyDateInc(paramInfo);
                                }
                            }
                        }
                        break;
                    }
                }
            }
            // 4. 날짜별 최종 신청자 수 최신화
            campaignMapper.updateApplicantsNum(campaignId);
        }

        // ================================================================
        // 파트 4: [이미지 처리 파일 업로드]
        // ================================================================
        MultipartFile file = campaignVO.getImage();
        if (file != null && !file.isEmpty()) {
            FilesVO uploadFile = new FilesVO();
            uploadFile.setFile(file);
            uploadFile.setFileSize(file.getSize());
            uploadFile.setFileType("campaign_File");
            uploadFile.setTargetType("campaign");
            uploadFile.setTargetId(campaignId);
            uploadFile.setId(campaignId);
            uploadFile.setStatusId(campaignId);
            uploadFile.setStatus("campaign");
            
            // 💡 주석 가이드: 실구현 시 실제 파일 테이블 인서트 매퍼를 여기에 호출하셔야 저장이 완결됩니다!
            fileService.upload(uploadFile);
        }
    
        return result;
    }

    @Override
    public int campaignDelete(String id) throws Exception {
        int result = campaignMapper.campaignDelete(id);
        
        // 삭제할 파일 처리
        // List<String> deleteFiles = board.getDeleteFiles();
        // if(deleteFiles != null && !deleteFiles.isEmpty()){
        //     for(String fileId : deleteFiles){
        //         log.info("fileId" + fileId);
        //         fileService.delete(fileId);
        //     }
        // }
        return result;
    }

    @Override
    public List<UserVO> leaderList() throws Exception {
        // TODO Auto-generated method stub
        List<UserVO> leaderList = campaignMapper.leaderList();
        return leaderList;
    }

    @Override
    public int applyDelete(String id) throws Exception{
        int result = campaignMapper.applyDelete(id);
        return result;
    }

    @Override
    public List<CampaignVO> securityType() throws Exception {
        // TODO Auto-generated method stub
        List<CampaignVO> securityType = campaignMapper.securityType();
        return securityType;
    }

    @Override
    public List<CampaignVO> campaign07(Option option, Page page) throws Exception {
        // TODO Auto-generated method stub
        log.info(":::::::::: CampaignsServiceImpl.list() ::::::::::");
        
        int total = campNotApplyCount(option, page);
        log.info(":::::::::: total :::::::::: " + total);
        page.setTotal(total);
        
        List<CampaignVO> list = campaignMapper.campaign07(option, page);
        
        return list;
    }

    private int campNotApplyCount(Option option, Page page) throws Exception{
        // TODO Auto-generated method stub
        return campaignMapper.campNotApplyCount(option, page);
    }

    @Transactional(rollbackFor = Exception.class) // 모든 예외에 대해 롤백 설정
    @Override
    public int updateExpiredCampaigns() throws Exception {
        log.info("--- 만료된 캠페인 및 파일 정리 프로세스 시작 ---");

        // 1. 캠페인 상태 변경 (is_active = 'N')
        int updatedCampaignCount = campaignMapper.updateExpiredCampaignsStatus();
        log.info("상태가 'N'으로 변경된 캠페인 수: {}건", updatedCampaignCount);

        // 2. 관련 파일 상태 변경 (is_deleted = 'Y')
        // 주의: 변경된 캠페인이 0건이라도 기한 지난 파일이 있을 수 있으므로 항상 실행하거나,
        // 로직에 따라 updatedCampaignCount > 0 일 때만 실행하도록 분기할 수 있습니다.
        // int updatedFileCount = fileService.updateFileCampaign();
        // log.info("삭제 처리된 관련 파일 수: {}건", updatedFileCount);

        log.info("--- 만료 처리 프로세스 완료 ---");
        return updatedCampaignCount;
    }

    @Override
    public List<CampaignVO> closedCampaign() throws Exception {
        List<CampaignVO> closedCampaign = campaignMapper.closedCampaign();

        return closedCampaign;
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 에러 발생 시 자동 롤백
    public UserCampaignVO overlapTitle(CampaignVO dto) throws Exception {
        Long campaignId = dto.getCampaignId();
        List<String> userNoList = dto.getUserNos();
        List<String> userIdList = dto.getUserIds();

        int currentCount = campaignMapper.countApplicants(dto.getCampaignId());
        int maxRecruitment = dto.getRecruitmentNum(); // 모집 인원 (예: 4)

        if (currentCount >= maxRecruitment) {
            throw new IllegalArgumentException("이미 모집 인원(" + maxRecruitment + "명)이 마감되었습니다.");
        }

        // 1단계: [검증] 체크된 모든 유저들을 먼저 '전부' 검사합니다.
        for (String userNo : userNoList) {
            log.info("userNo : " + userNo);
            log.info("dto : " + dto);

            UserCampaignVO overlapTitle = campaignMapper.overlapTitle(dto, userNo);
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
        // 2단계: [등록] 위의 for문(검증)을 에러 없이 '완전히' 통과했다면 중복이 없는 것입니다.
        // 이제 안전하게 하나씩 insert를 진행합니다.
        Long campaignId = dto.getCampaignId();
        List<String> userNoList = dto.getUserNos();
        List<String> userIdList = dto.getUserIds();

        LocalDate startDate = dto.getEventPeriodStr(); // 예: "2026-01-19" -> LocalDate
        LocalDate endDate = dto.getEventPeriodEnd();   // 예: "2026-01-21" -> LocalDate
        CampaignVO campaignVO = campaignMapper.campaignSelect(String.valueOf(dto.getCampaignId()));

        List<LocalDate> datesInRange = Stream.iterate(startDate, date -> date.plusDays(1))
                                            // startDate와 endDate 모두 포함
                                            .limit(endDate.toEpochDay() - startDate.toEpochDay() + 1)
                                            .collect(Collectors.toList());

        List<Map<String, Object>> insertList = new ArrayList<>();
        // datesInRange는 이미 자바단에 계산되어 있는 [2026-07-06, 2026-07-07...] 리스트

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

        int result = campaignMapper.userApply(paramMap);
        log.info("result + " + result);

        if(result > 0) {
            campaignMapper.updateApplicantsNum(campaignId);
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

        int result = campaignMapper.userCancel(paramMap);
        log.info("result + " + result);

        if(result > 0) {
            campaignMapper.updateApplicantsNum(campaignId);
        }

        log.info("총 등록된 날짜별 데이터 건수: " + result);

        // 모든 작업이 성공적으로 끝나면 성공 메시지 반환
        return result;
    }
}
