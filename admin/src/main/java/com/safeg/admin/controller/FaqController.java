package com.safeg.admin.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import com.safeg.admin.service.FaqService;
import com.safeg.admin.vo.AdminContentVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FaqController {

    final FaqService faqService;

    @GetMapping("/faq01")
    public String notice01(Model model, Option option, Page page) throws Exception {
        log.info("FAQ List 화면");
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
        log.info("FAQ update 화면");
        AdminContentVO faqSelect = faqService.faqSelect(faqId);

        model.addAttribute("faqSelect", faqSelect);

        return "faq/faq02";
    }

    @GetMapping("/faq03")
    public String faq03(Model model) throws Exception {
        log.info("FAQ insert 화면");

        return "faq/faq03";
    }

    @PostMapping("/faq04")
    public String faq04(Model model, AdminContentVO adminContentVO) throws Exception {
        log.info("FAQ insert 추가");
        log.info("faq04 : " + adminContentVO);

        int result = faqService.faqInsert(adminContentVO);
        if(result > 0){
            return "redirect:/faq01";
        }
        return "redirect:/faq01?error";
    }

    @PostMapping("/faq05")
    public String faq05(AdminContentVO adminContentVO) throws Exception {
        log.info("FAQ update 수정");

        int result = faqService.faqUpdate(adminContentVO);
        if(result > 0){
            return "redirect:/faq01";
        }
        return "redirect:/faq01?error";
    }

    @PostMapping("/faq06")
    public String faq06(@RequestParam("id") String faqId) throws Exception {
        log.info("FAQ Delete 삭제");

        int result = faqService.faqDelete(faqId);
        if(result > 0){
            return "redirect:/faq01";
        }
        return "redirect:/faq01?error";
    }
}
