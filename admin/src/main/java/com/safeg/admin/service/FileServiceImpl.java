package com.safeg.admin.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.safeg.admin.mapper.FileMapper;
import com.safeg.admin.vo.CommonData;
import com.safeg.admin.vo.FilesVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileServiceImpl implements FileService{
    
    @Autowired
    private FileMapper fileMapper;
    
    @Value("${upload.path}") // application.properties 에서 지정한 업로드 경로 가져옴
    private String uploadPath;

    // String uploadPath = CommonData.getUploadPath();

    @Override
    public List<FilesVO> list() throws Exception{
        List<FilesVO> fileList = fileMapper.list();
        return fileList;
    }

    @Override
    public FilesVO select(String id) throws Exception{

        log.info("id + " + id);
        String status = "campaign";
        String status_id = id;
        FilesVO file = fileMapper.select(id, status);
        return file;
    }

    @Override
    public int insert(FilesVO file) throws Exception{
        int result = fileMapper.insert(file);
        return result;
    }

    @Override
    public int update(FilesVO file) throws Exception{
        int result = fileMapper.update(file);
        return result;
    }

    /**
     * 파일 삭제
     * 1. 파일 시스템의 파일 삭제
     * 2. DB 파일 정보 삭제
     */
    @Override
    public int delete(String id) throws Exception{
        

        // 1. 파일 시스템의 파일 삭제
        // Files file = select(id);
        // String filePath = file.getFilePath();

        // File deleteFile = new File(filePath);
        // // 파일 존재 여부 확인
        // if(!deleteFile.exists()){
        //     log.info("파일이 존재하지 않습니다.");
        //     log.info("filePath : " + filePath);
        //     return 0;
        // }
        // int result = 0;
        // if(deleteFile.delete()){
        //     log.info("[FS] 파일 삭제 성공");
        //     // 2. DB 파일 정보 삭제
        //     result = fileMapper.delete(id);
        //     log.info("[DB] 파일 정보 삭제 성공");
        // }

        int result = fileMapper.delete(id);
        return result;
    }

    @Override
    public boolean upload(FilesVO file) throws Exception {
        log.info("file + " + file);
    
        // 파일 정보 추출
        MultipartFile mf = file.getFile();
        String originalName = mf.getOriginalFilename();
        int lastDotIndex = originalName.lastIndexOf('.');
        String extension = originalName.substring(lastDotIndex + 1);
        long fileSize = mf.getSize();
        byte[] fileData = mf.getBytes();
    
        log.info("원본 파일 명 : " + originalName);
        log.info("파일 용량 : " + fileSize);
        log.info("파일 데이터 : " + fileData);
    
        // [수정 1] 메서드 안에서 OS에 맞는 업로드 베이스 경로를 가져옵니다.
        String uploadPath = CommonData.getUploadPath();
        log.info("파일 업로드 경로 : " + uploadPath);
    
        // [수정 2] 업로드할 폴더가 없으면 자동으로 생성하는 로직 추가
        File targetDir = new File(uploadPath);
        if (!targetDir.exists()) {
            log.info("업로드 폴더가 존재하지 않아 새로 생성합니다: " + uploadPath);
            targetDir.mkdirs(); // 하위 디렉토리까지 한 번에 생성
        }
    
        // 1. 파일 복사
        // 파일명 중복 방지 : UUID 활용
        String fileName = UUID.randomUUID().toString() + "_" + originalName;
        File uploadFile = new File(uploadPath, fileName);
        
        // FileCopyUtils.copy(파일데이터, 파일객체) -> 실제 파일 업로드 실행
        FileCopyUtils.copy(fileData, uploadFile); 
        log.info("파일 업로드 완료: " + uploadFile.getAbsolutePath());
    
        // 2. DB 등록을 위한 데이터 세팅
        file.setImage(fileName);
        file.setFilePath(uploadFile.getPath());
        file.setFileSize(fileSize);
        file.setFileExtension(extension);
        file.setOriginalName(originalName);
        file.setSavedName(fileName);
    
        log.info("insert 전 file + " + file);
    
        // MyBatis를 통한 DB 인서트
        fileMapper.insert(file);
        
        return true;
    }

    @Override
    public List<FilesVO> listByParent(FilesVO file) throws Exception {
        List<FilesVO> fileList = fileMapper.listByParent(file);
        return fileList;
    }

    @Override
    public int deleteByParent(FilesVO file) throws Exception {
        List<FilesVO> deleteFileList = fileMapper.listByParent(file);

        // 파일 시스템 파일 삭제
        for(FilesVO f : deleteFileList){
            File deleteFile = new File(f.getFilePath());
            if(deleteFile.exists()){
                deleteFile.delete();
                // delete(f.getId());
            }
        }
        // 첨부된 파일 전체 한번에 삭제
        int result = fileMapper.deleteByParent(file);
        log.info(result + "건의 파일 정보가 삭제 되었습니다.");
        
        return result;
    }

    @Override
    public boolean bannerUpload(FilesVO file) throws Exception {
        // TODO Auto-generated method stub
        log.info("file + " + file);

        // 파일 정보
        MultipartFile mf = file.getFile();
        String originalName = mf.getOriginalFilename();
        int lastDotIndex = originalName.lastIndexOf('.');
        String extension = originalName.substring(lastDotIndex + 1);
        long fileSize = mf.getSize();
        byte[] fileData = mf.getBytes();
        String uploadPath = CommonData.getUploadPath();

        log.info("원본 파일 명 : " + originalName);
        log.info("파일 용량 : " + fileSize);
        log.info("파일 데이터 : " + fileData);

        log.info("파일 업로드 경로 : " + uploadPath);

        // ⭐️ 파일 업로드
        // 1. 파일 데이터를 업로드 경로에 복사
        // 2. 업로드된 파일 정보를 DB에 등록

        // 1. 파일복사
        // 파일명 중복 방지 : 파일명 뒤에 날짜데이터 또는 UID를 붙여준다.
        String fileName = UUID.randomUUID().toString() + "_" + originalName;
        File uploadFile = new File(uploadPath, fileName);
        // 파일 경로 : /Users/pieck/Documents/upload/UID_파일명.png
        // FileCopyUtils.copy(파일데이터, 파일객체)
        FileCopyUtils.copy(fileData, uploadFile); // 파일 업로드

        // 2. DB 등록
        file.setImage(fileName);
        file.setFilePath(uploadFile.getPath());
        file.setFileSize(fileSize);
        file.setFileExtension(extension);
        file.setOriginalName(originalName);
        file.setSavedName(fileName);

        fileMapper.bannerInsert(file);
        
        return true;
    }

    public FilesVO bannerSelect(Long id) throws Exception {
        // TODO Auto-generated method stub
        FilesVO fileBanner = new FilesVO();
        // fileBanner.setStatus("BANNER");

        fileBanner.setTargetType("banner");
        fileBanner.setTargetId(id);

        fileBanner = fileMapper.bannerSelect(id);
        return fileBanner;
    }

    @Override
    public boolean bannerUpdate(FilesVO file) throws Exception {
        // TODO Auto-generated method stub
        log.info("file + " + file);

        // 파일 정보
        MultipartFile mf = file.getFile();
        String originalName = mf.getOriginalFilename();
        int lastDotIndex = originalName.lastIndexOf('.');
        String extension = originalName.substring(lastDotIndex + 1);
        long fileSize = mf.getSize();
        byte[] fileData = mf.getBytes();
        String uploadPath = CommonData.getUploadPath();

        log.info("원본 파일 명 : " + originalName);
        log.info("파일 용량 : " + fileSize);
        log.info("파일 데이터 : " + fileData);

        log.info("파일 업로드 경로 : " + uploadPath);

        // ⭐️ 파일 업로드
        // 1. 파일 데이터를 업로드 경로에 복사
        // 2. 업로드된 파일 정보를 DB에 등록

        // 1. 파일복사
        // 파일명 중복 방지 : 파일명 뒤에 날짜데이터 또는 UID를 붙여준다.
        String fileName = UUID.randomUUID().toString() + "_" + originalName;
        File uploadFile = new File(uploadPath, fileName);
        // 파일 경로 : /Users/pieck/Documents/upload/UID_파일명.png
        // FileCopyUtils.copy(파일데이터, 파일객체)
        FileCopyUtils.copy(fileData, uploadFile); // 파일 업로드

        // 2. DB 등록
        // file.setImage(fileName);
        // file.setFilePath(uploadFile.getPath());
        // file.setFileSize(fileSize);

        file.setImage(fileName);
        file.setFilePath(uploadFile.getPath());
        file.setFileSize(fileSize);
        file.setFileExtension(extension);
        file.setOriginalName(originalName);
        file.setSavedName(fileName);

        fileMapper.bannerUpdate(file);
        
        return true;
    }

    @Override
    public int bannerRemoveFile(String id) throws Exception {
        // TODO Auto-generated method stub
        log.info("파일 삭제 서비스 impl : " + id);
        int result = fileMapper.bannerRemoveFile(id);

        return result;
    }

    public List<FilesVO> userImageFile(String id) throws Exception{
        log.info("userImageFile = " + id);
        List<FilesVO> image = fileMapper.userImageFile(id);
        return image;
    }

    @Override
    public FilesVO getMypageImage(String id, String targetType) throws Exception {
        log.info("id : " + id + " targetType : " + targetType);
        FilesVO file = fileMapper.getMypageImage(id, targetType);
        log.info("file : " + file);
        return file;
    }

    @Override
    public FilesVO getInfoImage(String id, String fileType, String targetType) throws Exception {
        log.info("id : " + id + " targetType : " + fileType + " targetType : " + targetType);
        FilesVO file = fileMapper.getInfoImage(id, fileType, targetType);
        log.info("file : " + file);
        return file;
    }
    
    @Override
    public int updateFileCampaign() throws Exception {
        int result = fileMapper.updateFileCampaign();

        return result;
    }
    
    // @Override
    // public Resource identificationFile(String fileType, String targetType) throws Exception {
    //     ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //     log.info("넘어온 값 : " + fileType + ", " + targetType);
    //     try (ZipOutputStream zos = new ZipOutputStream(baos)) {

    //         Set<Long> processedFileIds = new HashSet<>(); // 이미 추가된 fileId를 추적하여 중복 방지

    //         // 해당 userNo에 연결된 모든 fileId 목록을 조회
    //         // 캠페인 번호로 유저 번호 조회
    //         List<Long> fileIdsForUser = fileMapper.identFileList(); // 예시: 'PROFILE'

    //         for (Long fileId : fileIdsForUser) {
    //             // ⭐ 중복 파일 ID가 여러 번 조회되어도 ZIP에 한 번만 추가하도록 방지 ⭐
    //             if (!processedFileIds.add(fileId)) {
    //                 log.warn("File ID {} 는 이미 ZIP에 추가되었거나 중복 조회되었습니다. 건너뜝니다.", fileId);
    //                 continue; // 이미 처리했으면 건너뜀
    //             }
    //             FilesVO fileInfo ;
    //             fileInfo = getInfoImage(String.valueOf(fileId), fileType, targetType); // 단일 파일 정보 조회

                
    //             if (fileInfo == null) {
    //                 log.warn("File ID {} 에 대한 파일 정보를 찾을 수 없습니다. ZIP에서 제외합니다.", fileId);
    //                 continue;
    //             }

    //             Path filePath = Paths.get(fileInfo.getFilePath()); // FilesVO.getFilePath()는 full path여야 함
    //             if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
    //                 log.warn("경로 {} 의 파일 {} 을(를) 찾을 수 없거나 읽을 수 없습니다. ZIP에서 제외합니다.",
    //                             filePath.toString(), fileInfo.getOriginalName());
    //                 continue;
    //             }

    //             // ZIP Entry 생성 (ZIP 파일 내에서 보일 이름)
    //             // fileInfo.getOriginalName()으로 사용
    //             ZipEntry zipEntry = new ZipEntry(fileInfo.getOriginalName());
    //             zos.putNextEntry(zipEntry);

    //             // 실제 파일 데이터를 ZIP에 씀
    //             Files.copy(filePath, zos);
    //             zos.closeEntry();
    //             log.info("파일 '{}' (fileId: {}) ZIP에 추가 완료.", fileInfo.getOriginalName(), fileId);
    //         }
            
    //     } // zos.close()는 try-with-resources에 의해 자동으로 호출됨

    //     return new ByteArrayResource(baos.toByteArray()); // 생성된 ZIP 데이터를 Resource로 반환
    // }

    @Override
    public Resource identificationFile(String fileType, String targetType) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        log.info("넘어온 값 : " + fileType + ", " + targetType);
        
        // 1. 압축 스트림 열기
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            Set<Long> processedFileIds = new HashSet<>(); 

            // 🚨 중요: 나중에 꼭 파라미터를 넘겨서 해당 캠페인 것만 조회하도록 mapper 메서드를 수정하시는 걸 추천합니다!
            List<Long> fileIdsForUser = fileMapper.identFileList(fileType, targetType); 

            for(int i = 0; i < fileIdsForUser.size(); i++) {
                log.info("fileIdsForUser " + i + " : "+ fileIdsForUser.get(i));
            }
            for (Long fileId : fileIdsForUser) {
                log.info("fileIdA : " + fileId);
                if (!processedFileIds.add(fileId)) {
                    log.warn("File ID {} 는 이미 ZIP에 추가되었거나 중복 조회되었습니다. 건너뜝니다.", fileId);
                    continue; 
                }
                
                FilesVO fileInfo = getInfoImage(String.valueOf(fileId), fileType, targetType); 
                
                if (fileInfo == null) {
                    log.warn("File ID {} 에 대한 파일 정보를 찾을 수 없습니다. ZIP에서 제외합니다.", fileId);
                    continue;
                }

                Path filePath = Paths.get(fileInfo.getFilePath()); 
                if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                    log.warn("경로 {} 의 파일 {} 을(를) 찾을 수 없거나 읽을 수 없습니다. ZIP에서 제외합니다.",
                                filePath.toString(), fileInfo.getOriginalName());
                    continue;
                }

                // ✨ [버그 수정 1] 압축 파일 내 파일명 중복을 방지하기 위해 파일ID를 접두사로 붙임
                String safeFileName = fileId + "_" + fileInfo.getOriginalName();
                log.info("safeFileName : " + safeFileName);
                ZipEntry zipEntry = new ZipEntry(safeFileName);
                zos.putNextEntry(zipEntry);

                // 실제 파일 데이터를 ZIP에 복사
                Files.copy(filePath, zos);
                zos.closeEntry();
                log.info("파일 '{}' (fileId: {}) ZIP에 추가 완료.", safeFileName, fileId);
            }
            
            // ✨ 이 블록이 끝나면서 zos.close()가 호출되어 완벽한 ZIP 포맷 파일이 완성됩니다.
        } 

        // ✨ [버그 수정 2] zos가 확실하게 close 완료된 '이 위치'에서 데이터를 꺼내 반환해야 안 깨집니다!
        return new ByteArrayResource(baos.toByteArray()); 
    }

}
