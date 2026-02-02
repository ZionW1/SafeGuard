package com.safeg.user.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.user.vo.PointHistoryVO;
import com.safeg.user.vo.UserCampaignVO;

@Mapper
public interface ApplyMapper {

    public List<UserCampaignVO> userCampaignApply(@Param("id") String id, @Param("applyDate") LocalDate applyDate) throws Exception;

    public int updateStatus(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId, @Param("applyDate") LocalDate applyDate, @Param("statusValue") String statusValue) throws Exception;

    public String statusInfo(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId, @Param("applyDate") LocalDate applyDate) throws Exception;

    public int updatePay(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId) throws Exception;

    public int updateLatenessPay(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId) throws Exception;

    public int initStatus(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId, @Param("applyDate") LocalDate applyDate);

    public void addPointsReferrer(Long referrerId, int pointAmount) throws Exception;

    public void insertPointHistory(PointHistoryVO pointHistory) throws Exception;

    public void lateYn(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId, @Param("applyDate") LocalDate applyDate) throws Exception;

    public void pointFull(Long userNo) throws Exception;

}
