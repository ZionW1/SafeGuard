package com.safeg.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.safeg.user.service.NoticeService;
import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.NoticeVO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class NoticeController {
    
    @Autowired
    NoticeService noticeService;

    @GetMapping("/notice01")
    public String notice01(Model model, HttpServletRequest request) throws Exception {
        log.info("NoticeController noticeList() 호출");
        List<AdminContentVO> noticeList = noticeService.noticeList();
        log.info("NoticeController noticeList() 호출" + noticeList.toString()); ;
        
        model.addAttribute("currentURI", request.getRequestURI());

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("currentURI", request.getRequestURI()); // ⭐ 모델에 currentURI 추가

        return "/notice/notice01";
    }

    @GetMapping("/notice02")
    public String notice02(Model model, HttpServletRequest request, @RequestParam("id") String id) throws Exception {
        log.info("NoticeController noticeSelect() 호출 : " + id);
        AdminContentVO noticeSelect= noticeService.noticeSelect(id);
        log.info("NoticeController noticeSelect() 호출" + noticeSelect.toString()); ;

        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("noticeSelect", noticeSelect);

        return "/notice/notice02";
    }
}
