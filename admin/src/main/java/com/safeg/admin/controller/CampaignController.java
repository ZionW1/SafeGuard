package com.safeg.admin.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.safeg.admin.service.CampaignService;
import com.safeg.admin.service.FileService;
import com.safeg.admin.service.UserService;
import com.safeg.admin.vo.CampaignVO;
import com.safeg.admin.vo.CustomUser;
import com.safeg.admin.vo.FilesVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.UserCampaignVO;
import com.safeg.admin.vo.UserVO;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @GetMapping("/campaign01")
    public String campaign01(@AuthenticationPrincipal CustomUser authUser, Model model, Option option, Page page) throws Exception {
        List<CampaignVO> campaignsList = campaignService.campaignList(option, page);

        log.info("page : " + page);
        log.info("page.getRows : " + page.getRows());
        log.info("option : " + option);

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
        model.addAttribute("pageUrl", pageUrl);

        if(authUser != null){
            UserVO user = authUser.getUserVo();
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

        CampaignVO campaignSelect = campaignService.campaignSelect(id);
        List<UserVO> leaderList = campaignService.leaderList();
        List<CampaignVO> securityType = campaignService.securityType();
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

        List<UserVO> leaderList = campaignService.leaderList();
        List<CampaignVO> securityType = campaignService.securityType();

        model.addAttribute("leaderList", leaderList);
        model.addAttribute("securityType", securityType);

        model.addAttribute("user", username);

        return "campaign/campaign03";
    }

    // 등록 처리
    @PostMapping("/campaign04")
    public String campaign04(CampaignVO campaignVO) throws Exception {
        log.info("campaignVO.toString : " + campaignVO.toString());

        // campaignVO.setLeaderPhone(campaignVO.getLeaderPhone().replace(",", ""));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String username = customUser.getUsername(); // 로그인 아이디 (userId)

        int result = campaignService.campaignInsert(campaignVO);

        if(result > 0){
            return "redirect:/campaign01";
        }
        return "redirect:/insert?error";
        // return "redirect:/board/insert?error";
    }

    // 수정 처리
    // @PostMapping("/campaign05")
    // public String campaign05(CampaignVO campaign, RedirectAttributes reAttr) throws Exception{
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     CustomUser customUser = (CustomUser) authentication.getPrincipal();
    //     String username = customUser.getUsername(); // 로그인 아이디 (userId)
    //     log.info("수정 campaign05.toString : " + campaign.toString());

    //     try {
    //         int result = campaignsService.campaignUpdate(campaign);
    //         if(result > 0){
    //             reAttr.addFlashAttribute("message", "수정이 완료되었습니다.");
    //             return "redirect:/campaign01";
    //         } else {
    //             // 업데이트된 행이 0개인 경우
    //             return "redirect:/campaign02?error&id=" + campaign.getCampaignId();
    //         }
    //     } catch (Exception e) {
    //         log.error("캠페인 수정 중 오류 발생: ", e);
    //         // 에러 메시지를 가지고 안전한 페이지로 리다이렉트
    //         reAttr.addFlashAttribute("errorMessage", "시스템 오류가 발생했습니다.");
    //         return "redirect:/campaign02?id=" + campaign.getCampaignId();
    //     }
    // }
    @PostMapping("/campaign05")
    public ResponseEntity<String> campaign05(@ModelAttribute CampaignVO campaignVO) throws Exception {
        
        // 1. 수정 서비스 로직 수행
        campaignService.campaignUpdate(campaignVO);
        
        // 2. 부모 창을 새로고침하고 현재 팝업창을 닫는 스크립트 작성
        String script = "<script>" +
                        "   alert('수정이 완료되었습니다.');" +
                        "   if (window.opener) { " +
                        "       window.opener.location.reload(); " + // 부모 창(리스트 화면) 새로고침
                        "   }" +
                        "   window.close();" +                       // 현재 팝업창 닫기
                        "</script>";
        
        // 3. 브라우저가 스크립트로 인식할 수 있도록 Content-Type 설정 후 응답
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/html; charset=UTF-8");
        
        return new ResponseEntity<>(script, headers, HttpStatus.OK);
    }

    // 삭제 처리
    @PostMapping("/campaign06")
    public String campaign06(@RequestParam("id") String id) throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        String username = customUser.getUsername(); // 로그인 아이디 (userId)

        int result = campaignService.campaignDelete(id);
        int result1 = campaignService.applyDelete(id);

        if(result > 0){
            return "redirect:/campaign01";
        }
        return "redirect:/campaign02?error&id="+id;
    }

    @GetMapping("/campaign07")
    public String campaign07(Model model, Option option, Page page) throws Exception{
        List<CampaignVO> campaign07 = campaignService.campaign07(option, page);

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
        List<CampaignVO> campaignsList = campaignService.campaignList(option, page);

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
            UserVO user = authUser.getUserVo();
            model.addAttribute("user", user);
        }

        return "campaign/campaign09";
    }
    
    @PostMapping("/campaignPopup01/{campaignId}")
    public String userInfoList(@PathVariable("campaignId") Long campaignId, @RequestBody CampaignVO dto, Model model) throws Exception {
        List<UserVO> userInfoList = userService.userInfoList(campaignId);

        model.addAttribute("campaignTitle", dto.getCampaignTitle());
        model.addAttribute("campaignId", campaignId);
        model.addAttribute("userInfoList", userInfoList);
        
        return "campaign/campaignPopup01";
    }

    @PostMapping("/userApply")
    @ResponseBody
    public ResponseEntity<?> userApply(@RequestBody CampaignVO dto) throws Exception {
        Map<String, String> response = new HashMap<>();

        if(dto.getRecruitmentNum() <= dto.getApplicantsNum()) {
            response.put("message", "모집인과 신청인이 같거나 큽니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        try {
            campaignService.overlapTitle(dto);
            return ResponseEntity.ok().body("{\"message\": \"성공\"}");
        } catch (IllegalArgumentException e) {
            // 서비스에서 throw한 에러 메시지를 그대로 프론트로 전달
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response); 
        } catch (Exception e) {
            response.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @PostMapping("/userCancel")
    @ResponseBody
    public ResponseEntity<?> userCancel(@RequestBody CampaignVO dto) throws Exception {
        log.info("userCancel " + dto);
        Map<String, String> response = new HashMap<>();

        try {
            // 앞서 질문하셨던 Map 리턴 방식을 활용하여 화면에 결과 건수를 줍니다.
            int result = campaignService.userCancel(dto);
            response.put("message", String.valueOf(result)); 
            
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
