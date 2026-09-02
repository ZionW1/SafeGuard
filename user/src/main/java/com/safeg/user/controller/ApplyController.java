package com.safeg.user.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.safeg.user.service.ApplyService;
import com.safeg.user.service.ReviewService;
import com.safeg.user.util.EncryptionUtil;
import com.safeg.user.vo.CampaignVO;
import com.safeg.user.vo.CustomUser;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.PointHistoryVO;
import com.safeg.user.vo.ReviewVO;
import com.safeg.user.vo.UserCampaignVO;
import com.safeg.user.vo.UserVO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
@RequestMapping("/apply")
public class ApplyController {

    @Autowired
    ApplyService applyService;

    @Autowired
    ReviewService reviewService;

    @GetMapping("/userCampaignApply/{campaignId}")
    public String userCampaignApply(@AuthenticationPrincipal CustomUser authUser, Model model,
            @PathVariable("campaignId") String campaignId,
            @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date // 날짜가 없을 수도 있음
    // @PathVariable("date") @DateTimeFormat(pattern = "yy-MM-dd") LocalDate
    // applyDate // String 대신 LocalDate로 받고 패턴 지정
    ) throws Exception {

        log.info("userCampaignApply date : " + date);

        List<UserCampaignVO> dateList = applyService.getApplyDate(campaignId);
        model.addAttribute("applyDate", dateList); // 현재 보고 있는 날짜를 다시 전달

        // 2. 현재 조회할 '기준 날짜'를 결정합니다.
        LocalDate finalDate;
        String timeSegment = null;
        int workHour = 0;
        String wageChk = null;

        if (date != null) {
            finalDate = date;
        } else if (dateList != null && !dateList.isEmpty()) {
            finalDate = dateList.get(0).getApplyDate();
        } else {
            finalDate = LocalDate.now();
        }

        UserCampaignVO workInfo = applyService.getWorkInfo(campaignId, finalDate);
        log.info("getWorkInfo : " + workInfo);
        timeSegment = workInfo.getTimeSegment();
        workHour = workInfo.getWorkHour();
        wageChk = workInfo.getWageChk();

        model.addAttribute("workHour", workHour);
        model.addAttribute("wageChk", wageChk);

        model.addAttribute("currentDate", finalDate); // 현재 선택된 날짜 강조용
        model.addAttribute("campaignId", campaignId); // JS용
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof CustomUser) {
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            Long userIdFromDb = customUser.getId();
            model.addAttribute("userId", userIdFromDb);
        } else {
            model.addAttribute("userId", null);
            model.addAttribute("username", authentication.getName());
        }

        if (authUser != null) {
            UserVO user = authUser.getUserVo();
            model.addAttribute("user", user);
        }
        // ※ 주의: 두 번째 인자로 리스트가 아닌 '문자열 날짜'를 넘겨야 함
        List<UserCampaignVO> userCampaignApply = applyService.userCampaignApply(campaignId, finalDate, timeSegment);
        log.info("userCampaignApply.toString u : " + userCampaignApply.toString());
        // for(int i = 0; i < userCampaignApply.size(); i++){
        // // String originalPhone = user.getPhoneNum();
        // userCampaignApply.get(i).setPhoneNum(userCampaignApply.get(i).getPhoneNum().replaceAll("(\\d{3})(\\d{4})(\\d{4})",
        // "$1-$2-$3"));
        // }
        String reviewId = reviewService.reviewSelectConfirm(campaignId);
        model.addAttribute("userCampaignApply", userCampaignApply);
        model.addAttribute("applicantsNum", userCampaignApply.size() - 1); // 신청 수 추가
        model.addAttribute("reviewId", reviewId); // 신청 수 추가

        UserCampaignVO leaderCampaign = null; // 인솔자를 찾아서 저장할 변수

        // 1. 인솔자들을 모아둘 리스트를 생성합니다.
        List<UserCampaignVO> leaderList = new ArrayList<>();

        for (int i = 0; i < userCampaignApply.size(); i++) {
            UserCampaignVO currentCampaign = userCampaignApply.get(i);

            // 인솔자 조건 체크 ("8" 또는 "9")
            if ("8".equals(currentCampaign.getStatus()) || "9".equals(currentCampaign.getStatus())) {
                // 2. 조건에 맞는 인솔자를 리스트에 계속 담습니다. (덮어쓰지 않음)
                leaderList.add(currentCampaign);
            }
        }

        log.info("leaderList: " + leaderList.toString());
        int getPointInfo = applyService.getPointInfo(campaignId, finalDate);
        model.addAttribute("getPointInfo", getPointInfo);
        // 3. 반복문이 완전히 끝난 후, 인솔자 리스트를 통째로 모델에 담아 화면(HTML)으로 보냅니다.
        model.addAttribute("leaderList", leaderList);

        return "apply/userCampaignApply";
    }

    @GetMapping("/applyConfirm/{campaignId}")
    public String applyConfirm(@AuthenticationPrincipal CustomUser authUser, Model model,
            @PathVariable("campaignId") String campaignId,
            @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date // 날짜가 없을 수도 있음
    // @PathVariable("date") @DateTimeFormat(pattern = "yy-MM-dd") LocalDate
    // applyDate // String 대신 LocalDate로 받고 패턴 지정
    ) throws Exception {
        log.info("applyConfirm " + date);
        List<UserCampaignVO> dateList = applyService.getApplyDate(campaignId);
        model.addAttribute("applyDate", dateList); // 현재 보고 있는 날짜를 다시 전달
        log.info("applyConfirm A " + dateList);

        // 2. 현재 조회할 '기준 날짜'를 결정합니다.
        LocalDate finalDate;
        String timeSegment = null;
        int workHour = 0;
        String wageChk = null;
        // String timeSegment = null;

        if (date != null) {
            finalDate = date;
        } else if (dateList != null && !dateList.isEmpty()) {
            finalDate = dateList.get(0).getApplyDate();
        } else {
            finalDate = LocalDate.now();
        }
        log.info("applyConfirm B : " + finalDate);

        UserCampaignVO workInfo = applyService.getWorkInfo(campaignId, finalDate);
        log.info("getWorkInfo : " + workInfo);

        timeSegment = workInfo.getTimeSegment();
        workHour = workInfo.getWorkHour();
        wageChk = workInfo.getWageChk();

        model.addAttribute("workHour", workHour);
        model.addAttribute("wageChk", wageChk);

        model.addAttribute("currentDate", finalDate); // 현재 선택된 날짜 강조용
        model.addAttribute("campaignId", campaignId); // JS용
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof CustomUser) {
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            Long userIdFromDb = customUser.getId();
            model.addAttribute("userId", userIdFromDb);
        } else {
            model.addAttribute("userId", null);
            model.addAttribute("username", authentication.getName());
        }

        if (authUser != null) {
            UserVO user = authUser.getUserVo();
            model.addAttribute("user", user);
        }
        // ※ 주의: 두 번째 인자로 리스트가 아닌 '문자열 날짜'를 넘겨야 함
        log.info("campaign_id : " + campaignId);
        log.info("finalDate : " + finalDate);
        log.info("timeSegment : " + timeSegment);
        List<UserCampaignVO> userCampaignApply = applyService.userCampaignApply(campaignId, finalDate, timeSegment);

        String reviewId = reviewService.reviewSelectConfirm(campaignId);
        model.addAttribute("userCampaignApply", userCampaignApply);
        // model.addAttribute("applicantsNum",
        // userCampaignApply.get(0).getApplicantsNum()); // 신청 수 추가
        model.addAttribute("reviewId", reviewId); // 신청 수 추가

        UserCampaignVO leaderCampaign = null; // 인솔자를 찾아서 저장할 변수

        // for (int i = 0; i < userCampaignApply.size(); i++) {
        // UserCampaignVO currentCampaign = userCampaignApply.get(i);
        // if ("8".equals(currentCampaign.getStatus()) ||
        // "9".equals(currentCampaign.getStatus())) {
        // leaderCampaign = currentCampaign; // 첫 번째 인솔자 또는 유일한 인솔자를 여기에 저장 (논리에 따라)
        // break; // 만약 첫 인솔자만 중요하면 여기서 루프를 멈춤
        // }
        // }

        // if (leaderCampaign != null) {
        // model.addAttribute("leaderUserNo", leaderCampaign.getUserNo()); // 인솔자의 고유 번호
        // model.addAttribute("leaderStatus", leaderCampaign.getStatus()); // 인솔자의 상태 (이
        // 경우는 "9"일 것임)
        // model.addAttribute("leaderApplyDate", leaderCampaign.getApplyDate()); // 인솔자
        // 날짜
        // model.addAttribute("leaderPay", leaderCampaign.getLeaderPay()); // 인솔자 날짜
        // model.addAttribute("leaderCampaignId", leaderCampaign.getCampaignId()); //
        // 인솔자의 캠페인 번호
        // }
        List<UserCampaignVO> leaderList = new ArrayList<>();
        log.info("userCampaignApply : " + userCampaignApply);

        for (int i = 0; i < userCampaignApply.size(); i++) {
            UserCampaignVO currentCampaign = userCampaignApply.get(i);

            // 인솔자 조건 체크 ("8" 또는 "9")
            if ("8".equals(currentCampaign.getStatus()) || "9".equals(currentCampaign.getStatus())) {
                // 2. 조건에 맞는 인솔자를 리스트에 계속 담습니다. (덮어쓰지 않음)
                log.info("currentCampaign : " + currentCampaign);
                leaderList.add(currentCampaign);
            }
        }

        log.info("leaderList: " + leaderList.toString());

        // 3. 반복문이 완전히 끝난 후, 인솔자 리스트를 통째로 모델에 담아 화면(HTML)으로 보냅니다.
        model.addAttribute("leaderList", leaderList);
        int getPointInfo = applyService.getPointInfo(campaignId, finalDate);
        model.addAttribute("getPointInfo", getPointInfo);

        return "apply/applyConfirm";
    }

    @PatchMapping("/updateStatus")
    // public ResponseEntity<?> updateAttendanceStatus(
    // @PathVariable("userNo") Long userNo, @RequestBody UserCampaignVO request)
    // throws Exception {
    // 상태 업데이트
    public ResponseEntity<?> updateStatus(@RequestBody UserCampaignVO request) throws Exception {
        log.info("updateAttendanceStatus 호출됨: " + request.toString());
        List<Long> userNos = request.getUserNos();
        log.info("updateStatus userNos : " + userNos);
        if (userNos == null || userNos.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"message\": \"신청 인원이 없습니다.\"}");
        }

        // 여러 명의 처리 결과를 담아둘 리스트 (단일 메시지로 통일하고 싶다면 String 변수 하나만 써도 됩니다)
        List<String> resultMessages = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        String msg = "";

        for (int i = 0; i < userNos.size(); i++) {
            Long userNo = userNos.get(i);
            Long campaignId = request.getCampaignId();
            LocalDate applyDate = request.getApplyDate();
            String status = request.getStatus();
            int workHour = request.getWorkHour();

            String statusInfo = applyService.statusInfo(userNo, campaignId, applyDate);
            try {
                String updateStatus = applyService.updateStatus(userNo, campaignId, applyDate, status, workHour);
                successCount++; // 성공 카운트 증가

                // [수정 핵심] 기존의 'return ResponseEntity...' 구문들을 'msg = ...' 형태로 변수 저장 방식으로 변경합니다.

                if (statusInfo.equals("0")) {
                    if (updateStatus.equals("2")) {
                        applyService.updateStatus(userNo, campaignId, applyDate, statusInfo, workHour);
                        msg = "출근 or 지각 터치 후 퇴근 버튼 터치 하세요.";
                    } else if (updateStatus.equals("3")) {
                        applyService.lateYn(userNo, campaignId, applyDate);
                    }
                } else if (statusInfo.equals("1")) {
                    if (updateStatus.equals("2")) {
                        log.info("퇴근 터치 하셨으므로, 변경 불가 합니다.");
                        // applyService.pointFull(userNo, campaignId, applyDate);
                    } else if (updateStatus.equals("4") || updateStatus.equals("5")) {
                        applyService.updateStatus(userNo, campaignId, applyDate, statusInfo, workHour);
                        msg = "출근 or 지각 상태에서는 결근, 무단결근은 터치가 안됩니다.";
                    } else {
                        if (updateStatus.equals("3")) {
                            applyService.updateStatus(userNo, campaignId, applyDate, statusInfo, workHour);
                            msg = "출근 상태 입니다.";
                        } else {
                            msg = "출근 상태 입니다.";
                        }
                    }
                } else if (statusInfo.equals("2")) {
                    if (!updateStatus.equals("2")) {
                        applyService.updateStatus(userNo, campaignId, applyDate, statusInfo, workHour);
                        msg = "퇴근 터치 하셨으므로, 변경 불가 합니다.";
                    } else {
                        msg = "퇴근 상태 입니다.";
                    }
                } else if (statusInfo.equals("3")) {
                    if (updateStatus.equals("2")) {
                        // applyService.pointFull(userNo, campaignId, applyDate);
                    } else if (updateStatus.equals("4") || updateStatus.equals("5")) {
                        applyService.updateStatus(userNo, campaignId, applyDate, statusInfo, workHour);
                        msg = "출근 or 지각 상태에서는 결근, 무단결근은 터치가 안됩니다.";
                    } else {
                        if (updateStatus.equals("1")) {
                            applyService.updateStatus(userNo, campaignId, applyDate, statusInfo, workHour);
                            msg = "지각 상태 입니다.";
                        }
                    }
                } else if (statusInfo.equals("4")) {
                    if (updateStatus.equals("1") || updateStatus.equals("2") || updateStatus.equals("3")
                            || updateStatus.equals("5")) {
                        applyService.updateStatus(userNo, campaignId, applyDate, statusInfo, workHour);
                        msg = "결근 상태 입니다.";
                    }
                } else if (statusInfo.equals("5")) {
                    if (updateStatus.equals("1") || updateStatus.equals("2") || updateStatus.equals("3")
                            || updateStatus.equals("4")) {
                        applyService.updateStatus(userNo, campaignId, applyDate, statusInfo, workHour);
                        msg = "무단 결근 상태 입니다.";
                    }
                }

                // 이 유저의 최종 처리 메시지를 기록 (필요 시 활용)
                resultMessages.add("유저(" + userNo + "): " + msg);

            } catch (Exception e) {
                failCount++;
                log.error("출결 상태 업데이트 중 오류 발생: userNo={}, campaignId={}, error={}", userNo, campaignId, e.getMessage());
                resultMessages.add("유저(" + userNo + ") 실패: " + e.getMessage());
            }
        } // [for문 종료] 이제 모든 유저를 중간에 안 튕기고 다 돌았습니다!

        // ------------------------------------------------------------
        // 최종 응답 처리 (모든 루프가 끝난 "여기서" 딱 한 번 리턴합니다)
        // ------------------------------------------------------------
        Map<String, Object> finalResponse = new HashMap<>();
        if (msg == "") {
            finalResponse.put("success", true);
            finalResponse.put("message", successCount + "명의 출결 상태 처리가 완료되었습니다.");
            if (failCount > 0) {
                finalResponse.put("message", successCount + "명 성공, " + failCount + "명 실패했습니다.");
            }
        } else {
            finalResponse.put("success", true);
            finalResponse.put("message", msg);
            if (failCount > 0) {
                finalResponse.put("message", successCount + "명 성공, " + failCount + "명 실패했습니다.");
            }
        }

        return ResponseEntity.ok().body(finalResponse);
    }

    @PatchMapping("/updateStatusLeader")
    public ResponseEntity<?> updateStatusLeader(@RequestBody UserCampaignVO request) throws Exception {
        log.info("updateStatusLeader 호출됨: " + request.toString());
        Long userNo = request.getUserNo();
        Long campaignId = request.getCampaignId();
        LocalDate applyDate = request.getApplyDate();
        String status = request.getStatus(); // JavaScript에서 newStatus 필드로 보냈다면
        int workHour = request.getWorkHour();
        List<String> resultMessages = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        String msg = "";

        try {
            // 서비스 계층 호출: DB에 해당 userNo의 출결 상태를 request.getNewStatus()로 업데이트
            String statusInfo = applyService.getLeaderStatus(userNo, campaignId, applyDate);
            String updateStatus = applyService.updateLeader(userNo, campaignId, applyDate, status, workHour);

            successCount++; // 성공 카운트 증가

            if (statusInfo.equals("8")) {
                if (updateStatus.equals("3")) {
                    msg = "인솔자가 지각 상태로 업데이트 되었습니다.";
                } else if (updateStatus.equals("4")) {
                    msg = "인솔자가 결근 상태로 업데이트 되었습니다.";
                } else if (updateStatus.equals("5")) {
                    msg = "인솔자가 무단 결근 상태로 업데이트 되었습니다.";
                }
                if (updateStatus.equals("9")) {
                    msg = "인솔자가 퇴근 상태로 업데이트 되었습니다.";
                }
            } else {
                log.info("status _ 9");
                if (updateStatus.equals("3") || updateStatus.equals("4") || updateStatus.equals("5")) {
                    applyService.updateStatus(userNo, campaignId, applyDate, statusInfo, workHour);
                    msg = "퇴근 터치 하셨으므로, 변경 불가 합니다.";
                } else {
                    msg = "인솔자 퇴근 상태 입니다.";
                }
            }
            resultMessages.add("유저(" + userNo + "): " + msg);
        } catch (Exception e) {
            failCount++;
            log.error("출결 상태 업데이트 중 오류 발생: userNo={}, campaignId={}, error={}", userNo, campaignId, e.getMessage());
            resultMessages.add("유저(" + userNo + ") 실패: " + e.getMessage());
        }

        Map<String, Object> finalResponse = new HashMap<>();
        if (msg == "") {
            finalResponse.put("success", true);
            finalResponse.put("message", successCount + "명의 출결 상태 처리가 완료되었습니다.");
            if (failCount > 0) {
                finalResponse.put("message", successCount + "명 성공, " + failCount + "명 실패했습니다.");
            }
        } else {
            finalResponse.put("success", true);
            finalResponse.put("message", msg);
            if (failCount > 0) {
                finalResponse.put("message", successCount + "명 성공, " + failCount + "명 실패했습니다.");
            }
        }

        return ResponseEntity.ok().body(finalResponse);
    }

    @PatchMapping("/initStatus/{userNo}/{campaignId}/{applyDate}")
    // 상태 초기화
    public ResponseEntity<?> initStatus(@PathVariable("userNo") Long userNo,
            @PathVariable("campaignId") Long campaignId, @PathVariable("applyDate") LocalDate applyDate,
            @RequestBody Map<String, String> requestBody) throws Exception {
        String newStatus = requestBody.get("newStatus");
        String division = requestBody.get("division");

        log.info("initStatus 호출됨: userNo={}, campaignId={}, applyDate={}, newStatus={}, division={}", userNo,
                campaignId, applyDate, newStatus, division);
        int initStatus = 0;

        initStatus = applyService.initStatus(userNo, campaignId, applyDate, division);
        if (initStatus > 0) {
            return ResponseEntity.ok().body("{\"message\": \"초기화 성공.\"}");
        } else {
            return ResponseEntity.ok().body("{\"message\": \"초기화 실패.\"}");
        }
    }

    @PatchMapping("/rosterRemove/{userNo}/{campaignId}/{applyDate}")
    // 명단 삭제
    public ResponseEntity<?> rosterRemove(@PathVariable("userNo") Long userNo,
            @PathVariable("campaignId") Long campaignId, @PathVariable("applyDate") LocalDate applyDate,
            @RequestBody String newStatus) throws Exception {
        int initStatus = applyService.rosterRemove(userNo, campaignId, applyDate);
        if (initStatus > 0) {
            return ResponseEntity.ok().body("{\"message\": \"명단 삭제 성공.\"}");
        } else {
            return ResponseEntity.ok().body("{\"message\": \"명단 삭제 실패.\"}");
        }
    }

    // @GetMapping("/significant/{userNo}/{campaignId}/{applyDate}")
    @GetMapping("/significant/{userNo}/{userId}/{campaignId}/{applyDate}")
    public String showSignificantPage(
            @PathVariable("userNo") List<Long> userNo, @PathVariable("userId") List<String> userId,
            @PathVariable("campaignId") int campaignId, @PathVariable("applyDate") String applyDate, Model model)
            throws Exception {
        log.info(";;;;;;;;;;;III : " + userNo);
        log.info(";;;;;;;;;;;III userNo.size : " + userNo.size());
        log.info(";;;;;;;;;;;III : " + userId);
        log.info(";;;;;;;;;;;III userId.size : " + userId.size());

        if (userNo.size() <= 1) {
            PointHistoryVO getUserSgnf = applyService.getUserSgnf(campaignId, userNo, applyDate);

            if (getUserSgnf != null) {
                model.addAttribute("getUserSgnf", getUserSgnf);
            }
        }

        model.addAttribute("userNo", userNo);
        model.addAttribute("userId", userId);
        model.addAttribute("campaignId", campaignId);
        model.addAttribute("applyDate", applyDate);

        return "apply/significant";
    }

    @PostMapping("/significantInsert")
    public ResponseEntity<?> significantInsert(@RequestBody PointHistoryVO pointHistoryVO) throws Exception {
        // TODO: process POST request
        log.info("significantInsert + " + pointHistoryVO.getUserNos().size());
        log.info("pointHistoryVO: {}", pointHistoryVO);
        int result = 0;
        List<Long> userNos = pointHistoryVO.getUserNos();
        List<String> userIds = pointHistoryVO.getUserIds(); // 넘어온 아이디 목록

        // 두 리스트의 크기가 같아서 1:1 매칭이 되는지 검증합니다.
        if (userNos != null && userIds != null && userNos.size() == userIds.size()) {

            for (int i = 0; i < userNos.size(); i++) {
                Long userNo = userNos.get(i);
                String userId = userIds.get(i); // 같은 인덱스의 아이디를 가져옴

                log.info("처리 중 - userNo: {}, userId: {}", userNo, userId);

                // VO에 각각 매핑해서 넣어줍니다.
                pointHistoryVO.setUserNo(userNo);
                pointHistoryVO.setUserId(userId); // <- 외부에서 넘어온 아이디 직접 대입!

                // 공통 정보 세팅
                pointHistoryVO.setCategory("OVERPAY");
                pointHistoryVO.setSourceNo(userNo);
                Long pointId = applyService.pointSelect(pointHistoryVO);

                // 등록 혹은 수정 처리
                if (pointId != null && !pointId.equals(0L)) {
                    result = applyService.pointUpdate(pointHistoryVO);
                } else {
                    result = applyService.pointInsert(pointHistoryVO);
                }
            }
        }

        return ResponseEntity.ok().body("{\"message\": \"특이사항 저장 되었습니다.\"}");
    }

    @PostMapping("/pointEarn")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> pointEarn(@RequestBody List<UserCampaignVO> dto) {
        log.info("pointEarn");
        UserCampaignVO vo = dto.get(0);
        Map<String, Object> response = new HashMap<>();
        if (dto.isEmpty()) {
            response.put("success", false);
            response.put("message", "포인트에 입력할 정보가 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }
        log.info("vo + " + vo);

        try {
            List<UserCampaignVO> getUserInfo = applyService.getUserInfo(vo);
            int pointEarn = applyService.pointInsert(getUserInfo);

            if (pointEarn == 0) {
                response.put("success", false);
                response.put("message", "퇴근 상태가 아닙니다.");
                return ResponseEntity.badRequest().body(response);
            } else {
                response.put("success", true);
                response.put("message", pointEarn);
                return ResponseEntity.ok(response);
            }
            // response.put("success", true);
            // return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/popup01/{campaignId}")
    public String popup01(@PathVariable("campaignId") Long campaignId, @RequestBody CampaignVO dto, Option option,
            Model model) throws Exception {
        log.info("dto : " + dto);
        log.info("option : " + option);

        List<UserVO> userInfoList = applyService.userInfoList(campaignId, option);
        List<LocalDate> dates = dto.getEventPeriodStr().datesUntil(dto.getEventPeriodEnd().plusDays(1))
                .collect(Collectors.toList()); // 입력 날짜 List

        log.info("Dates" + dates);
        model.addAttribute("campaignTitle", dto.getCampaignTitle());
        model.addAttribute("campaignId", campaignId);
        model.addAttribute("userInfoList", userInfoList);
        model.addAttribute("dates", dates);
        model.addAttribute("option", option);

        return "apply/popup01";
    }

    @GetMapping("/popup01/search")
    @ResponseBody // 🔥 HTML이 아닌 JSON 데이터를 리턴하겠다는 선언!
    public List<UserVO> searchUsers(Model model, Option option, @RequestParam("campaignId") Long campaignId)
            throws Exception { // code, keyword 등이 담긴 객체
        log.info("searchUsers");
        // MyBatis Mapper를 호출해서 조건에 맞는 유저 리스트를 가져옵니다.
        List<UserVO> userInfoList = applyService.userInfoList(campaignId, option);
        model.addAttribute("option", option);

        return userInfoList;
        // return null;
    }

    @PostMapping("/userApply")
    @ResponseBody
    public ResponseEntity<?> userApply(@RequestBody CampaignVO dto) throws Exception {
        log.info("userApply : " + dto);
        Map<String, String> response = new HashMap<>();
        int result = 0;
        try {
            UserCampaignVO overlapTitle = applyService.overlapTitle(dto);
            log.info("overlapTitle : " + overlapTitle);

            // 중복된 타이틀이 존재한다면 안내 메시지와 함께 400 Bad Request 리턴
            if (overlapTitle != null) {
                // 중복이 발견되었더라도 사용자가 선택한 날짜가 'ALL'이 아니고,
                // 실제 중복된 날짜(applyDate)와 사용자가 선택한 날짜(applyDateS)가 다르다면 패스해야 함
                if (!"ALL".equals(dto.getApplyDateS()) && !dto.getApplyDateS().equals("")) {
                    log.info("dto.getApplyDateS(). " + dto.getApplyDateS());
                    // 겹치는 날짜는 6,7일인데 사용자는 8일을 골랐으므로 신청 진행!
                    result = applyService.userApply(dto);
                    response.put("message", "캠페인 신청이 완료되었습니다.");
                    return ResponseEntity.ok().body(response);
                }

                // 진짜로 날짜가 겹치거나 ALL인 경우엔 가차없이 튕기기
                response.put("message", "이미 [" + overlapTitle.getCampaignTitle() + "] 캠페인 일정이 있는 '"
                        + overlapTitle.getUserNm() + "' 유저가 포함되어 있습니다.");
                return ResponseEntity.badRequest().body(response);
            } else {
                if (!"ALL".equals(dto.getApplyDateS()) && !dto.getApplyDateS().equals("")) {
                    // 겹치는 날짜는 6,7일인데 사용자는 8일을 골랐으므로 신청 진행!
                    result = applyService.userApply(dto);
                    response.put("message", "캠페인 신청이 완료되었습니다.");
                    return ResponseEntity.ok().body(response);
                }
                result = applyService.userApply(dto);
                // response.put("message", "캠페인 신청이 완료되었습니다.");
                // return ResponseEntity.ok().body(response);
                response.put("message", String.valueOf(result));
                return ResponseEntity.ok().body(response);
            }
            // return ResponseEntity.ok().body("{\"message\": \"성공\"}");
        } catch (IllegalArgumentException e) {
            // 서비스에서 throw한 에러 메시지를 그대로 프론트로 전달
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/userCancel")
    @ResponseBody
    public ResponseEntity<?> userCancel(@RequestBody CampaignVO dto) throws Exception {
        log.info("userCancel " + dto);
        Map<String, String> response = new HashMap<>();

        try {
            // 앞서 질문하셨던 Map 리턴 방식을 활용하여 화면에 결과 건수를 줍니다.
            int result = applyService.userCancel(dto);
            response.put("message", String.valueOf(result));

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/chgDate/{campaignId}")
    @ResponseBody // 👈 HTML이 아니라 데이터(JSON)만 리턴하겠다는 선언!
    public List<UserVO> chgDate(@PathVariable("campaignId") Long campaignId, Option option, @RequestBody Map<String, String> paramMap) throws Exception {

        String applyDate = paramMap.get("applyDateS"); // 프론트에서 보낸 날짜값 ('ALL' 또는 '2026-07-06')
        log.info("aaa : " + applyDate);
        // 만약 'ALL' 이면 전체 조회, 특정 날짜면 해당 날짜만 조회하는 로직 필요
        List<UserVO> updatedUserList = null;
        if ("ALL".equals(applyDate)) {
            updatedUserList = applyService.userDateInfo(campaignId, option); // 전체 조회
        } else {
            updatedUserList = applyService.dateSelect(campaignId, option, applyDate); // 👈 날짜별 조회 (서비스에 메서드 구현 필요)
        }

        return updatedUserList; // 자바스크립트로 유저 리스트 배열이 JSON 형태로 바로 넘어감
    }

    @GetMapping("/filterUser/{campaignId}")
    @ResponseBody
    public List<UserVO> searchPopupUsers(@PathVariable("campaignId") Long campaignId, Option option) throws Exception {

        log.info("");
        log.info("option : " + option);
        log.info("option : " + option);

        String keyword = option.getKeyword();

        if (keyword != null && keyword.startsWith("010")) {
            option.setKeyword(EncryptionUtil.hash(option.getKeyword()));
        }
        // SQL에서 option(role, keyword 등)을 조건으로 조회한 리스트 반환
        return applyService.userInfoList(campaignId, option);
    }
}
