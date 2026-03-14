package com.safeg.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.admin.vo.AdminContentVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;

@Mapper
public interface FaqMapper {

    public List<AdminContentVO> faqList(@Param("option") Option option, @Param("page") Page page) throws Exception;

    public AdminContentVO faqSelect(String faqId) throws Exception;

    public int faqInsert(AdminContentVO adminContentVO) throws Exception;

    public int faqUpdate(AdminContentVO adminContentVO) throws Exception;

    public int faqDelete(String faqId) throws Exception;

}