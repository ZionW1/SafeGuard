package com.safeg.admin.service;

import java.util.List;

import com.safeg.admin.vo.AdminContentVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;

public interface FaqService {

    public List<AdminContentVO> faqList(Option option, Page page) throws Exception;

    public AdminContentVO faqSelect(String faqId) throws Exception;

    public int faqInsert(AdminContentVO adminContentVO) throws Exception;

    public int faqUpdate(AdminContentVO adminContentVO) throws Exception;

    public int faqDelete(String faqId) throws Exception;

}