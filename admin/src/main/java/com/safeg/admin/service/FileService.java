package com.safeg.admin.service;

import java.util.List;

import com.safeg.admin.vo.FilesVO;

public interface FileService {
    
    public List<FilesVO> list() throws Exception;

    public FilesVO select(String id) throws Exception;

    public int insert(FilesVO file) throws Exception;

    public int update(FilesVO file) throws Exception;

    public int delete(String id) throws Exception;

    // 파일 업로드
    public boolean upload(FilesVO uploadFile) throws Exception;
    
    // 부모 테이블 기준 파일 목록
    public List<FilesVO> listByParent(FilesVO file) throws Exception;

    
    // 부모 테이블 기준 파일 삭제
    public int deleteByParent(FilesVO file) throws Exception;

    public FilesVO bannerSelect(Long id) throws Exception;

    public boolean bannerUpload(FilesVO uploadFile) throws Exception;
    public int bannerRemoveFile(String id) throws Exception;

    public boolean bannerUpdate(FilesVO file) throws Exception;


}
