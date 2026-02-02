package com.safeg.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.NoticeVO;

@Mapper
public interface NoticeMapper {

    public List<AdminContentVO> noticeList() throws Exception;

    public AdminContentVO noticeSelect(String id) throws Exception;
    
}
