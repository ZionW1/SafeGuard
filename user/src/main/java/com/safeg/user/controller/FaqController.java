package com.safeg.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import com.safeg.user.service.FaqService;
import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.Page;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class FaqController {
    
    @Autowired
    FaqService faqService;

    @GetMapping("/faq01")
    public String faq01(Model model, HttpServletRequest request, Option option, Page page) throws Exception {
        List<AdminContentVO> faqList = faqService.faqList(option, page);
        
        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("faqList", faqList);

        String pageUrl = UriComponentsBuilder.fromPath("/faq01")
                        //.queryParam("page", page.getPage())
                        .queryParam("keyword", option.getKeyword())
                        .queryParam("code", option.getCode())
                        // .queryParam("rows", page.getRows())
                        .queryParam("orderCode", option.getOrderCode())
                        .build()
                        .toUriString();
        log.info("pageRows : " + page.getRows());
        
        model.addAttribute("pageUrl", pageUrl);

        return "faq/faq01";
    }

    @GetMapping("/faq02")
    public String faq02(Model model, HttpServletRequest request, @RequestParam("id") String id) throws Exception {
        AdminContentVO faqSelect= faqService.faqSelect(id);

        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("faqSelect", faqSelect);

        return "faq/faq02";
    }
    
    @GetMapping("/faq03")
    public String faq03(Model model, HttpServletRequest request, Option option, Page page) throws Exception {
        List<AdminContentVO> faqList = faqService.faqList(option, page);
        
        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("faqList", faqList);

        return "faq/faq03";
    }

}
