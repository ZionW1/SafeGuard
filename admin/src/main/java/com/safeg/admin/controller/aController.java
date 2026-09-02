package com.safeg.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.safeg.admin.service.FileService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class aController {

    @Autowired
    private FileService fileService;


    @PostMapping("/file/markDeleted/{id}") // 👈 POST 메서드로 변경하고 URL 의미도 변경
    @ResponseBody
    public ResponseEntity<String> markFileAsDeleted(@PathVariable("id") String id) throws Exception { // 메서드 이름도 변경하는 게 좋아

        // 여기 fileService.delete(id)는 이제 물리적 삭제가 아니라 'is_deleted = Y'로 업데이트하는 로직이어야 해!
        // 예를 들어: int result = fileService.markAsDeleted(id);
        int result = fileService.delete(id); // 현재 fileService.delete()가 이미 is_deleted를 변경한다고 가정

        // 파일 상태 변경 성공
        if (result > 0) {
            return ResponseEntity.ok("SUCCESS"); // HTTP 200 OK와 함께 "SUCCESS" 반환
        }
        // 파일 상태 변경 실패
        return ResponseEntity.status(400).body("FAIL"); // HTTP 400 Bad Request와 함께 "FAIL" 반환 (삭제 실패가 500은 아닐 수 있으니 400으로 변경)
    }
}
