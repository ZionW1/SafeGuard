package com.safeg.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safeg.admin.mapper.InquiryMapper;
import com.safeg.admin.vo.AdminContentVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.InquiryVO;

@Service
public class InquiryServiceImpl implements InquiryService {

    @Autowired
    InquiryMapper inquiryMapper;

    @Override
    public List<InquiryVO> inquiryList(Option option, Page page) throws Exception {
        // TODO Auto-generated method stub
        
        List<InquiryVO> inquiryList = inquiryMapper.inquiryList(option, page);

        return inquiryList;
    }

    @Override
    public InquiryVO inquirySelect(String inquiryId) throws Exception {
        // TODO Auto-generated method stub
        InquiryVO inquirySelect = inquiryMapper.inquirySelect(inquiryId);

        return inquirySelect;
    }

     // 1:1 문의사항 리스트
     // 1:1 문의사항 상세보기
     // 1:1 문의사항 등록
     // 1:1 문의사항 답변 작성
     // 1:1 문의사항 답변 수정
     // 1:1 문의사항 답변 삭제
     // 1:1 문의사항 답변 상태 변경 (예: 답변 완료, 답변 대기 등)
     // 1:1 문의사항 검색 및 필터링 (예: 날짜, 상태, 사용자 등)
    
}
