package com.safeg.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.safeg.user.service.FileService;
import com.safeg.user.service.LeaderService;
import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.CustomUser;
import com.safeg.user.vo.FilesVO;
import com.safeg.user.vo.UserVO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LeaderController {
    @Autowired
    LeaderService leaderService;

    @Autowired
    FileService fileService;

    @GetMapping("/leader01")
    public String leader01(@AuthenticationPrincipal CustomUser authUser, HttpServletRequest request, Model model) throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof CustomUser) {
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            Long userIdFromDb = customUser.getId(); // ⭐ users 테이블의 실제 id 값을 가져왔다! ⭐
            // FilesVO file = fileService.getMypageImage(String.valueOf(userIdFromDb), "profile");
            model.addAttribute("userId", userIdFromDb); // 뷰에서 DB ID를 사용할 수 있도록 모델에 추가
        }
        List<UserVO> leaderList = leaderService.leaderList();
        log.info("leader01" + leaderList);

        // model.addAttribute("file", file); // 뷰에서 DB ID를 사용할 수 있도록 모델에 추가
        model.addAttribute("leaderList", leaderList);
    // AdminContentVO leaderSelect = leaderService.leaderSelect();
        // model.addAttribute("leaderSelect", leaderSelect);
        // model.addAttribute("currentURI", request.getRequestURI());

        return "leader/leader01";
    }

    @GetMapping("/leader02")
    public String leader02(@AuthenticationPrincipal CustomUser authUser, HttpServletRequest request, Model model, @RequestParam("id") Long id) throws Exception{

        // AdminContentVO leaderSelect = leaderService.leaderSelect();
        // model.addAttribute("leaderSelect", leaderSelect);
        // model.addAttribute("currentURI", request.getRequestURI());

        AdminContentVO leaderSelect = leaderService.leaderSelect(id);
        log.info("leader01" + leaderSelect);

        model.addAttribute("leaderSelect", leaderSelect);
        model.addAttribute("currentURI", request.getRequestURI());

        return "leader/leader02";
    }
}
