package com.safeg.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.safeg.admin.service.NoticeService;
import com.safeg.admin.vo.CampaignVO;
import com.safeg.admin.vo.FilesVO;
import com.safeg.admin.vo.AdminContentVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.UserVO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;


@Controller
@Slf4j
public class NoticeController {
    
    @Autowired
    NoticeService noticeService;

    @GetMapping("/notice01")
    public String notice01(Model model, Option option, Page page) throws Exception {
        log.info("Admin NoticeController notice01() 호출");
        List<AdminContentVO> noticeList = noticeService.noticeList(option, page);

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("option", option);
        model.addAttribute("page", page);
        model.addAttribute("row", page.getRows());
        String pageUrl = UriComponentsBuilder.fromPath("/notice01")
                        //.queryParam("page", page.getPage())
                        .queryParam("keyword", option.getKeyword())
                        .queryParam("code", option.getCode())
                        // .queryParam("rows", page.getRows())
                        .queryParam("orderCode", option.getOrderCode())
                        .build()
                        .toUriString();
        model.addAttribute("pageUrl", pageUrl);

        return "notice/notice01";
    }

    // 상세 페이지 화면
    @GetMapping("/notice02")
    public String notice02(Model model, @RequestParam("id") String noticeId) throws Exception {
        log.info("Admin NoticeController notice02() 호출");
        AdminContentVO noticeSelect = noticeService.noticeSelect(noticeId);

        model.addAttribute("noticeSelect", noticeSelect);

        return "notice/notice02";
    }

    @GetMapping("/notice03")
    public String notice03(Model model) throws Exception {
        log.info("Admin NoticeController notice03() 호출");
        
        return "notice/notice03";
    }

    // 등록 처리
    @PostMapping("/notice04")
    public String notice04(Model model, AdminContentVO adminContentVO) throws Exception {
        log.info("Admin NoticeController notice05() 호출");
        log.info("campaignsVO : " + adminContentVO);
        // campaignsVO.setCampaignStatusId(1);
        
        int result = noticeService.noticeInsert(adminContentVO);
        if(result > 0){
            return "redirect:/notice01";
        }
        return "redirect:/notice03?error";
    }

    // 수정 처리
    @PostMapping("/notice05")
    public String notice05(AdminContentVO adminContentVO) throws Exception {
        
        log.info("Admin NoticeController notice05() 호출");
        log.info("campaignsVO : " + adminContentVO);
        // campaignsVO.setCampaignStatusId(1);
        
        int result = noticeService.noticeUpdate(adminContentVO);
        if(result > 0){
            return "redirect:/notice01";
        }
        return "redirect:/notice02?error";
        // return "redirect:/board/insert?error";
    }
    
}
