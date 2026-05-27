package com.safeg.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safeg.user.mapper.ReviewMapper;
import com.safeg.user.vo.ReviewVO;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public List<ReviewVO> reviewList() throws Exception {
        // TODO Auto-generated method stub
        List<ReviewVO> reviewList = reviewMapper.reviewList();
        return reviewList;
    }

    @Override
    public ReviewVO reviewSelect(Long reviewId) throws Exception {
        // TODO Auto-generated method stub
        ReviewVO reviewSelect = reviewMapper.reviewSelect(reviewId);
        return reviewSelect;
    }

    @Override
    public ReviewVO reviewInfo(Long campaignId) throws Exception {
        // TODO Auto-generated method stub
        ReviewVO reviewInfo = reviewMapper.reviewInfo(campaignId);

        return reviewInfo;
    }

    @Override
    public int reviewInsert(ReviewVO reviewVO) throws Exception {
        // TODO Auto-generated method stub
        int result = reviewMapper.reviewInsert(reviewVO);

        return result;
    }

}
