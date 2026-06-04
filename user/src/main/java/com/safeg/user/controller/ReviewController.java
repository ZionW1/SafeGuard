package com.safeg.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.safeg.user.service.ApplyService;
import com.safeg.user.service.CampaignService;
import com.safeg.user.service.FileService;
import com.safeg.user.service.ReviewService;
import com.safeg.user.vo.CampaignVO;
import com.safeg.user.vo.CustomUser;
import com.safeg.user.vo.FilesVO;
import com.safeg.user.vo.ReviewVO;
import com.safeg.user.vo.UserVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;

    @Autowired
    ApplyService applyService;

    @Autowired
    CampaignService campaignService;

    @Autowired
    FileService fileService;

    @GetMapping("/review01")
    public String reviewList() throws Exception {
        log.info("리뷰 리스트");

        reviewService.reviewList();

        return "review/review";
    }

    @GetMapping("/review02/{reviewId}")
    public String reviewSelectPage(@AuthenticationPrincipal CustomUser authUser, @PathVariable("reviewId") Long reviewId, ReviewVO reviewVO, BindingResult bindingResult, Model model) throws Exception {
        log.info("리뷰 상세페이지");

        ReviewVO reviewSelect = reviewService.reviewSelect(reviewId);

        model.addAttribute("review", reviewSelect);
        return "review/reviewSelect";
    }

    @GetMapping("/review03/{campaignId}")
    public String reviewInsertPage(@AuthenticationPrincipal CustomUser authUser, @PathVariable("campaignId") Long campaignId, ReviewVO reviewVO, BindingResult bindingResult, Model model) throws Exception {
        log.info("리뷰 등록 페이지");
        // reviewService.reviewList(campaignId);
        

        log.info("authUser.getUsername() : "+ authUser.getUsername());
        ReviewVO reviewInfo = reviewService.reviewInfo(campaignId);
        
        if (reviewInfo == null || authUser == null || !reviewInfo.getLeaderId().equals(authUser.getUsername())) {
            // 인솔자가 아니면 원래 목록 화면이나 에러 페이지로 튕겨버림
            return "redirect:/closedCampaign"; 
        }

        String campaignTitle = reviewInfo.getCampaignTitle();
        String placeAddr = reviewInfo.getPlaceAddr();
        String userId = reviewInfo.getLeaderId();
    
        // 2. [핵심 검증] 캠페인에 등록된 인솔자 ID와 현재 로그인한 유저 ID를 비교합니다.
        String leaderId = reviewInfo.getLeaderId(); // 캠페인 테이블에 저장된 인솔자 ID 컬럼명에 맞게 매칭
        String currentUserId = authUser.getUsername();
        
        // if (leaderId == null || !leaderId.equals(currentUserId)) {
        //     // 일치하지 않으면 진입을 막고 경고 페이지나 메인으로 리다이렉트
        //     model.addAttribute("errorMessage", "해당 캠페인의 담당 인솔자만 리뷰를 작성할 수 있습니다.");
        //     return "error/denied"; 
        // }
        model.addAttribute("userId", userId);
        model.addAttribute("campaignId", campaignId);
        model.addAttribute("campaignTitle", campaignTitle);
        model.addAttribute("placeAddr", placeAddr);

        return "review/reviewInsert";
    }

    @PostMapping("/review04")
    public ResponseEntity<?> reviewInsert(@AuthenticationPrincipal CustomUser authUser, ReviewVO reviewVO, BindingResult bindingResult, Model model) throws Exception {
        log.info("리뷰 등록 처리 : " + reviewVO.toString());
        MultipartFile file = reviewVO.getThumbnail();
        String savedFileName = "";
        // if (file == null || file.isEmpty()) {
        //     return ResponseEntity.badRequest().body("파일이 존재하지 않습니다.");
        // }

        FilesVO uploadFile = new FilesVO();
        if (file != null && !file.isEmpty()) {
            // FilesVO uploadFile = new FilesVO();
            uploadFile.setFile(file);
            uploadFile.setFileSize(file.getSize());
            uploadFile.setFileType("review_File");
            uploadFile.setTargetType("review_thumbnail");
            uploadFile.setTargetId(reviewVO.getCampaignId());
            uploadFile.setMimeType(file.getContentType());
            savedFileName = fileService.getFileName(uploadFile);

        } else {
            log.info("업로드된 파일이 없습니다. reviewVO.getCampaignId() : " + reviewVO.getCampaignId());
            uploadFile = fileService.select(String.valueOf(reviewVO.getCampaignId())); // DB에서 해당 캠페인 ID로 이미 업로드된 파일이 있는지 조회 (중복 방지용)
            log.info("uploadFile.toString : " + uploadFile.toString());
            savedFileName = uploadFile.getSavedName();
        }
        // 1. 파일 서비스에서 디스크 저장 및 DB 인서트를 처리합니다.
        // 💡 [팁] 파일 서비스 안에서 고유한 파일명(예: UUID)을 리턴하도록 구현하는 것이 좋습니다.
        // String savedFileName = fileService.getFileName(uploadFile);
        // String imageUrl = "/img?fileName=" + savedFileName;
        reviewVO.setThumbnailName(savedFileName);
        int result = reviewService.reviewInsert(reviewVO);
        if(result == 0){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 등록에 실패했습니다.");
        }
        return ResponseEntity.ok("Success");
    }

    // 이미지 업로드 후 이미지 확인
    @PostMapping("/review05")
    public ResponseEntity<?> uploadEditorImage(@RequestParam("image") MultipartFile file, @RequestParam("campaignId") Long campaignId) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("파일이 존재하지 않습니다.");
            }
            // 1. 파일 저장 로직
            // 2. DB Insert 로직 (여기서 테이블 없으면 catch로 튕김)
            FilesVO uploadFile = new FilesVO();
            if(file != null){
                // FilesVO uploadFile = new FilesVO();
                uploadFile.setFile(file);
                uploadFile.setFileSize(file.getSize());
                uploadFile.setFileType("review_File");
                uploadFile.setTargetType("review_content");
                uploadFile.setTargetId(campaignId);
                uploadFile.setMimeType(file.getContentType());
                
            }
            // 1. 파일 서비스에서 디스크 저장 및 DB 인서트를 처리합니다.
            // 💡 [팁] 파일 서비스 안에서 고유한 파일명(예: UUID)을 리턴하도록 구현하는 것이 좋습니다.
            String savedFileName = fileService.getFileName(uploadFile);
            String imageUrl = "/img?fileName=" + savedFileName;
            // img?id=${campaign.campaignId}
            // boolean imageUrl = fileService.upload(uploadFile); 
            return ResponseEntity.ok(imageUrl);
            
        } catch (Exception e) {
            // 💡 중요: 시큐리티가 가로채기 전에 자바 콘솔창에 진짜 에러 원인을 강제로 출력!
            System.out.println("====== [이미지 업로드 진짜 에러 원인] ======");
            e.printStackTrace(); 
            System.out.println("===========================================");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
