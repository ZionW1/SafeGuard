package com.safeg.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.safeg.user.vo.FilesVO;
import com.safeg.user.vo.UserAuth;
import com.safeg.user.vo.UserCampaignVO;
import com.safeg.user.vo.UserVO;
import com.safeg.user.vo.Users;
import com.safeg.user.mapper.UserMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public boolean login(UserVO userVo, HttpServletRequest request) throws Exception {
        log.info(":::::::::: UserServiceImpl - login ::::::::::");
        // 💍 토큰 생성
        String username = userVo.getUserId();    // 아이디
        String password = userVo.getPassword();    // 암호화되지 않은 비밀번호
        UsernamePasswordAuthenticationToken token 
            = new UsernamePasswordAuthenticationToken(username, password);
        
        // 토큰을 이용하여 인증
        Authentication authentication = authenticationManager.authenticate(token);
        
        // 인증 여부 확인
        boolean result = authentication.isAuthenticated();

        // 인증이 성공하면 SecurityContext에 설정
        if (result) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 세션에 인증 정보 설정 (세션이 없으면 새로 생성)
            HttpSession session = request.getSession(true);  // 세션이 없으면 새로 생성
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        }else{
            log.error("바로 로그인 인증에 실패하였습니다.");
        }

        return result;
    }

    @Override
    public UserVO select(String username) throws Exception {
        UserVO user = userMapper.select(username);
        return user;
    }

    @Override
    @Transactional // 트랜잭션 처리를 설정 (회원정보, 회원권한)
    public int join(UserVO userVo) throws Exception {
        String id = userVo.getUserId();
        String name = userVo.getName();
        // String password = userVo.getPassword();
        // String encodedPassword = passwordEncoder.encode(password);  // 🔒 비밀번호 암호화
        // userVo.setPassword(encodedPassword);
        // getInputReferrerUserId
        Long referrerNo = userMapper.getReferrerNo(userVo);

        if (referrerNo == null || referrerNo == 0) {
            userVo.setReferrerNo(null); // DB에 NULL 저장
        } else {
            userVo.setReferrerNo(referrerNo); // 유효한 추천인의 id(BIGINT) 저장
        }
        
        // 회원 등록
        int result = userMapper.join(userVo); // ⭐join 메서드가 userVo 하나만 받아서 처리하도록!

        log.info("result : " + result);
        log.info("result : " + userVo.getId());

        if( result > 0 ) {
            // 회원 기본 권한 등록
            UserAuth userAuth = new UserAuth();
            userAuth.setId(userVo.getId());
            userAuth.setName(id);
            userAuth.setAuthCd("02");
            userAuth.setAuth("ROLE_USER");

            result = userMapper.insertAuth(userAuth);
            log.info("result1 : " + result);
        }
        return result;
    }

    @Override
    public int update(Users user) throws Exception {
        int result = userMapper.update(user);
        return result;
    }

    @Override
    public int insertAuth(UserAuth userAuth) throws Exception {
        int result = userMapper.insertAuth(userAuth);
        return result;
    }

    @Override
    public boolean updateProfile(UserVO userVo) throws Exception {
        // TODO Auto-generated method stub
        
        MultipartFile file = userVo.getImage();
        log.info("MultipartFile : : : : : : : : " + file);
        if (userVo.getId() == null) {
            // 캠페인 ID를 가져오지 못했다면 예외 처리
            throw new RuntimeException("프로필 사진 등록 실패: 유저의 ID를 가져올 수 없습니다.");
        }
        boolean result = false;
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
            uploadFile.setFileType("user_File");
            uploadFile.setTargetType("profile");
            uploadFile.setTargetId(userVo.getId());
            uploadFile.setUserId(userVo.getUserId());
            uploadFile.setMimeType("");
            log.info("등록 처리 uploadFile : " + uploadFile);

            result = fileService.upload(uploadFile);
            
        }
        
        return result;
    }

    @Override
    public int totalUser() throws Exception {
        // TODO Auto-generated method stub
        int result = userMapper.totalUser();

        return result;
    }

    @Override
    public List<UserVO> bestAgentList() throws Exception {
        // TODO Auto-generated method stub
        List<UserVO> bestAgentList = userMapper.bestAgentList();
        return bestAgentList;
    }

    @Override
    public List<UserVO> bestPayList() throws Exception {
        // TODO Auto-generated method stub
        List<UserVO> bestPayList = userMapper.bestPayList();
        return bestPayList;
    }
    
}
