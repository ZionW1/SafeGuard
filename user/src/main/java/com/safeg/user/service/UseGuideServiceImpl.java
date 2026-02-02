package com.safeg.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safeg.user.mapper.UseGuideMapper;
import com.safeg.user.vo.UseGuideVO;

@Service
public class UseGuideServiceImpl implements UseGuideService {

    @Autowired
    UseGuideMapper useGuideMapper;

    @Override
    public UseGuideVO useGuide01() throws Exception {
        // TODO Auto-generated method stub
        UseGuideVO useGuide01 = useGuideMapper.useGuide01();
        return useGuide01; // 임시로 성공 반환
    }
    
}
