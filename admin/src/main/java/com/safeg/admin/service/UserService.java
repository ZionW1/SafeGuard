package com.safeg.admin.service;

import java.util.List;

import com.safeg.admin.vo.CampaignVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.UserVO;

public interface UserService {

    public int userJoin(UserVO userVO) throws Exception;
    
    public List<UserVO> userList(Option option, Page page) throws Exception;

    public UserVO userSelect(String id) throws Exception;

    public int userInfoUpdate(UserVO userVO) throws Exception;

    public int userRemove(String id) throws Exception;

    public int userUpdate(Long id) throws Exception;

    public int userLeaderUpdate(Long id) throws Exception;

    public int userStop(Long id) throws Exception;

    public int userUnstop(Long id) throws Exception;

    public int resetAllUserPay() throws Exception;

    public List<UserVO> userAddressList() throws Exception;

    public void guardTypeChange(UserVO userVO) throws Exception;

    public Long referrerId(String referrerId) throws Exception;

}
