package com.safeg.user.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.user.vo.CampaignVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.UserCampaignVO;

@Mapper
public interface CampaignMapper {

    public int campaignApply(@Param("dailyEntries") List<UserCampaignVO> userCampaign) throws Exception;
    // void insertUserCampaignPeriod(@Param("dailyEntries") List<UserCampaignDailyEntry> dailyEntries);

    public int updateApplicants(UserCampaignVO userCampaign) throws Exception;

    public List<CampaignVO> closedCampaign() throws Exception;

    public int updateCampaign(Long campaignId) throws Exception;

    public CampaignVO campaignSelect(Long id) throws Exception;

    public List<CampaignVO> allView(Option option) throws Exception;

    public List<UserCampaignVO> userCampaignApply(String id) throws Exception;

    public List<UserCampaignVO> appliedCampaign(String userId) throws Exception;

    public List<UserCampaignVO> campaignApplied(@Param("userId") String userId, @Param("campaignId") Long campaignId) throws Exception;

    public List<CampaignVO> searchCampaign(String schCamp) throws Exception;

    public int scheduleOverlap(UserCampaignVO userCampaignVO) throws Exception;

    public String overlapTitle(UserCampaignVO userCampaignVO) throws Exception;

    public List<CampaignVO> campaignProgress() throws Exception;

    public List<CampaignVO> campaignGuard() throws Exception;

    public List<CampaignVO> campaignFulfill() throws Exception;

    public CampaignVO notActiveCampaign(Long campaignId) throws Exception;

    public int applyCancel(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId, @Param("applyDate")LocalDate date) throws Exception;

}