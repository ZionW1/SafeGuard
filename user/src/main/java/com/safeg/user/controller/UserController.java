package com.safeg.user.controller;

import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.safeg.user.vo.CampaignVO;
import com.safeg.user.vo.CustomUser;
import com.safeg.user.vo.UserVO;
import com.safeg.user.vo.Users;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import com.safeg.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class UserController {

    // @Autowired
    // UserService userService;

    // private final PasswordEncoder passwordEncoder; // ⭐️ BCryptPasswordEncoder가 주입될 곳 ⭐️

    // // ⭐️ 생성자 주입 ⭐️
    // public UserController(UserService userService, PasswordEncoder passwordEncoder) {
    //     this.userService = userService;
    //     this.passwordEncoder = passwordEncoder;
    // }


    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // 모든 의존성을 생성자 주입으로 받음
    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
    * 로그인 화면
    * @return
    */
    @GetMapping("/user01")
    public String login(@CookieValue(value="remember-id", required = false) Cookie cookie, Model model ) {
        // @CookieValue(value="쿠키이름", required = 필수여부)
        // - required=true (default)  : 쿠키를 필수로 가져와서 없으면 에러
        // - required=false           : 쿠키 필수 ❌ ➡ 쿠키가 없으면 null, 에러❌
        log.info(":::::::::: 로그인 페이지 :::::::::: " + cookie);

        String username = "";
        boolean rememberId = false;
        if( cookie != null ) {
            log.info("CookieName : " + cookie.getName());
            log.info("CookieValue : " + cookie.getValue());
            username = cookie.getValue();
            rememberId = true;
        }
        model.addAttribute("username", username);
        model.addAttribute("rememberId", rememberId);
        return "user/user01";
    }

    @GetMapping("/user02")
    public String join(Model model) {
        log.info(":::::::::: 회원 가입 화면 ::::::::::");

        model.addAttribute("userVO", new UserVO());

        return "user/user02";
    }

    /**
     * 회원 가입 처리
     * 🔗 [POST] - /join
     * ➡   ⭕ /login
     *      ❌ /join?error
     * @param user
     * @return
     * @throws Exception
     */
    @PostMapping("/user02")
    public String joinPost(@Valid @ModelAttribute("userVO") UserVO userVO, BindingResult bindingResult, HttpServletRequest request) throws Exception {
        log.info(":::::::::: 회원 가입 처리 ::::::::::");
        log.info("user : " + userVO);
        log.info("getreferrerId : " + userVO.getReferrerId());

        if (!userVO.isPasswordConfirmed()) {
            log.info("비밀번호 불일치 (평문 비교) + " + userVO.isPasswordConfirmed());
            bindingResult.rejectValue("passwordConfirm", "password.mismatch", "비밀번호가 일치하지 않습니다.");
        }

        if (bindingResult.hasErrors()) {
            log.info("bindingResult.hasErrors");
            return "user/user02";
        }

        String rawPassword = userVO.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        userVO.setPassword(encodedPassword);
        
        // 회원가입 DB 저장
        int result = userService.join(userVO);
        
        boolean loginResult = false;
        
        if (result > 0) {
            // 로그인용 객체에 평문 비밀번호 세팅 (userVO를 새로 만들거나 복사해도 좋음)
            UserVO loginUser = new UserVO();
            loginUser.setUserId(userVO.getUserId());
            loginUser.setPassword(rawPassword);  // 반드시 평문 비밀번호 사용
        
            loginResult = userService.login(loginUser, request);
        }
        
            log.info("loginResult + " + loginResult);






        // String rawPasswordToEncode = userVO.getPassword(); // 평문 비밀번호 가져오기
        // String encodedPassword = passwordEncoder.encode(rawPasswordToEncode); // BCrypt로 암호화
        // log.info("rawPasswordToEncode" + rawPasswordToEncode);

        // userVO.setPassword(encodedPassword);
        // // 암호화 전 비밀번호
        // String plainPassword = userVO.getPassword();

        // log.info(":::::::::: 어드민 가입 처리 최종 (암호화 후) :::::::::: Password=" + userVO.getPassword());

        // // // 회원 가입 요청
        // int result = userService.join(userVO);

        // // // 회원 가입 성공 시, 바로 로그인
        // boolean loginResult = false;
        
        // if( result > 0 ) {
        //     // 암호화 전 비밀번호 다시 세팅
        //     // 회원가입 시, 비밀번호 암호화하기 때문에, 
        //     userVO.setPassword(plainPassword);

        //     log.info("plainPassword : " + plainPassword);
        //     loginResult = userService.login(userVO, request);
        //     log.info("loginResult + " + loginResult);

        // }
        if (loginResult){
            return "redirect:/"; // 메인 화면으로 이동
        }

        return "redirect:/join?error";
        
    }

    @GetMapping("/mypage")
    public String mypage(@AuthenticationPrincipal CustomUser authUser, Model model) throws Exception {
        log.info(":::::::::: 회원 마이 페이지 :::::::::: + " + authUser);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
        // ⭐⭐ 이 조건문이 굉장히 중요해! ⭐⭐
        if (authentication.getPrincipal() instanceof CustomUser) {
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            Long userIdFromDb = customUser.getId(); // users 테이블의 실제 id 값을 가져왔다!
            String usernameFormDb = customUser.getUsername();
            // ... (로그 출력) ...
            model.addAttribute("userId", userIdFromDb); // 뷰에서 DB ID를 사용할 수 있도록 모델에 추가
            model.addAttribute("username", usernameFormDb);

            // ...
        } else {
            // ⭐ 이 else 블록으로 진입하면 model.addAttribute("userId", null); 이 실행돼! ⭐
            model.addAttribute("userId", null); // userId가 null이 된다!
            model.addAttribute("username", authentication.getName());
        }

        if(authUser != null){
            UserVO user = authUser.getUserVo();
            model.addAttribute("user", user);
        }else{
            return "redirect:/";
        }
        log.info(authUser.getUserVo().toString());

        return "user/user03";
    }

    // @PostMapping("/updateInfo")
    // public String updateInfo(@AuthenticationPrincipal CustomUser authUser, UserVO userVo, Model model) throws Exception{
    //     log.info(":::::::::: 회원 마이 페이지 :::::::::: + " + authUser.getUserVo().getUserId());
    //     log.info(":::::::::: 회원 마이 페이지 :::::::::: + " + userVo);

    //     int result = 0;
    //     if(authUser != null){
    //         result = userService.updateInfo(userVo);
    //     }
    //     log.info(":::::::::: 회원 마이 페이지 완료 :::::::::: + " + result);

    //     // Users mypageSelect = userService.mypageSelect(authUser.getUser().getId());

    //     return "/index";
    // }

    @PostMapping("/updateProfile")
    public String updateProfile(@AuthenticationPrincipal CustomUser authUser, UserVO userVo, Model model) throws Exception{
        log.info(":::::::::: 회원 마이 페이지 :::::::::: + " + authUser.getUserVo().getUserId());
        log.info(":::::::::: 회원 마이 페이지 :::::::::: + " + userVo);

        boolean result = false;
        if(authUser != null){
            result = userService.updateProfile(userVo);
        }
        log.info(":::::::::: 회원 프로필 업데이트 완료 :::::::::: + " + result);

        // Users mypageSelect = userService.mypageSelect(authUser.getUser().getId());

        return "index";
    }

    @GetMapping("/addressCode")
    public String getPopupFragment() throws Exception {
        return "/popup/addressPopup :: popupBody"; // Fragment 이름 지정
    }

    // @GetMapping("/user04")
    // public String user04(@RequestParam(value = "userId", required = false) String id, Model model) {
    //     log.info(":::::::::: user04 호출 :::::::::: + ");
    //     if (id == null || id.isEmpty()) {
    //         // userId가 없는 경우의 처리 로직 (예: 에러 메시지 표시, 다른 페이지로 리다이렉트 등)
    //         // 로그인된 사용자의 ID를 세션/SecurityContext에서 가져와 사용하는 방법도 고려할 수 있어
    //         // 예: id = SecurityContextHolder.getContext().getAuthentication().getName();
    //         // 또는 그냥 빈 목록을 보여주거나, 로그인 페이지로 리다이렉트
    //         log.warn("userId 파라미터가 제공되지 않았습니다. 현재 로그인된 사용자 정보를 활용합니다.");
    //         // 혹은 throw new IllegalArgumentException("사용자 ID가 필요합니다.");
    //     }
    //     // id를 이용한 로직...
        
    //     return "/user/user04"; 
    // }

    // @GetMapping("/user04")
    // public String getUserPage(Model model) {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     String currentUserId = authentication.getName(); // 현재 로그인된 사용자의 ID
        
    //     log.info(":::::::::: user04 호출 :::::::::: + " + currentUserId);
    //     // currentUserId를 이용해서 필요한 데이터 조회 및 모델에 추가
    //     // ...
        
    //     model.addAttribute("userId", currentUserId); // 뷰에서 사용할 수도 있도록 모델에 추가
    //     return "/user/user04";
    // }

    @GetMapping("/bestAgent")
    public String bestAgent(@AuthenticationPrincipal CustomUser authUser, HttpServletRequest request, Model model) throws Exception{
        log.info(":::::::::: campaign01 화면 authUser :::::::::: " + authUser);

       // 캠페인 상세 정보 조회
        List<UserVO> bestAgentList = userService.bestAgentList();
        model.addAttribute("bestAgentList", bestAgentList);

        List<UserVO> bestPayList = userService.bestPayList();
        model.addAttribute("bestPayList", bestPayList);

        // // 로그인된 사용자가 있을 경우
        // if (authUser != null) {
        //     UserVO user = authUser.getUserVo();
        //     model.addAttribute("user", user);

        //     // 현재 조회하는 캠페인에 대한 사용자의 신청 내역이 있는지 확인
        //     UserCampaignVO campaignApply = campaignsService.campaignApplied(user.getUserId(), id);

        //     // 기본적으로 현재 캠페인 신청이 '가능'하다고 가정
        //     // 하지만 아래 로직을 통해 신청 불가능할 수 있음
        //     boolean canApply = true; 

        //     // 만약 사용자가 이 캠페인을 이미 신청했다면 신청 불가능
        //     if (campaignApply != null) {
        //         canApply = false; 
        //         campaignApply.setEventActive(false); // 신청 내역이 있으면 '활성화' 상태를 false로
        //     } else {
        //         // 사용자가 이 캠페인을 신청하지 않았다면, 다른 캠페인과의 기간 중복 여부 확인
        //         List<UserCampaignVO> appliedCampaigns = campaignsService.appliedCampaign(user.getUserId());
        //         model.addAttribute("appliedCampaign", appliedCampaigns); // 이미 신청한 캠페인 목록도 모델에 추가

        //         // 현재 캠페인의 신청 가능 기간
        //         LocalDate currentCampaignStartDate = campaignSelect.getAppPeriodStr(); // ⭐ AppPeriodStr -> start Date
        //         LocalDate currentCampaignEndDate = campaignSelect.getAppPeriodEnd();   // ⭐ AppPeriodEnd -> end Date

        //         // 기간이 유효한지 먼저 확인 (null 체크 등)
        //         if (currentCampaignStartDate == null || currentCampaignEndDate == null) {
        //             log.warn("현재 캠페인 ID: {} 의 신청 기간 정보가 유효하지 않습니다.", id);
        //             canApply = false; // 기간 정보가 없으면 신청 불가능
        //         } else {
        //             // 이미 신청한 캠페인들과 현재 캠페인 기간이 겹치는지 확인
        //             for (UserCampaignVO existingAppliedCampaign : appliedCampaigns) {
        //                 LocalDate existingStartDate = existingAppliedCampaign.getAppliedStrDate();
        //                 LocalDate existingEndDate = existingAppliedCampaign.getAppliedEndDate();

        //                 // 기존 신청 기간 정보도 유효한지 확인
        //                 if (existingStartDate == null || existingEndDate == null) {
        //                     log.warn("사용자 {} 님의 기존 신청 캠페인 ID: {} 기간 정보가 유효하지 않습니다.", user.getUserId(), existingAppliedCampaign.getCampaignId());
        //                     continue; // 이 캠페인은 건너뛰고 다음 캠페인 확인
        //                 }

        //                 // ⭐ 핵심 로직: 두 기간이 겹치는지 확인 ⭐
        //                 // 현재 캠페인의 시작일이 기존 캠페인의 종료일보다 빠르거나 같고 (겹치는 시작점),
        //                 // 현재 캠페인의 종료일이 기존 캠페인의 시작일보다 늦거나 같으면 (겹치는 종료점)
        //                 // 즉, `(Start1 <= End2) AND (End1 >= Start2)` 이면 기간이 겹침.
        //                 boolean isOverlap = 
        //                     !currentCampaignStartDate.isAfter(existingEndDate) && 
        //                     !currentCampaignEndDate.isBefore(existingStartDate);

        //                 // 또는 더 직관적인 표현:
        //                 // (!currentCampaignStartDate.isAfter(existingEndDate)) : 현재 캠페인 시작일이 기존 캠페인 종료일보다 뒤가 아니다 (즉, 같거나 빠르다)
        //                 // (!currentCampaignEndDate.isBefore(existingStartDate)) : 현재 캠페인 종료일이 기존 캠페인 시작일보다 앞이 아니다 (즉, 같거나 뒤다)
                        
        //                 if (isOverlap) {
        //                     canApply = false; // 기간이 겹치므로 현재 캠페인은 신청 불가능
        //                     log.info("캠페인 ID: {} 이 기존 신청 캠페인 ID: {} 와 기간이 겹침. 시작일: {}, 종료일: {} vs 시작일: {}, 종료일: {}",
        //                             id, existingAppliedCampaign.getCampaignId(), currentCampaignStartDate, currentCampaignEndDate, existingStartDate, existingEndDate);
        //                     // campaignApply.setEventActive(false); // 신청 내역이 있으면 '활성화' 상태를 false로
        //                     break; // 하나라도 겹치면 더 이상 검사할 필요 없음
        //                 }
        //             }
        //         }
        //     }
        
        //     // 최종적으로 이 캠페인이 신청 가능한지 여부를 model에 추가
        //     // `campaignSelect` VO 내에 `setCanApply` 같은 필드를 추가해서 사용하면 뷰에서 편리
        //     if(campaignSelect != null){
        //         campaignSelect.setApplyPossible(canApply); // CampaignVO에 `canApply` 필드를 추가해야 함
        //     }
        
        //     // `campaignApply`가 null이더라도 model에 넣어서 뷰에서 null 체크하도록
        //     model.addAttribute("campaignApply", campaignApply); 
        // }
    
        // // `currentURI`도 여전히 모델에 추가해야 해! (사이드바 active 클래스 때문)
        // // model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("currentURI", request.getRequestURI());

        return "user/user05";

    }

    
}
