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
            log.info("authUser : " + authUser);
            UserVO user = authUser.getUserVo();
            log.info("user : " + user);

            model.addAttribute("user", user);

            List<UserVO> userList = userService.userList(option, page);
            log.info("user List 호출 userList : " + userList);
            if(userList != null){
                log.info("userList not null");
                
                List<UserVO> userAddressList = userService.userAddressList();
                model.addAttribute("userAddressList", userAddressList);

            }
            model.addAttribute("userList", userList);
            model.addAttribute("option", option);
            model.addAttribute("rows", page.getRows());
            model.addAttribute("page", page);
            String pageUrl = UriComponentsBuilder.fromPath("/admin/user01")
                            //.queryParam("page", page.getPage())
                            .queryParam("keyword", option.getKeyword())
                            .queryParam("code", option.getCode())
                            // .queryParam("rows", page.getRows())
                            .queryParam("orderCode", option.getOrderCode())
                            .build()
                            .toUriString();
            log.info("pageRows : " + page.getRows());
            model.addAttribute("pageUrl", pageUrl);

        }
        return "user/user01";
    }

    @GetMapping("/user02")
    public String user02(@AuthenticationPrincipal CustomUser authUser, Model model, @RequestParam("id") String id) throws Exception {
        log.info(":::::::::: select :::::::::: " + id);

        UserVO userSelect = userService.userSelect(id);
        FilesVO file = fileService.select(id);
        log.info("DEBUG: nickName value before passing to template: [{}]", userSelect.getNickName());

        model.addAttribute("userSelect", userSelect);
        if(file != null){
            model.addAttribute("file", file);
        }
        return "user/user02";
    }
    @PostMapping("/user03")
    public String user03(@AuthenticationPrincipal CustomUser authUser, Model model, UserVO userVO) throws Exception {
        log.info(":::::::::: update :::::::::: " + userVO);
        String userId = String.valueOf(userVO.getId());
        log.info("userId :::::::::: " + userId);
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
        return "redirect:/admin/user01";
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
        return "redirect:/admin/user01";
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
}
