package com.safeg.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import com.safeg.user.service.SupportService;
import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.CustomUser;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.Page;
import com.safeg.user.vo.SupportVO;

import jakarta.servlet.http.HttpServletRequest;


@Controller
@Slf4j
public class SupportController {
    
    

    // @RequestParam(value = "userNo", required = false) Long userNo
    // public String support01(@RequestParam(value = "userNo", required = false) Long userNo) {
    //     log.info("userNo : " + userNo);
    //     return "support/support01";
    // }
    // 1:1 문의 List 페이지

    @Autowired
    private SupportService supportService;

    @GetMapping("/support01")
    public String support01(Model model, HttpServletRequest request, Option option, Page page) throws Exception {
        List<AdminContentVO> supportList = supportService.supportList(option, page);

        String pageUrl = UriComponentsBuilder.fromPath("/support01")
                        //.queryParam("page", page.getPage())
                        .queryParam("keyword", option.getKeyword())
                        .queryParam("code", option.getCode())
                        // .queryParam("rows", page.getRows())
                        .queryParam("orderCode", option.getOrderCode())
                        .build()
                        .toUriString();
        log.info("pageRows : " + page.getRows());

        model.addAttribute("supportList", supportList);
        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("pageUrl", pageUrl);
        return "support/support01";
    }

    @GetMapping("/support02")
    public String support02(Model model, HttpServletRequest request, Option option, Page page) throws Exception {
        List<AdminContentVO> supportList = supportService.supportList(option, page);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUser) {
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            Long id = customUser.getId(); // users 테이블의 실제 id 값을 가져왔다!
            String userId = customUser.getUsername();
            String userName = customUser.getUserNm();

            log.info("id : " + id);
            log.info("userId : " + userId);
            log.info("userName : " + userName);

            model.addAttribute("id", id); // userId가 null이 된다!
            model.addAttribute("userId", userId);
            model.addAttribute("userName", userName);

        } else {
            log.info("인증된 사용자가 없습니다.");
        }
        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("supportList", supportList);

        return "support/support02";
    }
    
    @GetMapping("/support03")
    public String support03(Model model, HttpServletRequest request, Option option, Page page, SupportVO supportVO) throws Exception {
        // List<AdminContentVO> supportList = supportService.supportList(option, page);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUser) {
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            Long id = customUser.getId(); // users 테이블의 실제 id 값을 가져왔다!
            String userId = customUser.getUsername();
            String userName = customUser.getUserNm();

            log.info("id : " + id);
            log.info("userId : " + userId);
            log.info("userName : " + userName);

            model.addAttribute("id", id); // userId가 null이 된다!
            model.addAttribute("userId", userId);
            model.addAttribute("userName", userName);

        } else {
            log.info("인증된 사용자가 없습니다.");
        }
        model.addAttribute("currentURI", request.getRequestURI());
        // model.addAttribute("supportList", supportList);

        return "support/support03";
    }

    @PostMapping("/support04")
    @ResponseBody // 비동기 응답을 위해 추가
    public ResponseEntity<?> support04(@RequestBody SupportVO supportVO) throws Exception {
        log.info("받은 데이터: {}", supportVO);
        try {
            // DB 저장 로직 (service.insertSupport(supportDto) 등)
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal instanceof CustomUser) {
                supportVO.setUserId(((CustomUser) principal).getUserId());
                supportVO.setUserNm(((CustomUser) principal).getUserNm());
            } else {
                supportVO.setUserId("GUEST"); // 비로그인 시 식별값
                // 비로그인 시 userNm이 null이면 DB NOT NULL 제약조건에 걸릴 수 있으니 확인!
                if(supportVO.getUserNm() == null) supportVO.setUserNm("비회원");
            }

            if (supportVO.getCampaignTitle() == null || supportVO.getContent() == null) {
                log.error("필수값 누락: Title={}, Content={}", supportVO.getCampaignTitle(), supportVO.getContent());
                return ResponseEntity.badRequest().body("필수 항목이 누락되었습니다.");
            }
            int result = supportService.insertSupport(supportVO);
            return result > 0 ? ResponseEntity.ok("success") : ResponseEntity.ok("fail");
        } catch (Exception e) {
            log.error("서버 내부 에러 발생!!!");
            e.printStackTrace(); // ◀ 이걸 추가해야 터미널에 진짜 범인이 찍힙니다!
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
