package com.safeg.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@Controller
public class ErrorController {
    
    @GetMapping("/error01")
    public String error(Model model) throws Exception {
        log.info("Admin HomeController error() 호출");
        
        return "error/error01";
    }
}
