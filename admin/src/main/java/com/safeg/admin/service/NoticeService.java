package com.safeg.admin.service;

import java.util.List;

import com.safeg.admin.vo.CampaignVO;
import com.safeg.admin.vo.AdminContentVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;

public interface NoticeService {

    public List<AdminContentVO> noticeList(Option option, Page page) throws Exception;

    public AdminContentVO noticeSelect(String noticeId) throws Exception;

    public int noticeInsert(AdminContentVO adminContentVO) throws Exception;

    public int noticeUpdate(AdminContentVO adminContentVO) throws Exception;
}
