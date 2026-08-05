package com.safeg.user.service;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.safeg.user.vo.CampaignVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.PointHistoryVO;
import com.safeg.user.vo.UserCampaignVO;
import com.safeg.user.vo.UserVO;

public interface ApplyService {

    public List<UserCampaignVO> userCampaignApply(String id, LocalDate applyDate, String timeSegment) throws Exception;

    public String updateStatus(Long userNo, Long campaignId, LocalDate applyDate, String statusValue, int workHour) throws Exception;

    public String statusInfo(Long userNo, Long campaignId, LocalDate applyDate) throws Exception;

    public int initStatus(Long userNo, Long campaignId, LocalDate applyDate, String division) throws Exception;

    public void lateYn(Long userNo, Long campaignId, LocalDate applyDate) throws Exception;

    public void pointFull(Long userNo, Long campaignId, LocalDate applyDate) throws Exception;

    public List<UserCampaignVO> getApplyDate(String campaignId) throws Exception;

    public int rosterRemove(Long userNo, Long campaignId, LocalDate applyDate) throws Exception;

    public int pointInsert(PointHistoryVO pointHistoryVO) throws Exception;

    public UserCampaignVO getWorkInfo(String campaignId, LocalDate applyDate) throws Exception;

    public PointHistoryVO getUserSgnf(int campaignId, List<Long> userNo, String missionDate) throws Exception;

    public String getLeaderStatus(Long userNo, Long campaignId, LocalDate applyDate) throws Exception;

    public String updateLeader(Long userNo, Long campaignId, LocalDate applyDate, String status, int workHour) throws Exception;

    public Long pointSelect(PointHistoryVO pointHistoryVO) throws Exception;

    public int pointUpdate(PointHistoryVO pointHistoryVO) throws Exception;

    public List<UserCampaignVO> getUserInfo(UserCampaignVO dto) throws Exception;

    public int pointInsert(List<UserCampaignVO> getUserInfo) throws Exception;

    public List<UserVO> userInfoList(Long campaignId, Option option) throws Exception;

    public int getPointInfo(String campaignId, LocalDate finalDate) throws Exception;

    public UserCampaignVO overlapTitle(CampaignVO dto) throws Exception;

    public int userApply(CampaignVO dto) throws Exception;

    public int userCancel(CampaignVO dto) throws Exception;

}
