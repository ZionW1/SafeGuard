package com.safeg.user.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.safeg.user.service.FileService;
import com.safeg.user.service.MyPageService;
import com.safeg.user.vo.CalendarEventVO;
import com.safeg.user.vo.CustomUser;
import com.safeg.user.vo.FilesVO;
import com.safeg.user.vo.PointHistoryVO;
import com.safeg.user.vo.UserAddressVO;
import com.safeg.user.vo.UserCampaignVO;
import com.safeg.user.vo.UserVO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

    @Autowired
    private MyPageService myPageService;

    @Autowired
    private FileService fileService;

    @GetMapping("/calendarPage")
    public String getUserPage(Model model, HttpServletRequest request) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = authentication.getName(); // 현재 로그인된 사용자의 ID
        
        // ⭐ CustomUser로 캐스팅하여 id 값 가져오기 ⭐
        if (authentication.getPrincipal() instanceof CustomUser) { // CustomUser 클래스로 확인
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            Long userIdFromDb = customUser.getId(); // ⭐ users 테이블의 실제 id 값을 가져왔다! ⭐
            String username = customUser.getUsername(); // 로그인 아이디 (userId)

            log.info(":::::::::: calendarPage 호출 ::::::::::");
            log.info("현재 로그인된 사용자의 DB ID: " + userIdFromDb);
            log.info("현재 로그인된 사용자의 username (로그인 ID): " + username);
            FilesVO file = fileService.getMypageImage(String.valueOf(userIdFromDb), "profile");

            model.addAttribute("currentURI", request.getRequestURI());
            model.addAttribute("userId", userIdFromDb); // 뷰에서 DB ID를 사용할 수 있도록 모델에 추가
            model.addAttribute("username", username); // 필요한 경우 username도 추가
            model.addAttribute("file", file); // 필요한 경우 username도 추가

        } else {
            // 로그인이 되어 있지 않거나, 익명 사용자 등 CustomUser가 아닌 경우
            model.addAttribute("userId", null); // 또는 다른 기본값 설정
            model.addAttribute("username", authentication.getName()); // 익명 사용자는 "anonymousUser"
        }
        // currentUserId를 이용해서 필요한 데이터 조회 및 모델에 추가
        // ...
        return "user/user04";
    }

    @GetMapping("/infoUpdate")
    public String mypage(@AuthenticationPrincipal CustomUser authUser, HttpServletRequest request, Model model) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info(":::::::::: 회원 마이 페이지 :::::::::: + " + authUser);
        if(authentication.getPrincipal() instanceof CustomUser){
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            Long userIdFromDb = customUser.getId(); // ⭐ users 테이블의 실제 id 값을 가져왔다! ⭐
            String username = customUser.getUsername(); // 로그인 아이디 (userId)

            FilesVO selectProfile = fileService.getMypageImage(String.valueOf(userIdFromDb), "profile");
            FilesVO getIdentity = fileService.getMypageImage(String.valueOf(userIdFromDb), "identification");
            FilesVO getCertificate = fileService.getMypageImage(String.valueOf(userIdFromDb), "certificate");

            UserVO user = authUser.getUserVo();
            log.info("user.getId() : " + user.getId());
            log.info("user 유저 : " + user);

            UserAddressVO getAddress = myPageService.getAddress(user.getId());
            log.info("getAddress() : " + getAddress);

            model.addAttribute("currentURI", request.getRequestURI());
            model.addAttribute("userId", userIdFromDb); // 뷰에서 DB ID를 사용할 수 있도록 모델에 추가
            model.addAttribute("username", username); // 필요한 경우 username도 추가
            model.addAttribute("file", selectProfile); // 필요한 경우 username도 추가
            model.addAttribute("getCertificate", getCertificate); // 필요한 경우 username도 추가
            model.addAttribute("getIdentity", getIdentity); // 필요한 경우 username도 추가

            model.addAttribute("getAddress", getAddress);
            model.addAttribute("user", user);
        }else{
            return "redirect:/";
        }
        log.info(authUser.getUserVo().toString());
        return "user/user03";
    }

    @GetMapping("/changePasswordPage")
    public String changePasswordPage(HttpServletRequest request, Model model) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info(":::::::::: changePasswordPage 호출 :::::::::: + ");
        if(authentication.getPrincipal() instanceof CustomUser){
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            Long userIdFromDb = customUser.getId(); // ⭐ users 테이블의 실제 id 값을 가져왔다! ⭐
            String username = customUser.getUsername(); // 로그인 아이디 (userId)
            FilesVO file = fileService.getMypageImage(String.valueOf(userIdFromDb), "profile");

            model.addAttribute("userId", userIdFromDb); // 뷰에서 DB ID를 사용할 수 있도록 모델에 추가
            model.addAttribute("username", username); // 필요한 경우 username도 추가
            model.addAttribute("file", file); // 필요한 경우 username도 추가

            model.addAttribute("currentURI", request.getRequestURI());
            model.addAttribute("userId", userIdFromDb); // 뷰에서 DB ID를 사용할 수 있도록 모델에 추가
            model.addAttribute("username", username); // 필요한 경우 username도 추가
        }else{
            return "redirect:/";
        }

        return "mypage/changePasswordPage";
    }


    @PostMapping("/changePassword")
    @ResponseBody // JSON 응답을 위해 사용
    public ResponseEntity<Map<String, Object>> changePassword(@RequestParam("currentPassword") String currentPassword, @RequestParam("newPassword") String newPassword) throws Exception {

        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null; // 실제 UserVO나 CustomUserDetails에서 사용자 ID를 추출해야 함
        // Long userIdFromDb = customUser.getId(); // ⭐ users 테이블의 실제 id 값을 가져왔다! ⭐
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "로그인 상태가 아닙니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        if(authentication.getPrincipal() instanceof CustomUser){
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            userId = customUser.getId(); // ⭐ users 테이블의 실제 id 값을 가져왔다! ⭐
            // String username = customUser.getUsername(); // 로그인 아이디 (userId)
            log.info("userIdFromDb : " + userId);
        }
        // 현재 로그인된 사용자의 ID를 가져옴 (UserDetailsService 구현에 따라 달라질 수 있음)
        // UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // String username = userDetails.getUsername(); // 또는 ID를 직접 가져올 수 있도록 설정

        // 현재 예시에서는 편의상 UserDetails에서 id를 가져온다고 가정
        // 실제로는 UserVO(혹은 CustomUserDetails)에서 id를 꺼내야 함
        // 여기서는 임시로 사용자의 식별자를 어떻게든 가져온다고 가정.
        // 예를 들어, 사용자 정보를 담은 CustomUserDetails에서 userId를 가져올 수 있음

        log.info("changePassword userId : " + userId);

        if (userId == null) {
            response.put("success", false);
            response.put("message", "사용자 정보를 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            boolean isChanged = myPageService.changeUserPassword(userId, currentPassword, newPassword);
            if (isChanged) {
                response.put("success", true);
                response.put("message", "비밀번호가 성공적으로 변경되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "현재 비밀번호가 일치하지 않거나 새 비밀번호를 변경할 수 없습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (IllegalArgumentException e) { // 서비스 계층에서 발생한 특정 예외 처리
            response.put("success", false);
            response.put("message", e.getMessage()); // 예를 들어 "새 비밀번호가 현재 비밀번호와 동일합니다."
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("비밀번호 변경 중 서버 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "비밀번호 변경 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/applyBodyguard")
    @ResponseBody
    public Map<String, Object> applyBodyguard(@AuthenticationPrincipal CustomUser authUser, @ModelAttribute UserVO userVO, Model model) throws Exception{
        log.info(":::::::::: applyBodyguard :::::::::: + " + authUser.getUserVo().getUserId());
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int result = 0;

        log.info(":::::::::: authUser 회원 마이 페이지 :::::::::: + " + authUser);
        log.info(":::::::::: userVO.getGuardType :::::::::: + " + userVO.getGuardType());


        if(authentication.getPrincipal() instanceof CustomUser){
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            Long userIdFromDb = customUser.getId(); // ⭐ users 테이블의 실제 id 값을 가져왔다! ⭐
            String username = customUser.getUsername(); // 로그인 아이디 (userId)
            log.info("userIdFromDb : " + userIdFromDb);
            log.info("username : " + username);
            // result = myPageService.applyBodyguard(userVo);

            try {
                // 일반 유저로 변경하는 서비스 로직 호출
                result = myPageService.applyBodyguard(userIdFromDb, userVO.getGuardType());

                response.put("success", true);
                response.put("message", "사용자가 성공적으로 정지 해제 상태로 변경되었습니다.");
            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "사용자 변경 중 오류가 발생했습니다: " + e.getMessage());
                // 필요하다면 에러 코드 등 추가 정보 포함
            }
        }

        log.info(":::::::::: 회원 프로필 업데이트 완료 :::::::::: + " + result);

        // Users mypageSelect = userService.mypageSelect(authUser.getUser().getId());

        return response;
    }

    // // FullCalendar는 보통 start, end 파라미터를 넘겨줘 (보여지는 달력 기간)
    // @GetMapping("/calendar")
    // @ResponseBody // 이 메서드만 데이터를 반환하게 함
    // public List<CalendarEventVO> getCalendarEvents(
    //         // FullCalendar는 보통 LocalDateTime 형식의 문자열을 보내므로 LocalDateTime으로 받으면 편리해
    //         @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start, // <-- 여기에 추가
    //         @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
    //         Principal principal) throws Exception { // ⭐ 이렇게 Principal 객체를 받아서 userId를 가져올 수 있어
    //     log.info(":::::::::: getCalendarEvents 호출 :::::::::: + ");

    //     String userId = principal.getName(); 
    //     log.info("현재 로그인한 사용자 ID: " + userId);
    //     log.info(userId + "님의 " + start + " 부터 " + end + " 까지의 캘린더 이벤트를 조회합니다.");
    //     log.info(":::::::::: getCalendarEvents 호출 - userId: {}, start: {}, end: {} ::::::::::", userId, start, end);
    //     // 실제로는 로그인된 사용자 정보를 기반으로 DB에서 날짜를 가져와야 해
    //     // 지금은 예시니까 임시 데이터로 보여줄게.
    //     // 마이클의 서비스에서 DB에서 "신청 날짜"와 "신청 완료 날짜"를 가져오는 로직이 필요해.

    //     List<LocalDate> appliedDates = myPageService.getAppliedDatesForUser(userId, start, end);
    //     List<LocalDate> completedDates = myPageService.getCompletedDatesForUser(userId, start, end);

    //     log.info("신청 날짜: " + appliedDates);
    //     log.info("완료 날짜: " + completedDates);
    //     // DB에서 가져온 신청 날짜 (예시)
    //     List<CalendarEventVO> events = Stream.concat(
    //         appliedDates.stream()
    //                     // 각 신청 날짜를 CalendarEventVO로 변환
    //                     .map(date -> new CalendarEventVO(
    //                         date.toString(), // ⭐ start: 해당 날짜를 문자열로
    //                         date.plusDays(1).toString(), // ⭐ end: 해당 날짜의 다음 날을 문자열로
    //                         "APPLIED",       // 이벤트 제목 (원하는 대로 지정)
    //                         "신청",    // ⭐ 모든 신청 이벤트에 적용할 색상 (예: 파란색)
    //                         "#629eeb"     // 상태
    //                     )),
    //         completedDates.stream()
    //                     // 각 완료 날짜를 CalendarEventVO로 변환
    //                     .map(date -> new CalendarEventVO(
    //                         date.toString(), // ⭐ start: 해당 날짜를 문자열로
    //                         date.plusDays(1).toString(), // ⭐ end: 해당 날짜의 다음 날을 문자열로
    //                         "COMPLETED",       // 이벤트 제목
    //                         "완료",    // ⭐ 모든 완료 이벤트에 적용할 색상 (예: 초록색)
    //                         "#28a745"   // 상태
    //                     ))
    //     ).collect(Collectors.toList());

    //     return events;
    // }

    @GetMapping("/calendar")
    @ResponseBody // 이 메서드만 데이터를 반환하게 함
    public List<CalendarEventVO> getCalendarEvents(
            // FullCalendar는 보통 LocalDateTime 형식의 문자열을 보내므로 LocalDateTime으로 받으면 편리해
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start, // <-- 여기에 추가
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            Principal principal) throws Exception { // ⭐ 이렇게 Principal 객체를 받아서 userId를 가져올 수 있어
        log.info(":::::::::: getCalendarEvents 호출 :::::::::: + ");

        String userId = principal.getName(); 
        log.info("현재 로그인한 사용자 ID: " + userId);
        log.info(userId + "님의 " + start + " 부터 " + end + " 까지의 캘린더 이벤트를 조회합니다.");
        log.info(":::::::::: getCalendarEvents 호출 - userId: {}, start: {}, end: {} ::::::::::", userId, start, end);
        // 실제로는 로그인된 사용자 정보를 기반으로 DB에서 날짜를 가져와야 해
        // 지금은 예시니까 임시 데이터로 보여줄게.
        // 마이클의 서비스에서 DB에서 "신청 날짜"와 "신청 완료 날짜"를 가져오는 로직이 필요해.

        List<CalendarEventVO> getEventStatus = myPageService.getCompletedDatesForUser(userId, start, end);

        log.info("getEventStatus : " + getEventStatus);
        // DB에서 가져온 신청 날짜 (예시)
        List<CalendarEventVO> events = ((Collection<CalendarEventVO>) getEventStatus).stream()
        .map(eventVO -> {
            // 매퍼에서 가져온 eventVO의 status 값을 확인
            String status = eventVO.getStatus();
            String displayTitle; // 화면에 보여줄 제목
            String color;        // 이벤트 색상
            String applyDate = eventVO.getApplyDate();
            
            switch (status) {
                case "0": // 'APPLIED' 상태일 때
                    displayTitle = "신청 중";
                    color = "#95a5a6"; // 파란색 계열
                    break;
                case "1": // 'COMPLETED' 상태일 때
                    displayTitle = "출근";
                    color = "#629eeb"; // 초록색 계열
                    break;
                // ⭐ 필요한 다른 상태에 대한 케이스 추가 ⭐
                case "2": // 예를 들어 '거절' 상태
                    displayTitle = "완료";
                    color = "#28a745"; // 붉은색 계열 (이전 추천 색상)
                    break;
                case "3": // 예를 들어 '취소' 상태
                    displayTitle = "지각";
                    color = "#8e44ad"; // 회색 계열 (이전 추천 색상)
                    break;
                case "4": // 예를 들어 '취소' 상태
                    displayTitle = "결근";
                    color = "#f39c12"; // 회색 계열 (이전 추천 색상)
                    break;
                case "5": // 예를 들어 '취소' 상태
                    displayTitle = "무단";
                    color = "#e74c3c"; // 회색 계열 (이전 추천 색상)
                    break;
                default: // 그 외의 알 수 없는 상태일 때
                    displayTitle = "알 수 없음";
                    color = "#cccccc"; // 기본 회색
                    break;
            }
            eventVO.setTitle(displayTitle);
            log.info("dispalyTitle : " + displayTitle);
            // ⭐ 기존 eventVO의 필드를 활용하여 새로운 CalendarEventVO를 생성 (또는 기존 객체를 수정) ⭐
            // 만약 CalendarEventVO가 불변 객체(Immutable)가 아니라면,
            // eventVO.setSomething(value)로 직접 필드를 설정해도 됨.
            // 여기서는 새로운 객체를 생성하는 방식 (더 안전함)으로 가정
            return new CalendarEventVO(
                eventVO.getTitle(), // 기존 title (DB에서 가져온 내부용 제목)
                applyDate, // 매퍼에서 가져온 start 값 사용 (String으로 가정)
                applyDate,   // 매퍼에서 가져온 end 값 사용 (String으로 가정)
                // displayTitle,       // 새롭게 설정한 displayTitle
                color,              // 새롭게 설정한 color
                eventVO.getStatus(), // 매퍼에서 가져온 status 값 그대로 사용
                eventVO.getApplyDate() // 매퍼에서 가져온 status 값 그대로 사용
            );
            // 만약 CalendarEventVO 생성자가 (start, end, displayTitle, color, status) 형식이라면:
            // return new CalendarEventVO(eventVO.getStart(), eventVO.getEnd(), displayTitle, color, eventVO.getStatus());

        })
    
        .collect(Collectors.toList());

        log.info("events : " + events);
        return events;
    }

    @PostMapping("/updateInfo")
    public String updateInfo(@AuthenticationPrincipal CustomUser authUser, UserVO userVO, BindingResult bindingResult, Model model) throws Exception{
        log.info(":::::::::: 회원 마이 페이지 :::::::::: + " + authUser.getUserVo().getUserId());
        log.info(":::::::::: 회원 마이 페이지 :::::::::: + " + userVO);
        log.info(":::::::::: userVO.getFullAddress() :::::::::: + " + userVO.getFullAddress());
        if (bindingResult.hasErrors()) {
            return "/join";
        }
        int result = 0;
        if(authUser != null){
            // result = userService.mypageUpdate(userVo);
            result = myPageService.updateInfo(userVO);
        }
        log.info(":::::::::: 회원 마이 페이지 완료 :::::::::: + " + result);

        // Users mypageSelect = userService.mypageSelect(authUser.getUser().getId());

        return "redirect:/mypage/infoUpdate";
    }

    @PostMapping("/uploadIdttImage")
    public String uploadIdttImage(@AuthenticationPrincipal CustomUser authUser, UserVO userVO, Model model) throws Exception{
        // log.info(":::::::::: uploadIdttImage :::::::::: + " + authUser.getUserVo().getUserId());
        log.info(":::::::::: uploadIdttImage :::::::::: + " + userVO);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUser) { // CustomUser 클래스로 확인
            boolean result = myPageService.uploadIdttImage(userVO);
        }

        return "redirect:/mypage/infoUpdate";
    }

    @PostMapping("/uploadCertImage")
    @ResponseBody
    public String uploadCertImage(@AuthenticationPrincipal CustomUser authUser, UserVO userVO, Model model) throws Exception{
        // log.info(":::::::::: uploadCertImage :::::::::: + " + authUser.getUserVo().getUserId());
        log.info(":::::::::: uploadCertImage :::::::::: + " + userVO);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUser) { // CustomUser 클래스로 확인
            boolean result = myPageService.uploadCertImage(userVO);
        }

        // if (!userVO.isPasswordConfirmed()) {
        //     log.info("비밀번호 불일치 (평문 비교) + " + userVO.isPasswordConfirmed());
        //     bindingResult.rejectValue("passwordConfirm", "password.mismatch", "비밀번호가 일치하지 않습니다.");
        // }

        // if (bindingResult.hasErrors()) {
        //     return "/join";
        // }
        // int result = 0;
        // if(authUser != null){
        //     // result = userService.mypageUpdate(userVo);
        //     result = myPageService.updateInfo(userVO);
        // }
        // log.info(":::::::::: 회원 마이 페이지 완료 :::::::::: + " + result);

        // Users mypageSelect = userService.mypageSelect(authUser.getUser().getId());

        return "redirect:/mypage/infoUpdate";
    }

    @PostMapping("/markDeleted/{id}") // 👈 POST 메서드로 변경하고 URL 의미도 변경
    @ResponseBody
    public ResponseEntity<String> markFileAsDeleted(@PathVariable("id") String id) throws Exception { // 메서드 이름도 변경하는 게 좋아
        log.info("=========================== markFileAsDeleted =========================" + id);

        // 여기 fileService.delete(id)는 이제 물리적 삭제가 아니라 'is_deleted = Y'로 업데이트하는 로직이어야 해!
        // 예를 들어: int result = fileService.markAsDeleted(id);
        int result = fileService.delete(id); // 현재 fileService.delete()가 이미 is_deleted를 변경한다고 가정

        log.info("=========================== result =========================" + result);

        // 파일 상태 변경 성공
        if (result > 0) {
            return ResponseEntity.ok("SUCCESS"); // HTTP 200 OK와 함께 "SUCCESS" 반환
        }
        // 파일 상태 변경 실패
        return ResponseEntity.status(400).body("FAIL"); // HTTP 400 Bad Request와 함께 "FAIL" 반환 (삭제 실패가 500은 아닐 수 있으니 400으로 변경)
    }

    @SuppressWarnings("null")
    @GetMapping("/point")
    public String pointList (@AuthenticationPrincipal CustomUser authUser, HttpServletRequest request, Model model) throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUser) {
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            Long id = customUser.getId();
            String username = customUser.getUsername();

            FilesVO file = fileService.getMypageImage(String.valueOf(id), "profile");

            log.info("pointList : id : " + id);
            log.info("pointList : username : " + username);
            
            model.addAttribute("userId", id);
            model.addAttribute("username", username);
            model.addAttribute("file", file);

            List<UserCampaignVO> campaignIds = myPageService.campaignId(id);
            List<UserCampaignVO> pointList = myPageService.pointList(id);
            List<UserCampaignVO> referrerPayList = myPageService.referrerPayList(id);

            log.info("campaignIds: " + campaignIds);

            int workPoint = 0;
            if (pointList != null && !pointList.isEmpty()) {
                for (UserCampaignVO workPay : pointList) {
                    workPoint += workPay.getCampaignPay();
                }
                pointList.get(0).setTotalPoint(workPoint); // 총 포인트를 첫 번째 객체에 설정
            }

            int referrerPoint = 0;
            if (referrerPayList != null && !referrerPayList.isEmpty()) {
                for (UserCampaignVO referrerPay : referrerPayList) {
                    referrerPoint += referrerPay.getTotalAmount();
                }
            }

            log.info("workPoint: " + workPoint);
            log.info("referrerPoint: " + referrerPoint);

            List<UserCampaignVO> leaderPayList = null;
            String authId = null;
            int leaderPoint = 0;

            if ((campaignIds != null && !campaignIds.isEmpty())&& "03".equals(campaignIds.get(0).getAuthId())){
                authId = campaignIds.get(0).getAuthId();
                log.info("authId: " + authId);

                if ("03".equals(authId)) {
                    log.info("campaignIds size: " + campaignIds.size());
                    leaderPayList = myPageService.leaderPayList(id, campaignIds);
                    if (leaderPayList != null && !leaderPayList.isEmpty()) {
                        for (UserCampaignVO leaderPay : leaderPayList) {
                            leaderPoint += leaderPay.getLeaderPoint();
                        }
                        leaderPayList.get(0).setTotalPoint(leaderPoint);
                        log.info("leaderPayList :  " + leaderPayList);
                        model.addAttribute("leaderPayList", leaderPayList);
                    } else {
                        model.addAttribute("leaderPayList", leaderPayList != null ? leaderPayList : Collections.emptyList());
                    }
                }
            }
            // aosqkf12!!
            log.info("leaderPoint: " + leaderPoint);

            int pointFull = myPageService.pointFull(id);
            int totalPoint = workPoint + referrerPoint + leaderPoint;

            log.info("authID: " + authId);

            log.info("totalPoint: " + totalPoint);

            model.addAttribute("pointList", pointList != null ? pointList : Collections.emptyList());
            model.addAttribute("authId", authId);
            model.addAttribute("referrerPayList", referrerPayList != null ? referrerPayList : Collections.emptyList());
            model.addAttribute("id", id);
            model.addAttribute("pointFull", pointFull);
            model.addAttribute("totalPoint", totalPoint);
            model.addAttribute("currentURI", request.getRequestURI());


            // model.addAttribute("leaderPayList", leaderPayList != null ? leaderPayList : Collections.emptyList());
            // model.addAttribute("pointList", pointList != null ? pointList : Collections.emptyList());

        }
        return "mypage/point";
    }
}