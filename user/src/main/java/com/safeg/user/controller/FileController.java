package com.safeg.user.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.safeg.user.mapper.MediaUtil;
import com.safeg.user.service.ApplyService;
import com.safeg.user.service.FileService;
import com.safeg.user.service.FileServiceImpl;
import com.safeg.user.vo.CampaignVO;
import com.safeg.user.vo.FilesVO;
import com.safeg.user.vo.UserCampaignVO;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    ApplyService applyService;

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

    @GetMapping("/selectProfile")
    public ResponseEntity<byte[]> selectProfile(@RequestParam("id") String id, @RequestParam("args") String args) throws Exception{
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
    @GetMapping("/fileImg/{id}")
    public ResponseEntity<byte[]> download(@PathVariable("id") String id) throws Exception{
        FilesVO file = fileService.select(id);

        String filePath = file.getFilePath();
        String fileName = file.getSavedName();
        // String fileName = file.getFileName();
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

    @GetMapping("/file/{id}") // 엔드포인트 변경
    @ResponseBody
    public ResponseEntity<byte[]> downloadSingleFile(@PathVariable("id") Long id) throws Exception {
        log.info("downloadSingleFile 호출 - fileId: {}", id);

        FilesVO fileInfo = fileService.select(String.valueOf(id)); // selectProfile 대신 select(Long id)를 사용하는 것을 가정

        if (fileInfo == null) {
            log.warn("다운로드할 파일 정보(fileId: {})를 DB에서 찾을 수 없습니다.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        File f = new File(fileInfo.getFilePath()); // FilesVO의 filePath가 이미 Full Path라고 가정

        if (!f.exists() || !f.isFile() || !f.canRead()) {
            log.error("파일 시스템에서 파일(경로: {})을 찾거나 읽을 수 없습니다.", f.getAbsolutePath());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        byte[] fileData = FileCopyUtils.copyToByteArray(f);
        HttpHeaders headers = new HttpHeaders();

        String contentTypeHeader;
        if (fileInfo.getMimeType() != null && !fileInfo.getMimeType().isEmpty()) {
            contentTypeHeader = fileInfo.getMimeType();
        } else {
            contentTypeHeader = getContentType(fileInfo.getOriginalName());
        }
        headers.setContentType(MediaType.parseMediaType(contentTypeHeader));

        String encodedFileName = URLEncoder.encode(fileInfo.getOriginalName(), StandardCharsets.UTF_8.toString()).replace("+", "%20");
        headers.setContentDispositionFormData("attachment", encodedFileName);
        headers.setContentLength(fileData.length); // 중요!
        
        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }
    
    // 이 헬퍼 메서드는 기존과 동일하게 유지
    private String getContentType(String fileName) {
        String fileExtension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            fileExtension = fileName.substring(i+1);
        }
        return switch (fileExtension.toLowerCase()) {
            case "pdf" -> "application/pdf"; case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png"; case "gif" -> "image/gif";
            case "txt" -> "text/plain"; case "zip" -> "application/zip";
            default -> "application/octet-stream";
        };
    }

    @PostMapping("/file/imgDownload") // 클라이언트에서 이 URL로 요청할 거야.
    @ResponseBody
    public ResponseEntity<Resource> downloadCampaignFilesAsZip(@RequestBody CampaignVO requestData) { // ⭐ 단일 Long campaignId를 받음 ⭐
        log.info("downloadCampaignFilesAsZip " + requestData.getCampaignId());
        if (requestData.getCampaignId() == null || requestData.getCampaignId() <= 0) {
            log.warn("다운로드할 유효한 campaignId가 전달되지 않았습니다.");
            return ResponseEntity.badRequest().body(null);
        }

        try {
            // fileService를 통해 해당 campaignId에 연결된 모든 파일을 ZIP으로 압축한 Resource를 반환받음
            Resource zipFileResource = fileService.createZipFile(requestData.getCampaignId());

            String zipFileName = URLEncoder.encode(requestData.getCampaignTitle() + "_" + requestData.getCampaignId() + "_파일묶음.zip", StandardCharsets.UTF_8.toString()).replace("+", "%20");

            log.info("캠페인 {} ZIP 파일 다운로드 요청 처리 - 생성된 ZIP 파일: {}", requestData.getCampaignId(), zipFileResource.getFilename());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"")
                    .body(zipFileResource);

        } catch (IOException e) {
            log.error("캠페인 {} ZIP 파일 생성 또는 읽기 중 오류 발생: {}", requestData.getCampaignId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            log.error("캠페인 {} 관련 ZIP 처리 중 예상치 못한 오류 발생: {}", requestData.getCampaignId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @ResponseBody
    @DeleteMapping("/file/{id}")
    public String deleteFile(@PathVariable("id") String id) throws Exception{
        log.info(":::::::::: FileController.deleteFile :::::::::: " + id);
        int result = fileService.delete(id);
        log.info("=========================== result =========================" + result);
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

    /**
     * 이미지 썸네일
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping("/bannerFile")
    public ResponseEntity<byte[]> bannerFile(@RequestParam("id") String id) throws Exception{
        log.info(":::::::::: FileController.thumbnail :::::::::: " + id);
        FilesVO file = fileService.bannerFile(id);
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

    @GetMapping("/file/excelDownload/{id}/{applyDate}")
    public ResponseEntity<byte[]> downloadCampaignApplicationsExcel(@PathVariable("id") String campaignId, @PathVariable("applyDate") LocalDate applyDate) throws Exception {
        log.info("downloadCampaignApplicationsExcel " + campaignId);
        // 1. DB에서 해당 캠페인의 모든 신청 정보 조회
        // List<UserCampaignVO> applications = userCampaignApplyService.getAllApplicationsForCampaign(campaignId);
        List<UserCampaignVO> applications = applyService.userCampaignApply(campaignId, applyDate);


        if (applications.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 신청 정보가 없으면 Not Found
        }

        // 2. 캠페인 이름 조회
        // String campaignName = userCampaignService.getCampaignNameById(campaignId);
        String campaignName = applications.get(0).getCampaignTitle();
        String finalCampaignName = (campaignName != null) ? campaignName : "캠페인";

        // 3. 엑셀 파일 생성
        byte[] excelBytes = fileService.ApplicationExcel(applications, finalCampaignName);

        // 4. HTTP 응답 헤더 설정 (다운로드하도록 유도)
        HttpHeaders headers = new HttpHeaders();
        // 파일명에 캠페인 이름과 ID를 포함하고 한글이 깨지지 않도록 인코딩
        String fileName = URLEncoder.encode(finalCampaignName + "_신청_목록_" + campaignId + ".xlsx", StandardCharsets.UTF_8);

        // 엑셀 파일 (xlsx) MIME 타입 설정
        headers.setContentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        headers.setContentLength(excelBytes.length);

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}
