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

import com.safeg.user.service.InquiryService;
import com.safeg.user.vo.AdminContentVO;
import com.safeg.user.vo.CustomUser;
import com.safeg.user.vo.InquiryVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.Page;

import jakarta.servlet.http.HttpServletRequest;


@Controller
@Slf4j
public class InquiryController {
    
    @Autowired
    private InquiryService inquiryService;
    
    @GetMapping("/inquiry01")
    public String inquiry01(Model model, HttpServletRequest request, Option option, Page page, InquiryVO inquiryVO) throws Exception {
        // List<AdminContentVO> supportList = supportService.supportList(option, page);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUser) {
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            Long id = customUser.getId(); // users 테이블의 실제 id 값을 가져왔다!
            String userId = customUser.getUsername();
            String userName = customUser.getUserNm();

            model.addAttribute("id", id); // userId가 null이 된다!
            model.addAttribute("userId", userId);
            model.addAttribute("userName", userName);

        } else {
            log.info("인증된 사용자가 없습니다.");
        }
        model.addAttribute("currentURI", request.getRequestURI());
        // model.addAttribute("supportList", supportList);

        return "inquiry/inquiry01";
    }

    @PostMapping("/inquiry02")
    @ResponseBody // 비동기 응답을 위해 추가
    public ResponseEntity<?> inquiry02(@RequestBody InquiryVO inquiryVO) throws Exception {
        log.info("받은 데이터: {}", inquiryVO);
        try {
            // DB 저장 로직 (service.insertSupport(supportDto) 등)
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal instanceof CustomUser) {
                inquiryVO.setUserId(((CustomUser) principal).getUserId());
                inquiryVO.setUserNm(((CustomUser) principal).getUserNm());
            } else {
                inquiryVO.setUserId("GUEST"); // 비로그인 시 식별값
                // 비로그인 시 userNm이 null이면 DB NOT NULL 제약조건에 걸릴 수 있으니 확인!
                if(inquiryVO.getUserNm() == null) inquiryVO.setUserNm("비회원");
            }

            if (inquiryVO.getCampaignTitle() == null || inquiryVO.getContent() == null) {
                log.error("필수값 누락: Title={}, Content={}", inquiryVO.getCampaignTitle(), inquiryVO.getContent());
                return ResponseEntity.badRequest().body("필수 항목이 누락되었습니다.");
            }
            int result = inquiryService.inquiryInsert(inquiryVO);
            return result > 0 ? ResponseEntity.ok("success") : ResponseEntity.ok("fail");
        } catch (Exception e) {
            log.error("서버 내부 에러 발생!!!");
            e.printStackTrace(); // ◀ 이걸 추가해야 터미널에 진짜 범인이 찍힙니다!
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
