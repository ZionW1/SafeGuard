package com.safeg.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.InquiryVO;

@Mapper
public interface InquiryMapper {

    public List<InquiryVO> inquiryList(@Param("option") Option option, @Param("page") Page page) throws Exception;

    public InquiryVO inquirySelect(String inquiryId) throws Exception;
    
}
