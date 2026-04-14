package com.safeg.user.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.safeg.user.service.ApplyService;
import com.safeg.user.vo.CustomUser;
import com.safeg.user.vo.UserCampaignVO;
import com.safeg.user.vo.UserVO;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/apply")
public class ApplyController {
    
    @Autowired
    ApplyService applyService;

    @GetMapping("/userCampaignApply/{campaignId}")
    public String userCampaignApply(@AuthenticationPrincipal CustomUser authUser, Model model, @PathVariable("campaignId") String campaignId, 
        @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date // 날짜가 없을 수도 있음
        // @PathVariable("date") @DateTimeFormat(pattern = "yy-MM-dd") LocalDate applyDate // String 대신 LocalDate로 받고 패턴 지정
        ) throws Exception{
        
        List<UserCampaignVO> dateList = applyService.getApplyDate(campaignId);
        model.addAttribute("applyDate", dateList); // 현재 보고 있는 날짜를 다시 전달

        // 2. 현재 조회할 '기준 날짜'를 결정합니다.
        LocalDate finalDate;
        if (date != null) { 
            finalDate = date; 
        } else if (dateList != null && !dateList.isEmpty()) {
            finalDate = dateList.get(0).getApplyDate(); 
        } else {
            finalDate = LocalDate.now();
        }

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

        if(authUser != null){
            UserVO user = authUser.getUserVo();
            model.addAttribute("user", user);
        }
        // ※ 주의: 두 번째 인자로 리스트가 아닌 '문자열 날짜'를 넘겨야 함
        List<UserCampaignVO> userCampaignApply = applyService.userCampaignApply(campaignId , finalDate);

        // for(int i = 0; i < userCampaignApply.size(); i++){
        //     // String originalPhone = user.getPhoneNum();
        //     userCampaignApply.get(i).setPhoneNum(userCampaignApply.get(i).getPhoneNum().replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3"));
        // }
        
        model.addAttribute("userCampaignApply", userCampaignApply);
        model.addAttribute("applicantsNum", userCampaignApply.size()-1); // 신청 수 추가
        UserCampaignVO leaderCampaign = null; // 인솔자를 찾아서 저장할 변수

        for (int i = 0; i < userCampaignApply.size(); i++) {
            UserCampaignVO currentCampaign = userCampaignApply.get(i);
            if ("8".equals(currentCampaign.getStatus())) {
                leaderCampaign = currentCampaign; // 첫 번째 인솔자 또는 유일한 인솔자를 여기에 저장 (논리에 따라)
                break; // 만약 첫 인솔자만 중요하면 여기서 루프를 멈춤
            }
        }

        if (leaderCampaign != null) {
            model.addAttribute("leaderUserNo", leaderCampaign.getUserNo()); // 인솔자의 고유 번호
            model.addAttribute("leaderStatus", leaderCampaign.getStatus()); // 인솔자의 상태 (이 경우는 "9"일 것임)
            model.addAttribute("leaderApplyDate", leaderCampaign.getApplyDate()); // 인솔자 날짜
            model.addAttribute("leaderCampaignId", leaderCampaign.getCampaignId()); // 인솔자의 캠페인 번호
        }
        return "apply/userCampaignApply";
    }

    @GetMapping("/applyConfirm/{campaignId}")
    public String applyConfirm(@AuthenticationPrincipal CustomUser authUser, Model model, @PathVariable("campaignId") String campaignId, 
        @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date // 날짜가 없을 수도 있음
        // @PathVariable("date") @DateTimeFormat(pattern = "yy-MM-dd") LocalDate applyDate // String 대신 LocalDate로 받고 패턴 지정
        ) throws Exception{
        
        List<UserCampaignVO> dateList = applyService.getApplyDate(campaignId);
        model.addAttribute("applyDate", dateList); // 현재 보고 있는 날짜를 다시 전달

        // 2. 현재 조회할 '기준 날짜'를 결정합니다.
        LocalDate finalDate;
        if (date != null) { 
            finalDate = date; 
        } else if (dateList != null && !dateList.isEmpty()) {
            finalDate = dateList.get(0).getApplyDate(); 
        } else {
            finalDate = LocalDate.now();
        }

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

        if(authUser != null){
            UserVO user = authUser.getUserVo();
            model.addAttribute("user", user);
        }
        // ※ 주의: 두 번째 인자로 리스트가 아닌 '문자열 날짜'를 넘겨야 함
        List<UserCampaignVO> userCampaignApply = applyService.userCampaignApply(campaignId , finalDate);

        // // 5. 인솔자 찾기 로직 (개선: 스트림이나 향상된 for문 사용 권장)
        // UserCampaignVO leaderCampaign = userCampaignApply.stream()
        //     .filter(c -> "8".equals(c.getStatus()))
        //     .findFirst()
        //     .orElse(null);

        // if (leaderCampaign != null) {
        //     model.addAttribute("leaderUserNo", leaderCampaign.getUserNo());
        //     model.addAttribute("leaderStatus", leaderCampaign.getStatus());
        //     // ... 필요한 정보 추가
        // }
        model.addAttribute("userCampaignApply", userCampaignApply);
        model.addAttribute("applicantsNum", userCampaignApply.size()-1); // 신청 수 추가

        UserCampaignVO leaderCampaign = null; // 인솔자를 찾아서 저장할 변수

        for (int i = 0; i < userCampaignApply.size(); i++) {
            UserCampaignVO currentCampaign = userCampaignApply.get(i);
            if ("8".equals(currentCampaign.getStatus())) {
                leaderCampaign = currentCampaign; // 첫 번째 인솔자 또는 유일한 인솔자를 여기에 저장 (논리에 따라)
                break; // 만약 첫 인솔자만 중요하면 여기서 루프를 멈춤
            }
        }

        if (leaderCampaign != null) {
            model.addAttribute("leaderUserNo", leaderCampaign.getUserNo()); // 인솔자의 고유 번호
            model.addAttribute("leaderStatus", leaderCampaign.getStatus()); // 인솔자의 상태 (이 경우는 "9"일 것임)
            model.addAttribute("leaderApplyDate", leaderCampaign.getApplyDate()); // 인솔자 날짜
            model.addAttribute("leaderCampaignId", leaderCampaign.getCampaignId()); // 인솔자의 캠페인 번호
        }
        return "apply/applyConfirm";
    }

// @PatchMapping("/attendance/updateStatus")
//     public ResponseEntity<?> updateAttendanceStatus(@RequestBody StatusUpdateRequest request) {
//         // @RequestBody 어노테이션이 JSON Body를 StatusUpdateRequest 객체로 자동 매핑해줘!

//         log.info("근태 상태 업데이트 요청: UserNo={}, CampaignId={}, ApplyDate={}, NewStatus={}",
//                 request.getUserNo(), request.getCampaignId(), request.getApplyDate(), request.getNewStatus());

//         try {
//             // Service 계층 호출 (실제 DB 업데이트 로직)
//             boolean success = attendanceService.updateStatus(
//                 request.getUserNo(),
//                 request.getCampaignId(),
//                 request.getApplyDate(),
//                 request.getNewStatus()
//             );

//             if (success) {
//                 return ResponseEntity.ok().body(Map.of("message", "상태가 성공적으로 업데이트되었습니다."));
//             } else {
//                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "상태 업데이트 실패"));
//             }
//         } catch (Exception e) {
//             log.error("근태 상태 업데이트 중 오류 발생: {}", e.getMessage(), e);
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 오류 발생", "error", e.getMessage()));
//         }
    // }

    @PatchMapping("/attendance/updateStatus")
    // public ResponseEntity<?> updateAttendanceStatus(
    //         @PathVariable("userNo") Long userNo, @RequestBody UserCampaignVO request) throws Exception {
    // 상태 업데이트
    public ResponseEntity<?> updateAttendanceStatus(@RequestBody UserCampaignVO request) throws Exception {
        log.info("근태 상태 업데이트 요청: UserNo={}, CampaignId={}, ApplyDate={}, NewStatus={}",
                request.getUserNo(), request.getCampaignId(), request.getApplyDate(), request.getStatus());
        Long userNo = request.getUserNo();
        Long campaignId = request.getCampaignId();
        LocalDate applyDate = request.getApplyDate();
        String status = request.getStatus(); // JavaScript에서 newStatus 필드로 보냈다면
        String statusInfo = applyService.statusInfo(userNo, campaignId, applyDate);

        try {
            // 서비스 계층 호출: DB에 해당 userNo의 출결 상태를 request.getNewStatus()로 업데이트
            String updateStatus = applyService.updateStatus(userNo, campaignId, applyDate, status);
            if(statusInfo.equals("0")){
                if(updateStatus.equals("2")){
                    String status0 = applyService.updateStatus(userNo, campaignId, applyDate, statusInfo);
                    return ResponseEntity.ok().body("{\"message\": \"출근 or 지각 터치 후 퇴근 버튼 터치 하세요.\"}");
                } else if (updateStatus.equals("3")) {
                    applyService.lateYn(userNo, campaignId, applyDate);
                    return ResponseEntity.ok().body("{\"message\": \"출결 상태가 성공적으로 업데이트되었습니다.\"}");
                }else{
                    return ResponseEntity.ok().body("{\"message\": \"출결 상태가 성공적으로 업데이트되었습니다.\"}");
                }
            }else if(statusInfo.equals("1")){
                if(updateStatus.equals("2")){
                    log.info("퇴근 터치 하셨으므로, 변경 불가 합니다.");
                    applyService.pointFull(userNo, campaignId, applyDate);
                    return ResponseEntity.ok().body("{\"message\": \"퇴근으로 성공적으로 업데이트되었습니다.\"}");
                } else if(updateStatus.equals("4") || updateStatus.equals("5")) {
                    String status0 = applyService.updateStatus(userNo, campaignId, applyDate, statusInfo);
                    return ResponseEntity.ok().body("{\"message\": \"출근 or 지각 상태에서는 결근, 무단결근은 터치가 안됩니다.\"}");
                } else{
                    if(updateStatus.equals("3")){
                        String status0 = applyService.updateStatus(userNo, campaignId, applyDate, statusInfo);
                        return ResponseEntity.ok().body("{\"message\": \"출근 상태 입니다.\"}");
                    }else{
                        return ResponseEntity.ok().body("{\"message\": \"출근 상태 입니다.\"}");
                    }
                }
            } else if(statusInfo.equals("2")){
                if (updateStatus.equals("2")){
                    return ResponseEntity.ok().body("{\"message\": \"출결 상태가 성공적으로 업데이트되었습니다.\"}");
                } else {
                    String status0 = applyService.updateStatus(userNo, campaignId, applyDate, statusInfo);
                    return ResponseEntity.ok().body("{\"message\": \"퇴근 터치 하셨으므로, 변경 불가 합니다.\"}");
                }
            } else if(statusInfo.equals("3")){
                if(updateStatus.equals("2")){
                    applyService.pointFull(userNo, campaignId, applyDate);
                    return ResponseEntity.ok().body("{\"message\": \"퇴근으로 성공적으로 업데이트되었습니다.\"}");
                }else if(updateStatus.equals("4") || updateStatus.equals("5")) {
                    String status0 = applyService.updateStatus(userNo, campaignId, applyDate, statusInfo);
                    return ResponseEntity.ok().body("{\"message\": \"출근 or 지각 상태에서는 결근, 무단결근은 터치가 안됩니다.\"}");
                }else{
                    if(updateStatus.equals("1")){
                        String status0 = applyService.updateStatus(userNo, campaignId, applyDate, statusInfo);
                        return ResponseEntity.ok().body("{\"message\": \"지각 상태 입니다.\"}");
                    }else{
                        return ResponseEntity.ok().body("{\"message\": \"지각 상태 입니다.\"}");
                    }
                }
            }
            
            else if(statusInfo.equals("4")){
                if(updateStatus.equals("1") || updateStatus.equals("2") || updateStatus.equals("3") || updateStatus.equals("5")){
                    String status0 = applyService.updateStatus(userNo, campaignId, applyDate, statusInfo);
                    return ResponseEntity.ok().body("{\"message\": \"결근 상태 입니다.\"}");
                }else{
                    return ResponseEntity.ok().body("{\"message\": \"결근 상태 입니다.\"}");
                }
            }else if(statusInfo.equals("5")){
                // 1. 응답할 데이터를 Map에 담습니다.
                Map<String, String> response = new HashMap<>();
                response.put("message", "무단 결근 상태 입니다.");

                if(updateStatus.equals("1") || updateStatus.equals("2") || updateStatus.equals("3") || updateStatus.equals("4")){
                    applyService.updateStatus(userNo, campaignId, applyDate, statusInfo);
                    // 2. 객체 자체를 리턴하면 자동으로 JSON {"message": "..."} 이 됩니다.
                    return ResponseEntity.ok().body(response);
                } else {
                    return ResponseEntity.ok().body(response);
                }
            }else{
                return ResponseEntity.ok().body("{\"message\": \"출결 상태가 성공적으로 업데이트되었습니다.\"}");
            }


            
            // 성공 응답 (HTTP 200 OK)
            // return ResponseEntity.ok().body("{\"message\": \"출결 상태가 성공적으로 업데이트되었습니다.\"}");
            // 실제 구현에서는 업데이트된 정보 등을 반환할 수도 있어.
        } catch (Exception e) {
            log.error("출결 상태 업데이트 중 오류 발생: userNo={}, campaignId={}, statusValue={}, error={}", userNo, campaignId, status, e.getMessage());
            // 실패 응답 (HTTP 500 Internal Server Error)
            return ResponseEntity.status(500).body("{\"message\": \"출결 상태 업데이트에 실패했습니다: " + e.getMessage() + "\"}");
        }
    }

    @PatchMapping("/initStatus/{userNo}/{campaignId}/{applyDate}")
    // 상태 초기화
    public ResponseEntity<?> getStatus(@PathVariable("userNo") Long userNo, @PathVariable("campaignId") Long campaignId, @PathVariable("applyDate") LocalDate applyDate, @RequestBody String newStatus) throws Exception {
        int initStatus = applyService.initStatus(userNo, campaignId, applyDate);

        return ResponseEntity.ok().body("{\"message\": \"초기화 성공.\"}");
    }
}
