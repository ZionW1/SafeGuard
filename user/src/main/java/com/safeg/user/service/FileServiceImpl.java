package com.safeg.user.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter; // 날짜 포맷팅용


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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.safeg.user.mapper.FileMapper;
import com.safeg.user.vo.FilesVO;
import com.safeg.user.vo.UserCampaignVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileServiceImpl implements FileService{
    
    @Autowired
    private FileMapper fileMapper;
    
    @Value("${upload.path}") // application.properties 에서 지정한 업로드 경로 가져옴
    private String uploadPath;

    @Override
    public FilesVO select(String id) throws Exception{
        FilesVO file = fileMapper.select(id);
        return file;
    }

    @Override
    public FilesVO getMypageImage(String id, String targetType) throws Exception{
        log.info("id : " + id + " targetType : " + targetType);
        FilesVO file = fileMapper.getMypageImage(id, targetType);
        log.info("file : " + file);
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
        // FilesVO file = select(id);
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

    public FilesVO bannerFile(String id) throws Exception {
        // TODO Auto-generated method stub
        FilesVO file = fileMapper.bannerFile(id);
        return file;
    }

    @Override
    // ⭐⭐ 새로 추가되는 ZIP 파일 생성 메서드 ⭐⭐
    public Resource createZipFile(Long userNo) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {

            Set<Long> processedFileIds = new HashSet<>(); // 이미 추가된 fileId를 추적하여 중복 방지

            // 해당 userNo에 연결된 모든 fileId 목록을 조회
            // 캠페인 번호로 유저 번호 조회
            List<Long> fileIdsForUser = fileMapper.userList(userNo); // 예시: 'PROFILE'

            log.info("UserNo: {} 에 대해 조회된 fileIds: {}", userNo, fileIdsForUser);

            for (Long fileId : fileIdsForUser) {
                // ⭐ 중복 파일 ID가 여러 번 조회되어도 ZIP에 한 번만 추가하도록 방지 ⭐
                if (!processedFileIds.add(fileId)) {
                    log.warn("File ID {} 는 이미 ZIP에 추가되었거나 중복 조회되었습니다. 건너뜝니다.", fileId);
                    continue; // 이미 처리했으면 건너뜀
                }

                FilesVO fileInfo = getMypageImage(String.valueOf(fileId), "certificate"); // 단일 파일 정보 조회
                if (fileInfo == null) {
                    log.warn("File ID {} 에 대한 파일 정보를 찾을 수 없습니다. ZIP에서 제외합니다.", fileId);
                    continue;
                }

                Path filePath = Paths.get(fileInfo.getFilePath()); // FilesVO.getFilePath()는 full path여야 함
                if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                    log.warn("경로 {} 의 파일 {} 을(를) 찾을 수 없거나 읽을 수 없습니다. ZIP에서 제외합니다.",
                                filePath.toString(), fileInfo.getOriginalName());
                    continue;
                }

                // ZIP Entry 생성 (ZIP 파일 내에서 보일 이름)
                // fileInfo.getOriginalName()으로 사용
                ZipEntry zipEntry = new ZipEntry(fileInfo.getOriginalName());
                zos.putNextEntry(zipEntry);

                // 실제 파일 데이터를 ZIP에 씀
                Files.copy(filePath, zos);
                zos.closeEntry();
                log.info("파일 '{}' (fileId: {}) ZIP에 추가 완료.", fileInfo.getOriginalName(), fileId);
            }
            
        } // zos.close()는 try-with-resources에 의해 자동으로 호출됨

        return new ByteArrayResource(baos.toByteArray()); // 생성된 ZIP 데이터를 Resource로 반환
    }

    @Override
    public byte[] ApplicationExcel(List<UserCampaignVO> applications, String campaignName) throws Exception {
        log.info("엑셀 파일 생성 요청: 캠페인명 - {}", campaignName);
        log.info("applications - {}", applications);

        // 1. 새 워크북 생성 (.xlsx 형식)
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("캠페인 신청 목록"); // 시트 이름 설정

        // 2. 헤더 스타일 설정 (선택 사항이지만, 깔끔하게 보이도록)
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬

        // 3. 헤더 Row 생성 및 컬럼명 추가
        Row headerRow = sheet.createRow(0); // 첫 번째 줄 (0 인덱스)
        String[] headers = {"No.","캠페인명", "신청자 ID", "신청자명","상태"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // 4. 데이터 Row 생성
        int rowNum = 1; // 헤더 다음 줄부터 시작 (1 인덱스)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // 날짜 포맷터

        log.info("generateCampaignApplicationsExcel : ");

        for (UserCampaignVO app : applications) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;

            row.createCell(colNum++).setCellValue(rowNum - 1); // No.
            row.createCell(colNum++).setCellValue(campaignName); // 캠페인 이름
            row.createCell(colNum++).setCellValue(app.getUserId()); // 신청자 ID
            row.createCell(colNum++).setCellValue(app.getUserNm()); // 신청자명
            row.createCell(colNum++).setCellValue(app.getStatusNm()); // 상태

            // TODO: UserCampaignDto에 더 많은 정보가 있다면 여기에 추가해 줘!
            // 예시: row.createCell(colNum++).setCellValue(app.getPhoneNumber()); // 전화번호
        }

        // 5. 컬럼 너비 자동 조정 (선택 사항이지만 보기 좋게)
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 6. 워크북을 ByteArrayOutputStream에 써서 byte[]로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close(); // 워크북 사용 후 꼭 닫아줘야 메모리 누수를 방지할 수 있어.

        return outputStream.toByteArray();
    }
}
