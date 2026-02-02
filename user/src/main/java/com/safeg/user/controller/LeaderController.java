package com.safeg.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.safeg.user.service.LeaderService;
import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.CustomUser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LeaderController {
    @Autowired
    LeaderService leaderService;

    @GetMapping("leader01")
    public String leader01(@AuthenticationPrincipal CustomUser authUser, HttpServletRequest request, Model model) throws Exception{

        AdminContentVO leaderSelect = leaderService.leaderSelect();

        model.addAttribute("leaderSelect", leaderSelect);
        model.addAttribute("currentURI", request.getRequestURI());

        return "/leader/leader01";
        
    }
}
