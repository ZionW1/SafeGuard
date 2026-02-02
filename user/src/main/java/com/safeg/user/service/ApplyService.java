package com.safeg.user.service;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.safeg.user.vo.UserCampaignVO;

public interface ApplyService {

    public List<UserCampaignVO> userCampaignApply(String id, LocalDate applyDate) throws Exception;

    public String updateStatus(Long userNo, Long campaignId, LocalDate applyDate, String statusValue) throws Exception;

    public String statusInfo(Long userNo, Long campaignId, LocalDate applyDate) throws Exception;

    public int initStatus(Long userNo, Long campaignId, LocalDate applyDate) throws Exception;

    public void lateYn(Long userNo, Long campaignId, LocalDate applyDate) throws Exception;

    public void pointFull(Long userNo, Long campaignId, LocalDate applyDate) throws Exception;

}
