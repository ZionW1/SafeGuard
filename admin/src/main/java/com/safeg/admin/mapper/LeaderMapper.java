package com.safeg.admin.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.safeg.admin.vo.AdminContentVO;

@Mapper
public interface LeaderMapper {

    public AdminContentVO leaderSelect() throws Exception;

    public int leaderInsert(AdminContentVO adminContentVO) throws Exception;

    public int leaderUpdate(AdminContentVO adminContentVO) throws Exception;
    
}
