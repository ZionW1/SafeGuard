package com.safeg.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import com.safeg.admin.service.FaqService;
import com.safeg.admin.service.NoticeService;
import com.safeg.admin.vo.AdminContentVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class FaqController {

    @Autowired
    FaqService faqService;

    @GetMapping("/faq01")
    public String notice01(Model model, Option option, Page page) throws Exception {

        List<AdminContentVO> faqList = faqService.faqList(option, page);

        model.addAttribute("faqList", faqList);
        model.addAttribute("option", option);
        model.addAttribute("page", page);
        model.addAttribute("row", page.getRows());
        String pageUrl = UriComponentsBuilder.fromPath("/faq01")
                        //.queryParam("page", page.getPage())
                        .queryParam("keyword", option.getKeyword())
                        .queryParam("code", option.getCode())
                        // .queryParam("rows", page.getRows())
                        .queryParam("orderCode", option.getOrderCode())
                        .build()
                        .toUriString();
        model.addAttribute("pageUrl", pageUrl);

        return "faq/faq01";
    }

    @GetMapping("/faq02")
    public String faq02(Model model, @RequestParam("id") String faqId) throws Exception {
        log.info("Admin FaqController faq02() 호출");
        AdminContentVO faqSelect = faqService.faqSelect(faqId);

        model.addAttribute("faqSelect", faqSelect);

        return "faq/faq02";
    }

    @GetMapping("/faq03")
    public String faq03(Model model) throws Exception {
        log.info("Admin FaqController faq03() 호출");
        
        return "faq/faq03";
    }
    
    @PostMapping("/faq04")
    public String faq04(Model model, AdminContentVO adminContentVO) throws Exception {
        log.info("Admin FaqController faq04() 호출");
        log.info("faq04 : " + adminContentVO);
        
        int result = faqService.faqInsert(adminContentVO);
        if(result > 0){
            return "redirect:/faq01";
        }
        return "redirect:/faq01?error";
    }

    @PostMapping("/faq05")
    public String faq05(AdminContentVO adminContentVO) throws Exception {
        
        log.info("Admin FaqController faq05() 호출");
        log.info("faq05 : " + adminContentVO);
        
        int result = faqService.faqUpdate(adminContentVO);
        if(result > 0){
            return "redirect:/faq01";
        }
        return "redirect:/faq01?error";
    }

    @PostMapping("/faq06")
    public String faq06(@RequestParam("id") String faqId) throws Exception {
        
        log.info("Admin FaqController faq06() 호출");
        log.info("faq06 : " + faqId);
        
        int result = faqService.faqDelete(faqId);
        if(result > 0){
            return "redirect:/faq01";
        }
        return "redirect:/faq01?error";
    }
}
