package com.safeg.user.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.user.vo.CalendarEventVO;
import com.safeg.user.vo.PointHistoryVO;
import com.safeg.user.vo.UserAddressVO;
import com.safeg.user.vo.UserCampaignVO;
import com.safeg.user.vo.UserVO;

@Mapper
public interface MyPageMapper {

    public List<LocalDate> getAppliedDatesForUser(@Param("userId") String userId, @Param("startDate")LocalDateTime start, @Param("endDate")LocalDateTime end) throws Exception;

    public List<CalendarEventVO> getCompletedDatesForUser(@Param("userId") String userId, @Param("startDate")LocalDateTime start, @Param("endDate")LocalDateTime end) throws Exception;

    public UserAddressVO getAddress(Long id) throws Exception;

    public int updateInfo(UserVO userVo) throws Exception;

    public void updateAddress(UserVO userVo) throws Exception;

    public void insertAddress(UserVO userVo) throws Exception;

    public List<UserCampaignVO> pointList(Long id) throws Exception;

    public List<UserCampaignVO> referrerPayList(Long id) throws Exception;

    // public List<PointHistoryVO> leaderPayList(Long id) throws Exception;
    List<UserCampaignVO> leaderPayList(@Param("params") Map<String, Object> params) throws Exception;

    public int applyBodyguard(@Param("id") Long id, @Param("guardType") String guardType) throws Exception;

    public UserVO findUserById(Long userId) throws Exception;

    public int updatePassword(@Param("userId") Long userId, @Param("encodedNewPassword") String encodedNewPassword) throws Exception;

    public List<UserCampaignVO> campaignId(Long id) throws Exception;

    public int pointFull(Long id) throws Exception;


}
