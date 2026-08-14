package com.safeg.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.safeg.admin.mapper.CampaignMapper;
import com.safeg.admin.util.EncryptionUtil;
import com.safeg.admin.vo.CampLeaderVO;
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
    public int campaignInsert(CampaignVO campaignVO) throws Exception {
        log.info("등록 처리 impl : " + campaignVO);
        // 1. 캠페인 기본 정보 삽입
        int activeLeaderCount = 0;

        if (campaignVO.getLeaderList() != null) {
            for (CampLeaderVO leader : campaignVO.getLeaderList()) {
                // 인솔자 ID가 존재하고, 페이가 0보다 큰 경우 카운트 증가
                if (leader.getLeaderNo() != null && !leader.getLeaderPay().equals(0)) {
                    activeLeaderCount++;
                }
            }
        }

        // 계산된 카운트를 campaignVO의 applicantsNum 필드에 세팅!
        campaignVO.setApplicantsNum(activeLeaderCount);

        int result = campaignMapper.campaignInsert(campaignVO);

        Long campaignId = campaignVO.getCampaignId(); // 데이터 타입에 맞게 getter 호출

        log.info("캠페인 등록 후 생성된 ID: " + campaignId);

        LocalDate startDate;
        LocalDate endDate;

        log.info("등록 처리 impl : " + campaignVO);

        MultipartFile file = campaignVO.getImage();
        if (campaignId == null) {
            throw new RuntimeException("캠페인 등록 실패: 캠페인 ID를 가져올 수 없습니다.");
        }

        System.out.println("캠페인 '" + campaignVO.getCampaignTitle() + "' 등록 완료 및 인솔자 연결 완료!");

        // 파일 업로드 로직
        if(file != null && !file.isEmpty()){
            FilesVO uploadFile = new FilesVO();
            uploadFile.setFile(file);
            uploadFile.setFileSize(file.getSize());
            uploadFile.setFileType("campaign_File");
            uploadFile.setTargetType("campaign");
            uploadFile.setTargetId(campaignId);
            uploadFile.setMimeType("");
            uploadFile.setId(campaignId);
            uploadFile.setStatusId(campaignId);
            uploadFile.setStatus("campaign");
            log.info("등록 처리 uploadFile : " + uploadFile);

            fileService.upload(uploadFile);
        }

        // 날짜 파싱
        try {
            startDate = campaignVO.getEventPeriodStr();
            endDate = campaignVO.getEventPeriodEnd();
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

        // // ✨ LeaderId가 Null이 아니고 비어있지 않을 때만 진입하도록 수정!
        // if (campaignsVO.getLeaderId() != null && !campaignsVO.getLeaderId().isEmpty()) {

        //     for (LocalDate date : datesInRange) {
        //         UserCampaignVO dailyEntry = new UserCampaignVO();
        //         dailyEntry.setCampaignId(campaignId);

        //         // 올바른 LeaderId와 LeaderNo 세팅
        //         dailyEntry.setUserId(campaignsVO.getLeaderId());
        //         dailyEntry.setUserNo(campaignsVO.getLeaderNo());

        //         // 급여가 0원인 4번 캠페인은 'N', 급여가 있는 5번 캠페인은 'Y'로 정상 분기
        //         if(campaignsVO.getLeaderPay() == 0){
        //             dailyEntry.setLeadApply("N");
        //         } else {
        //             dailyEntry.setLeadApply("Y");
        //         }

        //         dailyEntry.setApplicantsNum(campaignsVO.getApplicantsNum());
        //         dailyEntry.setEventPeriodStr(campaignsVO.getEventPeriodStr());
        //         dailyEntry.setEventPeriodEnd(campaignsVO.getEventPeriodEnd());
        //         dailyEntry.setTimeSegment(campaignsVO.getTimeSegment());
        //         dailyEntry.setApplyDate(date);

        //         dailyEntriesToInsert.add(dailyEntry);
        //     }
        // }

        // 5. 매퍼를 통해 DB에 배치 삽입
        if (!dailyEntriesToInsert.isEmpty()) {
            log.info("dailyEntriesToInsert 정보: " + dailyEntriesToInsert);
            result = campaignMapper.insertCampaignLeaderApply(dailyEntriesToInsert);
        } else {
            log.warn("🚨 경고: 삽입할 인솔자 날짜별 리스트가 비어있습니다. (인솔자 정보 누락 의심)");
        }

        // 코드 타입명 바인딩
        if ("01".equals(campaignVO.getTypeCode())) {
            campaignVO.setTypeNm("경호");
        } else if ("02".equals(campaignVO.getTypeCode())) {
            campaignVO.setTypeNm("진행");
        } else if ("03".equals(campaignVO.getTypeCode())) {
            campaignVO.setTypeNm("수행");
        }

        // 알리고 알림톡/SMS 발송
        String AppPeriod = campaignVO.getAppPeriodStr().toString() + " ~ "+ campaignVO.getAppPeriodEnd().toString();
        String EventPeriod = campaignVO.getEventPeriodStr().toString() + " ~ "+ campaignVO.getEventPeriodEnd().toString();

        // 1. 방어막: leaderList가 비어있거나 인솔자가 없는 경우 안전하게 처리
        String leaderPh = "";
        if (campaignVO.getLeaderList() != null && !campaignVO.getLeaderList().isEmpty()) {
            // 0번째 인솔자 번호 추출 (null이면 공백 처리)
            leaderPh = campaignVO.getLeaderList().get(0).getLeaderPh();
            if (leaderPh == null) leaderPh = "";
        }

        String companyPh = campaignVO.getCompanyPh();

        // 2. 알림톡 발송 판단 로직
        // 인솔자 번호가 없거나, 업체 번호와 인솔자 번호가 완전히 같은 경우 -> 업체에만 1번 발송
        // if (leaderPh.isEmpty() || companyPh.equals(leaderPh)) {
        //     aligoSmsService.registrationAsync(companyPh, campaignVO.getTypeNm(), campaignVO.getCampaignTitle(),
        //         campaignVO.getRecruitmentNum(), AppPeriod, EventPeriod,
        //         "https://행집.com/apply/userCampaignApply/" + campaignId, companyPh);
        // }
        // // 업체 번호와 인솔자 번호가 서로 다른 경우 -> 각각 1번씩 총 2번 발송
        // else {
        //     // 인솔자에게 발송 (💡 campaignVO.getLeaderPhone() 대신 통일성 있게 leaderPh 변수 사용을 권장합니다)
        //     aligoSmsService.registrationAsync(leaderPh, campaignVO.getTypeNm(), campaignVO.getCampaignTitle(),
        //         campaignVO.getRecruitmentNum(), AppPeriod, EventPeriod,
        //         "https://행집.com/apply/userCampaignApply/" + campaignId, leaderPh);

        //     // 업체에게 발송
        //     aligoSmsService.registrationAsync(companyPh, campaignVO.getTypeNm(), campaignVO.getCampaignTitle(),
        //         campaignVO.getRecruitmentNum(), AppPeriod, EventPeriod,
        //         "https://행집.com/apply/userCampaignApply/" + campaignId, companyPh);
        // }

        return result;
    }

    public void campLeaderInsert(CampLeaderVO leader) throws Exception {
        log.info("campLeaderInsert 호출됨. leaderList: " + leader);
        campaignMapper.campLeaderInsert(leader);

        if (leader != null) {
            log.info("번째 인솔자 ID: " + leader.getLeaderId());
            log.info("번째 인솔자 leaderNo: " + leader.getLeaderNo());
            log.info("번째 인솔자 LEADER POINT: " + leader.getLeaderPoint());
        }
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
        log.info("overlapTitle" + dto.getLeaderPay());
        Long campaignId = dto.getCampaignId();
        List<String> userNoList = dto.getUserNos();
        List<String> userIdList = dto.getUserIds();

        int userNoSize = userNoList.size();
        int currentCount = campaignMapper.countApplicants(dto.getCampaignId());
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

    @Override
    @Transactional(rollbackFor = Exception.class) // 에러 발생 시 자동 롤백
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

    public UserCampaignVO applySelect(Long campaignId) throws Exception {
        UserCampaignVO applySelect = campaignMapper.applySelect(campaignId);
        return applySelect;
    }

    @Override
    @Transactional
    public int leaderUpdate(CampaignVO dto) throws Exception {
        Long campaignId = dto.getCampaignId();
        List<CampLeaderVO> newList = dto.getLeaderList() != null ? dto.getLeaderList() : Collections.emptyList();
        CampaignVO oldCampaign = campaignMapper.campaignSelect(String.valueOf(campaignId));

        boolean isSamePeriod = Objects.equals(oldCampaign.getEventPeriodStr(), dto.getEventPeriodStr())
                        && Objects.equals(oldCampaign.getEventPeriodEnd(), dto.getEventPeriodEnd());

        // 1. 전체 행사 기간 날짜 리스트 (신규 추가 시 INSERT 할 용도)
        List<LocalDate> campaignDates = Stream.iterate(dto.getEventPeriodStr(), date -> date.plusDays(1))
            .limit(dto.getEventPeriodEnd().toEpochDay() - dto.getEventPeriodStr().toEpochDay() + 1)
            .collect(Collectors.toList());
        log.info(campaignDates.toString());
        log.info("행사 기간이 변경되었는지 여부: {}", isSamePeriod);
        log.info(" 행사 기간: {}", campaignDates.get(0));
        log.info(" 행사 기간: {}", campaignDates.get(campaignDates.size() - 1));
        log.info("행사 기간이 변경되어 캠페인 날짜를 업데이트했습니다. 새로운 행사 기간: {} ~ {}", campaignDates.get(0), campaignDates.get(campaignDates.size() - 1));
        // 2. DB에 저장된 기존 인솔자 목록 조회
        List<CampLeaderVO> dbList = campaignMapper.campaignLeader(campaignId);

        if(!isSamePeriod) {
            log.warn("행사 기간이 변경되었습니다. 인솔자 정보를 업데이트하기 전에 행사 기간을 먼저 확인해주세요.");
            campaignMapper.updateCampaignDate(dto);

            for (LocalDate date : campaignDates) {
                campaignMapper.updateApplyDate(campaignId, date);
                // campaignMapper.deleteLeader(dbLeader);
            }

            // 3. [삭제 처리] DB엔 있지만 프론트 목록에서 사라진 인솔자 DELETE
            for (CampLeaderVO dbLeader : dbList) {
                boolean existsInNew = newList.stream().anyMatch(n ->
                    Objects.equals(n.getLeaderNo(), dbLeader.getLeaderNo())
                );

                if (!existsInNew) {
                    campaignMapper.deleteLeader(dbLeader);
                }
            }

            // 4. [업데이트 / 삽입 처리] 프론트에서 넘어온 인솔자 목록 처리
            for (CampLeaderVO newLeader : newList) {
                newLeader.setCampaignId(campaignId); // FK 세팅
                boolean isExistInDb = dbList.stream().anyMatch(db ->
                    newLeader.getLeaderNo() != null && Objects.equals(db.getLeaderNo(), newLeader.getLeaderNo())
                );

                if (isExistInDb) {
                    // 기존 인솔자 -> 모든 날짜 행 UPDATE (금액/포인트 반영)
                    campaignMapper.updateLeader(newLeader);
                } else {
                    // 신규 인솔자 -> 행사 전체 날짜 수만큼 반복 INSERT
                    for (LocalDate date : campaignDates) {
                        newLeader.setApplyDate(date);
                        campaignMapper.insertLeader(newLeader);
                    }
                }
            }
        } else {

        }





        return 1;
    }
    // 1. 기존 인솔자 목록과 신규 인솔자 목록 비교
    // 2. 행사 기간이 변경되었는지 확인
    // 3. DB에만 존재하는 인솔자 삭제
    // 4. 신규 인솔자 삽입 및 기존 인솔자 업데이트
    // 5. 인솔자 신청 여부 업데이트
    // 6. 캠페인 활성화/마감 처리
    // 7. 캠페인 마스터 테이블 업데이트
    // 8. 날짜 일수 조정 처리
    // 9. 이미지 처리 파일 업로드
    // ================================================================
    public int campaignUpdate(CampaignVO dto) throws Exception {
        // 일정이 추가가 아닌 전부 바뀌면 insert가 아니라 update가 됨.
        log.info("수정 처리 impl : {}", dto);
        int result = 0;

        // 1. 기존 데이터 조회
        UserCampaignVO oldCampaign = campaignMapper.applySelect(dto.getCampaignId());

        int oldRecNum = oldCampaign.getRecruitmentNum(); // 기존 모집인
        int appNum = oldCampaign.getApplicantsNum(); // 기존 신청자
        int newRecNum = dto.getRecruitmentNum(); // 입력 모집인

        LocalDate oldAppEnd = oldCampaign.getAppPeriodEnd(); // 기존 신청 마지막일
        LocalDate oldStartDate = oldCampaign.getEventPeriodStr(); // 기존 행사 시작 일
        LocalDate oldEndDate = oldCampaign.getEventPeriodEnd(); // 기존 행사 마지막 날

        Long campaignId = dto.getCampaignId();
        String timeSegment = dto.getTimeSegment();

        int vacantSeats = newRecNum - appNum; // 확대 시 빈자리 계산

        // 기존/신규 날짜 리스트 생성
        // oldDates : [2026-08-20, 2026-08-21, 2026-08-22, 2026-08-23]
        List<LocalDate> oldDates = oldStartDate.datesUntil(oldEndDate.plusDays(1)).collect(Collectors.toList()); // 기존 날짜 List
        // newDates : [2026-08-20, 2026-08-21, 2026-08-22, 2026-08-23]
        List<LocalDate> newDates = dto.getEventPeriodStr().datesUntil(dto.getEventPeriodEnd().plusDays(1)).collect(Collectors.toList()); // 입력 날짜 List

        int oldDateSize = oldDates.size(); // 기존 날짜 크기
        int newDateSize = newDates.size(); // 입력 날짜 크기

        LocalDate sourceDate = null;

        UserCampaignVO param = new UserCampaignVO();
        param.setCampaignId(campaignId);
        param.setTimeSegment(timeSegment);

        // ---------------------------------------------------------------
        // [STEP 1] 캠페인 상태값(Y/N) 판단 및 세팅
        // ---------------------------------------------------------------
        boolean isExpired = dto.getAppPeriodEnd().isBefore(LocalDate.now()); // 오늘 날짜보다 마감일이 이전이면 만료
        boolean isFull = appNum >= newRecNum; // 신청자가 정원 이상인지 여부

        log.info("isExpired(만료 여부): {}, isFull(정원 초과 여부): {}", isExpired, isFull);

        // 2. 상태값 단일 조건 분기
        if (!isExpired && !isFull) {
            // 기간도 남아있고 잔여석도 있는 경우 -> 활성화('Y')
            log.info("정원 여유 및 기간 유효로 인한 캠페인 활성화('Y')");
            campaignMapper.updateIsActive(campaignId, "Y");
        } else {
            // 2. 그 외 모든 케이스 (정원 초과 OR 기간 만료) -> 마감('N')
            log.info("[캠페인 마감] 사유 - 정원초과(isFull): {}, 기간만료(isExpired): {} -> ID: {}", isFull, isExpired, campaignId);
            campaignMapper.updateIsActive(campaignId, "N");
        }

        // boolean isPeriodChanged = !oldAppEnd.equals(dto.getAppPeriodEnd()); // 모집일
        // boolean isPeriodShortened = dto.getAppPeriodEnd().isBefore(oldAppEnd); // 마감일이 앞으로 당겨짐(단축)
        // boolean isCapacityIncreased = newRecNum > oldRecNum; // 모집인 증가 여부
        // boolean increased = newRecNum > appNum; // 모집인보다 신청인이 더 많을때

        // log.info("isPeriodChanged : " + isPeriodChanged);
        // log.info("isPeriodShortened : " + isPeriodShortened);
        // log.info("isCapacityIncreased : " + isCapacityIncreased);

        // // ---------------------------------------------------------------
        // // 조건 A: 마감일이 늘어났거나, 정원이 늘어나서 빈자리가 생겼을 때 -> 활성화('Y')
        // // && !isPeriodShortened
        // // ---------------------------------------------------------------
        // // if (!isPeriodShortened || (isPeriodChanged)) {
        // //     // 단, 기간이 남아있고 인원도 널널할 때만 켜야 하므로 안전장치 조건을 추가해 줍니다.
        // //     if (appNum < newRecNum && !campaignVO.getAppPeriodEnd().isBefore(LocalDate.now())) {
        // //         log.info("정원 확대 또는 기간 연장으로 인한 캠페인 활성화('Y')");
        // //         campaignMapper.updateIsActive(campaignId, "Y");
        // //     } else if (appNum >= newRecNum || isPeriodChanged) {
        // //         log.info("캠페인 상태 변경 시도 -> ID: {}, 상태: {}", campaignId, "N");
        // //         log.info("정원 초과 또는 기간 만료(단축 포함)로 인한 캠페인 마감 처리('N')");
        // //         campaignMapper.updateIsActive(campaignId, "N");
        // //     }
        // // }

        // ---------------------------------------------------------------
        // [STEP 2] 날짜 변경(일수 조정) 처리
        // ---------------------------------------------------------------
        // 조건 1: 수정한 날짜 크기(일수)와 기존 날짜 크기 같을 때
        if (newDateSize == oldDateSize && (!oldStartDate.equals(dto.getEventPeriodStr()) || !oldEndDate.equals(dto.getEventPeriodEnd()))) {
            // 일수는 같으나 날짜 자체가 이동한 경우
            log.info("조건 1: 수정한 날짜 크기(일수)가 같을 때 -> 실제 날짜 값이 변했을 때만 변경");
            for (int i = 0; i < oldDateSize; i++) {
                log.info("조건 1: 수정한 날짜 크기(일수)가 같을 때 -> 실제 날짜 값이 변했을 때만 변경");
                campaignMapper.updateUserDate(dto, oldDates.get(i), newDates.get(i));
                campaignMapper.updateLeaderDate(dto, oldDates.get(i), newDates.get(i));
            }
        }
        // 조건 2: 수정한 날짜 크기(일수)가 줄어들었을 때 (3일 -> 2일)
        else if (newDateSize < oldDateSize) {
            // 일수가 줄어든 경우
            for (int i = 0; i < newDateSize; i++) {
                campaignMapper.updateUserDate(dto, oldDates.get(i), newDates.get(i));
                campaignMapper.updateLeaderDate(dto, oldDates.get(i), newDates.get(i));
            }
            for (int i = newDateSize; i < oldDateSize; i++) {
                campaignMapper.deleteApplyDate(campaignId, oldDates.get(i));
                campaignMapper.deleteApplyDateLeader(campaignId, oldDates.get(i));
            }
        }
        // 조건 3: 수정한 날짜 크기(일수)가 늘어났을 때 (2일 -> 3일)
        else if (newDateSize > oldDateSize) {
            // 일수가 늘어난 경우 (기존 일수 이동 + 신규 날짜에 데이터 복사)
            log.info("조건 3: 수정한 날짜 크기(일수)가 늘어났을 때 (2일 -> 3일)");
            // 1. 기존 일정 분량 우선 이동
            for (int i = 0; i < oldDateSize; i++) {
                campaignMapper.updateUserDate(dto, oldDates.get(i), newDates.get(i));
                campaignMapper.updateLeaderDate(dto, oldDates.get(i), newDates.get(i));
            }

            sourceDate = newDates.get(oldDateSize - 1); // 복사 대상 원본 날짜
            log.info("sourceDate: {}", sourceDate);

            // 2-1. 유저 날짜 확장 처리
            List<Long> activeUserNos = campaignMapper.getActiveUserNos(campaignId, timeSegment); // is_deleted의 값이 N
            log.info("activeUserNos: {}", activeUserNos);


            // 기존 전체 날짜 리스트
            List<LocalDate> reSearchDate = oldCampaign.getEventPeriodStr().datesUntil(oldCampaign.getEventPeriodEnd().plusDays(1)).collect(Collectors.toList()); // 기존 날짜 List
            log.info("reSearchDate" + reSearchDate);

            if (activeUserNos != null && !activeUserNos.isEmpty()) {
                // 2-2. 늘어난 날짜 데이터를 처리하는 루프
                for (int i = oldDateSize; i < newDateSize; i++) {
                    LocalDate targetDate = newDates.get(i); // 새로 늘어난 대상 날짜
                    log.info("[늘어난 날짜 처리] i = {}, 대상 날짜(targetDate) = {}", i, targetDate);
                    // ---------------------------------------------------------------
                    // 새로 늘어난 날짜(targetDate)에 과거 탈락 기록('Y')이 있는 유저들을 먼저 'N'으로 부활!
                    // ---------------------------------------------------------------
                    // 2-3. 과거 삭제 기록('Y')이 있던 유저들 일괄 부활('N')
                    param.setApplyDate(targetDate);
                    param.setUserNos(activeUserNos);
                    param.setAppliedStrDate(dto.getAppPeriodStr());
                    param.setAppliedEndDate(dto.getAppPeriodEnd());
                    param.setEventPeriodStr(dto.getEventPeriodStr());
                    param.setEventPeriodEnd(dto.getEventPeriodEnd());
                    param.setTimeSegment(timeSegment);

                    log.info("[부활 진행] 대상 날짜: {}, 유저목록: {}", targetDate, activeUserNos);
                    campaignMapper.reApplyUser(param); // UPDATE user_campaign SET is_deleted = 'N'

                    // 날짜가 확장되었으므로 기존 활성 유저는 무조건 신규 날짜로 복사
                    for (Long userNo : activeUserNos) {
                        // 원본 날짜(sourceDate)의 신청 정보 조회
                        UserCampaignVO applyDateInfo = campaignMapper.applyDateInfo(campaignId, userNo, sourceDate, timeSegment);
                        if(applyDateInfo != null ) {
                            UserCampaignVO paramInfo = new UserCampaignVO();
                            paramInfo.setCampaignId(campaignId);
                            paramInfo.setUserNo(userNo);

                            // ★ [핵심 수정] 복사 destination은 새로 늘어난 targetDate가 되어야 합니다!
                            paramInfo.setApplyDate(targetDate);
                            paramInfo.setBeforeDate(sourceDate); // 원본 날짜 전달

                            paramInfo.setEventPeriodStr(dto.getEventPeriodStr());
                            paramInfo.setEventPeriodEnd(dto.getEventPeriodEnd());
                            paramInfo.setTimeSegment(timeSegment);
                            paramInfo.setStatus("0");

                            log.info("[신규 날짜 데이터 복사/INSERT] 유저: {}, 적용날짜: {}", userNo, targetDate);
                            campaignMapper.copyUser(paramInfo);
                        }
                    }
                }
            }

            // 2-4. 인솔자 날짜 확장 처리 (★ return 0 버그 수정: null/empty 체크 후 내부 실행)
            List<Long> activeLeaderNos = campaignMapper.getActiveLeaderNos(campaignId); // is_deleted의 값이 N
            log.info("activeLeaderNos: {}", activeLeaderNos);
            // 복사 원본이 될 기존의 마지막 날짜 (sourceDate)

            if (activeLeaderNos != null && !activeLeaderNos.isEmpty()) {
                for (int i = oldDateSize; i < newDateSize; i++) {
                    LocalDate targetDate = newDates.get(i); // 새로 늘어난 대상 날짜
                    log.info("[늘어난 날짜 처리] i = {}, 대상 날짜(targetDate) = {}", i, targetDate);
                    // ---------------------------------------------------------------
                    // 새로 늘어난 날짜(targetDate)에 과거 탈락 기록('Y')이 있는 유저들을 먼저 'N'으로 부활!
                    // ---------------------------------------------------------------

                    // 2-1. 과거 삭제 기록('Y')이 있던 유저들 일괄 부활('N')
                    param.setApplyDate(targetDate);
                    param.setUserNos(activeLeaderNos);
                    param.setAppliedStrDate(dto.getAppPeriodStr());
                    param.setAppliedEndDate(dto.getAppPeriodEnd());
                    param.setEventPeriodStr(dto.getEventPeriodStr());
                    param.setEventPeriodEnd(dto.getEventPeriodEnd());
                    param.setTimeSegment(timeSegment);

                    log.info("[부활 진행] 대상 날짜: {}, 유저목록: {}", targetDate, activeLeaderNos);
                    campaignMapper.reApplyLeader(param); // UPDATE user_campaign SET is_deleted = 'N'

                    for (Long userNo : activeLeaderNos) {
                        // 원본 날짜(sourceDate)의 신청 정보 조회
                        UserCampaignVO applyDateInfo = campaignMapper.applyDateInfoLeader(campaignId, userNo, sourceDate, timeSegment);
                        if(applyDateInfo != null ) {
                            UserCampaignVO paramInfo = new UserCampaignVO();
                            paramInfo.setCampaignId(campaignId);
                            paramInfo.setUserNo(userNo);

                            // ★ [핵심 수정] 복사 destination은 새로 늘어난 targetDate가 되어야 합니다!
                            paramInfo.setApplyDate(targetDate);
                            paramInfo.setBeforeDate(sourceDate); // 원본 날짜 전달
                            paramInfo.setAppliedStrDate(dto.getAppPeriodStr());
                            paramInfo.setAppliedEndDate(dto.getAppPeriodEnd());
                            paramInfo.setEventPeriodStr(dto.getEventPeriodStr());
                            paramInfo.setEventPeriodEnd(dto.getEventPeriodEnd());
                            paramInfo.setTimeSegment(timeSegment);
                            paramInfo.setStatus("8");

                            log.info("[신규 날짜 데이터 복사/INSERT] 유저: {}, 적용날짜: {}", userNo, targetDate);
                            campaignMapper.copyLeader(paramInfo);
                        }
                    }
                }
            }
            // 4. 날짜별 최종 신청자 수 최신화
            campaignMapper.updateApplicantsNum(campaignId);
        }


        // ---------------------------------------------------------------
        // [STEP 3] 정원 변동 처리 (확대 / 축소)
        // ---------------------------------------------------------------
        List<LocalDate> retainDates = new ArrayList<>(oldDates);
        retainDates.retainAll(newDates); // 교집합(유지되는 날짜)
        List<LocalDate> targetDates = retainDates.isEmpty() ? newDates : retainDates;

        if (oldRecNum < newRecNum) { // 정원 확대 시
            if (vacantSeats > 0) {
                // 1. 날짜가 늘어났거나 그대로일 때 (oldSize <= newSize 그리고 oldSize > 0)
                // 결과: newDates 리스트에서 기존 일정의 마지막 위치(인덱스 oldSize - 1)에 해당하는 날짜를 가져옵니다.
                // 예시:oldSize = 2, newSize = 4 인 경우:
                // 조건: 2 > 0 && 2 <= 4 -> True 실행: newDates.get(1) -> newDates의
                // 2번째 날짜 반환
                // 2. 날짜가 줄어들었을 때 (oldSize > newSize)
                // 결과: null
                // 예시:oldSize = 4, newSize = 2 인 경우:
                // 조건: 4 > 0 && 4 <= 2 -> False 실행: null 반환
                // 3. 기존 날짜가 아예 없었을 때 (oldSize = 0)
                // 결과: null
                // 예시:oldSize = 0, newSize = 3 인 경우:
                // 조건: 0 > 0 -> False 실행: null 반환
                sourceDate = (oldDateSize > 0 && oldDateSize <= newDateSize) ? newDates.get(oldDateSize - 1) : null;
                // 정원 확대로 인해 기존 탈락자 중에서 부활시킬 대상 유저를 조회 (is_deleted = 'Y'인 유저 중에서 선착순으로 vacantSeats만큼 조회)
                List<Long> targetUserNos = campaignMapper.targetUserNos(campaignId, timeSegment, vacantSeats, sourceDate);
                List<Long> targetLeaderNos = campaignMapper.targetLeaderNos(campaignId, sourceDate);
                log.info("정원 확대로 인한 기존 탈락자 부활 대상 유저 조회 완료! sourceDate: {}, targetUserNos: {}, targetLeaderNos: {}", sourceDate, targetUserNos, targetLeaderNos);

                if (targetUserNos != null && !targetUserNos.isEmpty()) {
                    // 💡 1단계: 날짜별로 돌면서 '그 날짜'의 '그 시간대' 데이터만 정확히 부활
                    log.info("정원 확대로 인한 기존 신청자 부활 시작! 대상 유저: {}", targetUserNos);
                    param.setUserNos(targetUserNos);
                    for (int i = 0; i < newDateSize; i++) {
                        LocalDate currentDate = newDates.get(i); // ★ oldDates가 아니라 newDates 사용!

                        param.setLimitCount(vacantSeats);
                        param.setAppliedStrDate(dto.getAppPeriodStr());
                        param.setAppliedEndDate(dto.getAppPeriodEnd());
                        param.setEventPeriodStr(dto.getEventPeriodStr());
                        param.setEventPeriodEnd(dto.getEventPeriodEnd());

                        param.setApplyDate(currentDate);

                        log.info("[부활 진행] 적용 날짜: {}, 대상 유저: {}", currentDate, targetUserNos);

                        int count = campaignMapper.reApplyUser(param);
                        log.info("날짜 [{}] 부활 처리 완료 건수: {}", currentDate, count);
                    }
                }
                // ★ 인솔자 부활 루프
                if (targetLeaderNos != null && !targetLeaderNos.isEmpty()) {
                    param.setUserNos(targetLeaderNos);
                    for (int i = 0; i < newDateSize; i++) {
                        param.setApplyDate(newDates.get(i));
                        campaignMapper.reApplyLeader(param);
                    }
                }
                campaignMapper.updateApplicantsNum(campaignId);
            }
            if (oldRecNum < newRecNum && oldDateSize < newDateSize) {
                campaignMapper.updateIsDeleted(campaignId, "N", "Y");
            }
        } else { // 정원 축소 시
            // 문제점 : 모집인 줄고, 일정이 늘어나면 > 유저와 인솔자 날짜 복구 안 됨 (해당 apply_date에 데이터가 있는데 UPDATE가 안 됨)
            if (appNum >= newRecNum) {
                // [상황 A] 진짜로 정원이 넘치거나 딱 꽉 찼을 때
                campaignMapper.updateIsDeleted(campaignId, "N", "N");

                int exceedCount = appNum - newRecNum;

                if (exceedCount > 0) {
                    log.info("정원 초과로 인한 신청자 탈락 처리! 인원: {}명", exceedCount);
                    campaignMapper.updateUcIsDeleted(campaignId, exceedCount);
                    campaignMapper.updateApplicantsNum(campaignId);
                }
            } else { // [상황 B] 정원을 줄였지만 여전히 정원에 여유가 있을 때
                // 여유 자리가 있으므로 캠페인은 계속 모집 중('Y') 상태여야 함
                campaignMapper.updateIsDeleted(campaignId, "N", "Y");

                if (vacantSeats > 0) {
                    // 억울하게 잘려 있는 사람 목록을 가져와서 다시 'N'으로 부활시킨다!
                    sourceDate = (oldDateSize > 0) ? newDates.get(oldDateSize - 1) : null;

                    List<Long> targetUserNos = campaignMapper.targetUserNos(campaignId, timeSegment, vacantSeats, sourceDate);

                    if (targetUserNos != null && !targetUserNos.isEmpty()) {
                        param.setLimitCount(vacantSeats);
                        param.setUserNos(targetUserNos); // 유저 리스트 세팅

                        // 💡 [리팩토링] 외부로 빠진 param 오브젝트 재활용
                        for (LocalDate curDate : newDates) { // ★ oldDates 대신 newDates 기준
                            param.setApplyDate(curDate);
                            campaignMapper.reApplyUser(param);
                        }
                    }
                }
            }
        }


        // ---------------------------------------------------------------
        // [STEP 4] 인솔자(CampLeader) 목록 수정/삭제/신규 추가
        // ---------------------------------------------------------------
        // 프론트에서 받은 인솔자 목록
        List<CampLeaderVO> newList = dto.getLeaderList() != null ? dto.getLeaderList() : Collections.emptyList();
        // 기존 DB에 저장된 인솔자 목록
        List<CampLeaderVO> dbLeaderList = campaignMapper.campaignLeader(campaignId);

        // 3. [삭제 처리] DB엔 있지만 프론트 목록에서 사라진 인솔자 DELETE
        for (CampLeaderVO dbLeader : dbLeaderList) {
            boolean existsInNew = newList.stream().anyMatch(n ->
                Objects.equals(n.getLeaderNo(), dbLeader.getUserNo())
            );

            if (!existsInNew) {
                log.info("삭제 처리: DB에만 존재하는 인솔자 감지! 삭제 진행: " + dbLeader);
                campaignMapper.deleteLeader(dbLeader);
            }
        }

        // 4. [업데이트 / 삽입 처리] 프론트에서 넘어온 인솔자 목록 처리
        for (CampLeaderVO newLeader : newList) {
            log.info("Processing newLeader: " + newLeader);
            newLeader.setCampaignId(campaignId);

            // 타입 차이 방지를 위해 String.valueOf() 사용
            boolean isExistInDb = dbLeaderList.stream().anyMatch(db ->
                db.getUserNo() != null && newLeader.getLeaderNo() != null &&
                Objects.equals(String.valueOf(db.getUserNo()), String.valueOf(newLeader.getLeaderNo()))
            );
            log.info("인솔자 No [{}] -> DB 존재 여부: {}", newLeader.getLeaderNo(), isExistInDb);

            if (isExistInDb) {
                // [기존 인솔자] 행사 기간 전체 날짜에 대해 UPDATE 실행
                for (LocalDate date : newDates) {
                    newLeader.setApplyDate(date);
                    // 날짜별로 이미 존재하는 행은 UPDATE (금액/포인트 변경 반영)
                    // updateLeader의 쿼리가 (WHERE campaign_id = #{campaignId} AND user_no = #{leaderNo} AND apply_date = #{applyDate}) 인지 확인
                    int updatedRows = campaignMapper.updateLeader(newLeader);
                    log.info("기존 인솔자 날짜별 UPDATE 시도: " + newLeader + ", 날짜: " + date + ", 업데이트된 행 수: " + updatedRows);
                    // 만약 해당 날짜에 데이터가 없어서 UPDATE된 행이 0개라면 새로 INSERT
                    if (updatedRows == 0) {
                        log.info("해당 날짜에 데이터가 없어 신규 INSERT 진행: " + newLeader + ", 날짜: " + date);
                        campaignMapper.insertLeader(newLeader);
                    } else {
                        log.info("기존 인솔자 날짜별 UPDATE 완료: " + newLeader + ", 날짜: " + date);
                    }
                }
            } else {
                log.info("신규 인솔자 감지! INSERT 진행: " + newLeader);
                // [신규 인솔자] 행사 기간 전체 날짜 수만큼 반복 INSERT
                for (LocalDate date : newDates) {
                    newLeader.setApplyDate(date);
                    // newLeader.setIsDeleted("N");
                    log.info("신규 인솔자 INSERT 진행: " + newLeader + ", 날짜: " + date);
                    campaignMapper.insertLeader(newLeader);
                }
            }
        }

        // ---------------------------------------------------------------
        // [STEP 5] 데이터 동기화 & 원본 마스터 테이블 최종 업데이트 & 파일 업로드
        // ---------------------------------------------------------------
        // 1. 신청자 수 최종 재계산
        campaignMapper.updateApplicantsNum(campaignId);

        // 2. 캠페인 마스터 테이블 최종 업데이트
        result = campaignMapper.campaignUpdate(dto);

        // ================================================================
        // 파트 4: [이미지 처리 파일 업로드]
        // ================================================================
        MultipartFile file = dto.getImage();
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

            fileService.upload(uploadFile);
        }

        return result;

    }
}
