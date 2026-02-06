package com.safeg.user.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import com.safeg.user.service.CampaignService;
import com.safeg.user.service.FileService;
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

    // @Autowired
    // private FileService fileService;

    @SuppressWarnings("null")
    @GetMapping("/campaign01")
    public String campaign01(@AuthenticationPrincipal CustomUser authUser, Model model, @RequestParam("id") String id, @RequestParam("userId") String userId, 
        @RequestParam(value = "eventPeriodStr", required = false) LocalDate eventPeriodStr, @RequestParam(value = "eventPeriodEnd", required = false) LocalDate eventPeriodEnd) throws Exception{
        log.info(":::::::::: campaign01 화면 :::::::::: " + id +" :::::::::: " +  userId + " :::::::::: " + eventPeriodStr + " :::::::::: " + eventPeriodEnd); ;
        log.info(":::::::::: campaign01 화면 authUser :::::::::: " + authUser);

       // 캠페인 상세 정보 조회
        CampaignVO campaignSelect = campaignsService.campaignSelect(id);
        log.info(":::::::::: campaign01 campaignSelect :::::::::: " + campaignSelect);

        model.addAttribute("campaignSelect", campaignSelect);

        // 로그인된 사용자가 있을 경우
        if (authUser != null) {
            UserVO user = authUser.getUserVo();
            model.addAttribute("user", user);

            // 현재 조회하는 캠페인에 대한 사용자의 신청 내역이 있는지 확인
            log.info(":::::::::: campaignApply 전 :::::::::: " + authUser);

            List<UserCampaignVO> campaignApply = campaignsService.campaignApplied(user.getUserId(), id);
            log.info(":::::::::: campaignApply 후 :::::::::: " + campaignApply);

            // 기본적으로 현재 캠페인 신청이 '가능'하다고 가정
            // 하지만 아래 로직을 통해 신청 불가능할 수 있음
            boolean canApply = true; 

            // 만약 사용자가 이 캠페인을 이미 신청했다면 신청 불가능
            if (campaignApply.size() > 0) {
                canApply = false; 
                for(int i = 0; i < campaignApply.size(); i++){
                    campaignApply.get(i).setEventActive(false); // 신청 내역이 있으면 '활성화' 상태를 false로
                }
                model.addAttribute("campaignApply", campaignApply.get(0));

            } else {
                log.info(":::::::::: campaignApply else :::::::::: " + campaignApply);

                // 사용자가 이 캠페인을 신청하지 않았다면, 다른 캠페인과의 기간 중복 여부 확인
                List<UserCampaignVO> appliedCampaigns = campaignsService.appliedCampaign(user.getUserId());
                model.addAttribute("appliedCampaign", appliedCampaigns); // 이미 신청한 캠페인 목록도 모델에 추가

                // 현재 캠페인의 신청 가능 기간
                LocalDate currentCampaignStartDate = campaignSelect.getAppPeriodStr(); // ⭐ AppPeriodStr -> start Date
                LocalDate currentCampaignEndDate = campaignSelect.getAppPeriodEnd();   // ⭐ AppPeriodEnd -> end Date

                // 기간이 유효한지 먼저 확인 (null 체크 등)
                if (currentCampaignStartDate == null || currentCampaignEndDate == null) {
                    log.warn("현재 캠페인 ID: {} 의 신청 기간 정보가 유효하지 않습니다.", id);
                    canApply = false; // 기간 정보가 없으면 신청 불가능
                } else {
                    // 이미 신청한 캠페인들과 현재 캠페인 기간이 겹치는지 확인
                    for (UserCampaignVO existingAppliedCampaign : appliedCampaigns) {
                        LocalDate existingStartDate = existingAppliedCampaign.getAppliedStrDate();
                        LocalDate existingEndDate = existingAppliedCampaign.getAppliedEndDate();

                        // 기존 신청 기간 정보도 유효한지 확인
                        if (existingStartDate == null || existingEndDate == null) {
                            log.warn("사용자 {} 님의 기존 신청 캠페인 ID: {} 기간 정보가 유효하지 않습니다.", user.getUserId(), existingAppliedCampaign.getCampaignId());
                            continue; // 이 캠페인은 건너뛰고 다음 캠페인 확인
                        }

                        // ⭐ 핵심 로직: 두 기간이 겹치는지 확인 ⭐
                        // 현재 캠페인의 시작일이 기존 캠페인의 종료일보다 빠르거나 같고 (겹치는 시작점),
                        // 현재 캠페인의 종료일이 기존 캠페인의 시작일보다 늦거나 같으면 (겹치는 종료점)
                        // 즉, `(Start1 <= End2) AND (End1 >= Start2)` 이면 기간이 겹침.
                        boolean isOverlap = 
                            !currentCampaignStartDate.isAfter(existingEndDate) && 
                            !currentCampaignEndDate.isBefore(existingStartDate);

                        // 또는 더 직관적인 표현:
                        // (!currentCampaignStartDate.isAfter(existingEndDate)) : 현재 캠페인 시작일이 기존 캠페인 종료일보다 뒤가 아니다 (즉, 같거나 빠르다)
                        // (!currentCampaignEndDate.isBefore(existingStartDate)) : 현재 캠페인 종료일이 기존 캠페인 시작일보다 앞이 아니다 (즉, 같거나 뒤다)
                        
                        if (isOverlap) {
                            canApply = false; // 기간이 겹치므로 현재 캠페인은 신청 불가능
                            log.info("캠페인 ID: {} 이 기존 신청 캠페인 ID: {} 와 기간이 겹침. 시작일: {}, 종료일: {} vs 시작일: {}, 종료일: {}",
                                    id, existingAppliedCampaign.getCampaignId(), currentCampaignStartDate, currentCampaignEndDate, existingStartDate, existingEndDate);
                            // campaignApply.setEventActive(false); // 신청 내역이 있으면 '활성화' 상태를 false로
                            break; // 하나라도 겹치면 더 이상 검사할 필요 없음
                        }
                    }
                }
            }
            
            // 최종적으로 이 캠페인이 신청 가능한지 여부를 model에 추가
            // `campaignSelect` VO 내에 `setCanApply` 같은 필드를 추가해서 사용하면 뷰에서 편리
            if(campaignSelect != null){
                campaignSelect.setApplyPossible(canApply); // CampaignVO에 `canApply` 필드를 추가해야 함
            }
            
            // `campaignApply`가 null이더라도 model에 넣어서 뷰에서 null 체크하도록
            // model.addAttribute("campaignApply", campaignApply.get(0)); 
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
    public String closedCampaign(Model model) throws Exception{
        log.info("closedCampaign");

        List<CampaignVO> closedCampaign = campaignsService.closedCampaign();
        log.info("closedCampaign" + closedCampaign);

        model.addAttribute("closedCampaign", closedCampaign);

        return "campaign/campaign09";
    }
    

    
}
