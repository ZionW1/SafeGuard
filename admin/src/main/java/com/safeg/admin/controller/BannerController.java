package com.safeg.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/admin")
public class BannerController {
    
    @Autowired
    private BannerService bannerService;

    @Autowired
    private FileService fileService;

    // @RequestMapping("/banner01")
    @GetMapping("/banner01")
    public String banner01(Model model,
                // @RequestParam(name="keyword", defaultValue = "") String keyword
                Option option, 
                // @RequestParam(name="rows", defaultValue = "10") int rows
                Page page) throws Exception {
        log.info(":::::::::: banner01 ::::::::::");
        log.info(":::::::::: list ::::::::::");
        log.info("option : " + option);
        log.info("page : " + page);
        List<BannerVO> bannerList = bannerService.bannerList(option, page);

        log.info("option : " + option);
        log.info("page.getPage : " + page.getPage());
        log.info("page.getTotal : " + page.getTotal());
        log.info("page.getCount : " + page.getCount());
        log.info("page.getStart : " + page.getStart());
        log.info("page.getEnd : " + page.getEnd());
        
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
        log.info("pageRows : " + page.getRows());
        model.addAttribute("pageUrl", pageUrl);
        
        return "banner/banner01";
    }
    
    @GetMapping("/banner02")
    public String banner02(Model model, @RequestParam("id") Long id) throws Exception {
        log.info(":::::::::: banner02 :::::::::: " + id);
        
        BannerVO bannerSelect = bannerService.bannerSelect(id);
        log.info("bannerSelect : " + bannerSelect);
        String status = "BANNER";
        FilesVO file = fileService.bannerSelect(id);
        log.info("file : " + file);

        model.addAttribute("bannerSelect", bannerSelect);
        model.addAttribute("file", file);

        // int result = boardService.insert(board);
        // if(result > 0){
        //     return "redirect:/board/list";
        // }
        // bannerVO.setCampaignStatusId(1);
        
        // int result = campaignsService.insert(bannerVO);
        // if(result > 0){
        //     return "redirect:/admin/campaign/list";
        // }
        // return "redirect:/admin/campaign/insert?error";
        // // return "redirect:/board/insert?error";
        // log.info(":::::::::: banner02 ::::::::::");
        return "banner/banner02";
    }

    // 배너 수정 처리
    @PostMapping("/banner02")
    public String banner02Post(BannerVO bannerVO) throws Exception {
        log.info("등록 처리");
        log.info("bannerVO : " + bannerVO);
        
        int result = bannerService.bannerUpdate(bannerVO);
        if(result > 0){
            return "redirect:/admin/banner01";
        }
        return "redirect:/admin/banner02?error";
    }

    // @RequestMapping("/banner03")
    @GetMapping("/banner03")
    public String banner03() throws Exception {

        log.info("등록 화면");
        // int result = boardService.insert(board);
        // if(result > 0){
        //     return "redirect:/board/list";
        // }
        // bannerVO.setCampaignStatusId(1);
        
        // int result = campaignsService.insert(bannerVO);
        // if(result > 0){
        //     return "redirect:/admin/campaign/list";
        // }
        // return "redirect:/admin/campaign/insert?error";
        // // return "redirect:/board/insert?error";
        // log.info(":::::::::: banner02 ::::::::::");
        return "banner/banner03";
    }

    // 배너 등록 처리
    @PostMapping("/banner03")
    public String banner03Post(BannerVO bannerVO) throws Exception {
        log.info("등록 처리");
        log.info("bannerVO : " + bannerVO);
        
        int result = bannerService.bannerInsert(bannerVO);
        if(result > 0){
            return "redirect:/admin/banner01";
        }
        return "redirect:/admin/banner03?error";
    }
}
