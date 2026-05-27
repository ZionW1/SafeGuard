package com.safeg.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.safeg.user.vo.ReviewVO;

@Mapper
public interface ReviewMapper {

    public List<ReviewVO> reviewList() throws Exception;

    public ReviewVO reviewSelect(Long reviewId) throws Exception;

    public int reviewInsert(ReviewVO reviewVO) throws Exception;

    public ReviewVO reviewInfo(Long campaignId) throws Exception;

}
