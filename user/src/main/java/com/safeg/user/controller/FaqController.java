package com.safeg.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        log.info("FaqController faq01() 호출");
        List<AdminContentVO> faqList = faqService.faqList(option, page);
        log.info("FaqController faqList() 호출" + faqList.toString()); ;
        
        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("faqList", faqList);

        return "faq/faq01";
    }

    @GetMapping("/faq03")
    public String faq03(Model model, HttpServletRequest request, Option option, Page page) throws Exception {
        log.info("FaqController faq01() 호출");
        List<AdminContentVO> faqList = faqService.faqList(option, page);
        log.info("FaqController faqList() 호출" + faqList.toString()); ;
        
        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("faqList", faqList);

        return "faq/faq03";
    }

    @GetMapping("/faq02")
    public String faq02(Model model, HttpServletRequest request, @RequestParam("id") String id) throws Exception {
        log.info("FaqController faq02() 호출 : " + id);
        AdminContentVO faqSelect= faqService.faqSelect(id);
        log.info("FaqController faqSelect() 호출" + faqSelect.toString()); ;

        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("faqSelect", faqSelect);

        return "faq/faq02";
    }
}
