package com.safeg.user.service;

import java.util.List;

import com.safeg.user.vo.ReviewVO;

public interface ReviewService {
    
    public List<ReviewVO> reviewList() throws Exception;

    public ReviewVO reviewSelect(Long reviewId) throws Exception;

    public int reviewInsert(ReviewVO reviewVO) throws Exception;

	public ReviewVO reviewInfo(Long campaignId) throws Exception;
}
