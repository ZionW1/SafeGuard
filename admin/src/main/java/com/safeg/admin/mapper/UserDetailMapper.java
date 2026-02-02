package com.safeg.admin.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.safeg.admin.vo.UserAuth;
import com.safeg.admin.vo.UserVO;
import com.safeg.admin.vo.Users;

@Mapper
public interface UserDetailMapper {

    // 회원 조회
    public UserVO select(String id) throws Exception;

    // 회원 가입
    public int join(UserVO userVo) throws Exception;

    // 회원 수정
    public int update(Users user) throws Exception;

    // 회원 권한 등록
    public int insertAuth(UserAuth userAuth) throws Exception;

    public int mypageUpdate(UserVO userVo) throws Exception;

}
