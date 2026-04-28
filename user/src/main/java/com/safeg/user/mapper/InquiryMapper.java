package com.safeg.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.Page;
import com.safeg.user.vo.InquiryVO;

@Mapper
public interface InquiryMapper {
    public int inquiryInsert(InquiryVO inquiryVO) throws Exception;
    
}
