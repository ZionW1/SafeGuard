package com.safeg.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.safeg.admin.service.LeaderService;
import com.safeg.admin.service.UseGuideService;
import com.safeg.admin.vo.AdminContentVO;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class LeaderController {

    @Autowired
    LeaderService leaderService;
    
    @GetMapping("/leader01")
    public String leader01(Model model) throws Exception {
        log.info("leader01");
        AdminContentVO leaderSelect = leaderService.leaderSelect();
        log.info("leader01" + leaderSelect);

        model.addAttribute("leaderSelect", leaderSelect);
        return "leader/leader01";
    }

    @PostMapping("/leader02")
    public String leader02(AdminContentVO adminContentVO) throws Exception{
        int result = 0;
        // 'content'는 HTML form의 textarea name="content"에서 넘어온 값
        System.out.println("--- CKEditor에서 넘어온 내용 ---");
        System.out.println(adminContentVO.getContent());
        System.out.println("----------------------------");
        if(adminContentVO.getAction().equals("I")){
            adminContentVO.setAuthor("Admin");
            result = leaderService.useGuideInsert(adminContentVO);
        } else {
            adminContentVO.setAuthor("Admin");
            result = leaderService.useGuideUpdate(adminContentVO);
        }
        
        System.out.println("DB 저장 결과: " + result);
        // ⭐️ 여기서 DB에 저장하는 로직을 구현해야 해. ⭐️
        // 1. Service 레이어를 호출하여 DB 트랜잭션 시작
        // 2. DAO/Repository를 통해 DB의 특정 테이블 컬럼에 `content` 문자열을 저장
        //    (예: 게시글 테이블의 'content' 컬럼, VARCHAR 또는 TEXT 타입)
        // 3. 성공 시 메시지 출력 또는 다른 페이지로 리다이렉트

        // 예시: 간단한 콘솔 출력 후 성공 페이지로 리다이렉트
        return "redirect:/admin/leader01"; // 저장 성공 후 리다이렉트될 페이지 (예: 목록 페이지)
    }
    // List<UserVO> leaderList = leaderService.leaderList();
    // log.info("leader01" + leaderList);
    // model.addAttribute("leaderList", leaderList);
    // return "leader/leader01";
}
