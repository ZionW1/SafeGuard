package com.safeg.admin.service;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.safeg.admin.mapper.FileMapper;
import com.safeg.admin.vo.FilesVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileServiceImpl implements FileService{
    
    @Autowired
    private FileMapper fileMapper;
    
    @Value("${upload.path}") // application.properties 에서 지정한 업로드 경로 가져옴
    private String uploadPath;

    @Override
    public List<FilesVO> list() throws Exception{
        List<FilesVO> fileList = fileMapper.list();
        return fileList;
    }

    @Override
    public FilesVO select(String id) throws Exception{
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
    public boolean upload(FilesVO file) throws Exception{
        log.info("file + " + file);

        // 파일 정보
        MultipartFile mf = file.getFile();
        String originalName = mf.getOriginalFilename();
        int lastDotIndex = originalName.lastIndexOf('.');
        String extension = originalName.substring(lastDotIndex + 1);
        long fileSize = mf.getSize();
        byte[] fileData = mf.getBytes();

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

        log.info("insert 전 file + " + file);

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
    public int bannerFileRemove(FilesVO uploadFile) throws Exception {
        // TODO Auto-generated method stub
        log.info("파일 삭제 서비스 impl : " + uploadFile);
        int result = fileMapper.bannerFileRemove(uploadFile);

        return result;
    }
}
