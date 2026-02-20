package com.safeg.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.safeg.admin.vo.AdminContentVO;
import com.safeg.admin.vo.UserVO;

@Mapper
public interface LeaderMapper {

    public AdminContentVO leaderSelect(Long id) throws Exception;

    public int leaderInsert(AdminContentVO adminContentVO) throws Exception;

    public int leaderUpdate(AdminContentVO adminContentVO) throws Exception;

    public List<UserVO> leaderList() throws Exception;
    
}
