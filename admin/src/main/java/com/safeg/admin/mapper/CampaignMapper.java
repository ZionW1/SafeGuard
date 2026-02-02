package com.safeg.admin.mapper;

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

    public int insertSchedule(CampaignVO campaignsVO);

	public List<CampaignVO> securityType() throws Exception;

    public List<CampaignVO> campaign07(@Param("option") Option option, @Param("page") Page page);

    public int updateExpiredCampaignsStatus() throws Exception;

    public int leaderInsert(UserCampaignVO userCampaignVO) throws Exception;

    public int insertCampaignLeaderApply(@Param("dailyEntries") List<UserCampaignVO> userCampaign) throws Exception;

}
