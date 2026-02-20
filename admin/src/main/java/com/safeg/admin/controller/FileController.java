package com.safeg.admin.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.safeg.admin.vo.FilesVO;
import com.safeg.admin.mapper.MediaUtil;
import com.safeg.admin.service.FileService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 이미지 썸네일
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping("/img")
    public ResponseEntity<byte[]> thumbnail(@RequestParam("id") String id) throws Exception{
        log.info("thumbnail : " + id);
        FilesVO file = fileService.select(id);
        
        String filePath = file.getFilePath();
        // 파일 객체 생성
        File f = new File(filePath);
        // 파일 데이터
        byte[] fileData = FileCopyUtils.copyToByteArray(f);
        
        // 컨텐츠 파일 지정
        // 확장자로 컨텐츠 타입 지정
        // - 확장자 : .jpg, .png ...
        String ext = filePath.substring(filePath.lastIndexOf(".") + 1); // 확장자
        MediaType mediaType = MediaUtil.getMediaType(ext);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);

        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }
    
    
    /**
     * 다운로드
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping("/file/{id}")
    public ResponseEntity<byte[]> download(@PathVariable("id") String id) throws Exception{
        FilesVO file = fileService.select(id);

        String filePath = file.getFilePath();
        String fileName = file.getOriginalName();
        
        // 한글 파일명 인코딩
        fileName = URLEncoder.encode(fileName,"UTF-8");

        // 파일 객체 생성
        File f = new File(filePath);
        // 파일데이터
        byte[] fileData = FileCopyUtils.copyToByteArray(f);
        // 파일 응답을 위한 헤더 설정
        // ContentType : application/octet-stream
        // Content-Disposition : attachment; filename="파일명.확장자"
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);


        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }


    @ResponseBody
    @DeleteMapping("/file/{id}")
    // @PostMapping("/file/{id}") // 👈 POST로 변경하고 URL에 "delete" 추가
    public String deleteFile(@PathVariable("id") String id) throws Exception{
        log.info("=========================== deleteFile =========================" + id);

        int result = fileService.delete(id);
        log.info("=========================== result =========================" + result);
        // 파일 삭제 성공
        if(result > 0){
            return "SUCCESS";
        }
        // 파일 삭제 실패
        return "FAIL";
    }

    // @PostMapping("/file/markDeleted/{id}") // 👈 POST 메서드로 변경하고 URL 의미도 변경
    // @ResponseBody
    // public ResponseEntity<String> markFileAsDeleted(@PathVariable("id") String id) throws Exception { // 메서드 이름도 변경하는 게 좋아
    //     log.info("=========================== markFileAsDeleted =========================" + id);

    //     // 여기 fileService.delete(id)는 이제 물리적 삭제가 아니라 'is_deleted = Y'로 업데이트하는 로직이어야 해!
    //     // 예를 들어: int result = fileService.markAsDeleted(id);
    //     int result = fileService.delete(id); // 현재 fileService.delete()가 이미 is_deleted를 변경한다고 가정

    //     log.info("=========================== result =========================" + result);

    //     // 파일 상태 변경 성공
    //     if (result > 0) {
    //         return ResponseEntity.ok("SUCCESS"); // HTTP 200 OK와 함께 "SUCCESS" 반환
    //     }
    //     // 파일 상태 변경 실패
    //     return ResponseEntity.status(400).body("FAIL"); // HTTP 400 Bad Request와 함께 "FAIL" 반환 (삭제 실패가 500은 아닐 수 있으니 400으로 변경)
    // }

    /**
     * 파일 목록
     * @param param - parentTable, parentNo
     * @return
     * @throws Exception
     */
    @GetMapping("/file")
    public String fileList(Model model, FilesVO file) throws Exception{
        List<FilesVO> fileList = fileService.listByParent(file);
        model.addAttribute("fileList", fileList);
        return "/file/list";
    }

    @GetMapping("/banner/img")
    public ResponseEntity<byte[]> bannerSelect(@RequestParam("id") Long id) throws Exception{
        log.info(":::::::::: FileController.thumbnail :::::::::: " + id);
        FilesVO file = fileService.bannerSelect(id);
        log.info("file :::::::::: " + file.toString());
        log.info(file.getFilePath() + " :::::::::: filePath ");

        String filePath = file.getFilePath();
        log.info(file.getFilePath() + " :::::::::: filePath ");
        // 파일 객체 생성
        File f = new File(filePath);
        // 파일 데이터
        byte[] fileData = FileCopyUtils.copyToByteArray(f);
        
        // 컨텐츠 파일 지정
        // 확장자로 컨텐츠 타입 지정
        // - 확장자 : .jpg, .png ...
        String ext = filePath.substring(filePath.lastIndexOf(".") + 1); // 확장자
        MediaType mediaType = MediaUtil.getMediaType(ext);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);

        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }

    private final String uploadDir = "src/main/resources/static/images/";
    // private final String uploadDir = "Users/pieck/Documents/upload/images";

    @PostMapping("/upload/image") // CKEditor의 uploadUrl과 일치시켜야 해!
    @ResponseBody // Map 객체를 JSON 형태로 반환하기 위함
    public Map<String, Object> uploadImage(@RequestParam("upload") MultipartFile file) {
        System.out.println("업로드 API 호출됨!");

        log.info("upload/image uploadImage");
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("uploaded", false);
            response.put("error", Map.of("message", "파일이 비어있습니다."));
            return response;
        }

        try {
            // 업로드 디렉토리가 없으면 생성
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 고유한 파일 이름 생성 (중복 방지)
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uuidFileName = UUID.randomUUID().toString() + fileExtension; // UUID로 고유한 이름 생성
            Path filePath = Paths.get(uploadDir + uuidFileName);

            // 파일 저장
            Files.copy(file.getInputStream(), filePath);

            // CKEditor가 요구하는 JSON 형식으로 응답 반환
            // 여기서는 웹에서 접근할 URL을 "/images/" 뒤에 파일 이름 붙여서 만들어.
            // Thymeleaf 등에서 static 폴더는 보통 /로 접근 가능해.
            String fileUrl = "/admin/images/" + uuidFileName; 
            
            response.put("uploaded", true);
            response.put("url", fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("uploaded", false);
            response.put("error", Map.of("message", "파일 저장 중 오류가 발생했습니다: " + e.getMessage()));
        }
        return response;
    }
}
