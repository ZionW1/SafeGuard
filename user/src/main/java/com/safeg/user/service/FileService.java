package com.safeg.user.service;

import java.util.List;

import org.springframework.core.io.Resource;

import com.safeg.user.vo.FilesVO;
import com.safeg.user.vo.UserCampaignVO;

public interface FileService {

    public FilesVO select(String id) throws Exception;

    public FilesVO getMypageImage(String id, String targetType) throws Exception;

    public int insert(FilesVO file) throws Exception;

    public int update(FilesVO file) throws Exception;

    public int delete(String id) throws Exception;

    // 파일 업로드
    public boolean upload(FilesVO uploadFile) throws Exception;

    // 부모 테이블 기준 파일 목록
    public List<FilesVO> listByParent(FilesVO file) throws Exception;

    // 부모 테이블 기준 파일 삭제
    public int deleteByParent(FilesVO file) throws Exception;

    // 배너 사진
    public FilesVO bannerFile(String id) throws Exception;

    // 프로필 사진 다운 (zip)
    public Resource createZipFile(Long userNos) throws Exception;

    public byte[] ApplicationExcel(List<UserCampaignVO> applications, String finalCampaignName) throws Exception;

}
