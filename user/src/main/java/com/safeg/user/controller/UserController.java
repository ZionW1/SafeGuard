package com.safeg.user.controller;

import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;

import com.safeg.user.vo.CampaignVO;
import com.safeg.user.vo.CustomUser;
import com.safeg.user.vo.PointHistoryVO;
import com.safeg.user.vo.UserVO;
import com.safeg.user.vo.Users;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import com.safeg.user.service.UserService;
import com.safeg.user.util.EncryptionUtil;

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
    @ResponseBody // JSON 응답을 위해 추가
    public ResponseEntity<?> joinPost(@Valid @ModelAttribute("userVO") UserVO userVO, BindingResult bindingResult, HttpServletRequest request) throws Exception {
        log.info(":::::::::: 회원 가입 처리 (Async) :::::::::: " + userVO);

        // 1. 비밀번호 일치 확인
        if (!userVO.isPasswordConfirmed()) {
            bindingResult.rejectValue("passwordConfirm", "password.mismatch", "비밀번호가 일치하지 않습니다.");
        }

        // 2. 유효성 검사 에러 처리
        if (bindingResult.hasErrors()) {
            // 모든 에러 메시지를 수집하여 반환
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(Map.of("success", false, "errors", errors));
        }

        // String hashedPhone = EncryptionUtil.hash(userVO.getPhoneNum());
    
        // // 2. 중복 체크 (DB의 phone_hash 컬럼과 비교)
        // if (userService.phoneDuplicate(hashedPhone)) {
        //     throw new RuntimeException("이미 사용 중인 번호입니다.");
        // }

        // userVO.setPhoneHash(hashedPhone);

        // 3. 비밀번호 암호화 및 저장
        String rawPassword = userVO.getPassword();
        userVO.setPassword(passwordEncoder.encode(rawPassword));
        
        int result = userService.join(userVO);
        
        if (result > 0) {
            // 4. 자동 로그인 시도
            UserVO loginUser = new UserVO();
            loginUser.setUserId(userVO.getUserId());
            loginUser.setPassword(rawPassword);
            
            boolean loginResult = userService.login(loginUser, request);
            
            if (loginResult) {
                return ResponseEntity.ok(Map.of("success", true, "url", "/"));
            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("success", false, "message", "회원가입 처리 중 오류가 발생했습니다."));
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
    @ResponseBody // 비동기 응답을 위해 추가
    public ResponseEntity<String> updateProfile(@AuthenticationPrincipal CustomUser authUser, UserVO userVo, Model model) throws Exception{
        log.info(":::::::::: 회원 마이 페이지 :::::::::: + " + authUser.getUserVo().getUserId());
        log.info(":::::::::: 회원 마이 페이지 :::::::::: + " + userVo);

        boolean result = false;
        // if(authUser != null){
        //     result = userService.updateProfile(userVo);
        // }
        // log.info(":::::::::: 회원 프로필 업데이트 완료 :::::::::: + " + result);


        try {
            // 이미지 저장 로직 실행 (Service 호출)
            result = userService.updateProfile(userVo);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fail");
        }
        // Users mypageSelect = userService.mypageSelect(authUser.getUser().getId());

        // return "redirect:/mypage/calendarPage";
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

        List<PointHistoryVO> bestPayList = userService.bestPayList();
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

    @GetMapping("/findId")
    public String findId(Model model) {
        log.info(":::::::::: 아이디 찾기 화면 ::::::::::");

        model.addAttribute("userVO", new UserVO());

        return "user/user06";
    }

    @PostMapping("/findId")
    @ResponseBody // JSON 응답을 위해 추가
    public ResponseEntity<?> findIdPost(@RequestParam("userNm") String userNm, @RequestParam("phoneNum") String phoneNumber, HttpServletRequest request) throws Exception {
        log.info("아이디 찾기 요청: 이름={}, 번호={}", userNm, phoneNumber);
        // // 2. 유효성 검사 에러 처리
        
        String userId = userService.findUserId(userNm, phoneNumber);
        
        if (userId != null) {
                return ResponseEntity.ok(Map.of("success", true, "userId", userId));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("success", false, "message", "아이디 찾는 중 오류가 발생했습니다."));
    }

    @GetMapping("/findPassword")
    public String findPassword(Model model) {
        log.info(":::::::::: 비밀번호 재등록 화면 ::::::::::");

        model.addAttribute("userVO", new UserVO());

        return "user/user07";
    }

    @PostMapping("/reRegPw")
    @ResponseBody // JSON 응답을 위해 추가
    public ResponseEntity<?> reRegPw(@Valid @ModelAttribute("userVO") UserVO userVO, BindingResult bindingResult, HttpServletRequest request) throws Exception {
        log.info(":::::::::: 회원 가입 처리 (Async) :::::::::: " + userVO);

        // 1. 비밀번호 일치 확인
        if (!userVO.isPasswordConfirmed()) {
            bindingResult.rejectValue("passwordConfirm", "password.mismatch", "비밀번호가 일치하지 않습니다.");
        }
        log.info("phone Num + " + userVO.getPhoneNum());

        // 2. 유효성 검사 에러 처리
        if (bindingResult.hasErrors()) {
            // 모든 에러 메시지를 수집하여 반환
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(Map.of("success", false, "errors", errors));
        }

        // 3. 비밀번호 암호화 및 저장
        String rawPassword = userVO.getPassword();
        userVO.setPassword(passwordEncoder.encode(rawPassword));
        int result = userService.reRegPw(userVO);
        
        if (result > 0) {
                return ResponseEntity.ok(Map.of("success", true));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("success", false, "message", "패스워드 변경 처리 중 오류가 발생했습니다."));
    }

    public String inquiryPhoneNum(String inquiryPhoneNum) throws Exception{
        String phoneNUm = userService.inquiryPhoneNum(inquiryPhoneNum);

        return phoneNUm;
    }
    // public boolean isPhoneNumberDuplicate(String phoneNumber) {
    //     // DB에서 해당 번호로 가입된 유저가 있는지 확인 (count나 select)
    //     return userMapper.existsByPhoneNumber(phoneNumber); 
    // }

    @GetMapping("/check-id")
    @ResponseBody
    public ResponseEntity<Boolean> checkId(@RequestParam("userId") String userId) throws Exception {
        // DB에서 해당 ID가 존재하는지 확인 (count 조회)
        boolean checkId = userService.checkId(userId);
        return ResponseEntity.ok(checkId);
    }
}
