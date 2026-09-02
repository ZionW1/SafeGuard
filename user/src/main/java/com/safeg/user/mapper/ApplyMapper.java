package com.safeg.user.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.user.vo.CampaignVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.PointHistoryVO;
import com.safeg.user.vo.UserCampaignVO;
import com.safeg.user.vo.UserVO;

@Mapper
public interface ApplyMapper {

    public List<UserCampaignVO> userCampaignApply(@Param("campaignId") String campaignId, @Param("applyDate") LocalDate applyDate, @Param("timeSegment") String timeSegment) throws Exception;

    public int updateStatus(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId, @Param("applyDate") LocalDate applyDate, @Param("statusValue") String statusValue) throws Exception;

    public String statusInfo(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId, @Param("applyDate") LocalDate applyDate) throws Exception;

    public int updatePay(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId) throws Exception;

    public int updateLatenessPay(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId) throws Exception;

    public int initStatus(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId, @Param("applyDate") LocalDate applyDate, @Param("division") String division);

    public void deletePoint(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId, @Param("applyDate") LocalDate applyDate, @Param("division") String division);

    public void addPointsReferrer(Long referrerId, int pointAmount) throws Exception;

    public void lateYn(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId, @Param("applyDate") LocalDate applyDate) throws Exception;

    public void pointFull(Long userNo) throws Exception;

    public List<UserCampaignVO> getApplyDate(String campaignId) throws Exception;

    public int fullAttendCount( @Param("userNo") Long userNo,  @Param("fullAttendDate") String fullAttendDate) throws Exception;

    public int pointAmount(Long campaignId) throws Exception;

    public CampaignVO leaderAmount(Long campaignId) throws Exception;

    public void updateAttendPoint(PointHistoryVO myPoint) throws Exception;

    public int rosterRemove(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId, @Param("applyDate") LocalDate applyDate) throws Exception;

    public int applyList(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId);

    public void applicantsMinus(Long campaignId) throws Exception;

    public UserCampaignVO getWorkInfo(@Param("campaignId") String campaignId, @Param("applyDate") LocalDate applyDate) throws Exception;

    public PointHistoryVO getUserSgnf(@Param("campaignId") int campaignId, @Param("userNo") List<Long> userNo, @Param("missionDate") String missionDate) throws Exception;

    public String getLeaderStatus(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId, @Param("applyDate") LocalDate applyDate);

    public int updateLeader(@Param("userNo") Long userNo, @Param("campaignId") Long campaignId, @Param("applyDate") LocalDate applyDate, @Param("statusValue") String statusValue) throws Exception;

    public int LeaderAttendCount(@Param("userNo") Long userNo, @Param("fullAttendDate") String fullAttendDate);

	public Long pointSelect(PointHistoryVO pointHistoryVO) throws Exception;

    public int insertPointHistory(PointHistoryVO pointHistory) throws Exception;

    public int pointUpdate(PointHistoryVO pointHistoryVO) throws Exception;

    public List<UserCampaignVO> getUserInfo(UserCampaignVO dto) throws Exception;

    public int pointInsert(UserCampaignVO dto) throws Exception;

    public List<UserCampaignVO> getLeaderInfo(UserCampaignVO dto) throws Exception;

    public List<UserVO> userInfoList(@Param("campaignId") Long campaignId, @Param("option") Option option) throws Exception;

    public int getPointInfo(@Param("campaignId") String campaignId, @Param("finalDate") LocalDate finalDate) throws Exception;

    public int countApplicants(Long campaignId) throws Exception;

    public UserCampaignVO overlapTitle(@Param("dto") CampaignVO dto, @Param("userNo") String userNo) throws Exception;

    public CampaignVO campaignSelect(String valueOf) throws Exception;

    public int userApply(Map<String, Object> paramMap) throws Exception;

    public void updateApplicantsNum(Long campaignId) throws Exception;

    public int userCancel(Map<String, Object> paramMap) throws Exception;

    public int getPay(@Param("ucChoice") String ucChoice, @Param("campaignId") Long campaignId, @Param("userNo") Long userNo, @Param("wageChk") String wageChk) throws Exception;

    public List<UserVO> userDateInfo(@Param("campaignId") Long campaignId, @Param("option") Option option) throws Exception;

    public List<UserVO> dateSelect(@Param("campaignId") Long campaignId, @Param("option") Option option, @Param("applyDate") String applyDate) throws Exception;
}
