package com.safeg.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import com.safeg.admin.service.CampaignService;
import com.safeg.admin.service.FileService;
import com.safeg.admin.vo.CampaignVO;
import com.safeg.admin.vo.CustomUser;
import com.safeg.admin.vo.FilesVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.UserVO;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class CampaignController {

    @Autowired
    private CampaignService campaignsService;

    @Autowired
    private FileService fileService;

    @GetMapping("/campaign01")
    public String campaign01(@AuthenticationPrincipal CustomUser authUser, Model model,
                // @RequestParam(name="keyword", defaultValue = "") String keyword
                Option option, 
                // @RequestParam(name="rows", defaultValue = "10") int rows
                Page page
                ) throws Exception {
        log.info(":::::::::: list :::::::::: 호출 page " + page );
        log.info(":::::::::: list :::::::::: 호출 option " + option);
        log.info("option : " + option);
        log.info("page : " + page);
        List<CampaignVO> campaignsList = campaignsService.campaignList(option, page);

        log.info("option : " + option);
        log.info("page.getPage : " + page.getPage());
        log.info("page.getTotal : " + page.getTotal());
        log.info("page.getCount : " + page.getCount());
        log.info("page.getStart : " + page.getStart());
        log.info("page.getEnd : " + page.getEnd());
        

        
        // for(CampaignVO c : campaignsList){
        //     log.info("campaignsList c : " + c);
        //     log.info("campaignsList c getCreatedAt : " + c.getEventPeriodEnd());
        //     if(c.getEventPeriodEnd() != null && !c.getEventPeriodEnd().isEmpty()){
        //         String eventEnd = c.getEventPeriodEnd().substring(0, 10);
        //         c.setEventPeriodEnd(eventEnd);
        //     }
        // }
        

        model.addAttribute("campaignsList", campaignsList);
        model.addAttribute("option", option);
        model.addAttribute("rows", page.getRows());
        model.addAttribute("page", page);

        String pageUrl = UriComponentsBuilder.fromPath("/campaign/list")
                        //.queryParam("page", page.getPage())
                        .queryParam("keyword", option.getKeyword())
                        .queryParam("code", option.getCode())
                        // .queryParam("rows", page.getRows())
                        .queryParam("orderCode", option.getOrderCode())
                        .build()
                        .toUriString();
        log.info("pageRows : " + page.getRows());
        model.addAttribute("pageUrl", pageUrl);
        if(authUser != null){
            log.info("authUser : " + authUser);
            UserVO user = authUser.getUserVo();
            log.info("user : " + user);

            model.addAttribute("user", user);
        }
        return "campaign/campaign01";
    }

    // 상세보기 처리
    @GetMapping("/campaign02")
    public String campaign02(Model model, @RequestParam("id") String id) throws Exception {
        log.info(":::::::::: select :::::::::: " + id);

        CampaignVO campaignSelect = campaignsService.campaignSelect(id);
        List<UserVO> leaderList = campaignsService.leaderList();
        List<CampaignVO> securityType = campaignsService.securityType();
        FilesVO file = fileService.select(id);
        
        log.info("campaignSelect : " + campaignSelect);
        
        log.info("leaderList : " + leaderList);
        log.info("securityType : " + securityType);
        log.info("file : " + file);
        for(UserVO leader : leaderList){
            if(leader.getId().equals(campaignSelect.getLeaderCode())){
                campaignSelect.setLeaderName(leader.getUserNm());
                campaignSelect.setLeaderNo(leader.getId());
                campaignSelect.setPhoneNum(leader.getPhoneNum());
                campaignSelect.setLeaderId(leader.getUserId());
                break;
            }
        }
        // campaignSelect.setPhoneNum(leaderList.get(0).getPhoneNum());
        model.addAttribute("campaignSelect", campaignSelect);
        model.addAttribute("leaderList", leaderList);
        model.addAttribute("securityType", securityType);
        if(file != null){
            model.addAttribute("file", file);
        }else {
            model.addAttribute("file", null);
        }
        return "campaign/campaign02";
    }

    @GetMapping("/campaign03")
    public String campaign03(Model model) throws Exception{
        log.info(":::::::::: 인서트 ::::::::::");

        List<UserVO> leaderList = campaignsService.leaderList();
        List<CampaignVO> securityType = campaignsService.securityType();

        log.info("leaderList : " + leaderList);
        log.info("securityType : " + securityType);

        model.addAttribute("leaderList", leaderList);
        model.addAttribute("securityType", securityType);

        return "campaign/campaign03";
    }

    // 등록 처리
    @PostMapping("/campaign04")
    public String campaign04(CampaignVO campaignVO) throws Exception {
        log.info("등록 처리");
        log.info("campaignsVO : " + campaignVO);
        // campaignsVO.setCampaignStatusId(1);
        
        Long leaderId = campaignVO.getLeaderNo();

        int result = campaignsService.campaignInsert(campaignVO);

        if(result > 0){
            return "redirect:/campaign01";
        }
        return "redirect:/insert?error";
        // return "redirect:/board/insert?error";
    }

    // 수정 처리
    @PostMapping("/campaign05")
    public String campaign05(CampaignVO campaign) throws Exception{
        log.info("수정 처리 : " + campaign);
        int result = campaignsService.campaignUpdate(campaign);
        if(result > 0){
            return "redirect:/campaign01";
        }
        return "redirect:/update?error&id=" + campaign.getCampaignId();
    }

    // 삭제 처리
    @PostMapping("/campaign06")
    public String campaign06(@RequestParam("id") String id) throws Exception{
        log.info("삭제 처리 : " + id);
        // int result = campaignsService.campaignDelete(id);
        // if(result > 0){
        //     return "redirect:/admin/campaign01";
        // }
        return "redirect:/campaign02?error&id="+id;
    }

    @GetMapping("/campaign07")
    public String campaign07(Model model,
            // @RequestParam(name="keyword", defaultValue = "") String keyword
            Option option, 
            // @RequestParam(name="rows", defaultValue = "10") int rows
            Page page) throws Exception{
        log.info(":::::::::: campaign07 ::::::::::");
        log.info("option : " + option);
        log.info("page : " + page);
        List<CampaignVO> campaign07 = campaignsService.campaign07(option, page);

        log.info("option : " + option);
        log.info("page.getPage : " + page.getPage());
        log.info("page.getTotal : " + page.getTotal());
        log.info("page.getCount : " + page.getCount());
        log.info("page.getStart : " + page.getStart());
        log.info("page.getEnd : " + page.getEnd());
        
        model.addAttribute("campaignsList", campaign07);
        model.addAttribute("option", option);
        model.addAttribute("rows", page.getRows());
        model.addAttribute("page", page);

        String pageUrl = UriComponentsBuilder.fromPath("/campaign/campaign07")
                        //.queryParam("page", page.getPage())
                        .queryParam("keyword", option.getKeyword())
                        .queryParam("code", option.getCode())
                        // .queryParam("rows", page.getRows())
                        .queryParam("orderCode", option.getOrderCode())
                        .build()
                        .toUriString();
        log.info("pageRows : " + page.getRows());
        model.addAttribute("pageUrl", pageUrl);
        return "campaign/campaign07";
    }
    
}
