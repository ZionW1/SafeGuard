package com.safeg.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safeg.user.mapper.FaqMapper;
import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.Page;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FaqServiceImpl implements FaqService{

    @Autowired
    FaqMapper faqMapper;
    
    @Override
    public List<AdminContentVO> faqList(Option option, Page page) throws Exception {
        // TODO Auto-generated method stub
        List<AdminContentVO> faqList = faqMapper.faqList(option, page);
        log.info("Admin FaqServiceImpl faqList() 호출" + faqList);

        return faqList;    
    }


    @Override
    public AdminContentVO faqSelect(String faqId) throws Exception {
        // TODO Auto-generated method stub
        AdminContentVO faqSelect = faqMapper.faqSelect(faqId);
        log.info("Admin FaqServiceImpl faqSelect() 호출" + faqSelect);
        return faqSelect;
    }
    
}
