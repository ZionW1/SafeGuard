package com.safeg.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safeg.user.mapper.LeaderMapper;
import com.safeg.user.vo.AdminContentVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LeaderServiceImpl implements LeaderService{

    @Autowired
    LeaderMapper leaderMapper;

    public AdminContentVO leaderSelect() throws Exception{
        // TODO Auto-generated method stub
        log.info("LeaderServiceImpl leaderSelect 호출");

        AdminContentVO leaderSelect = leaderMapper.leaderSelect();
        return leaderSelect;
    }
}
