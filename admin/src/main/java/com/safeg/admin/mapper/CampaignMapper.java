package com.safeg.admin.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.admin.vo.CampaignVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.UserCampaignVO;
import com.safeg.admin.vo.UserVO;


@Mapper
public interface CampaignMapper {

    public List<CampaignVO> campaignList(@Param("option") Option option, @Param("page") Page page) throws Exception;

    public int campaignCount(@Param("option") Option option) throws Exception;

    public CampaignVO campaignSelect(@Param("id") String id) throws Exception;

    public int campaignInsert(CampaignVO campaignsVO) throws Exception;

    public List<UserVO> leaderList() throws Exception;

    public int campaignUpdate(CampaignVO campaign) throws Exception;

    public int campaignDelete(String id) throws Exception;
    
    public int applyDelete(String id) throws Exception;
    
    public int insertSchedule(CampaignVO campaignsVO);

	public List<CampaignVO> securityType() throws Exception;

    public List<CampaignVO> campaign07(@Param("option") Option option, @Param("page") Page page);

    public int updateExpiredCampaignsStatus() throws Exception;

    public int leaderInsert(UserCampaignVO userCampaignVO) throws Exception;

    public int insertCampaignLeaderApply(@Param("dailyEntries") List<UserCampaignVO> userCampaign) throws Exception;

    public List<CampaignVO> closedCampaign() throws Exception;

    public UserCampaignVO applySelect(Long campaignsId) throws Exception;

    public void updateApplyDate(@Param("campaignId") Long campaignId, @Param("eventPeriodStr") LocalDate eventPeriodStr, @Param("eventPeriodEnd") LocalDate eventPeriodEnd, @Param("oldDate") LocalDate oldDate, @Param("newDate") LocalDate newDate) throws Exception;

    public void deleteApplyDate(@Param("campaignId") Long campaignId, @Param("leftoverDate") LocalDate leftoverDate) throws Exception;

    public void copyApplyDate(@Param("campaignId") Long campaignId, @Param("status") String status, @Param("isLeader") String isLeader, @Param("sourceDate") LocalDate sourceDate, @Param("targetDate") LocalDate targetDate, @Param("newStrDate") LocalDate newStrDate, @Param("newEndDate") LocalDate newEndDate) throws Exception;

    public void leaderUpdate(@Param("campaignId") Long campaignId, @Param("oldLeaderNo") Long oldLeaderNo, @Param("leaderNo") Long leaderNo, @Param("leaderId") String leaderId) throws Exception;

    public void updateIsDeleted(@Param("campaignId") Long campaignId, @Param("isDeleted") String isDeleted, @Param("isActive") String isActive) throws Exception;

    public void updateUcIsDeleted(@Param("campaignId") Long campaignId, @Param("exceedCount") int exceedCount) throws Exception;

    public void updateApplicantsNum(@Param("campaignId") Long campaignId, @Param("newNum") int newNum) throws Exception;

}
