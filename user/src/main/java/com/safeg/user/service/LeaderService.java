package com.safeg.user.service;

import java.util.List;

import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.UserVO;

public interface LeaderService {

    public AdminContentVO leaderSelect() throws Exception;

    public List<UserVO> leaderList() throws Exception;

    public AdminContentVO leaderSelect(Long id) throws Exception;

}
