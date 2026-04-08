package com.safeg.admin.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.safeg.admin.vo.CommonData;
import com.safeg.admin.vo.FilesVO;
import com.safeg.admin.mapper.MediaUtil;
import com.safeg.admin.service.FileService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
public class FileController {

    @Autowired
    private FileService fileService;
    
    @Value("${upload.path}")
    private String uploadPath;
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

    String uploadDir = CommonData.getUploadPath(); // 여기서 호출!    // private final String uploadDir = "Users/pieck/Documents/upload/images";

    @PostMapping("/upload/image")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("upload") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file == null || file.isEmpty()) {
            response.put("uploaded", false);
            response.put("error", Map.of("message", "파일이 비어있습니다."));
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 1. 폴더 생성 확인
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 2. 고유한 파일명 생성
            String originalFileName = file.getOriginalFilename();
            String fileExtension = (originalFileName != null && originalFileName.contains(".")) 
                                    ? originalFileName.substring(originalFileName.lastIndexOf(".")) 
                                    : "";
            String uuidFileName = UUID.randomUUID().toString() + fileExtension;
            
            // 3. 파일 저장 (단 한 번만 수행!)
            Path targetPath = Paths.get(uploadDir).resolve(uuidFileName);
            
            // transferTo는 가장 효율적인 파일 저장 방식입니다.
            file.transferTo(targetPath.toFile());

            log.info("파일 업로드 성공: " + targetPath.toString());

            // 4. 성공 응답 구성
            // WebConfig에서 /images/** 를 /Users/pieck/Documents/upload/ 로 매핑했으므로 아래 경로가 맞습니다.
            String fileUrl = "/admin/images/" + uuidFileName; 
            
            response.put("uploaded", true);
            response.put("url", fileUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("파일 업로드 중 오류 발생", e);
            response.put("uploaded", false);
            response.put("error", Map.of("message", "서버 오류: " + e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 삭제 처리
    @PostMapping("/bannerRemoveFile")
    @ResponseBody
    public String bannerRemoveFile(@RequestParam("id") String id) throws Exception{

        log.info("=========================== deleteFile ========================= " + id);
        
        log.info("삭제 처리 : " + id);
        int result = fileService.bannerRemoveFile(id);
        log.info("=========================== result ========================= " + result);
        if(result > 0){
            return "SUCCESS";
        }
        return "FAIL";
    }

    @GetMapping("/selectProfile")
    public ResponseEntity<byte[]> selectProfile(@RequestParam("id") String id, @RequestParam("args") String args) throws Exception {
        log.info("selectProfile id " + id);
        log.info("args : " + args);
        FilesVO file ;
        if(args == null || args.equals("")) {
            log.warn("Invalid args parameter for selectProfile: {}", args);
            log.info("Invalid args parameter for selectProfile: {}", args);
            
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if(args.equals("2")) {
            file = fileService.getMypageImage(id, "identification");
            if (file == null) {
                log.error("No profile image found for user id: {}", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                log.info("Profile image found for user id: {}", id + " : identification");
            }
        } else if(args.equals("3")) {
            file = fileService.getMypageImage(id, "certificate");
            if (file == null) {
                log.error("No profile image found for user id: {}", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                log.info("Profile image found for user id: {}", id + " : certificate");
            }
        }else {
            file = fileService.getMypageImage(id, "profile");
            if (file == null) {
                log.error("No profile image found for user id: {}", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                log.info("Profile image found for user id: {}", id + " : profile");
            }
        }
        // FilesVO file = fileService.getMypageImage(id, "profile");

        String filePath = file.getFilePath();
        log.info("filePath : " + filePath);
        // 파일 객체 생성
        File f = new File(filePath);
        // 파일 데이터
        byte[] fileData = FileCopyUtils.copyToByteArray(f);
        
        // 컨텐츠 파일 지정
        // 확장자로 컨텐츠 타입 지정
        // - 확장자 : .jpg, .png ...
        String ext = filePath.substring(filePath.lastIndexOf(".") + 1); // 확장자
        MediaType mediaType = MediaUtil.getMediaType(ext);
        log.info("Detected MediaType: " + mediaType);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);

        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }
}
