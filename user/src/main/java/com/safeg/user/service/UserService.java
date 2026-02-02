package com.safeg.user.service;

import java.util.List;

import com.safeg.user.vo.UserAuth;
import com.safeg.user.vo.UserVO;
import com.safeg.user.vo.Users;

import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    // 로그인
    public boolean login(UserVO userVo, HttpServletRequest request) throws Exception;
    
    // 조회
    public UserVO select(String username) throws Exception;

    // 회원 가입
    public int join(UserVO userVo) throws Exception;

    // 회원 수정
    public int update(Users user) throws Exception;

    // 회원 권한 등록
    public int insertAuth(UserAuth userAuth) throws Exception;
    
    public boolean updateProfile(UserVO userVo) throws Exception;

    public int totalUser() throws Exception;

    public List<UserVO> bestAgentList() throws Exception;

    public List<UserVO> bestPayList() throws Exception;

}
