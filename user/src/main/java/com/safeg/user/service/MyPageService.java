package com.safeg.user.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.safeg.user.vo.CalendarEventVO;
import com.safeg.user.vo.PointHistoryVO;
import com.safeg.user.vo.UserAddressVO;
import com.safeg.user.vo.UserCampaignVO;
import com.safeg.user.vo.UserVO;

public interface MyPageService {
    
    public List<LocalDate> getAppliedDatesForUser(String userId, LocalDateTime start, LocalDateTime end) throws Exception;
    
    public List<CalendarEventVO> getCompletedDatesForUser(String userId, LocalDateTime start, LocalDateTime end) throws Exception;

    public UserAddressVO getAddress(Long id) throws Exception;

    public int updateInfo(UserVO userVo) throws Exception;

    public boolean uploadCertImage(UserVO userVo) throws Exception;

    public boolean uploadIdttImage(UserVO userVO) throws Exception;

    public List<UserCampaignVO> pointList(Long id) throws Exception;

    public List<UserCampaignVO> referrerPayList(Long id) throws Exception;

    public List<UserCampaignVO> leaderPayList(Long id, List<UserCampaignVO> campaignIds) throws Exception;

    public int applyBodyguard(Long id, String guardType) throws Exception;

    public boolean changeUserPassword(Long userId, String currentPassword, String newPassword) throws Exception;

    public List<UserCampaignVO> campaignId(Long id) throws Exception;

    public int pointFull(Long id) throws Exception;
}
