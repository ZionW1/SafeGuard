package com.safeg.user.controller;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import com.safeg.user.vo.CampaignVO;
import com.safeg.user.vo.CommonData;
import com.safeg.user.vo.CustomUser;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.Page;
import com.safeg.user.vo.UserCampaignVO;
import com.safeg.user.vo.UserVO;
import com.safeg.user.vo.Users;
import com.safeg.user.service.AuthService;
import com.safeg.user.service.BannerService;
import com.safeg.user.service.CampaignService;
import com.safeg.user.service.MainService;
import com.safeg.user.service.UserService;
import com.safeg.user.vo.BannerVO;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class HomeController {
    
    @Autowired
    MainService mainService;

    @Autowired
    UserService userService;

    @Autowired
    BannerService bannerService;

    @Autowired
    AuthService authService;

    @Autowired
    CampaignService campaignService;
    @GetMapping("")
    // public String home(Principal principal, Model) throws Exception{
    // public Stirng home(Authentication authentication, Model model) throws Exception{
    // public String home(@AuthenticationPrincipal User authUser, Model model) throws Exception{
    public String home(@AuthenticationPrincipal CustomUser authUser, Model model) throws Exception{
        log.info(":::::::::: 메인 화면 :::::::::: + " + authUser);
        List<BannerVO> bannerImage = mainService.bannerImage();
        List<CampaignVO> campaignFavorite = mainService.campaignFavorite();
        List<CampaignVO> campaignWorkable = mainService.campaignWorkable();
        List<CampaignVO> campaignNew = mainService.campaignNew();
        List<BannerVO> bannerList = bannerService.bannerList();

        if (campaignFavorite != null && !campaignFavorite.isEmpty()) { // 리스트가 null이 아니고 비어있지 않을 때만!
            log.info("campaignFavorite.get(0) = " + campaignFavorite.get(0)); // 이제 안전해!
            
            for (int i = 0; i < campaignFavorite.size(); i++) {
                // 이 for 루프도 campaignFavorite이 비어있으면 실행 안 될 거라 이 부분은 괜찮아!
                log.info("campaignFavorite.get(i).() " + campaignFavorite.toString()); // toString()은 리스트 전체를 출력해
                log.info("campaignFavorite.get(i).getRecruitNum() " + campaignFavorite.get(i).getRecruitNum());
                log.info("campaignFavorite.get(i).getApplicantsNum() " + campaignFavorite.get(i).getApplicantsNum());
    
                if (campaignFavorite.get(i).getApplicantsNum() == campaignFavorite.get(i).getRecruitmentNum()) {
                    campaignService.updateCampaign(campaignFavorite.get(i).getCampaignId());
                    List<UserCampaignVO> userCampaignVO = mainService.applyDate(campaignFavorite.get(i).getCampaignId());

                    authService.sendApply(campaignFavorite.get(i).getCampaignId(), userCampaignVO);
                }
            }
        } else {
            log.info("DEBUG: campaignFavorite 리스트가 비어있습니다. (또는 null)");
            // campaignFavorite이 비어있을 때 특별히 처리할 로직이 있다면 여기에 추가
            // 예를 들어, model.addAttribute("noFavoriteCampaigns", true); 같은 것을 추가해서 템플릿에서 조건부 렌더링 가능
        }
        
        log.info("bannerList : : : : " + bannerList);
        model.addAttribute("campaignFavorite", campaignFavorite);
        model.addAttribute("campaignWorkable", campaignWorkable);
        model.addAttribute("campaignNew", campaignNew);
        model.addAttribute("bannerImage", bannerImage);
        model.addAttribute("bannerList", bannerList);

        //model.addAttribute("pageUrl", pageUrl);
        if(authUser != null){
            log.info("authUser : " + authUser);
            UserVO user = authUser.getUserVo();
            log.info("user : " + user);

            model.addAttribute("user", user);
        }
        
        return "index";
    }

    // @GetMapping("/campaign01")
    // // public String home(Principal principal, Model) throws Exception{
    // // public Stirng home(Authentication authentication, Model model) throws Exception{
    // // public String home(@AuthenticationPrincipal User authUser, Model model) throws Exception{
    // public String campaign01(@AuthenticationPrincipal CustomUser authUser, Model model, @RequestParam("id") String id, @RequestParam("userId") String userId, 
    //     @RequestParam(value = "eventPeriodStr", required = false) LocalDate eventPeriodStr, @RequestParam(value = "eventPeriodEnd", required = false) LocalDate eventPeriodEnd) throws Exception{
    //     log.info(":::::::::: campaign01 화면 :::::::::: " + id +" :::::::::: " +  userId + " :::::::::: " + eventPeriodStr + " :::::::::: " + eventPeriodEnd); ;
    //         if(authUser != null){
    //         UserVO user = authUser.getUserVo();
    //         UserCampaignVO campaignApply = mainService.campaignApply(user.getUserId(), id );
    //         List<UserCampaignVO> appliedCampaign = mainService.appliedCampaign(user.getUserId());
    //         if(appliedCampaign != null && campaignApply != null){
    //         log.info("appliedCampaign : " + appliedCampaign);

    //             LocalDate startDate = campaignApply.getEventPeriodStr();
    //             LocalDate endDate = campaignApply.getEventPeriodEnd();

    //             // 오늘 날짜가 시작일 <= 오늘 <= 종료일 이면 이벤트 활성화 중으로 간주
    //             boolean isEventActive = true;
    //             if (startDate == null || endDate == null) {
    //                 isEventActive = false; // 날짜 정보가 없으면 신청 불가능
    //             } else {
    //                 // 사용자가 신청한 이벤트들과 현재 캠페인 기간이 겹치는지 확인
    //                 for (UserCampaignVO appliedPeriod : appliedCampaign) {
    //                     LocalDate appliedStart = appliedPeriod.getAppliedStrDate();
    //                     LocalDate appliedEnd = appliedPeriod.getAppliedEndDate();
    
    //                     // ⭐ 기간 중복 판단 로직: (campaignStartDate <= appliedEndDate && campaignEndDate >= appliedStartDate) ⭐
    //                     // 현재 캠페인의 시작일이 기존 신청의 종료일보다 빠르거나 같고,
    //                     // 현재 캠페인의 종료일이 기존 신청의 시작일보다 늦거나 같으면 겹치는 것!
    //                     log.info(userId + "님의 기존 신청 기간: " + startDate.compareTo(appliedEnd) + " ~ " + endDate.compareTo(appliedStart));
    //                     if (startDate.compareTo(appliedEnd) <= 0 && endDate.compareTo(appliedStart) >= 0) {
    //                         isEventActive = false; // 기간이 겹치므로 신청 불가능
    //                         break; // 더 이상 검사할 필요 없음
    //                     }
    //                 }
    //             }
    //             campaignApply.setEventActive(isEventActive); // VO에 신청 가능 여부 설정
    //         model.addAttribute("campaignApply", campaignApply);
    //         }
    //         model.addAttribute("user", user);
    //     }
    //     CampaignVO campaignSelect = mainService.campaignSelect(id);

    //     // log.info("campaignSelect : : : : " + campaignSelect.getMission());
    //     model.addAttribute("campaignSelect", campaignSelect);
    //     return "campaign01";
    // }

    @GetMapping("/campaign07")
    @ResponseBody // <-- 이 어노테이션을 추가해줘!
    // public String home(Principal principal, Model) throws Exception{
    // public Stirng home(Authentication authentication, Model model) throws Exception{
    // public String home(@AuthenticationPrincipal User authUser, Model model) throws Exception{
    public CampaignVO campaign07(@AuthenticationPrincipal CustomUser authUser, Model model, @RequestParam("id") String id) throws Exception{
        log.info(":::::::::: campaignSelect 화면 :::::::::: " + id +" +++++++ ") ;
        if(authUser != null){
            UserVO user = authUser.getUserVo();
            List<UserCampaignVO> campaignApply = mainService.campaignApply(user.getUserId(), id );
            model.addAttribute("user", user);
            if(campaignApply.size() != 0){
                model.addAttribute("campaignApply", campaignApply.get(0));
            }
            

        }
        CampaignVO campaignSelect = mainService.campaignSelect(id);

        // log.info("campaignSelect : : : : " + campaignSelect.getMission());
        // model.addAttribute("campaignSelect", campaignSelect);
        return mainService.campaignSelect(id);
    }

    @GetMapping("/campaign08")
    // public String home(Principal principal, Model) throws Exception{
    // public Stirng home(Authentication authentication, Model model) throws Exception{
    // public String home(@AuthenticationPrincipal User authUser, Model model) throws Exception{
    public String campaign08(@AuthenticationPrincipal CustomUser authUser, Model model, @ModelAttribute("option") Option option, Page page) throws Exception{
        log.info(":::::::::: All View 화면 :::::::::: " + option + " :::::::::: " + page);
        if(authUser != null){
            UserVO user = authUser.getUserVo();
            // UserCampaignVO campaignApply = mainService.campaignApply(user.getUserId());
            model.addAttribute("user", user);
            // model.addAttribute("campaignApply", campaignApply);
        }


        

        String pageUrl = UriComponentsBuilder.fromPath("/campaign08")
                        //.queryParam("page", page.getPage())
                        .queryParam("keyword", option.getKeyword())
                        .queryParam("code", option.getCode())
                        // .queryParam("rows", page.getRows())
                        .queryParam("orderCode", option.getOrderCode())
                        .build()
                        .toUriString();
        log.info("pageRows : " + page.getRows());
        
        List<CampaignVO> allView = mainService.allView(option);
        log.info("allView : " + allView);
        model.addAttribute("option", option);
        model.addAttribute("rows", page.getRows());
        model.addAttribute("page", page);
        model.addAttribute("allView", allView);
        model.addAttribute("pageUrl", pageUrl);

        return "campaign/campaign08";
    }
    
    @GetMapping("/campaign09")
    @ResponseBody
    // public String home(Principal principal, Model) throws Exception{
    // public Stirng home(Authentication authentication, Model model) throws Exception{
    // public String home(@AuthenticationPrincipal User authUser, Model model) throws Exception{
    public List<CampaignVO> campaign09(@AuthenticationPrincipal CustomUser authUser, Model model, @Param("option") Option option) throws Exception{
        log.info(":::::::::: All View 화면 :::::::::: " + option);
        // option.setOrderCode(0);
        if(authUser != null){
            UserVO user = authUser.getUserVo();
            // UserCampaignVO campaignApply = mainService.campaignApply(user.getUserId());
            model.addAttribute("user", user);
            // model.addAttribute("campaignApply", campaignApply);
        }
        List<CampaignVO> allViewOption = mainService.allView(option);

        model.addAttribute("allViewOption", allViewOption);
        
        return allViewOption;
    }

    @GetMapping("/addressPopup")
    public String getPopupFragment() throws Exception {
        return "/popup/addressPopup :: popupBody"; // Fragment 이름 지정
    }
    
    /**
     * 캠페인 신청
     * 🔗 [POST] - /join
     * ➡   ⭕ /login
     *      ❌ /join?error
     * @param user
     * @return
     * @throws Exception
     */
    @PostMapping("/apply")
    @ResponseBody
    public String campaignApply(@RequestBody UserCampaignVO userCampaign, HttpServletRequest request) throws Exception {
        log.info(":::::::::: 캠페인 신청 처리 ::::::::::");
        log.info("캠페인 신청 처리 : " + userCampaign);

        // 캠페인 신청 요청
        int result = campaignService.campaignApply(userCampaign);

        
        // // 회원 가입 성공 시, 바로 로그인
        boolean loginResult = false;
        // if( result > 0 ) {
        //     // 암호화 전 비밀번호 다시 세팅
        //     // 회원가입 시, 비밀번호 암호화하기 때문에, 
        //     user.setPassword(plainPassword);
        //     loginResult = userService.login(user, request);
        // }
        // if (loginResult){
        //     return "redirect:/"; // 메인 화면으로 이동
        // }
        // if( result > 0){
        //     return "redirect:/login";
        // }

        return "redirect:/";
        
    }

    @GetMapping("/userCampaignApply")
    // @ResponseBody
    // public String home(Principal principal, Model) throws Exception{
    // public Stirng home(Authentication authentication, Model model) throws Exception{
    // public String home(@AuthenticationPrincipal User authUser, Model model) throws Exception{
    public String userCampaignApply(@AuthenticationPrincipal CustomUser authUser, Model model, @RequestParam("id") String id) throws Exception{
        log.info(":::::::::: userCampaignApply 화면 :::::::::: " + id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
        // ⭐⭐ 이 조건문이 굉장히 중요해! ⭐⭐
        if (authentication.getPrincipal() instanceof CustomUser) {
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            Long userIdFromDb = customUser.getId(); // users 테이블의 실제 id 값을 가져왔다!
            // ... (로그 출력) ...
            model.addAttribute("userId", userIdFromDb); // 뷰에서 DB ID를 사용할 수 있도록 모델에 추가
            // ...
        } else {
            // ⭐ 이 else 블록으로 진입하면 model.addAttribute("userId", null); 이 실행돼! ⭐
            model.addAttribute("userId", null); // userId가 null이 된다!
            model.addAttribute("username", authentication.getName());
        }

        // option.setOrderCode(0);
        if(authUser != null){
            UserVO user = authUser.getUserVo();
            // UserCampaignVO campaignApply = mainService.campaignApply(user.getUserId());
            model.addAttribute("user", user);
            // model.addAttribute("campaignApply", campaignApply);
        }
        List<UserCampaignVO> userCampaignApply = mainService.userCampaignApply(id);

        model.addAttribute("userCampaignApply", userCampaignApply);
        log.info("recruitmentNum : " + userCampaignApply);
        
        return "apply/userCampaignApplyM";
    }
    
    @GetMapping("/userCampaignApply01")
    public String userCampaignApply01(@AuthenticationPrincipal CustomUser authUser, Model model, @RequestParam("id") String id) throws Exception{
        log.info(":::::::::: userCampaignApply 화면 :::::::::: " + id);
        // option.setOrderCode(0);
        if(authUser != null){
            UserVO user = authUser.getUserVo();
            // UserCampaignVO campaignApply = mainService.campaignApply(user.getUserId());
            model.addAttribute("user", user);
            // model.addAttribute("campaignApply", campaignApply);
        }
        List<UserCampaignVO> userCampaignApply = mainService.userCampaignApply(id);

        model.addAttribute("userCampaignApply", userCampaignApply);
        
        return "userCampaignApply";
    }

    @GetMapping("/ask")
    public String getMethodName(@AuthenticationPrincipal CustomUser authUser, Model model) throws Exception { 
        //, @RequestParam String param
        log.info(":::::::::: ask 화면 :::::::::: " + authUser + " :::::::::: ");
        int campaignCount = mainService.campaignCount();

        int totalUsers = userService.totalUser();
        int totalGuard = userService.totalGuard();
        int totalCampaign = mainService.totalCampaign();

        log.info("campaignCount : : : : " + campaignCount);
        List<CampaignVO> campaignDeleted = mainService.campaignDeleted();
        if(authUser != null){
            UserVO user = authUser.getUserVo();
            model.addAttribute("user", user);
        }
        model.addAttribute("campaignCount", campaignCount);
        model.addAttribute("campaignDeleted", campaignDeleted);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalGuard", totalGuard);
        model.addAttribute("totalCampaign", totalCampaign);

        return "ask01";
    }

    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "privacy";
    }
}
