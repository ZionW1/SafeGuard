package com.safeg.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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

    @Override
    public List<CampaignVO> campaignList(Option option, Page page) throws Exception {
        // TODO Auto-generated method stub
        log.info(":::::::::: CampaignsServiceImpl.list() ::::::::::");
        
        int total = campaignCount(option);
        log.info(":::::::::: total :::::::::: " + total);
        page.setTotal(total);
        
        List<CampaignVO> list = campaignsMapper.campaignList(option, page);
        
        return list;
    }

    @Override
    public int campaignCount(Option option) throws Exception {
        return campaignsMapper.campaignCount(option);
    }
    
    @Override
    @Transactional // 두 작업이 하나의 트랜잭션으로 묶이도록!
    public int campaignInsert(CampaignVO campaignsVO) throws Exception {
        // TODO Auto-generated method stub
        int result = campaignsMapper.campaignInsert(campaignsVO);

        LocalDate startDate;
        LocalDate endDate;

        log.info("등록 처리 impl : " + campaignsVO);
        
        MultipartFile file = campaignsVO.getImage();
        if (campaignsVO.getCampaignId() == null) {
            // 캠페인 ID를 가져오지 못했다면 예외 처리
            throw new RuntimeException("캠페인 등록 실패: 캠페인 ID를 가져올 수 없습니다.");
        }

        // 2. user_campaign 테이블에 인솔자 데이터 추가
        // UserCampaignDto 또는 Map 등으로 데이터를 준비
        UserCampaignVO userCampaignVO = new UserCampaignVO();
        userCampaignVO.setCampaignId(campaignsVO.getCampaignId());
        userCampaignVO.setIsSelected("1");
        userCampaignVO.setUserNo(campaignsVO.getLeaderNo()); // 인솔자(사용자) ID
        userCampaignVO.setUserId(campaignsVO.getLeaderCode()); // 인솔자(사용자) ID
        userCampaignVO.setPfmcScore(campaignsVO.getLeaderPay()); // 인솔자(사용자) ID
        userCampaignVO.setRole("LEADER"); // 인솔자 역할을 명시 (예: LEADER, PARTICIPANT 등)

        // int result1 = campaignsMapper.leaderInsert(userCampaignVO);

        System.out.println("캠페인 '" + campaignsVO.getCampaignTitle() + "' 등록 완료 및 인솔자(ID: " + userCampaignVO + ") 연결 완료!");

        if(file != null){
            
            // Files uploadFile = new Files();
            // uploadFile.setFile(file);
            // uploadFile.setParentTable("campaign");
            // uploadFile.setParentNo(campaignsVO.getId());
            // uploadFile.setType("main");
            // fileService.upload(uploadFile);
            
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

        try {
            startDate = campaignsVO.getEventPeriodStr(); // 예: "2026-01-19" -> LocalDate
            endDate = campaignsVO.getEventPeriodEnd();   // 예: "2026-01-21" -> LocalDate
        } catch (DateTimeParseException e) {
            // 날짜 형식 파싱에 실패하면 예외 처리
            throw new IllegalArgumentException("캠페인 기간 날짜 형식 오류: " + e.getMessage());
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
            dailyEntry.setCampaignId(campaignsVO.getCampaignId());
            dailyEntry.setUserId(campaignsVO.getLeaderCode());
            dailyEntry.setUserNo(campaignsVO.getLeaderNo());
            dailyEntry.setApplicantsNum(campaignsVO.getApplicantsNum());
            dailyEntry.setEventPeriodStr(campaignsVO.getEventPeriodStr());
            dailyEntry.setEventPeriodEnd(campaignsVO.getEventPeriodEnd());
            
            dailyEntry.setApplyDate(date);
            dailyEntriesToInsert.add(dailyEntry);
        }

        // 5. 매퍼를 통해 DB에 배치 삽입 (또는 하나씩 삽입)
        if (!dailyEntriesToInsert.isEmpty()) {
            log.info("dailyEntriesToInsert " + dailyEntriesToInsert);

            result = campaignsMapper.insertCampaignLeaderApply(dailyEntriesToInsert);
            // applyMapper.insertUserCampaignPeriod(dailyEntriesToInsert); // 아래 Mapper 메서드 참조
        }

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
    public int campaignUpdate(CampaignVO campaignsVO) throws Exception {
        // TODO Auto-generated method stub
        log.info("수정 처리 impl : " + campaignsVO);
        // campaignsVO.setCampaignStatusId(1);

        int result = campaignsMapper.campaignUpdate(campaignsVO);
        log.info("수정 처리 campaignsVO : " + campaignsVO);

        MultipartFile file = campaignsVO.getImage();

        if (file != null && !file.isEmpty()) {
            // Files uploadFile = new Files();
            // uploadFile.setFile(file);
            // uploadFile.setParentTable("campaign");
            // uploadFile.setParentNo(campaignsVO.getId());
            // uploadFile.setType("main");
            // fileService.upload(uploadFile);
            
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

            //uploadFile.setCampaignId(campaignsVO.getId());
            fileService.upload(uploadFile);
            
        }
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
    public List<CampaignVO> securityType() throws Exception {
        // TODO Auto-generated method stub
        List<CampaignVO> securityType = campaignsMapper.securityType();
        return securityType;
    }

    @Override
    public List<CampaignVO> campaign07(Option option, Page page) throws Exception {
        // TODO Auto-generated method stub
        log.info(":::::::::: CampaignsServiceImpl.list() ::::::::::");
        
        int total = campaignCount(option);
        log.info(":::::::::: total :::::::::: " + total);
        page.setTotal(total);
        
        List<CampaignVO> list = campaignsMapper.campaign07(option, page);
        
        return list;
    }
    @Transactional // 트랜잭션 관리
    public int updateExpiredCampaigns() throws Exception{
        log.info("만료된 캠페인 상태 업데이트 시작...");
        int updatedCount = campaignsMapper.updateExpiredCampaignsStatus();
        log.info("만료된 캠페인 {}건이 'Y'로 업데이트 되었습니다.", updatedCount);
        return updatedCount;
    }

}
