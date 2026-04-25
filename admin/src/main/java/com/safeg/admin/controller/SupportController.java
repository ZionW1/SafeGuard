package com.safeg.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.UriComponentsBuilder;

import com.safeg.admin.service.FaqService;
import com.safeg.admin.service.SupportService;
import com.safeg.admin.vo.AdminContentVO;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class SupportController {

    @Autowired
    SupportService supportService;
    
    // 1:1 문의사항 리스트
    @GetMapping("/support01")
    public String supportList() {
        List<AdminContentVO> supportList = supportService.supportList(option, page);

        String pageUrl = UriComponentsBuilder.fromPath("/support01")
                        //.queryParam("page", page.getPage())
                        .queryParam("keyword", option.getKeyword())
                        .queryParam("code", option.getCode())
                        // .queryParam("rows", page.getRows())
                        .queryParam("orderCode", option.getOrderCode())
                        .build()
                        .toUriString();
        log.info("pageRows : " + page.getRows());

        model.addAttribute("supportList", supportList);
        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("pageUrl", pageUrl);
        return "support/support01";
    }
    // 1:1 문의사항 상세보기
    // 1:1 문의사항 등록
    // 1:1 문의사항 답변 작성
    // 1:1 문의사항 답변 수정
    // 1:1 문의사항 답변 삭제
    // 1:1 문의사항 답변 상태 변경 (예: 답변 완료, 답변 대기 등)
    // 1:1 문의사항 검색 및 필터링 (예: 날짜, 상태, 사용자 등)
    
}
