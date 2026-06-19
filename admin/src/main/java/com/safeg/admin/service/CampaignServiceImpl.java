package com.safeg.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
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
    private CampaignMapper campaignsMapper;

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
        
        List<CampaignVO> list = campaignsMapper.campaignList(option, page);
        
        return list;
    }

    @Override
    public int campaignCount(Option option, Page page) throws Exception {
        return campaignsMapper.campaignCount(option, page);
    }
    
    @Override
    @Transactional // 두 작업이 하나의 트랜잭션으로 묶이도록!
    public int campaignInsert(CampaignVO campaignsVO) throws Exception {
        // 1. 캠페인 기본 정보 삽입
        int result = campaignsMapper.campaignInsert(campaignsVO);
    
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
            result = campaignsMapper.insertCampaignLeaderApply(dailyEntriesToInsert);
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
        aligoSmsService.registrationAsync(campaignsVO.getCompanyPh(), campaignsVO.getTypeNm(), campaignsVO.getCampaignTitle(), campaignsVO.getRecruitmentNum(), AppPeriod, EventPeriod, "https://행집.com/campaign/" + campaignsVO.getCampaignId(), campaignsVO.getLeaderPhone());
    
        return result;
    }

    @Override
    public CampaignVO campaignSelect(String id) throws Exception {
        // TODO Auto-generated method stub
        String status = "1";
        log.info("campaignSelectDetail");
        CampaignVO campaignDetail = campaignsMapper.campaignSelect(id);
        return campaignDetail;
    }

    

    @Override
    @Transactional // 두 작업이 하나의 트랜잭션으로 묶이도록!
    public int campaignUpdate(CampaignVO campaignsVO) throws Exception {
        // TODO Auto-generated method stub
        log.info("수정 처리 impl : " + campaignsVO);
        int result = 0;
        // [기존 날짜 리스트, 인솔자, 모집인원 수]
        UserCampaignVO oldCampaign = campaignsMapper.applySelect(campaignsVO.getCampaignId());

        int oldNum = oldCampaign.getRecruitmentNum();  // 기존 정원 (예: 10명)
        int newNum = campaignsVO.getRecruitmentNum();  // 새 정원 (예: 5명)
        int appNum = oldCampaign.getApplicantsNum();   // 현재 신청한 사람 수 (예: 8명)
        
        // 정원에 변동이 있을 때만 로직 실행
        if (oldNum != newNum) {
            // Case 1: 정원을 늘렸을 때 (예: 10명 -> 15명) -> 무조건 활성화
            if (oldNum < newNum) {
                campaignsMapper.updateIsDeleted(campaignsVO.getCampaignId(), "N", "Y");
            } 
            // Case 2: 정원을 줄였을 때 (예: 10명 -> 5명)
            else {
                // 이미 신청한 사람이 새 정원을 초과했거나 꽉 찼다면 -> 모집 마감(비활성화)
                // 이미 신청한 사람 수가 새 정원을 초과했거나 딱 찼다면
                ////////////////////여기 부터 06.09일
                if (appNum >= newNum) { 
                    // 💡 초과했을 때뿐만 아니라 '딱 꽉 찼을 때'도 마감되어야 하므로 >= 가 안전합니다.
                    // 1. 캠페인 자체는 모집 마감(비활성화) 처리
                    campaignsMapper.updateIsDeleted(campaignsVO.getCampaignId(), "N", "N");
                    // 2. 몇 '명'이 초과되었는지 인원수 계산 (예: 8명 - 5명 = 3명)
                    int exceedCount = appNum - newNum;
                    if (exceedCount > 0) {
                        log.info("정원 초과로 인한 후순위 신청자 자르기 시작! 초과 인원: {}명", exceedCount);
                        // 매퍼 호출: "이 캠페인에서 늦게 신청한 순서대로 exceedCount만큼 잘라서 Y로 바꿔라"
                        // 매퍼 호출: 초과된 사람 수(exceedCount)를 던집니다.
                        campaignsMapper.updateUcIsDeleted(campaignsVO.getCampaignId(), exceedCount);
                        // 3. ★ [중요] 캠페인 본체 테이블의 applicants_num 컬럼도 줄어든 정원 숫자로 맞춰줍니다.
                        // (3명을 잘랐으니 신청자 수도 새 정원인 5명으로 업데이트해 주는 것이 안전합니다.)
                        campaignsMapper.updateApplicantsNum(campaignsVO.getCampaignId(), newNum);                    }
                } 
                else {
                    // 정원을 줄였어도 아직 자리가 남아있다면 -> 계속 모집(활성화)
                    campaignsMapper.updateIsDeleted(campaignsVO.getCampaignId(), "N", "Y");
                }
            }
        }

        if (oldCampaign != null && !campaignsVO.getLeaderId().equals(oldCampaign.getUserId())) {
            campaignsMapper.leaderUpdate(campaignsVO.getCampaignId(), oldCampaign.getUserNo(), campaignsVO.getLeaderNo(), campaignsVO.getLeaderId());
        }

        LocalDate oldStartDate = oldCampaign.getEventPeriodStr();
        LocalDate oldEndDate = oldCampaign.getEventPeriodEnd();
        String isLeader = oldCampaign.getIsLeader();

        // [기존 날짜 리스트]
        List<LocalDate> oldDates = oldStartDate.datesUntil(oldEndDate.plusDays(1)).collect(Collectors.toList());
        result = campaignsMapper.campaignUpdate(campaignsVO);

        // 2. [새로운 날짜 리스트] (작성하신 코드)
        List<LocalDate> newDates = campaignsVO.getEventPeriodStr().datesUntil(campaignsVO.getEventPeriodEnd().plusDays(1)).collect(Collectors.toList());

        int oldSize = oldDates.size(); // 기존 3일 (27, 28, 29)
        int newSize = newDates.size(); // 새 일정 일수

        log.info("oldDates : " + oldDates);
        log.info("newDates : " + newDates);
        log.info("oldSize : " + oldSize);
        log.info("newSize : " + newSize);
        
        // 상황 A: 일수가 줄어들었을 때 (3일 -> 2일)
        Long campaignId = campaignsVO.getCampaignId();

        // ================================================================
        // 조건 1: 수정한 날짜 크기(일수)가 같을 때 (예: 3일 -> 3일)
        // ================================================================
        if (newSize == oldSize) {
            // 순서대로 1:1 매칭해서 업데이트 (27일->02일, 28일->03일, 29일->04일)
            for (int i = 0; i < oldSize; i++) {
                LocalDate oldDate = oldDates.get(i);
                LocalDate newDate = newDates.get(i);
                
                log.info("매퍼 호출 - 캠페인ID: {}, oldDate: {}, newDate: {}, getEventPeriod: {}, getEventPeriod: {}", campaignId, oldDate, newDate, campaignsVO.getEventPeriodStr(), campaignsVO.getEventPeriodEnd());

                // 매퍼 호출: "이 캠페인의 기존 oldDate 날짜인 사람들을 전부 newDate로 바꿔라"
                campaignsMapper.updateApplyDate(campaignId, campaignsVO.getEventPeriodStr(), campaignsVO.getEventPeriodEnd(), oldDate, newDate);
            }
        }

        // ================================================================
        // 조건 2: 수정한 날짜 크기(일수)가 줄어들었을 때 (예: 3일 -> 2일)
        // ================================================================
        else if (newSize < oldSize) {
            // 1. 새 일정 크기만큼은 정상적으로 1:1 매칭해서 이동 (1~2일차 이동)
            for (int i = 0; i < newSize; i++) {
                LocalDate oldDate = oldDates.get(i);
                LocalDate newDate = newDates.get(i);
                log.info("매퍼 호출 - 캠페인ID: {}, oldDate: {}, newDate: {}, getEventPeriod: {}, getEventPeriod: {}", campaignId, oldDate, newDate, campaignsVO.getEventPeriodStr(), campaignsVO.getEventPeriodEnd());
                campaignsMapper.updateApplyDate(campaignId, campaignsVO.getEventPeriodStr(), campaignsVO.getEventPeriodEnd(), oldDate, newDate);
            }
            
            // 2. 갈 곳이 없어진 초과 인원(3일차)은 삭제 처리 ('Y')
            for (int i = newSize; i < oldSize; i++) {
                LocalDate leftoverDate = oldDates.get(i);
                log.info("삭제 처리 매퍼 호출 - 캠페인ID: {}, leftoverDate: {}", campaignId, leftoverDate);
                
                // 매퍼 호출: "이 캠페인의 이 남은 날짜에 신청한 사람들은 삭제('Y') 처리해라"
                campaignsMapper.deleteApplyDate(campaignId, leftoverDate);
            }
        }

        // ================================================================
        // 조건 3: 수정한 날짜 크기(일수)가 늘어났을 때 (예: 2일 -> 3일)
        // ================================================================
        else if (newSize > oldSize) {
            // 1. 기존에 존재하던 일수만큼은 새 일정 날짜로 먼저 1:1 이동 (1~2일차 이동)
            for (int i = 0; i < oldSize; i++) {
                LocalDate oldDate = oldDates.get(i);
                LocalDate newDate = newDates.get(i);
                log.info("매퍼 호출 - 캠페인ID: {}, oldDate: {}, newDate: {}, getEventPeriod: {}, getEventPeriod: {}", campaignId, oldDate, newDate, campaignsVO.getEventPeriodStr(), campaignsVO.getEventPeriodEnd());

                campaignsMapper.updateApplyDate(campaignId, campaignsVO.getEventPeriodStr(), campaignsVO.getEventPeriodEnd(), oldDate, newDate);
            }
            
            // 2. 늘어난 날짜만큼 루프 돌며 데이터 복사 (INSERT)
            // 복사의 기준(원본)은 이동이 완료된 '기존 일정의 마지막 날'로 잡습니다.
            LocalDate sourceDate = newDates.get(oldSize - 1); // 예: 이동 완료된 2일차 날짜
            log.info("복사 원본 날짜(sourceDate) : " + sourceDate);

            for (int i = oldSize; i < newSize; i++) {
                LocalDate targetDate = newDates.get(i); // 새롭게 늘어난 3일차 날짜 (29일)

                if(isLeader.equals("Y")) {
                    campaignsVO.setStatusCode("8");
                    campaignsVO.setLeaderCode("Y");
                    continue; // 인솔자 데이터는 복사 후 다음 루프로 넘어감
                } else {
                    campaignsVO.setStatusCode("2");
                    campaignsVO.setLeaderCode("N");
                }
                // 매퍼 호출: "sourceDate에 일하는 사람들을 복사해서 targetDate에 새로 꼽아라"
                campaignsMapper.copyApplyDate(campaignId, campaignsVO.getStatusCode(), campaignsVO.getLeaderCode(), sourceDate, targetDate, campaignsVO.getEventPeriodStr(), campaignsVO.getEventPeriodEnd());
            }
        }

        MultipartFile file = campaignsVO.getImage();

        if (file != null && !file.isEmpty()) {
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
        }

        return result;
    }

    @Override
    public int campaignDelete(String id) throws Exception {
        int result = campaignsMapper.campaignDelete(id);
        
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
        List<UserVO> leaderList = campaignsMapper.leaderList();
        return leaderList;
    }

    @Override
    public int applyDelete(String id) throws Exception{
        int result = campaignsMapper.applyDelete(id);
        return result;
    }

    @Override
    public List<CampaignVO> securityType() throws Exception {
        // TODO Auto-generated method stub
        List<CampaignVO> securityType = campaignsMapper.securityType();
        return securityType;
    }

    @Override
    public List<CampaignVO> campaign07(Option option, Page page) throws Exception {
        // TODO Auto-generated method stub
        log.info(":::::::::: CampaignsServiceImpl.list() ::::::::::");
        
        int total = campNotApplyCount(option, page);
        log.info(":::::::::: total :::::::::: " + total);
        page.setTotal(total);
        
        List<CampaignVO> list = campaignsMapper.campaign07(option, page);
        
        return list;
    }

    private int campNotApplyCount(Option option, Page page) throws Exception{
        // TODO Auto-generated method stub
        return campaignsMapper.campNotApplyCount(option, page);
    }

    @Transactional(rollbackFor = Exception.class) // 모든 예외에 대해 롤백 설정
    @Override
    public int updateExpiredCampaigns() throws Exception {
        log.info("--- 만료된 캠페인 및 파일 정리 프로세스 시작 ---");

        // 1. 캠페인 상태 변경 (is_active = 'N')
        int updatedCampaignCount = campaignsMapper.updateExpiredCampaignsStatus();
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
        List<CampaignVO> closedCampaign = campaignsMapper.closedCampaign();

        return closedCampaign;
    }
}
