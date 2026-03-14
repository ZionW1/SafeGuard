package com.safeg.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safeg.admin.mapper.FaqMapper;
import com.safeg.admin.mapper.NoticeMapper;
import com.safeg.admin.vo.AdminContentVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;

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

    @Override
    public int faqInsert(AdminContentVO adminContentVO) throws Exception {
        // TODO Auto-generated method stub
        log.info("Admin FaqServiceImpl faqInsert() 호출" + adminContentVO);
        int result = faqMapper.faqInsert(adminContentVO);
        return result;
    }

    @Override
    public int faqUpdate(AdminContentVO adminContentVO) throws Exception {
        // TODO Auto-generated method stub
        log.info("Admin FaqServiceImpl faqUpdate() 호출" + adminContentVO);
        int result = faqMapper.faqUpdate(adminContentVO);
        return result;
    }


    @Override
    public int faqDelete(String faqId) throws Exception {
        // TODO Auto-generated method stub
        log.info("Admin FaqServiceImpl faqDelete() 호출" + faqId);
        int result = faqMapper.faqDelete(faqId);
        return result;
    }

}
