package com.safeg.admin.controller;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.safeg.admin.vo.CommonData;
import com.safeg.admin.vo.FilesVO;
import com.safeg.admin.mapper.MediaUtil;
import com.safeg.admin.service.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@Controller
public class FileController {

    private final FileService fileService;

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

    /**
     * 삭제
     * @param id
     * @return
     * @throws Exception
     */
    @ResponseBody
    @DeleteMapping("/file/{id}")
    // @PostMapping("/file/{id}") // 👈 POST로 변경하고 URL에 "delete" 추가
    public String deleteFile(@PathVariable("id") String id) throws Exception{
        int result = fileService.delete(id);

        // 파일 삭제 성공
        if(result > 0){
            return "SUCCESS";
        }
        // 파일 삭제 실패
        return "FAIL";
    }

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

    String uploadDir = CommonData.getUploadPath(); // 여기서 호출!    // private final String uploadDir = "Users/pieck/Documents/upload/images";

    /**
     * 배너 파일 목록
     * @param param - parentTable, parentNo
     * @return
     * @throws Exception
     */
    @GetMapping("/banner/img")
    public ResponseEntity<byte[]> bannerSelect(@RequestParam("id") Long id) throws Exception{
        FilesVO file = fileService.bannerSelect(id);
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

    // 배너 삭제 처리
    @PostMapping("/bannerRemoveFile")
    @ResponseBody
    public String bannerRemoveFile(@RequestParam("id") String id) throws Exception{
        int result = fileService.bannerRemoveFile(id);
        if(result > 0){
            return "SUCCESS";
        }
        return "FAIL";
    }

    /**
     * faq, leader, useGuide, notice 업로드
     * @param param - file
     * @return
     * @throws Exception
     */
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

    /**
     * 이미지 (확인)
     * @param param - id, args(유형)
     * @return
     * @throws Exception
     */
    @GetMapping("/selectProfile")
    public ResponseEntity<byte[]> selectProfile(@RequestParam("id") String id, @RequestParam("args") String args) throws Exception {
        FilesVO file ;

        if(args == null || args.equals("")) {
            // 증,프로필 아닐 때
            log.warn("Invalid args parameter for selectProfile: {}", args);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if(args.equals("2")) {
            // 주민등록증
            file = fileService.getMypageImage(id, "identification");
            if (file == null) {
                log.error("No identification image found for user id: {}", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                log.info("identification image found for user id: {}", id + " : identification");
            }
        } else if(args.equals("3")) {
            // 이수증
            file = fileService.getMypageImage(id, "certificate");
            if (file == null) {
                log.error("No certificate image found for user id: {}", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                log.info("certificate image found for user id: {}", id + " : certificate");
            }
        }else {
            // 프로필 사진
            file = fileService.getMypageImage(id, "profile");
            if (file == null) {
                log.error("No profile image found for user id: {}", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                log.info("Profile image found for user id: {}", id + " : profile");
            }
        }

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
     * 이미지 다운로드
     * @param param - FilesVO requestData
     * @return
     * @throws Exception
     */
    @PostMapping("/downloadImg")
    @ResponseBody
    public ResponseEntity<Resource> downloadImg(@RequestBody FilesVO requestData) {
        try {
            Resource identificationFile = fileService.identificationFile(requestData.getFileType(), requestData.getTargetType());

            // zipFileName = URLEncoder.encode("신분증_파일묶음.zip", StandardCharsets.UTF_8.toString()).replace("+", "%20");
            String zipFileName = URLEncoder.encode("신분증_파일묶음.zip", StandardCharsets.UTF_8.toString()).replace("+", "%20");
            // 1. 서버 컴퓨터에 파일이 실제로 저장되어 있는 절대 경로 설정 (본인 환경에 맞게 수정)
            // 예: Mac/Linux라면 "/Users/username/upload/" , Windows라면 "C:/upload/"
            // String uploadDir = "/Users/safeg/upload/";

            // // Path fileStorageLocation = Paths.get(uploadPath).toAbsolutePath().normalize();
            // Path targetPath = fileStorageLocation.resolve(filePath).normalize();

            // Resource resource = new UrlResource(targetPath.toUri());

            // // // 2. 파일이 진짜 존재하는지 확인
            // // if (!resource.exists()) {
            // //     return ResponseEntity.notFound().build();
            // // }

            // // // 3. 다운로드될 파일명 한글 깨짐 방지 처리
            // // String filename = resource.getFilename();
            // String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

            // // 4. 브라우저에게 "이건 화면에 띄우지 말고 '다운로드'해라"라고 명령 전달 (attachment)
            // return ResponseEntity.ok()
            //         .contentType(MediaType.APPLICATION_OCTET_STREAM)
            //         .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"")
            //         .body(resource);


            // return ResponseEntity.ok()
            //         .contentType(MediaType.parseMediaType("application/zip"))
            //         .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"")
            //         .body(identificationFile);
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                // ✨ 파일 크기를 브라우저에 정확히 알려주기 위해 contentLength 추가
                .contentLength(identificationFile.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"")
                .body(identificationFile);
        } catch (Exception e) {
            log.error("파일 다운로드 중 에러 발생: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
