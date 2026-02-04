package com.safeg.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.UserVO;

@Mapper
public interface LeaderMapper {

    public AdminContentVO leaderSelect1() throws Exception;

    public List<UserVO> leaderList() throws Exception;

    public UserVO leaderSelect(Long id) throws Exception;
    
}
