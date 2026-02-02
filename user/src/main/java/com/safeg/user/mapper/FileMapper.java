package com.safeg.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.user.vo.FilesVO;

@Mapper
public interface FileMapper {
    
    public List<FilesVO> list() throws Exception;

    public FilesVO select(String id) throws Exception;

    public int insert(FilesVO file) throws Exception;

    public int update(FilesVO file) throws Exception;

    public int delete(String id) throws Exception;
    
    // 부모 테이블 기준 파일 목록
    public List<FilesVO> listByParent(FilesVO file) throws Exception;
    
    // 부모 테이블 기준 파일 삭제
    public int deleteByParent(FilesVO file) throws Exception;

    public FilesVO bannerFile(String id) throws Exception;

    public FilesVO getMypageImage(@Param("id") String id, @Param("targetType") String targetType) throws Exception;

    public List<Long> userList(@Param("id") Long campaignId) throws Exception;
}
