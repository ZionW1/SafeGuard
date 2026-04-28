package com.safeg.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import com.safeg.admin.service.FaqService;
import com.safeg.admin.service.InquiryService;
import com.safeg.admin.vo.AdminContentVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.InquiryVO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class InquiryController {

    @Autowired
    InquiryService inquiryService;
    
    // 1:1 문의사항 리스트
    @GetMapping("/inquiry01")
    public String inquiryList(Option option, Page page, Model model, HttpServletRequest request) throws Exception{
        List<InquiryVO> inquiryList = inquiryService.inquiryList(option, page);

        String pageUrl = UriComponentsBuilder.fromPath("/inquiry01")
                        //.queryParam("page", page.getPage())
                        .queryParam("keyword", option.getKeyword())
                        .queryParam("code", option.getCode())
                        // .queryParam("rows", page.getRows())
                        .queryParam("orderCode", option.getOrderCode())
                        .build()
                        .toUriString();
        log.info("pageRows : " + page.getRows());

        model.addAttribute("inquiryList", inquiryList);
        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("pageUrl", pageUrl);
        return "inquiry/inquiry01";
    }
    // 1:1 문의사항 상세보기
    @GetMapping("/inquiry02")
    public String supportSelect(@RequestParam("inquiryId") String inquiryId, Model model, HttpServletRequest request) throws Exception{
        log.info("Admin SupportController supportSelect() 호출 : supportId = " + inquiryId);

        InquiryVO inquirySelect = inquiryService.inquirySelect(inquiryId);
        log.info("Admin SupportController supportList() 호출 : "  + inquirySelect.toString());

        model.addAttribute("inquirySelect", inquirySelect);
        model.addAttribute("currentURI", request.getRequestURI());

        return "inquiry/inquiry02";
    }
    // 1:1 문의사항 등록
    // 1:1 문의사항 답변 작성
    // 1:1 문의사항 답변 수정
    // 1:1 문의사항 답변 삭제
    // 1:1 문의사항 답변 상태 변경 (예: 답변 완료, 답변 대기 등)
    // 1:1 문의사항 검색 및 필터링 (예: 날짜, 상태, 사용자 등)
    
}
