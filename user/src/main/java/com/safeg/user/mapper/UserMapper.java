package com.safeg.user.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.user.vo.UserAuth;
import com.safeg.user.vo.UserVO;
import com.safeg.user.vo.Users;


@Mapper
public interface UserMapper {

    // 회원 조회
    public UserVO select(String id) throws Exception;

    // 회원 가입
    public int join(UserVO userVo) throws Exception;

    // 회원 수정
    public int update(Users user) throws Exception;

    // 회원 권한 등록
    public int insertAuth(UserAuth userAuth) throws Exception;

    public int updateProfile(UserVO userVo) throws Exception;

    public int totalUser() throws Exception;

    public List<UserVO> bestAgentList() throws Exception;

    public List<UserVO> bestPayList() throws Exception;

    public Long getReferrerNo(UserVO userVo) throws Exception;

    public Long getReferrerNoById(Long userNo) throws Exception;

    public boolean phoneDuplicate(String phoneNumber) throws Exception;

    public String findUserId(@Param("userNm") String userNm, @Param("phoneNum") String phoneNum) throws Exception;
}
