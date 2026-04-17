package com.safeg.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    public String campaign01(@AuthenticationPrincipal CustomUser authUser, Model model, Option option, Page page) throws Exception {
        List<CampaignVO> campaignsList = campaignsService.campaignList(option, page);

        model.addAttribute("campaignsList", campaignsList);
        model.addAttribute("option", option);
        model.addAttribute("rows", page.getRows());
        model.addAttribute("page", page);

        String pageUrl = UriComponentsBuilder.fromPath("/campaign01")
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
            UserVO user = authUser.getUserVo();
            log.info("user : " + user);

            model.addAttribute("user", user);
        }

        return "campaign/campaign01";
    }

    // 상세보기 처리
    @GetMapping("/campaign02")
    public String campaign02(Model model, @RequestParam("id") String id) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String username = customUser.getUsername(); // 로그인 아이디 (userId)

        CampaignVO campaignSelect = campaignsService.campaignSelect(id);
        List<UserVO> leaderList = campaignsService.leaderList();
        List<CampaignVO> securityType = campaignsService.securityType();
        FilesVO file = fileService.select(id);
        
        for(UserVO leader : leaderList){
            if(leader.getId().equals(campaignSelect.getLeaderCode())){
                campaignSelect.setLeaderName(leader.getUserNm());
                campaignSelect.setLeaderNo(leader.getId());
                campaignSelect.setPhoneNum(leader.getPhoneNum());
                campaignSelect.setLeaderId(leader.getUserId());
                break;
            }
        }

        model.addAttribute("campaignSelect", campaignSelect);
        model.addAttribute("leaderList", leaderList);
        model.addAttribute("securityType", securityType);

        if(file != null){
            model.addAttribute("file", file);
        }else {
            model.addAttribute("file", null);
        }
        model.addAttribute("user", username);
        return "campaign/campaign02";
    }

    @GetMapping("/campaign03")
    public String campaign03(Model model) throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String username = customUser.getUsername(); // 로그인 아이디 (userId)

        List<UserVO> leaderList = campaignsService.leaderList();
        List<CampaignVO> securityType = campaignsService.securityType();

        model.addAttribute("leaderList", leaderList);
        model.addAttribute("securityType", securityType);

        model.addAttribute("user", username);

        return "campaign/campaign03";
    }

    // 등록 처리
    @PostMapping("/campaign04")
    public String campaign04(CampaignVO campaignVO) throws Exception {
        log.info("campaignVO.toString : " + campaignVO.toString());

        campaignVO.setLeaderPhone(campaignVO.getLeaderPhone().replace(",", ""));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String username = customUser.getUsername(); // 로그인 아이디 (userId)

        int result = campaignsService.campaignInsert(campaignVO);

        if(result > 0){
            return "redirect:/campaign01";
        }
        return "redirect:/insert?error";
        // return "redirect:/board/insert?error";
    }

    // 수정 처리
    @PostMapping("/campaign05")
    public String campaign05(CampaignVO campaign, RedirectAttributes reAttr) throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String username = customUser.getUsername(); // 로그인 아이디 (userId)

        try {
            int result = campaignsService.campaignUpdate(campaign);
            if(result > 0){
                reAttr.addFlashAttribute("message", "수정이 완료되었습니다.");
                return "redirect:/campaign01";
            } else {
                // 업데이트된 행이 0개인 경우
                return "redirect:/campaign02?error&id=" + campaign.getCampaignId();
            }
        } catch (Exception e) {
            log.error("캠페인 수정 중 오류 발생: ", e);
            // 에러 메시지를 가지고 안전한 페이지로 리다이렉트
            reAttr.addFlashAttribute("errorMessage", "시스템 오류가 발생했습니다.");
            return "redirect:/campaign02?id=" + campaign.getCampaignId();
        }
    }

    // 삭제 처리
    @PostMapping("/campaign06")
    public String campaign06(@RequestParam("id") String id) throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String username = customUser.getUsername(); // 로그인 아이디 (userId)

        int result = campaignsService.campaignDelete(id);

        if(result > 0){
            return "redirect:/campaign01";
        }
        return "redirect:/campaign02?error&id="+id;
    }

    @GetMapping("/campaign07")
    public String campaign07(Model model, Option option, Page page) throws Exception{
        List<CampaignVO> campaign07 = campaignsService.campaign07(option, page);

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

        model.addAttribute("pageUrl", pageUrl);
        
        return "campaign/campaign07";
    }
    
    @GetMapping("/campaign09")
    public String campaign09(@AuthenticationPrincipal CustomUser authUser, Model model, Option option, Page page) throws Exception {
        log.info("campaign09");
        List<CampaignVO> campaignsList = campaignsService.campaignList(option, page);

        model.addAttribute("campaignsList", campaignsList);
        model.addAttribute("option", option);
        model.addAttribute("rows", page.getRows());
        model.addAttribute("page", page);

        String pageUrl = UriComponentsBuilder.fromPath("/campaign09")
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

        return "campaign/campaign09";
    }
    
}
