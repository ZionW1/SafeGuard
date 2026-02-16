package com.safeg.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.safeg.admin.mapper.UserMapper;
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

    @Override
    public List<UserVO> userList(Option option, Page page) throws Exception {
        // TODO Auto-generated method stub
        log.info("userList impl 호출");
        List<UserVO> userList = userMapper.userList(option, page);

        return userList;
    }

    @Override
    public UserVO userSelect(String id) throws Exception {
        // TODO Auto-generated method stub
        log.info("userSelect impl 호출");
        UserVO userSelect = userMapper.userSelect(id);

        return userSelect;    }

    @Override
    public int userInfoUpdate(UserVO userVO) throws Exception {
        // TODO Auto-generated method stub
        int result = userMapper.userInfoUpdate(userVO);
        return result;
    }

    @Override
    public int userRemove(String id) throws Exception {
        // TODO Auto-generated method stub
        log.info("userRemove impl 호출 : " + id);
        int result = userMapper.userRemove(id);
        return result;
    }

    @Override
    @Transactional
    // 01 = admin, 02 = user, 03 = leader
    // 02로 변경
    public int userUpdate(Long id) throws Exception{
        // TODO Auto-generated method stub
        log.info("userUpdate impl 호출 : " + id);
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
    public int resetAllUserPay() throws Exception {
        // TODO Auto-generated method stub
        int result = userMapper.resetAllUserPay();
        return result;
    }

    @Override
    public List<UserVO> userAddressList() throws Exception {
        // TODO Auto-generated method stub
        List<UserVO> userAddressList = userMapper.userAddressList();
        log.info("userAddressList : " + userAddressList);
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
}
