package com.safeg.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.admin.vo.BannerVO;
import com.safeg.admin.vo.FilesVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;

@Mapper
public interface BannerMapper {

    public List<BannerVO> bannerList(@Param("option") Option option, @Param("page") Page page) throws Exception;

    public BannerVO bannerSelect(@Param("id") Long id) throws Exception;

    public int bannerInsert(BannerVO bannerVO) throws Exception;

    public void bannerUpload(@Param("bannerVO") BannerVO bannerVO, @Param("id") String id) throws Exception;

    public int bannerUpdate(BannerVO bannerVO) throws Exception;

    public int count(Option option) throws Exception;

	public int bannerOldRemove() throws Exception;

    
}
