package com.safeg.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safeg.admin.mapper.LeaderMapper;
import com.safeg.admin.mapper.UseGuideMapper;
import com.safeg.admin.vo.AdminContentVO;

import lombok.extern.slf4j.Slf4j;



@Service
@Slf4j
public class LeaderServiceImpl implements LeaderService{

    @Autowired
    LeaderMapper leaderMapper;

    public AdminContentVO leaderSelect() throws Exception{

        log.info("LeaderServiceImpl leaderSelect 호출");

        AdminContentVO leaderSelect = leaderMapper.leaderSelect();
        return leaderSelect;
    }

    @Override
    public int useGuideInsert(AdminContentVO adminContentVO) throws Exception {
        // TODO Auto-generated method stub
        log.info("LeaderServiceImpl useGuideInsert 호출");
        log.info("adminContentVO " + adminContentVO);

        int result = leaderMapper.leaderInsert(adminContentVO);
        return result;
    }

    @Override
    public int useGuideUpdate(AdminContentVO adminContentVO) throws Exception {
        // TODO Auto-generated method stub
        log.info("LeaderServiceImpl useGuideUpdate 호출");
        log.info("adminContentVO " + adminContentVO);

        int result = leaderMapper.leaderUpdate(adminContentVO);
        return result;
    }
}
