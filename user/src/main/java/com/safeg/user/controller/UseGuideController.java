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
        UseGuideVO useGuide01 = useGuideService.useGuide01();

        if (useGuide01 == null) {
            log.info("조회된 이용가이드 데이터가 없습니다. 빈 객체를 생성합니다.");
            useGuide01 = new UseGuideVO();
            useGuide01.setTitle("등록된 이용가이드가 없습니다.");
            useGuide01.setContent("내용을 등록해 주세요.");
            useGuide01.setAuthor("관리자");
        } else {
            // 2. 데이터가 있을 때만 로그를 찍음 (안 그러면 여기서 에러!)
            log.info("Saved Content: " + useGuide01.getContent());
        }
        return "useGuide/useGuide01"; // useGuide03.html
    }
}
