package com.safeg.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safeg.user.mapper.NoticeMapper;
import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.CampaignVO;
import com.safeg.user.vo.NoticeVO;

@Service
public class NoticeServiceImpl implements NoticeService {
    
    @Autowired
    NoticeMapper noticeMapper;
    
    public List<AdminContentVO> noticeList() throws Exception {
        // TODO Auto-generated method stub
        List<AdminContentVO> noticeList = noticeMapper.noticeList();

        return noticeList;

    }

    @Override
    public AdminContentVO noticeSelect(String id) throws Exception {
        // TODO Auto-generated method stub
        AdminContentVO noticeSelect = noticeMapper.noticeSelect(id);

        return noticeSelect;
    }
    
}
