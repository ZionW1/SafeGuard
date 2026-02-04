package com.safeg.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safeg.user.mapper.LeaderMapper;
import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.UserVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LeaderServiceImpl implements LeaderService{

    @Autowired
    LeaderMapper leaderMapper;

    public AdminContentVO leaderSelect() throws Exception{
        // TODO Auto-generated method stub
        log.info("LeaderServiceImpl leaderSelect 호출");

        AdminContentVO leaderSelect = leaderMapper.leaderSelect1();
        return leaderSelect;
    }

    @Override
    public List<UserVO> leaderList() throws Exception {
        // TODO Auto-generated method stub
        log.info("LeaderServiceImpl leaderList 호출");

        List<UserVO> leaderList = leaderMapper.leaderList();
        return leaderList;
    }

    @Override
    public UserVO leaderSelect(Long id) throws Exception {
        // TODO Auto-generated method stub
        log.info("LeaderServiceImpl leaderSelect 호출");

        UserVO leaderSelect = leaderMapper.leaderSelect(id);
        return leaderSelect;
    }
}
