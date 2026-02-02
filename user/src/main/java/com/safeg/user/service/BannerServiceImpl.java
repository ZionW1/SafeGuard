package com.safeg.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safeg.user.vo.BannerVO;
import com.safeg.user.mapper.BannerMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BannerServiceImpl implements BannerService{
    
    @Autowired
    BannerMapper bannerMapper;

    public List<BannerVO> bannerList() throws Exception {
        List<BannerVO> bannerList = bannerMapper.bannerList();
        log.info("bannerList + " + bannerList);

        return bannerList;
    }

}
