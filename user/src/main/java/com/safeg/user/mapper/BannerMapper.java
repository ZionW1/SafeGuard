package com.safeg.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.safeg.user.vo.BannerVO;

@Mapper
public interface BannerMapper {

    List<BannerVO> bannerList() throws Exception;
    
}
