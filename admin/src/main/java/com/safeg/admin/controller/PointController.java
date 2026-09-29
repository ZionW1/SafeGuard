package com.safeg.admin.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;


import com.safeg.admin.service.PointService;
import com.safeg.admin.vo.CustomUser;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.PointHistoryVO;
import com.safeg.admin.vo.UserVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PointController {

    private final PointService pointService;

    @GetMapping("/point01")
    public String pointList(@RequestParam(value = "selectedMonth", required = false) String selectedMonth, @AuthenticationPrincipal CustomUser authUser, Model model, Option option, Page page) throws Exception {
        log.info("pointList called with selectedMonth: {}", selectedMonth);
        log.info("pointList called with option: {}", option);
        log.info("pointList called with page: {}", page);
        // 1. 만약 사용자가 월을 선택하지 않고 처음 들어왔다면(selectedMonth가 null이거나 비어있다면)
        if (selectedMonth == null || selectedMonth.trim().isEmpty()) {
            // 현재 날짜를 기준으로 'YYYY-MM' 형식의 문자열 생성 (예: "2026-09")
            selectedMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("YYYY-MM"));
        }
        // 1. 포인트 목록 조회
        log.info("Selected Month: {}", selectedMonth);
        List<PointHistoryVO> pointList = pointService.getPointList(option, page, selectedMonth);

        // 2. 포인트 합계 조회
        // int totalPoint = pointService.getTotalPoint(null, selectedMonth); // userNo를 null로 전달하여 전체 포인트 합계를 가져옴

        // 3. 모델에 데이터 추가
        model.addAttribute("pointList", pointList);
        model.addAttribute("selectedMonth", selectedMonth);
        // model.addAttribute("totalPoint", totalPoint);

        model.addAttribute("option", option);
        model.addAttribute("page", page);
        model.addAttribute("row", page.getRows());
        String pageUrl = UriComponentsBuilder.fromPath("/point01")
                        .queryParam("selectedMonth", selectedMonth)
                        .queryParam("keyword", option.getKeyword())
                        .queryParam("code", option.getCode())
                        // .queryParam("rows", page.getRows())
                        .queryParam("orderCode", option.getOrderCode())
                        .build()
                        .toUriString();
        model.addAttribute("pageUrl", pageUrl);

        if(authUser != null){
            UserVO user = authUser.getUserVo();
            model.addAttribute("user", user);
        }
        // 4. 뷰 이름 반환
        return "point/pointList";
    }

    @GetMapping("/point02")
    public String pointSelect(Model model, @RequestParam("userNo") String userNo, @RequestParam("selectedMonth") String selectedMonth) throws Exception {

        log.info("pointSelect called with userNo: {}, selectedMonth: {}", userNo, selectedMonth);
        // 1. 포인트 상세 조회
        List<PointHistoryVO> pointSelect = pointService.pointSelect(userNo, selectedMonth);
        int totalPoint = pointService.getTotalPoint(userNo, selectedMonth);

        // 2. 포인트 합계 조회
        List<PointHistoryVO> workList = pointService.getWorkList(userNo, selectedMonth);
        List<PointHistoryVO> leaderList = pointService.getLeaderList(userNo, selectedMonth);
        List<PointHistoryVO> referrerList = pointService.getReferrerList(userNo, selectedMonth);
        List<PointHistoryVO> attendList = pointService.getAttendList(userNo, selectedMonth);
        List<PointHistoryVO> overPayList = pointService.getOverPayList(userNo, selectedMonth);

        // 금액 문자열을 안전하게 정수로 변환 (null이면 0)
        int wAmt = pointService.workAmount(userNo, selectedMonth);
        int lAmt = pointService.leaderAmount(userNo, selectedMonth);
        int rAmt = pointService.referrerAmount(userNo, selectedMonth);
        int aAmt = pointService.attendAmount(userNo, selectedMonth);
        int opAmt = pointService.overPayAmount(userNo, selectedMonth);


        model.addAttribute("workList", workList != null ? workList : Collections.emptyList());
        model.addAttribute("leaderList", leaderList != null ? leaderList : Collections.emptyList());
        model.addAttribute("referrerList", referrerList != null ? referrerList : Collections.emptyList());
        model.addAttribute("attendList", attendList != null ? attendList : Collections.emptyList());
        model.addAttribute("overPayList", overPayList != null ? overPayList : Collections.emptyList());

        model.addAttribute("referrerAmount", rAmt);
        model.addAttribute("attendAmount", aAmt);
        model.addAttribute("workAmount", wAmt);
        model.addAttribute("leaderAmount", lAmt);
        model.addAttribute("overPayAmount", opAmt);

        // 3. 모델에 데이터 추가
        model.addAttribute("pointSelect", pointSelect);
        model.addAttribute("totalPoint", totalPoint);
        // 4. 뷰 이름 반환
        return "point/pointSelect";
    }
}
