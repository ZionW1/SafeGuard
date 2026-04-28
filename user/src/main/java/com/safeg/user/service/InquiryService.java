package com.safeg.user.service;

import java.util.List;

import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.InquiryVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.Page;

public interface InquiryService {
    public int inquiryInsert(InquiryVO inquiryVO) throws Exception;
}
