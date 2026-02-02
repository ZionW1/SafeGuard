package com.safeg.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.safeg.user.service.UseGuideService;
import com.safeg.user.vo.UseGuideVO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class UseGuideController {
    
    @Autowired
    UseGuideService useGuideService;

    @GetMapping("/useGuide01")
    public String useGuide01(Model model, HttpServletRequest request) throws Exception {

        log.info("UseGuide saveContent success");
        UseGuideVO  useGuide01 = useGuideService.useGuide01();
        log.info("Saved Content: " + useGuide01.getContent());

        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("useGuide01", useGuide01);
        
        return "/useGuide/useGuide01"; // useGuide03.html
    }
}
