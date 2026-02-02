package com.safeg.admin.service;

import java.util.List;

import com.safeg.admin.vo.BannerVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;

public interface BannerService {

    public List<BannerVO> bannerList(Option option, Page page) throws Exception;

    public BannerVO bannerSelect(Long id) throws Exception;

    public int bannerInsert(BannerVO bannerVO) throws Exception;

    public int bannerUpdate(BannerVO bannerVO) throws Exception;

    int count(Option option) throws Exception;

}
