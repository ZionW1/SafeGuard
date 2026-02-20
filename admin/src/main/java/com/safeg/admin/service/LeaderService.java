package com.safeg.admin.service;

import java.util.List;

import com.safeg.admin.vo.AdminContentVO;
import com.safeg.admin.vo.UserVO;

public interface LeaderService {

    public AdminContentVO leaderSelect(Long id) throws Exception;

    public int useGuideInsert(AdminContentVO adminContentVO) throws Exception;

    public int useGuideUpdate(AdminContentVO adminContentVO) throws Exception;

    public List<UserVO> leaderList() throws Exception;

}
