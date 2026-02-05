package com.safeg.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import com.safeg.admin.config.CryptoUtils;
import com.safeg.admin.service.CampaignService;
import com.safeg.admin.service.UserService;
import com.safeg.admin.vo.CampaignVO;
import com.safeg.admin.vo.CommonData;
import com.safeg.admin.vo.CustomUser;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.UserVO;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/")
@Slf4j
public class HomeController {
    @Autowired
    private CampaignService campaignsService;

    @Autowired
    private UserService userService;

    // private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    private final PasswordEncoder passwordEncoder; // ⭐️ BCryptPasswordEncoder가 주입될 곳 ⭐️

    // ⭐️ 생성자 주입 ⭐️
    public HomeController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("")
    public String home(@AuthenticationPrincipal CustomUser authUser, Model model,
                Option option, 
                Page page) throws Exception{
        log.info(":::::::::: 메인 화면 :::::::::: " + authUser);
        List<CampaignVO> campaignsList = campaignsService.campaignList(option, page);

        log.info(":::::::::: list :::::::::: " + campaignsList);
        model.addAttribute("campaignsList", campaignsList);
        model.addAttribute("option", option);
        model.addAttribute("row", page.getRows());
        model.addAttribute("page", page);

        String pageUrl = UriComponentsBuilder.fromPath("/campaign/list")
                        //.queryParam("page", page.getPage())
                        .queryParam("keyword", option.getKeyword())
                        .queryParam("code", option.getCode())
                        // .queryParam("rows", page.getRows())
                        .queryParam("orderCode", option.getOrderCode())
                        .build()
                        .toUriString();
        model.addAttribute("pageUrl", pageUrl);

        if(authUser != null){
            log.info("authUser : " + authUser);
            UserVO user = authUser.getUserVo();
            log.info("user : " + user);

            model.addAttribute("user", user);
        }
        // return "/admin/list";
        return "index";
    }
    @GetMapping("/join")
    public String join01(Model model) throws Exception{
        log.info(":::::::::: 어드민 가입 화면 ::::::::::");
        // return "/admin/list";
        model.addAttribute("userVO", new UserVO());
        return "join";
    }

    @PostMapping("/join")
    public String join02(@Valid @ModelAttribute("userVO") UserVO userVO, BindingResult bindingResult, Model model) throws Exception{
        log.info(":::::::::: 어드민 가입 처리 ::::::::::" + userVO);

        // ⭐️⭐️⭐️ 가장 중요! 비밀번호 확인은 평문끼리 해야 해! ⭐️⭐️⭐️
        // UserVO에서 가져온 rawPassword와 rawPasswordConfirm은 이미 평문 상태야.
        // 여기서 이 두 평문값을 userVO의 password와 passwordConfirm 필드에 그대로 둔 상태로
        // isPasswordConfirmed()를 호출해야 해.

        // 기존 로그
        log.info(":::::::::: 어드민 가입 처리 (초기 평문) :::::::::: Password=" + userVO.getPassword() + ", PasswordConfirm=" + userVO.getPasswordConfirm());

        // ⭐️ 비밀번호 일치 여부 확인 (평문끼리 비교) ⭐️
        // userVO.isPasswordConfirmed() 호출 전에 userVO.password와 userVO.passwordConfirm에는
        // 모두 사용자가 입력한 "평문 비밀번호"가 들어있어야 해.
        // @ModelAttribute("userVO")가 이미 그렇게 바인딩해줬을 거야.
        if (!userVO.isPasswordConfirmed()) {
            log.info("비밀번호 불일치 (평문 비교) + " + userVO.isPasswordConfirmed());
            bindingResult.rejectValue("passwordConfirm", "password.mismatch", "비밀번호가 일치하지 않습니다.");
        }

        if (bindingResult.hasErrors()) {
            return "join";
        }

        // ⭐️⭐️⭐️ 이제 DB에 저장할 비밀번호만 BCrypt로 암호화! ⭐️⭐️⭐️
        String rawPasswordToEncode = userVO.getPassword(); // 평문 비밀번호 가져오기
        String encodedPassword = passwordEncoder.encode(rawPasswordToEncode); // BCrypt로 암호화

        // userVO의 password 필드에 암호화된 비밀번호를 세팅 (DB 저장을 위해)
        userVO.setPassword(encodedPassword);

        // userVO.passwordConfirm 필드는 DB에 저장되지 않으므로, 더 이상 건드릴 필요 없어.
        // 만약 서비스 계층으로 넘겨줄 때 passwordConfirm 필드가 필요없다면
        // userVO.setPasswordConfirm(null) 등으로 명확히 비워줄 수도 있어.
        // 하지만 보통은 그냥 둔 상태로 서비스 계층으로 넘겨주면 서비스는 password 필드만 사용할 거야.

        log.info(":::::::::: 어드민 가입 처리 최종 (암호화 후) :::::::::: Password=" + userVO.getPassword());
        // 이제 password 필드에 BCrypt 해시값이 들어있고, userVO.getPasswordConfirm()은 평문이거나 null일 거야.

        // 서비스 계층으로 암호화된 비밀번호가 담긴 userVO 전달
        int result = userService.userJoin(userVO);
        log.info("join02 : " + result);

        return "redirect:/admin/campaign01";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("user") UserVO user, BindingResult bindingResult, Model model) {

        if (!user.isPasswordConfirmed()) {
            bindingResult.rejectValue("passwordConfirm", "password.mismatch", "비밀번호가 일치하지 않습니다.");
        }

        if (bindingResult.hasErrors()) {
            return "signupForm"; // 회원가입 폼 다시 보여주기
        }

        // 회원가입 처리 로직
        return "redirect:/login";
    }
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // ⭐️ 1. DB에서 복사해온 BCrypt 비밀번호 해시 ⭐️
    String dbEncodedPassword = "$2a$10$WjYSTDOL90Bn3IQuzmxH/ONmAMj9BYGjIprR.pxUDo4cE59.oxX.e"; // ⭐️여기에 DB에서 복사한 값을 넣어줘!⭐️

    // ⭐️ 2. 네가 로그인 시도할 때 입력하는 평문 비밀번호 ⭐️
    String enteredPlainPassword = "MJordan23!"; // ⭐️여기에 네가 웹 폼에 입력하는 비밀번호를 넣어줘!⭐️
    //1234qwer!!       zxcvasdf1           1234qwer!!
    // ⭐️ 비교 수행 ⭐️
    boolean isMatch = encoder.matches(enteredPlainPassword, dbEncodedPassword);

    System.out.println("입력된 평문 비밀번호: " + enteredPlainPassword);
    System.out.println("DB에 저장된 해시: " + dbEncodedPassword);
    System.out.println("비밀번호 일치 여부: " + isMatch);

    // 만약 회원가입 시 평문 'MJordan23!'을 암호화한 해시와 로그인 시 'MJordan23!'을 비교해서 일치하는지 확인
    // String newHash = encoder.encode(enteredPlainPassword);
    // System.out.println("새로 생성된 해시: " + newHash);
    // boolean testMatch = encoder.matches(enteredPlainPassword, newHash);
    // System.out.println("새로 생성된 해시와 일치 여부: " + testMatch); // 이건 항상 true여야 해.

    }
    

//     BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
// DB에 저장된 jjj의 BCrypt 비밀번호 해시값을 가져와. ($2a$10$Au6xQ0N/mzggLNcN/E/eve5vamCc/v0jdYebiOaSIuhcyyNSoRW5m)
// 로그인 폼에 입력한 평문 비밀번호 (예: 1234)
// boolean isMatch = encoder.matches("입력한평문비밀번호", "DB의BCrypt해시");
// 이 코드를 실행해서 isMatch가 true가 나오는지 확인해 봐. false가 나온다면 평문 비밀번호가 틀린 거야.

}
