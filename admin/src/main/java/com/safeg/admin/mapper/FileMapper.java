package com.safeg.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.admin.vo.FilesVO;

@Mapper
public interface FileMapper {
    
    public List<FilesVO> list() throws Exception;

    public FilesVO select(@Param("id") String id, @Param("status") String status) throws Exception;

    public int insert(FilesVO file) throws Exception;

    public int update(FilesVO file) throws Exception;

    public int delete(String id) throws Exception;
    
    // 부모 테이블 기준 파일 목록
    public List<FilesVO> listByParent(FilesVO file) throws Exception;
    
    // 부모 테이블 기준 파일 삭제
    public int deleteByParent(FilesVO file) throws Exception;

    public int bannerInsert(FilesVO file) throws Exception;

    public FilesVO bannerSelect(Long id) throws Exception;
    // public int bannerUpload(@Param("file") Files uploadFile) throws Exception;
    public int bannerUpdate(FilesVO file) throws Exception;

    public int bannerRemoveFile(String id) throws Exception;
    
}
