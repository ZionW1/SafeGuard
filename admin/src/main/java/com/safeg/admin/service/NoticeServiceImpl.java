package com.safeg.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safeg.admin.mapper.NoticeMapper;
import com.safeg.admin.vo.CampaignVO;
import com.safeg.admin.vo.AdminContentVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    NoticeMapper noticeMapper;

    @Override
    public List<AdminContentVO> noticeList(Option option, Page page) throws Exception {
        // TODO Auto-generated method stub
        List<AdminContentVO> notice01 = noticeMapper.noticeList(option, page);
        log.info("Admin NoticeServiceImpl notice01() 호출" + notice01);
        return notice01;
    }

    @Override
    public AdminContentVO noticeSelect(String noticeId) throws Exception {
        // TODO Auto-generated method stub
        AdminContentVO notice02 = noticeMapper.noticeSelect(noticeId);
        log.info("Admin NoticeServiceImpl notice02() 호출" + notice02);
        return notice02;
    }

    @Override
    public int noticeInsert(AdminContentVO adminContentVO) throws Exception {
        // TODO Auto-generated method stub
        log.info("Admin NoticeServiceImpl notice04() 호출" + adminContentVO);
        int result = noticeMapper.noticeInsert(adminContentVO);
        return result;
    }

    @Override
    public int noticeUpdate(AdminContentVO adminContentVO) throws Exception {
        // TODO Auto-generated method stub
        log.info("Admin NoticeServiceImpl notice04() 호출" + adminContentVO);
        int result = noticeMapper.noticeUpdate(adminContentVO);
        return result;
    }

    
}
