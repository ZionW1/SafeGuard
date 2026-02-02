package com.safeg.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.safeg.admin.vo.BannerVO;
import com.safeg.admin.vo.FilesVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.mapper.BannerMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BannerServiceImpl implements BannerService{
    
    @Autowired
    private BannerMapper bannerMapper;

    @Autowired 
    FileService fileService;

    @Override
    public List<BannerVO> bannerList(Option option, Page page) throws Exception {
        // TODO Auto-generated method stub
        log.info(":::::::::: BannerServiceImpl.bannerList() ::::::::::");
        int total = count(option);
        if(total > 3){
            // page.setCount(total);
        }
        List<BannerVO> bannerList = bannerMapper.bannerList(option, page);
        
        return bannerList;
    }

    @Override
    public int count(Option option) throws Exception {
        return bannerMapper.count(option);
    }

    @Override
    public BannerVO bannerSelect(Long id) throws Exception {
        // TODO Auto-generated method stub
        BannerVO bannerSelect = bannerMapper.bannerSelect(id);
        return bannerSelect;
    }


    @Override
    public int bannerUpdate(BannerVO bannerVO) throws Exception {
        // TODO Auto-generated method stub
        log.info("수정 처리 impl : " + bannerVO);
        int result = bannerMapper.bannerUpdate(bannerVO);
        log.info("수정 처리 후 : " + bannerVO.getFile());

        MultipartFile file = bannerVO.getFile();
        
        // uploadFile.setStatusId(bannerVO.getId());
        // uploadFile.setId(bannerVO.getId());

        if(file != null){
            FilesVO uploadFile = new FilesVO();

            uploadFile.setFile(file);
            uploadFile.setFileSize(file.getSize());
            uploadFile.setStatus("BANNER");

            uploadFile.setFile(file);
            uploadFile.setFileSize(file.getSize());
            uploadFile.setFileType("banner_File");
            uploadFile.setTargetType("banner");
            uploadFile.setTargetId(bannerVO.getBannerId());
            uploadFile.setMimeType("");

            // uploadFile.setId(bannerVO.getBannerId());
            // uploadFile.setStatusId(bannerVO.getBannerId());
            // uploadFile.setStatus("banner");

            log.info(null+":::::::::: banner update file :::::::::: bannerVO " + bannerVO);
            log.info(null+":::::::::: banner update file :::::::::: uploadFile " + uploadFile);
            // fileService.bannerFileRemove(uploadFile);
            fileService.bannerUpload(uploadFile);
        }

        return result;
    }

    @Override
    public int bannerInsert(BannerVO bannerVO) throws Exception {
        // TODO Auto-generated method stub
        log.info("등록 처리 impl : " + bannerVO);
        int result = bannerMapper.bannerInsert(bannerVO);

        MultipartFile file = bannerVO.getFile();

        if(result > 0 && file != null){
            FilesVO uploadFile = new FilesVO();
            // uploadFile.setFile(file);
            // uploadFile.setFileSize(file.getSize());
            // uploadFile.setStatus("BANNER");
            // // uploadFile.setStatusId(bannerVO.getId());
            // fileService.bannerUpload(uploadFile);

            uploadFile.setFile(file);
            uploadFile.setFileSize(file.getSize());
            uploadFile.setFileType("banner_File");
            uploadFile.setTargetType("banner");
            uploadFile.setTargetId(bannerVO.getBannerId());
            uploadFile.setMimeType("");

            uploadFile.setId(bannerVO.getBannerId());
            uploadFile.setStatusId(bannerVO.getBannerId());
            uploadFile.setStatus("banner");
            log.info("등록 처리 uploadFile : " + uploadFile);
            fileService.bannerUpload(uploadFile);
        }
        int total = count(new Option());
        log.info(":::::::::: banner insert total :::::::::: " + total);

        if(total > 3){
            int oldRemove = bannerMapper.bannerOldRemove();
            log.info(":::::::::: banner insert oldRemove :::::::::: " + oldRemove);
        }
        return result;
    }




    // @Override
    // public CampaignVO campaignSelect(String id) throws Exception {
    //     // TODO Auto-generated method stub
    //     log.info("campaignSelectDetail");
    //     CampaignVO campaignDetail = campaignsMapper.campaignSelect(id);
    //     return campaignDetail;
    // }
}