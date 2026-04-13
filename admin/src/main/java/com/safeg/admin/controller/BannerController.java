package com.safeg.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import com.safeg.admin.service.FileService;
import com.safeg.admin.vo.BannerVO;
import com.safeg.admin.vo.FilesVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.service.BannerService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class BannerController {
    
    @Autowired
    private BannerService bannerService;

    @Autowired
    private FileService fileService;

    
    // 배너 리스트
    @GetMapping("/banner01")
    public String banner01(Model model, Option option, Page page) throws Exception {
        List<BannerVO> bannerList = bannerService.bannerList(option, page);

        model.addAttribute("bannerList", bannerList);
        model.addAttribute("option", option);
        model.addAttribute("rows", page.getRows());
        model.addAttribute("page", page);

        String pageUrl = UriComponentsBuilder.fromPath("/banner/list")
                        //.queryParam("page", page.getPage())
                        .queryParam("keyword", option.getKeyword())
                        .queryParam("code", option.getCode())
                        // .queryParam("rows", page.getRows())
                        .queryParam("orderCode", option.getOrderCode())
                        .build()
                        .toUriString();
        model.addAttribute("pageUrl", pageUrl);
        
        return "banner/banner01";
    }
    
    // 배너 상세보기
    @GetMapping("/banner02")
    public String banner02(Model model, @RequestParam("id") Long id) throws Exception {
        
        BannerVO bannerSelect = bannerService.bannerSelect(id);
        FilesVO file = fileService.bannerSelect(id);

        model.addAttribute("bannerSelect", bannerSelect);
        model.addAttribute("file", file);

        return "banner/banner02";
    }

    // 배너 수정 처리
    @PostMapping("/banner02")
    public String banner02Post(BannerVO bannerVO) throws Exception {
        int result = bannerService.bannerUpdate(bannerVO);
        if(result > 0){
            return "redirect:/banner01";
        }
        return "redirect:/banner02?error";
    }

    // 배너 등록 화면
    @GetMapping("/banner03")
    public String banner03() throws Exception {

        return "banner/banner03";
    }

    // 배너 등록 처리
    @PostMapping("/banner03")
    public String banner03Post(BannerVO bannerVO) throws Exception {
        int result = bannerService.bannerInsert(bannerVO);
        if(result > 0){
            return "redirect:/banner01";
        }
        return "redirect:/banner03?error";
    }
}
