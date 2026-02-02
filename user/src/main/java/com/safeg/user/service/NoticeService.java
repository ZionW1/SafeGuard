package com.safeg.user.service;

import java.util.List;

import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.NoticeVO;

public interface NoticeService {

    public List<AdminContentVO> noticeList() throws Exception;

    public AdminContentVO noticeSelect(String id) throws Exception;
    
}
