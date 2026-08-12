package com.safeg.admin.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.admin.vo.CampLeaderVO;
import com.safeg.admin.vo.CampaignVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.UserCampaignVO;
import com.safeg.admin.vo.UserVO;


@Mapper
public interface CampaignMapper {

    public List<CampaignVO> campaignList(@Param("option") Option option, @Param("page") Page page) throws Exception;

    public int campaignCount(@Param("option") Option option, @Param("page") Page page) throws Exception;

    public CampaignVO campaignSelect(@Param("id") String id) throws Exception;

    public int campaignInsert(CampaignVO campaignsVO) throws Exception;

    public int campLeaderInsert(CampLeaderVO leaderList) throws Exception;

    public List<UserVO> leaderList() throws Exception;

    public int campaignUpdate(CampaignVO campaign) throws Exception;

    public int campaignDelete(String id) throws Exception;

    public int applyDelete(String id) throws Exception;

    public int insertSchedule(CampaignVO campaignsVO);

	public List<CampaignVO> securityType() throws Exception;

    public List<CampaignVO> campaign07(@Param("option") Option option, @Param("page") Page page);

    public int campNotApplyCount(@Param("option") Option option, @Param("page") Page page) throws Exception;

    public int updateExpiredCampaignsStatus() throws Exception;

    public int leaderInsert(UserCampaignVO userCampaignVO) throws Exception;

    public int insertCampaignLeaderApply(@Param("dailyEntries") List<UserCampaignVO> userCampaign) throws Exception;

    public List<CampaignVO> closedCampaign() throws Exception;

    public UserCampaignVO applySelect(Long campaignsId) throws Exception;

    public int reApplyUser(UserCampaignVO campaignsVO) throws Exception;

    public int reApplyLeader(UserCampaignVO campaignsVO) throws Exception;


    public void copyApplyDate(@Param("campaignId") Long campaignId, @Param("status") String status, @Param("isLeader") String isLeader, @Param("leadApply") String leadApply, @Param("sourceDate") LocalDate sourceDate, @Param("targetDate") LocalDate targetDate, @Param("newStrDate") LocalDate newStrDate, @Param("newEndDate") LocalDate newEndDate) throws Exception;

    public void copyApplyDateInc(UserCampaignVO userCampaignVO) throws Exception;

    public void leaderUpdate(@Param("campaignId") Long campaignId, @Param("oldLeaderNo") Long oldLeaderNo, @Param("leaderNo") Long leaderNo, @Param("leaderId") String leaderId) throws Exception;

    public void updateIsDeleted(@Param("campaignId") Long campaignId, @Param("isDeleted") String isDeleted, @Param("isActive") String isActive) throws Exception;

    public void updateUcIsDeleted(@Param("campaignId") Long campaignId, @Param("exceedCount") int exceedCount) throws Exception;

    public void updateApplicantsNum(@Param("campaignId") Long campaignId) throws Exception;

    public UserCampaignVO overlapTitle(@Param("dto") CampaignVO dto, @Param("userNo") String userNo) throws Exception;

    public int userApply(Map<String, Object> paramMap) throws Exception;

    public int userCancel(Map<String, Object> paramMap) throws Exception;

    public int countApplicants(Long campaignId) throws Exception;

    public void updateIsActive(@Param("campaignId") Long campaignId, @Param("isActive") String isActive) throws Exception;

    public List<Long> targetUserNos(@Param("campaignId") Long campaignId, @Param("timeSegment") String timeSegment, @Param("vacantSeats") int vacantSeats, @Param("sourceDate") LocalDate sourceDate) throws Exception;

    public List<Long> targetLeaderNos(@Param("campaignId") Long campaignId, @Param("sourceDate") LocalDate sourceDate) throws Exception;

    public int leaderUpdate(Map<String, Object> paramMap) throws Exception;

    public List<CampLeaderVO> campaignLeader(Long campaignId) throws Exception;

    public void deleteLeader(CampLeaderVO dbLeader) throws Exception;

    public int updateLeader(CampLeaderVO newLeader) throws Exception;

    public void insertLeader(CampLeaderVO newLeader) throws Exception;

    public void updateCampaignDate(CampaignVO dto) throws Exception;

    public void updateApplyDate(Long campaignId, LocalDate date) throws Exception;

    public void upsertLeaders(List<CampLeaderVO> leadersToSync) throws Exception;

    public int upsertUserCampaign(UserCampaignVO param) throws Exception;

    public void deleteRemovedLeaders(@Param("campaignId") Long campaignId, @Param("list") List<CampLeaderVO> leadersToSync);

    public void deleteAllLeadersByCampaignId(Long campaignId);

    public List<Long> inquiryUser(UserCampaignVO param) throws Exception;

    public int userAddApply(UserCampaignVO param) throws Exception;



    public List<Long> getActiveUserNos(@Param("campaignId") Long campaignId, @Param("timeSegment") String timeSegment) throws Exception;

    public List<Long> getActiveLeaderNos(@Param("campaignId") Long campaignId) throws Exception;

    public UserCampaignVO applyDateInfo(@Param("campaignId") Long campaignId, @Param("userNo") Long userNo, @Param("applyDate") LocalDate applyDate, @Param("timeSegment") String timeSegment) throws Exception;

    public UserCampaignVO applyDateInfoLeader(@Param("campaignId") Long campaignId, @Param("userNo") Long userNo, @Param("applyDate") LocalDate applyDate, @Param("timeSegment") String timeSegment) throws Exception;

    public void updateUserDate(@Param("dto") CampaignVO dto, @Param("oldDate") LocalDate oldDate, @Param("newDate") LocalDate newDate) throws Exception;

    public void updateLeaderDate(@Param("dto") CampaignVO dto, @Param("oldDate") LocalDate oldDate, @Param("newDate") LocalDate newDate) throws Exception;

    public void deleteApplyDate(@Param("campaignId") Long campaignId, @Param("leftoverDate") LocalDate leftoverDate) throws Exception;

    public void deleteApplyDateLeader(@Param("campaignId") Long campaignId, @Param("leftoverDate") LocalDate leftoverDate) throws Exception;

    public void copyUser(UserCampaignVO userCampaignVO) throws Exception;

    public void copyLeader(UserCampaignVO userCampaignVO) throws Exception;

}