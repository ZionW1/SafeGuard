package com.safeg.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safeg.user.mapper.SupportMapper;
import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.Page;
import com.safeg.user.vo.SupportVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SupportServiceImpl implements SupportService {

    @Autowired
    private SupportMapper supportMapper;

    @Override
    public List<AdminContentVO> supportList(Option option, Page page) throws Exception {
        // TODO Auto-generated method stub
        List<AdminContentVO> supportList = supportMapper.supportList(option, page);

        return supportList;
    }

    @Override
    public int insertSupport(SupportVO supportVO) throws Exception {
        log.info("SupportServiceImpl.insertSupport called with: {}", supportVO);
        // TODO Auto-generated method stub
            // DB 저장 로직 구현 (예: supportMapper.insertSupport(supportVO))
            // 예시로 로그 출력
            int result = supportMapper.insertSupport(supportVO);

            return result;
    }
    
}
