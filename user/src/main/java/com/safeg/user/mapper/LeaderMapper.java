package com.safeg.user.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.safeg.user.vo.AdminContentVO;

@Mapper
public interface LeaderMapper {

    public AdminContentVO leaderSelect() throws Exception;
    
}
