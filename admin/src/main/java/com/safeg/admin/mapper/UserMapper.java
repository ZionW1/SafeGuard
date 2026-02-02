package com.safeg.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.UserAuth;
import com.safeg.admin.vo.UserVO;
import com.safeg.admin.vo.Users;

@Mapper
public interface UserMapper {

    // 회원 조회
    public UserVO select(String id) throws Exception;

    // 회원 가입
    public int userJoin(UserVO userVo) throws Exception;

    // 회원 수정
    public int update(Users user) throws Exception;

    // 회원 권한 등록
    public int insertAuth(UserAuth userAuth) throws Exception;

    public int mypageUpdate(UserVO userVo) throws Exception;

    public List<UserVO> userList(@Param("option") Option option, @Param("page") Page page);

    public UserVO userSelect(String id) throws Exception;

    public int userInfoUpdate(UserVO userVO) throws Exception;

    public int userRemove(String id) throws Exception;

    public int userLeaderUpdate(String id) throws Exception;

    public int userStop(String id) throws Exception;

    public int userUnstop(String id) throws Exception;

    public int resetAllUserPay() throws Exception;

    public List<UserVO> userAddressList() throws Exception;

    public int userUpdate(@Param("id") String id, @Param("authRole") String authRole, @Param("authCode") String authCode) throws Exception;

    public int userAuthUpdate(@Param("id") String id, @Param("authRole") String authRole, @Param("authCode") String authCode);

    public void guardTypeChange(UserVO userVO);

}
