package com.safeg.admin.service;

import com.safeg.admin.vo.AdminContentVO;

public interface LeaderService {

    public AdminContentVO leaderSelect() throws Exception;

    public int useGuideInsert(AdminContentVO adminContentVO) throws Exception;

    public int useGuideUpdate(AdminContentVO adminContentVO) throws Exception;

}
