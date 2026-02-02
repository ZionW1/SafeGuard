package com.safeg.user.service;

import java.util.List;

import com.safeg.user.vo.BannerVO;

public interface BannerService {

    public List<BannerVO> bannerList() throws Exception;
    
}
