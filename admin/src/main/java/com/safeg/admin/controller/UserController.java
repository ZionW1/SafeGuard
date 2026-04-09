package com.safeg.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.safeg.admin.service.FileService;
import com.safeg.admin.service.UserService;
import com.safeg.admin.util.EncryptionUtil;
import com.safeg.admin.vo.CampaignVO;
import com.safeg.admin.vo.CustomUser;
import com.safeg.admin.vo.FilesVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.UserVO;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
@Slf4j
public class UserController {
    
    @Autowired
    UserService userService;

    @Autowired
    private FileService fileService;

    @GetMapping("/user01")
    public String user01(@AuthenticationPrincipal CustomUser authUser, Model model, Option option, Page page) throws Exception {
        log.info("user List 호출 option : " + option);
        log.info("user List 호출 page : " + page);
        if(authUser != null){
            UserVO user = authUser.getUserVo();
            // 1. URL은 원본 키워드를 유지 (사용자에게 보여지는 용도)
            // String pageUrl = UriComponentsBuilder.fromPath("/admin/user01")
            // .queryParam("keyword", option.getKeyword()) // 원본 유지
            // .queryParam("code", option.getCode())
            // .queryParam("orderCode", option.getOrderCode())
            // .build()
            // .toUriString();

            String pageUrl = UriComponentsBuilder.fromPath("/user01")
                        //.queryParam("page", page.getPage())
                        .queryParam("keyword", option.getKeyword())
                        .queryParam("code", option.getCode())
                        // .queryParam("rows", page.getRows())
                        .queryParam("orderCode", option.getOrderCode())
                        .build()
                        .toUriString();
            // 2. DB 검색을 위한 파라미터 가공 (내부 로직)
            if(option.getCode() == 5 && option.getKeyword() != null) {
                // DB 조회용 객체에만 해시값을 담아서 서비스로 넘김
                option.setKeyword(EncryptionUtil.hash(option.getKeyword()));
            }

            if (option.getCode() == 6 && option.getKeyword() != null) {
                Map<String, String> typeMap = Map.of(
                    "스텝", "00",
                    "경호원 신청 중", "01",
                    "경호원", "02",
                    "수행", "03",
                    "인솔자", "04"
                );
                
                // 일치하는 한글 키워드가 있으면 코드값으로 변경, 없으면 원본 유지
                String codeValue = typeMap.get(option.getKeyword());
                if (codeValue != null) {
                    option.setKeyword(codeValue);
                }
            }
            
            List<UserVO> userList = userService.userList(option, page); // 해시된 키워드로 DB 조회
            if(userList != null){
                log.info("userList not null");
                List<UserVO> userAddressList = userService.userAddressList();
                model.addAttribute("userAddressList", userAddressList);
            }

            // 3. 뷰로 보낼 때는 다시 원본 키워드로 복구 (검색창에 입력한 번호가 남도록)
            // 만약 위에서 option 객체를 직접 수정했다면, 다시 원본으로 바꿔주거나
            // 모델에 원본 키워드를 따로 담아서 보냅니다.
            model.addAttribute("user", user);
            model.addAttribute("pageUrl", pageUrl);
            model.addAttribute("userList", userList);
            model.addAttribute("option", option);
            model.addAttribute("rows", page.getRows());
            model.addAttribute("page", page);
            model.addAttribute("pageUrl", pageUrl);

        }
        return "user/user01";
    }

    @GetMapping("/user02")
    public String user02(@AuthenticationPrincipal CustomUser authUser, Model model, @RequestParam("id") String id) throws Exception {
        log.info(":::::::::: select :::::::::: " + id);

        UserVO userSelect = userService.userSelect(id);
        FilesVO file = fileService.select(id);
        log.info("DEBUG: nickName value before passing to template: [{}]", userSelect.getNickname());

        List<FilesVO> profileImage = fileService.userImageFile(id);
        
        log.info("profileImage.size() : " + profileImage.size());
        log.info("profileImage : " + profileImage.toString());

                if(profileImage.size() == 0){
                    log.info("user02 profile 0");
                    model.addAttribute("profile", "N");
                    model.addAttribute("identification", "N");
                    model.addAttribute("certificate", "N");
                } else if(profileImage.size() == 1) { 
                    if("profile".equals(profileImage.get(0).getTargetType())) {
                        model.addAttribute("profile", "Y");
                        model.addAttribute("identification", "N");
                        model.addAttribute("certificate", "N");
                    } else if("identification".equals(profileImage.get(0).getTargetType())) {
                        model.addAttribute("profile", "N");
                        model.addAttribute("identification", "Y");
                        model.addAttribute("certificate", "N");
                    } else if("certificate".equals(profileImage.get(0).getTargetType())) {
                        model.addAttribute("profile", "N");
                        model.addAttribute("identification", "N");
                        model.addAttribute("certificate", "Y");
                    }
                } else if(profileImage.size() == 2) {
                    if ("profile".equals(profileImage.get(0).getTargetType()) && "identification".equals(profileImage.get(1).getTargetType())) {
                        model.addAttribute("profile", "Y");
                        model.addAttribute("identification", "Y");
                        model.addAttribute("certificate", "N");
                    } else if ("profile".equals(profileImage.get(0).getTargetType()) && "certificate".equals(profileImage.get(1).getTargetType())){
                        model.addAttribute("profile", "Y");
                        model.addAttribute("identification", "N");
                        model.addAttribute("certificate", "Y");
                    } else if ("identification".equals(profileImage.get(0).getTargetType()) && "certificate".equals(profileImage.get(1).getTargetType())){
                        model.addAttribute("profile", "N");
                        model.addAttribute("identification", "Y");
                        model.addAttribute("certificate", "Y");
                    }

                } else if(profileImage.size() == 3) {
                    model.addAttribute("profile", "Y");
                    model.addAttribute("identification", "Y");
                    model.addAttribute("certificate", "Y");
                } 
                    // if(i == 0 || i == 1 || i == 2 && profileImage.get(i).getTargetType() != null){
                    //     model.addAttribute("" + profileImage.get(i).getTargetType(), "Y");
                    // }
                    // if(i == 0 && "profile".equals(profileImage.get(i).getTargetType())) {
                    //     if(i == 1 && profileImage.get(i).getTargetType() == null){
                    //         model.addAttribute("identification", "N");
                    //     } else if (i == 2 && profileImage.get(i).getTargetType() == null) {
                    //         model.addAttribute("certificate", "N");
                    //     }
                    // } else if(i == 0 && "identification".equals(profileImage.get(i).getTargetType())) {
                    //     if(i == 1 && profileImage.get(i).getTargetType() == null){
                    //         model.addAttribute("profile", "N");
                    //         model.addAttribute("certificate", "N");
                    //     }
                    // } else if(i == 0 && "certificate".equals(profileImage.get(i).getTargetType())) {
                    //     if(i == 1 && profileImage.get(i).getTargetType() == null){
                    //         model.addAttribute("profile", "N");
                    //         model.addAttribute("identification", "N");
                    //     }
                    // }
                    for(int i = 0; i < profileImage.size(); i++) {

            }
        
        model.addAttribute("userSelect", userSelect);
        if(file != null){
            model.addAttribute("file", file);
        }
        return "user/user02";
    }
    @PostMapping("/user03")
    public String user03(@AuthenticationPrincipal CustomUser authUser, Model model, UserVO userVO, 
        @RequestParam(value="keyword", required=false) String keyword,
        @RequestParam(value="code", required=false, defaultValue="0") int code,
        @RequestParam(value="orderCode", required=false, defaultValue="0") int orderCode,
        @RequestParam(value="page", defaultValue="1") int page
    ) throws Exception {
        Long referrerNo = 0L;
        log.info(":::::::::: update :::::::::: " + userVO);
        log.info(":::::::::: update page :::::::::: " + page);
        String userId = String.valueOf(userVO.getId());
        if(userVO.getReferrerId() != null && !userVO.getReferrerId().isEmpty()) {
            log.info("user03 getReferrerId : " + userVO.getReferrerId());
            referrerNo = userService.referrerId(userVO.getReferrerId());
            userVO.setReferrerNo(referrerNo);
        }

        log.info("userId :::::::::: " + userId);
        log.info("referrerNo :::::::::: " + referrerNo);
        log.info("getReferrerNo :::::::::: " + userVO.getReferrerNo());
        log.info("getReferrerId :::::::::: " + userVO.getReferrerId());

        int result = userService.userInfoUpdate(userVO);

        FilesVO file = fileService.select(userId);
        
        if(file != null){
            model.addAttribute("file", file);
        }
        if(result > 0){
            log.info("user03 update success");
            model.addAttribute("msg", "");
        } else {
            log.info("user03 update fail");
            model.addAttribute("msg", "");
        }
        return "redirect:/user01?page=" + page ;
        // redirect:/admin/campaign01
    }
    
    @PostMapping("/user04")
    public String user04(@RequestParam("id") String id) throws Exception {
        log.info(":::::::::: remove :::::::::: " + id);
        log.info(id);

        int result = userService.userRemove(id);
        
        if(result > 0){
            log.info("user04 userRemove success");
        } else {
            log.info("user04 userRemove fail");
        }
        return "redirect:/user01";
        // redirect:/admin/campaign01
    }

    // 리더자로 변경
    @PostMapping("/user05")
    @ResponseBody
    public Map<String, Object> user05(@ModelAttribute UserVO userVO) throws Exception {
        // log.info(":::::::::: remove :::::::::: " + id);
        // log.info(id);

        // int result = userService.userUpdate(id);
        
        // if(result > 0){
        //     log.info("user05 userUpdate success");
        // } else {
        //     log.info("user05 userUpdate fail");
        // }
        // return "redirect:/admin/user02?id=" + id;
        // // redirect:/admin/campaign01
        // 또는 UserVO 형태로 받을 수 있음
    // public Map<String, Object> updateUserToGeneral(UserVO userVO) {
        log.info(":::::::::: user05 userUpdate :::::::::: " + userVO.getId());
        Map<String, Object> response = new HashMap<>();
        try {
            // 일반 유저로 변경하는 서비스 로직 호출
            userService.userUpdate(userVO.getId()); // 또는 userVO를 넘겨서 처리

            response.put("success", true);
            response.put("message", "사용자가 성공적으로 일반 유저로 변경되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "사용자 변경 중 오류가 발생했습니다: " + e.getMessage());
            // 필요하다면 에러 코드 등 추가 정보 포함
        }
        return response;
    }

    @PostMapping("/user06")
    @ResponseBody
    public Map<String, Object> user06(@ModelAttribute UserVO userVO) throws Exception {
        log.info(":::::::::: user06 userUpdate :::::::::: " + userVO.getId());
        Map<String, Object> response = new HashMap<>();
        try {
            // 일반 유저로 변경하는 서비스 로직 호출
            userService.userLeaderUpdate(userVO.getId()); // 또는 userVO를 넘겨서 처리

            response.put("success", true);
            response.put("message", "사용자가 성공적으로 일반 유저로 변경되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "사용자 변경 중 오류가 발생했습니다: " + e.getMessage());
            // 필요하다면 에러 코드 등 추가 정보 포함
        }
        return response;
    }

    @PostMapping("/user07")
    @ResponseBody
    public Map<String, Object> user07(@ModelAttribute UserVO userVO) throws Exception {
        log.info(":::::::::: user07 userStop :::::::::: " + userVO.getId());
        Map<String, Object> response = new HashMap<>();
        try {
            // 일반 유저로 변경하는 서비스 로직 호출
            userService.userStop(userVO.getId()); // 또는 userVO를 넘겨서 처리

            response.put("success", true);
            response.put("message", "사용자가 성공적으로 정지 상태로 변경되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "사용자 변경 중 오류가 발생했습니다: " + e.getMessage());
            // 필요하다면 에러 코드 등 추가 정보 포함
        }
        return response;
    }

    @PostMapping("/user08")
    @ResponseBody
    public Map<String, Object> user08(@ModelAttribute UserVO userVO) throws Exception {
        log.info(":::::::::: user06 userUpdate :::::::::: " + userVO.getId());
        Map<String, Object> response = new HashMap<>();
        try {
            // 일반 유저로 변경하는 서비스 로직 호출
            userService.userUnstop(userVO.getId()); // 또는 userVO를 넘겨서 처리

            response.put("success", true);
            response.put("message", "사용자가 성공적으로 정지 해제 상태로 변경되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "사용자 변경 중 오류가 발생했습니다: " + e.getMessage());
            // 필요하다면 에러 코드 등 추가 정보 포함
        }
        return response;
    }

    @PostMapping("/user09")
    @ResponseBody
    public Map<String, Object> user09(@ModelAttribute UserVO userVO) throws Exception {
        log.info(":::::::::: user09 guardType Change :::::::::: " + userVO.getId());
        Map<String, Object> response = new HashMap<>();

        try {
            // 일반 유저로 변경하는 서비스 로직 호출
            userService.guardTypeChange(userVO); // 또는 userVO를 넘겨서 처리

            response.put("success", true);
            response.put("message", "사용자가 성공적으로 정지 해제 상태로 변경되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "사용자 변경 중 오류가 발생했습니다: " + e.getMessage());
            // 필요하다면 에러 코드 등 추가 정보 포함
        }

        return response;
    }

    @PostMapping("/user/resetPoint")
    @ResponseBody
    public String resetPoint(@RequestParam("userNo") int userNo) {
        try {
            userService.resetAllUserPay();
            userService.resetAllUserApply();
            return "success";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    @PostMapping("/settlementAll")
    @ResponseBody
    public Map<String, Object> settlementAll() {
        log.info("settlementAll");
        Map<String, Object> result = new HashMap<>();
        try {
            // Service를 호출해서 실제 DB 작업을 수행합니다.
            int count = userService.settlementAll();
            result.put("success", true);
            result.put("count", count);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result; // 이 데이터가 다시 JS의 'result'로 돌아갑니다.
    }
}
