package com.safeg.admin.service;

import com.safeg.admin.vo.AdminContentVO;
import com.safeg.admin.vo.UseGuideVO;

public interface UseGuideService {

    public AdminContentVO useGuideSelect() throws Exception;

    public int useGuideInsert(AdminContentVO adminContentVO) throws Exception;

    public int useGuideUpdate(AdminContentVO adminContentVO) throws Exception;

}
