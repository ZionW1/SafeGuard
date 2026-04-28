package com.safeg.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safeg.user.mapper.InquiryMapper;
import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.InquiryVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.Page;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InquiryServiceImpl implements InquiryService {

    @Autowired
    private InquiryMapper supportMapper;

    @Override
    public int inquiryInsert(InquiryVO inquiryVO) throws Exception {
        log.info("SupportServiceImpl.insertSupport called with: {}", inquiryVO);
        // TODO Auto-generated method stub
            // DB 저장 로직 구현 (예: supportMapper.insertSupport(supportVO))
            // 예시로 로그 출력
            int result = supportMapper.inquiryInsert(inquiryVO);

            return result;
    }
    
}
