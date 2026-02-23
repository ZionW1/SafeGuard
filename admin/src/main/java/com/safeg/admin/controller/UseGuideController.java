package com.safeg.admin.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.safeg.admin.service.UseGuideService;
import com.safeg.admin.vo.AdminContentVO;
import com.safeg.admin.vo.UseGuideVO;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class UseGuideController {
    @Autowired
    UseGuideService useGuideService;
    
    @RequestMapping("/useGuide01")
    public String useGuide01(Model model) throws Exception {
        log.info("UseGuide useGuide01");
        AdminContentVO useGuideSelect = useGuideService.useGuideSelect();
        log.info("UseGuide useGuide01" + useGuideSelect);

        model.addAttribute("useGuideSelect", useGuideSelect);
        return "useGuide/useGuide01";
    }
    
    // CKEditor에서 작성된 내용을 POST 요청으로 받아 데이터베이스에 저장
    @PostMapping("/useGuide02")
    public String saveContent(AdminContentVO adminContentVO) throws Exception{
        int result = 0;
        // 'content'는 HTML form의 textarea name="content"에서 넘어온 값
        System.out.println("--- CKEditor에서 넘어온 내용 ---");
        System.out.println(adminContentVO.getContent());
        System.out.println("----------------------------");
        if(adminContentVO.getAction().equals("I")){
            adminContentVO.setAuthor("Admin");
            result = useGuideService.useGuideInsert(adminContentVO);
        } else {
            adminContentVO.setAuthor("Admin");
            result = useGuideService.useGuideUpdate(adminContentVO);
        }
        
        System.out.println("DB 저장 결과: " + result);
        // ⭐️ 여기서 DB에 저장하는 로직을 구현해야 해. ⭐️
        // 1. Service 레이어를 호출하여 DB 트랜잭션 시작
        // 2. DAO/Repository를 통해 DB의 특정 테이블 컬럼에 `content` 문자열을 저장
        //    (예: 게시글 테이블의 'content' 컬럼, VARCHAR 또는 TEXT 타입)
        // 3. 성공 시 메시지 출력 또는 다른 페이지로 리다이렉트

        // 예시: 간단한 콘솔 출력 후 성공 페이지로 리다이렉트
        return "redirect:/useGuide01"; // 저장 성공 후 리다이렉트될 페이지 (예: 목록 페이지)
    }

    // 이미지 업로드 요청을 처리하는 컨트롤러 (이전 구현과 동일)
    // 이 메서드는 이미 구현했으니 참고만 해
    // @PostMapping("/upload/image")
    // @ResponseBody
    // public Map<String, Object> uploadImage(@RequestParam("upload") MultipartFile file) {
    //     log.info("upload/image uploadIamge");
    //     Map<String, Object> responseData = new HashMap<>();
    //     if (!file.isEmpty()) {
    //         try {
    //             // 실제 이미지 파일을 서버 특정 경로에 저장
    //             String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                
    //             File uploadDir = new File("/path/to/your/upload/directory");
    //             if (!uploadDir.exists()) {
    //                 uploadDir.mkdirs();
    //             }
    //             File targetFile = new File("/path/to/your/upload/directory", fileName); // 실제 서버 경로 지정
    //             file.transferTo(targetFile);

    //             // CKEditor에 반환할 URL (클라이언트에서 접근 가능한 경로)
    //             String fileUrl = "/uploads/" + fileName; // /uploads는 스프링 리소스 핸들러에 설정되어야 함

    //             responseData.put("uploaded", 1);
    //             responseData.put("url", fileUrl);
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //             responseData.put("uploaded", 0);
    //             responseData.put("error", Map.of("message", "파일 업로드 실패: " + e.getMessage()));
    //         }
    //     } else {
    //         responseData.put("uploaded", 0);
    //         responseData.put("error", Map.of("message", "업로드할 파일이 없습니다."));
    //     }
    //     return responseData;
    // }

}
