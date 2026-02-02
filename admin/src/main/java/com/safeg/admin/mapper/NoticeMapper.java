package com.safeg.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.admin.vo.CampaignVO;
import com.safeg.admin.vo.AdminContentVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;

@Mapper
public interface NoticeMapper {

    public List<AdminContentVO> noticeList(@Param("option") Option option, @Param("page")Page page) throws Exception;

    public AdminContentVO noticeSelect(String noticeId) throws Exception;

    public int noticeInsert(AdminContentVO adminContentVO) throws Exception;

    public int noticeUpdate(AdminContentVO adminContentVO) throws Exception;
    
}
