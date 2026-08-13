package com.safeg.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.safeg.admin.mapper.UserMapper;
import com.safeg.admin.util.EncryptionUtil;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.UserAuth;
import com.safeg.admin.vo.UserVO;

import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    // @Autowired
    // UserMapper userMapper;

    private final UserMapper userMapper; // User 저장 로직 (DB 접근)
    private final PasswordEncoder passwordEncoder; // BCryptPasswordEncoder가 주입될 거야

    // 생성자 주입
    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // 유저 추가
    @Override
    @Transactional
    public int userJoin(UserVO userVO) throws Exception {
        String id = userVO.getUserId();

        // 4. 암호화된 비밀번호가 담긴 UserVO 객체를 DB에 저장

        int result = userMapper.userJoin(userVO);

        if( result > 0 ) {
            // 회원 기본 권한 등록
            UserAuth userAuth = new UserAuth();
            userAuth.setId(userVO.getId());
            userAuth.setName(id);
            userAuth.setAuthCd("01");
            userAuth.setAuth("ROLE_ADMIN");

            result = userMapper.insertAuth(userAuth);
            log.info("result1 : " + result);
        }

        return result;
    }

    // userList 유저 리스트
    @Override
    public List<UserVO> userList(Option option, Page page) throws Exception {
        // TODO Auto-generated method stub

        int total = userCount(option);
        page.setTotal(total);
        List<UserVO> userList = userMapper.userList(option, page);

        for(int i = 0; i < userList.size(); i++) {
            String rawPhone = userList.get(i).getPhoneNum(); // ex: "01012345678"
            if (rawPhone != null) {
                // 10자리, 11자리 모두 자동으로 하이픈을 찌르는 정규식 적용
                String formattedPhone = rawPhone.replaceAll("[^0-9]", "")
                                                .replaceFirst("(^02|[0-9]{3})([0-9]{3,4})([0-9]{4})$", "$1-$2-$3");
                userList.get(i).setPhoneNum(formattedPhone); // 하이픈이 포함된 값으로 세팅
            }
        }

        return userList;
    }

    // 유저 카운트
    public int userCount(Option option) throws Exception {
        return userMapper.userCount(option);
    }

    // 유저 상세 페이지
    @Override
    public UserVO userSelect(String id) throws Exception {
        // TODO Auto-generated method stub
        UserVO userSelect = userMapper.userSelect(id);

        return userSelect;
    }

    // 유저 업데이트
    @Override
    public int userInfoUpdate(UserVO userVO) throws Exception {
        // TODO Auto-generated method stub
        int result = userMapper.userInfoUpdate(userVO);
        return result;
    }

    @Override
    public int userRemove(String id) throws Exception {
        // TODO Auto-generated method stub
        int result = userMapper.userRemove(id);
        return result;
    }

    @Override
    @Transactional
    // 01 = admin, 02 = user, 03 = leader
    // 02로 변경
    public int userUpdate(Long id) throws Exception{
        // TODO Auto-generated method stub
        String idStr = String.valueOf(id);
        int result = userMapper.userUpdate(idStr, "USER", "02");
        if( result > 0 ){
            log.info("userUpdate 성공");
            int result1 = userMapper.userAuthUpdate(idStr, "ROLE_USER", "02");
            log.info("userAuthUpdate 성공 : " + result1);
        } else {
            log.info("userUpdate 실패");
        }
        return result;
    }

    @Override
    @Transactional
    // 01 = admin, 02 = user, 03 = leader
    // 03로 변경
    public int userLeaderUpdate(Long id) throws Exception{
        // TODO Auto-generated method stub
        log.info("userLeaderUpdate impl 호출 : " + id);
        String idStr = String.valueOf(id);
        int result = userMapper.userUpdate(idStr, "LEADER", "03");
        if( result > 0 ){
            log.info("userUpdate 성공");
            int result1 = userMapper.userAuthUpdate(idStr, "ROLE_LEADER", "03");
            log.info("userAuthUpdate 성공 : " + result1);
        } else {
            log.info("userUpdate 실패");
        }
        return result;
    }

    @Override
    public int userStop(Long id) throws Exception{
        // TODO Auto-generated method stub
        log.info("userLeaderUpdate impl 호출 : " + id);
        String idStr = String.valueOf(id);

        int result = userMapper.userStop(idStr);
        return result;
    }

    @Override
    public int userUnstop(Long id) throws Exception{
        // TODO Auto-generated method stub
        log.info("userLeaderUpdate impl 호출 : " + id);
        String idStr = String.valueOf(id);

        int result = userMapper.userUnstop(idStr);
        return result;
    }

    @Override
    @Transactional
    public int resetAllUserPay() throws Exception {
        // TODO Auto-generated method stub
        int result = userMapper.resetAllUserPay();
        userMapper.resetPointHistory();

        return result;
    }

    @Override
    @Transactional
    public int resetAllUserApply() throws Exception {
        // TODO Auto-generated method stub
        int result = userMapper.resetPointHistory();
        userMapper.resetPointHistory();

        return result;
    }

    @Override
    public List<UserVO> userAddressList() throws Exception {
        // TODO Auto-generated method stub
        List<UserVO> userAddressList = userMapper.userAddressList();

        return userAddressList;
    }

    @Override
    public void guardTypeChange(UserVO userVO) throws Exception {
        // TODO Auto-generated method stub
        userMapper.guardTypeChange(userVO);
    }

    @Override
    public Long referrerId(String referrerId) throws Exception {
        // TODO Auto-generated method stub
        Long referrerNo = userMapper.referrerId(referrerId);
        log.info("referrerId referrerNo : : : : : : : " + referrerNo);
        return referrerNo;
    }
    // // 비밀번호 변경 로직도 유사하게 구현하면 돼
    // public void changePassword(String userId, String newRawPassword) {
    //     UserVO user = userRepository.findByUserId(userId); // 사용자 조회
    //     if (user != null) {
    //         String encodedNewPassword = passwordEncoder.encode(newRawPassword);
    //         user.setPassword(encodedNewPassword);
    //         userRepository.update(user); // DB에 업데이트
    //     }
    // }
    @Override
    @Transactional
    public int settlementAll() throws Exception{
        int insertedCount = userMapper.settlementAll();
        userMapper.resetAllUserApply();

        if (insertedCount > 0) {
            // 2. 정산에 포함된 history 내역들을 'Y'로 변경 (중복 정산 방지)
            userMapper.resetAllUserPay();
        }

        return insertedCount;
    }

    public int updateUserInfo(UserVO userVO) throws Exception {
        log.info("userVo " + userVO);
        int result = userMapper.updateUserInfo(userVO);

        return result;
    }

    @Override
    public List<UserVO> userInfoList(Long campaignId, Option option) throws Exception {
        List<UserVO> userInfoList = userMapper.userInfoList(campaignId, option);

        return userInfoList;
    }

    @Override
    public List<UserVO> userInfoDate(Long campaignId, String applyDate) throws Exception {
        List<UserVO> userInfoDate = userMapper.userInfoDate(campaignId, applyDate);
        log.info("userInfoDate : " + userInfoDate);

        return userInfoDate;
    }

    @Override
    @Transactional
    public int migrateNumber() throws Exception {
        List<UserVO> userList = userMapper.userListAll();
        int count = 0;
        for(int i = 0; i < userList.size(); i++) {
            log.info("userList : {}, userNo {}" , userList.get(i).getPhoneNum(), userList.get(i).getId());
        }

        for (UserVO user : userList) {
            String rawPhone = user.getPhoneNum(); // DB에서 가져온 컬럼 값

            // 1. null이거나 빈 값은 스킵
            if (rawPhone == null || rawPhone.trim().isEmpty()) {
                continue;
            }

            String cleanPhone = null;

            try {
                // Case A: 암호화가 안 된 평문 데이터인 경우 ("010"으로 시작)
                if (rawPhone.startsWith("010")) {
                    cleanPhone = rawPhone.replace("-", ""); // "-"가 있으면 떼고, 없으면 그대로 유지
                }
                // Case B: 암호화되어 있는 데이터인 경우
                else {
                    String decrypted = EncryptionUtil.decrypt(rawPhone);
                    if (decrypted != null) {
                        cleanPhone = decrypted.replace("-", ""); // 복호화 후 "-" 제거
                    }
                }

                log.info("cleanPhone : {}, user.getPhoneNum() {}", cleanPhone, user.getPhoneNum());

                // 2. 하이픈이 제거된 11자리 평문 번호로 양방향 암호문 및 단방향 해시 재생성
                if (cleanPhone != null && !cleanPhone.isEmpty()) {
                    String newEnc = EncryptionUtil.encrypt(cleanPhone);
                    String newHash = EncryptionUtil.hash(cleanPhone);
                    log.info("newEnc : {}, newHash : {}", newEnc, newHash);
                    // 3. DB 일괄 업데이트
                    userMapper.updateUserPhoneData(user.getId(), newEnc, newHash);
                    count++;
                }

            } catch (Exception e) {
                // 혹시라도 복호화에 실패한 이상한 값이 섞여있어도 에러 로그만 남기고 다음 유저로 진행
                log.error("마이그레이션 실패 (userId: {}, rawData: {}) - 원인: {}",
                        user.getId(), rawPhone, e.getMessage());
            }
        }

        log.info("=== 전화번호 데이터 정제 및 마이그레이션 완료 : 총 {}건 ===", count);
        return count;
    }

    public int migrateConfirm() throws Exception {
        List<UserVO> userList = userMapper.userListAll();
        log.info("userList : " + userList.size());

        int count = 0;
        for(int i = 0; i < userList.size(); i++) {
            log.info("userList : " + userList.get(i).getPhoneNum());
        }
        for (UserVO user : userList) {
            // 암호화된 번호 존재 여부 확인
            if (user.getPhoneNum() != null && !user.getPhoneNum().isEmpty()) {
                try {
                    // ① 기존 양방향 암호화 컬럼 복호화 (예: "010-1234-5678")
                    String decrypted = EncryptionUtil.decrypt(user.getPhoneNum());
                    log.info("decrypted : {}, user : {}", decrypted, user.getUserNm());

                    if (decrypted != null && decrypted.contains("-")) {
                        log.info("decrypted : {}, user : {}", decrypted, user.getUserNm());
                    }
                } catch (Exception e) {
                    log.error("마이그레이션 실패 - userId: {}", user.getId(), e);
                }
            }
        }
        return count;
    }
}
