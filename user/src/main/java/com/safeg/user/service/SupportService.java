package com.safeg.user.service;

import java.util.List;

import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.Page;
import com.safeg.user.vo.SupportVO;

public interface SupportService {
    public List<AdminContentVO> supportList(Option option, Page page) throws Exception;

    public int insertSupport(SupportVO supportVO) throws Exception;
}
