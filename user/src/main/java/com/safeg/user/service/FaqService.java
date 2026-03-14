package com.safeg.user.service;

import java.util.List;

import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.Page;

public interface FaqService {

    List<AdminContentVO> faqList(Option option, Page page) throws Exception;

    public AdminContentVO faqSelect(String id) throws Exception;

}
