package com.safeg.user.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.safeg.user.mapper.MyPageMapper;
import com.safeg.user.mapper.UserMapper;
import com.safeg.user.util.EncryptionUtil;
import com.safeg.user.vo.CalendarEventVO;
import com.safeg.user.vo.FilesVO;
import com.safeg.user.vo.PointHistoryVO;
import com.safeg.user.vo.UserAddressVO;
import com.safeg.user.vo.UserCampaignVO;
import com.safeg.user.vo.UserVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor // ⭐ 3. 이 어노테이션이 있어야 final 필드를 초기화하는 생성자를 Lombok이 자동 생성해줌! ⭐
public class MyPageServiceImpl implements MyPageService {

    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    private MyPageMapper myPageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileService fileService;

    @Override
    public List<LocalDate> getAppliedDatesForUser(String userId, LocalDateTime start, LocalDateTime end) throws Exception {
        // TODO Auto-generated method stub
        // return Arrays.asList(LocalDate.of(2025, 12, 10), LocalDate.of(2025, 12, 15), LocalDate.of(2025, 12, 26));
        return myPageMapper.getAppliedDatesForUser(userId, start, end);
    }
    @Override
    public List<CalendarEventVO> getCompletedDatesForUser(String userId, LocalDateTime start, LocalDateTime end) throws Exception {
        
        // TODO Auto-generated method stub
        // return Arrays.asList(LocalDate.of(2025, 12, 5), LocalDate.of(2025, 12, 12));
        return myPageMapper.getCompletedDatesForUser(userId, start, end);
    }
    @Override
    public UserAddressVO getAddress(Long id) throws Exception {
        // TODO Auto-generated method stub
        return myPageMapper.getAddress(id);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInfo(UserVO userVo) throws Exception {
        // 1. 휴대폰 중복 체크 (나를 제외한 타인 중복 확인)
        String hashedPhone = EncryptionUtil.hash(userVo.getPhoneNum());
        log.info("updateInfo hashedPhone : " + hashedPhone);
        if (userMapper.phoneDuplicate(hashedPhone, userVo.getUserId())) {
            throw new RuntimeException("DUPLICATE_PHONE");
        }
        userVo.setPhoneHash(hashedPhone);
    
        // 2. 회원 기본 정보 업데이트 (전화번호, 이름 등)
        int result = myPageMapper.updateInfo(userVo);
        if (result < 1) throw new RuntimeException("USER_UPDATE_FAILED");
    
        // 3. 주소 처리 (기존 주소 삭제 처리 후 신규 삽입)
        if (userVo.getFullAddress() != null && !userVo.getFullAddress().isEmpty()) {
            // 기존에 등록된 주소가 있는지 확인 (삭제 안 된 것 위주)
            UserAddressVO existingAddress = myPageMapper.getAddress(userVo.getId());
    
            if (existingAddress != null) {
                // [중요] 기존 주소를 'Y'로 마킹 (작성하신 updateAddress 호출)
                myPageMapper.updateAddress(userVo);
                log.info("기존 주소 삭제(Soft Delete) 완료");
            }
            
            // 항상 새로운 주소를 Insert (이력을 남기기 위해)
            myPageMapper.insertAddress(userVo);
            log.info("새로운 주소 데이터 삽입 완료");
        }
    
        return result;
    }

    @Override
    public boolean uploadImage(UserVO userVo) throws Exception {
        // TODO Auto-generated method stub
        
        MultipartFile getCertImage = userVo.getCertImage();
        MultipartFile getIdImage = userVo.getIdImage();
        
        if (userVo.getId() == null) {
            // 캠페인 ID를 가져오지 못했다면 예외 처리
            throw new RuntimeException("인수증 사진 등록 실패: 유저의 ID를 가져올 수 없습니다.");
        }
        boolean result = false;
        FilesVO uploadFileCert = new FilesVO();
        FilesVO uploadFileId = new FilesVO();

        if(getCertImage != null){
            
            // Files uploadFile = new Files();
            // uploadFile.setFile(file);
            // uploadFile.setParentTable("campaign");
            // uploadFile.setParentNo(campaignsVO.getId());
            // uploadFile.setType("main");
            // fileService.upload(uploadFile);
            
            uploadFileCert.setFile(getCertImage);
            uploadFileCert.setFileSize(getCertImage.getSize());
            uploadFileCert.setFileType("user_File");
            uploadFileCert.setTargetType("certificate");
            uploadFileCert.setTargetId(userVo.getId());
            uploadFileCert.setUserId(userVo.getUserId());
            uploadFileCert.setMimeType("");
            log.info("등록 처리 uploadFile : " + uploadFileCert);

            result = fileService.upload(uploadFileCert);
            
        } 
        
        if(getIdImage != null){
            uploadFileId.setFile(getIdImage);
            uploadFileId.setFileSize(getIdImage.getSize());
            uploadFileId.setFileType("user_File");
            uploadFileId.setTargetType("identification");
            uploadFileId.setTargetId(userVo.getId());
            uploadFileId.setUserId(userVo.getUserId());
            uploadFileId.setMimeType("");
            log.info("등록 처리 uploadFile : " + uploadFileId);

            result = fileService.upload(uploadFileId);
        }
        
        return result;
    }

    @Override
    public List<UserCampaignVO> pointList(Long id) throws Exception {
        // TODO Auto-generated method stub
        List<UserCampaignVO> pointList = myPageMapper.pointList(id);

        return pointList;
    }

    @Override
    public List<UserCampaignVO> referrerPayList(Long id) throws Exception {
        // TODO Auto-generated method stub
        List<UserCampaignVO> referrerPayList = myPageMapper.referrerPayList(id);

        return referrerPayList;
    }

    @Override
    public List<UserCampaignVO> leaderPayList(Long id, List<UserCampaignVO> campaignIds) throws Exception {
        // TODO Auto-generated method stub
        List<Long> campaignIdList = campaignIds.stream()
        .map(UserCampaignVO::getCampaignId)
        .collect(Collectors.toList());

        LocalDate targetDate = LocalDate.now();  // 오늘 날짜
        LocalDate startDate = targetDate.withDayOfMonth(1);  // 이번 달 1일
        LocalDate endDate = targetDate.withDayOfMonth(targetDate.lengthOfMonth());  // 이번 달 마지막 날

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);  // 사용자 또는 조건용 id
        params.put("campaignIds", campaignIdList);  // List<Long> 타입 캠페인 아이디 목록
        params.put("startDate", startDate.toString()); // "yyyy-MM-dd" 문자열
        params.put("endDate", endDate.toString());

        log.info("leaderPayList params : " + params);


        // List<PointHistoryVO> leaderPayList = myPageMapper.leaderPayList(id);
        List<UserCampaignVO> leaderPayList = myPageMapper.leaderPayList(params);

        log.info("leaderPayList params : " + leaderPayList);


        return leaderPayList;
    }

    @Override
    public int applyBodyguard(Long id, String guardType) throws Exception {
        // TODO Auto-generated method stub
        log.info("MyPageServiceImpl applyBodyguard id : " + id);
        log.info("MyPageServiceImpl applyBodyguard guardType : " + guardType);
        int result =  myPageMapper.applyBodyguard(id, guardType);

        return result;
    }
    
    @Transactional // 트랜잭션 관리
    @Override
    public boolean changeUserPassword(Long userId, String currentPassword, String newPassword) throws Exception {
        // 1. 현재 사용자 정보(특히 저장된 해시된 비밀번호)를 DB에서 가져옴
        UserVO user = myPageMapper.findUserById(userId); // ⭐ findUserById는 MyPageMapper에 마이클이 추가해야 함! ⭐

        if (user == null) {
            throw new IllegalStateException("해당 사용자를 찾을 수 없습니다.");
        }

        // 2. 입력된 현재 비밀번호와 DB에 저장된 비밀번호가 일치하는지 확인
        // passwordEncoder.matches(평문 비밀번호, 해시된 비밀번호)
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            // 현재 비밀번호 불일치
            return false; // 컨트롤러에서 이에 맞는 메시지 반환
        }

        // 3. 새 비밀번호가 현재 비밀번호와 다른지 확인
        // (필수 사항은 아니지만, 보안 및 UX 측면에서 권장)
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }
        
        // 4. 새 비밀번호를 해싱
        String encodedNewPassword = passwordEncoder.encode(newPassword);

        // 5. DB에 새 비밀번호 저장 (업데이트)
        // ⭐ updatePassword는 MyPageMapper에 마이클이 추가해야 함! ⭐
        int result = myPageMapper.updatePassword(userId, encodedNewPassword);

        return result > 0; // 업데이트 성공 여부 반환
    }
    @Override
    public List<UserCampaignVO> campaignId(Long id) throws Exception {
        // TODO Auto-generated method stub
        List<UserCampaignVO> campaignIds = myPageMapper.campaignId(id);

        return campaignIds;
    }
    @Override
    public int pointFull(Long id) throws Exception {
        // TODO Auto-generated method stub
        int pointFull = myPageMapper.pointFull(id);
        log.info("MyPageServiceImpl pointFull pointFull : " + pointFull);

        return pointFull;
    }
    
}
