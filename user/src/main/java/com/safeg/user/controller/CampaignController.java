package com.safeg.user.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import com.safeg.user.service.CampaignService;
import com.safeg.user.service.FileService;
import com.safeg.user.service.UserService;
import com.safeg.user.vo.CampaignVO;
import com.safeg.user.vo.CustomUser;
import com.safeg.user.vo.FilesVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.Page;
import com.safeg.user.vo.UserCampaignVO;
import com.safeg.user.vo.UserVO;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class CampaignController {

    @Autowired
    private CampaignService campaignsService;

    @Autowired
    private UserService userService;

    // @Autowired
    // private FileService fileService;

    @SuppressWarnings("null")
    @GetMapping("/campaign/{campaignId}")
    public String campaign01( @PathVariable("campaignId") Long campaignId, @AuthenticationPrincipal CustomUser authUser, Model model) throws Exception { // , @RequestParam("id") String id, @RequestParam("userId") String userId) throws Exception{
        // log.info(":::::::::: campaign01 화면 :::::::::: " + id +" :::::::::: " +  userId); ;
        log.info(":::::::::: campaign01 화면 authUser :::::::::: " + authUser);

       // 캠페인 상세 정보 조회
        CampaignVO campaignSelect = campaignsService.campaignSelect(campaignId);
        if (campaignSelect == null) {
            log.error("ID가 {}인 캠페인을 찾을 수 없습니다.", campaignId);
            // 에러 페이지로 바로 보내거나 리다이렉트
            return "error/404"; 
        }
        
        
        String formattedContent = campaignSelect.getMission();
        model.addAttribute("mission", formattedContent);

        model.addAttribute("campaignSelect", campaignSelect);

        // 로그인된 사용자가 있을 경우
        if (authUser != null) {
            UserVO user = authUser.getUserVo();
            model.addAttribute("user", user);

            // 현재 조회하는 캠페인에 대한 사용자의 신청 내역이 있는지 확인
            log.info(":::::::::: campaignApply 전 :::::::::: " + user);

            List<UserCampaignVO> campaignApply = campaignsService.campaignApplied(user.getUserId(), campaignSelect.getCampaignId());
            UserVO userSelect = userService.select(user.getUserId());
            log.info(":::::::::: userSelect :::::::::: " + userSelect);

            model.addAttribute("guardType", userSelect.getGuardType());
            
            log.info(":::::::::: campaignApply 후 :::::::::: " + campaignApply);

            // 2. 리스트 데이터 접근 시 안전하게 처리
            if (campaignApply != null && !campaignApply.isEmpty()) {
                model.addAttribute("campaignApply", campaignApply.get(0));
            }
        
            // 기본적으로 현재 캠페인 신청이 '가능'하다고 가정
            // 하지만 아래 로직을 통해 신청 불가능할 수 있음
            boolean canApply = true; 

            // 만약 사용자가 이 캠페인을 이미 신청했다면 신청 불가능
            // --- 수정 및 보완된 주요 부분 ---

            if (campaignApply != null && !campaignApply.isEmpty()) {
                // 1. 이미 신청한 경우
                canApply = false; 
                for (UserCampaignVO vo : campaignApply) {
                    vo.setEventActive(false); 
                }
                model.addAttribute("campaignApply", campaignApply.get(0)); // 안전하게 리스트 안에서 꺼냄

            } else {
                // 2. 신청하지 않은 경우 -> 기간 중복 체크
                List<UserCampaignVO> appliedCampaigns = campaignsService.appliedCampaign(user.getUserId());
                model.addAttribute("appliedCampaign", appliedCampaigns);

                LocalDate currentStart = campaignSelect.getAppPeriodStr();
                LocalDate currentEnd = campaignSelect.getAppPeriodEnd();

                if (currentStart == null || currentEnd == null) {
                    canApply = false;
                } else if (appliedCampaigns != null) { // null 체크 추가
                    for (UserCampaignVO existing : appliedCampaigns) {
                        LocalDate existStart = existing.getAppliedStrDate();
                        LocalDate existEnd = existing.getAppliedEndDate();

                        if (existStart == null || existEnd == null) continue;

                        // 기간 중복 조건 (하나라도 참이면 겹침)
                        boolean isOverlap = !currentStart.isAfter(existEnd) && !currentEnd.isBefore(existStart);
                        
                        if (isOverlap) {
                            canApply = false;
                            break;
                        }
                    }
                }
            }

            // 최종 상태 저장
            if(campaignSelect != null) {
                campaignSelect.setApplyPossible(canApply);
            }
        }
        return "campaign/campaign01";
    }

    // // 상세보기 처리
    // @GetMapping("/campaign02")
    // public String campaign0102(Model model, @RequestParam("id") String id) throws Exception {
    //     log.info(":::::::::: select :::::::::: " + id);

    //     CampaignVO campaignSelect = campaignsService.campaignSelect(id);
    //     List<UserVO> leaderList = campaignsService.leaderList();
    //     List<CampaignVO> securityType = campaignsService.securityType();
    //     FilesVO file = fileService.select(id);
        
    //     log.info("campaignSelect : " + campaignSelect);
    //     log.info("leaderList : " + leaderList);
    //     log.info("securityType : " + securityType);
    //     log.info("file : " + file);

    //     model.addAttribute("campaignSelect", campaignSelect);
    //     model.addAttribute("leaderList", leaderList);
    //     model.addAttribute("securityType", securityType);
    //     if(file != null){
    //         model.addAttribute("file", file);
    //     }
    //     return "/campaign/select";
    // }

    // @GetMapping("/campaign03")
    // public String campaign0103(Model model) throws Exception{
    //     log.info(":::::::::: 인서트 ::::::::::");

    //     List<UserVO> leaderList = campaignsService.leaderList();
    //     List<CampaignVO> securityType = campaignsService.securityType();

    //     log.info("leaderList : " + leaderList);
    //     log.info("securityType : " + securityType);

    //     model.addAttribute("leaderList", leaderList);
    //     model.addAttribute("securityType", securityType);

    //     return "/campaign/insert";
    // }

    // // 등록 처리
    // @PostMapping("/campaign04")
    // public String insertPost(CampaignVO campaignsVO) throws Exception {
    //     log.info("등록 처리");
    //     log.info("campaignsVO : " + campaignsVO);
    //     // campaignsVO.setCampaignStatusId(1);
        
    //     int result = campaignsService.campaignInsert(campaignsVO);
    //     if(result > 0){
    //         return "redirect:/";
    //     }
    //     return "redirect:/campaign/insert?error";
    //     // return "redirect:/board/insert?error";
    // }

    // // 수정 처리
    // @PostMapping("/campaign05")
    // public String updatePost(CampaignVO campaign) throws Exception{
    //     log.info("수정 처리 : " + campaign);
    //     int result = campaignsService.campaignUpdate(campaign);
    //     if(result > 0){
    //         return "redirect:/campaign/list";
    //     }
    //     return "redirect:/campaign/update?error&id="+campaign.getCampaignId();
    // }

    // // 수정 처리
    // @PostMapping("/delete")
    // public String deletePost(@RequestParam("id") String id) throws Exception{
    //     log.info("수정 처리 : " + id);
    //     int result = campaignsService.campaignDelete(id);
    //     if(result > 0){
    //         return "redirect:/admin/campaign/list";
    //     }
    //     return "redirect:/admin/campaign/update?error&id="+id;
    // }

    @GetMapping("/closedCampaign")
    public String closedCampaign(@AuthenticationPrincipal CustomUser authUser, Model model, @ModelAttribute("option") Option option, Page page) throws Exception{
        log.info("closedCampaign");
        
        if(authUser != null){
            UserVO user = authUser.getUserVo();
            // UserCampaignVO campaignApply = mainService.campaignApply(user.getUserId());
            model.addAttribute("user", user);
            // model.addAttribute("campaignApply", campaignApply);
        }

        List<CampaignVO> closedCampaign = campaignsService.closedCampaign();
        log.info("closedCampaign" + closedCampaign);

        model.addAttribute("closedCampaign", closedCampaign);

        return "campaign/campaign09";
    }
    

    @GetMapping("/search")
    public String searchCampaign(@RequestParam("schCamp") String schCamp, Model model) throws Exception {
        log.info("1. 검색 시작: " + schCamp);
    
        List<CampaignVO> list = null;

        if (schCamp != null && !schCamp.trim().isEmpty()) {
            // 검색어가 있으면 검색 결과 조회
            list = campaignsService.searchCampaign(schCamp);
            log.info("페이지 이동 검색 결과 개수: " + list.size());
        } else {
            // 검색어가 없으면 전체 목록 조회
            // list = campaignsService.selectAllCampaigns();
        }
        model.addAttribute("campaignList", list); // HTML에서 사용하는 이름과 일치해야 함
        model.addAttribute("keyword", schCamp);    // 검색창에 검색어 유지용
        log.info("3. 모델 담기 완료, 조각 리턴 직전");
        // "파일명 :: 조각이름" 형태로 리턴 (Thymeleaf Fragment 활용)
        return "campaign/campaignSearch";
    }

//     @GetMapping("/campaign/list")
// public String campaignList(@RequestParam(value = "schCamp", required = false) String schCamp, Model model) {
//     List<CampaignVO> list;

//     if (schCamp != null && !schCamp.trim().isEmpty()) {
//         // 검색어가 있으면 검색 결과 조회
//         list = campaignsService.searchCampaign(schCamp);
//         log.info("페이지 이동 검색 결과 개수: " + list.size());
//     } else {
//         // 검색어가 없으면 전체 목록 조회
//         list = campaignsService.selectAllCampaigns();
//     }

//     model.addAttribute("campaignList", list); // HTML에서 사용하는 이름과 일치해야 함
//     model.addAttribute("keyword", schCamp);    // 검색창에 검색어 유지용
    
//     return "campaign/list"; // 전체 페이지 리턴
// }
}
