package com.safeg.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.Page;

@Mapper
public interface FaqMapper {

    public List<AdminContentVO> faqList(@Param("option") Option option, @Param("page") Page page) throws Exception;
    
    public AdminContentVO faqSelect(String faqId) throws Exception;

}
