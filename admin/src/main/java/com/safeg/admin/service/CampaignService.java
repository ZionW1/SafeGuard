package com.safeg.admin.service;

import java.util.List;

import com.safeg.admin.vo.CampLeaderVO;
// import com.safeg.admin.vo.CampaignTypesVO;
import com.safeg.admin.vo.CampaignVO;
import com.safeg.admin.vo.FilesVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.UserCampaignVO;
import com.safeg.admin.vo.UserVO;

public interface CampaignService {

    public List<CampaignVO> campaignList(Option option, Page page) throws Exception;

    public int campaignCount(Option option, Page page) throws Exception;

    public CampaignVO campaignSelect(String campaignId) throws Exception;

    public int campaignInsert(CampaignVO campaignsVO) throws Exception;

    public int campaignUpdate(CampaignVO campaign) throws Exception;

    public int campaignDelete(String id) throws Exception;

    public int applyDelete(String id) throws Exception;

    public List<UserVO> leaderList() throws Exception;

    public List<CampaignVO> securityType() throws Exception;

    public List<CampaignVO> campaign07(Option option, Page page) throws Exception;

    public int updateExpiredCampaigns() throws Exception;

    public List<CampaignVO> closedCampaign() throws Exception;

    public UserCampaignVO overlapTitle(CampaignVO dto) throws Exception;

    public int userCancel(CampaignVO dto) throws Exception;

    public int userApply(CampaignVO dto) throws Exception;

    public void campLeaderInsert(CampLeaderVO leaderList) throws Exception;

    public int leaderUpdate(CampaignVO dto) throws Exception;

    public UserCampaignVO applySelect(Long campaignId) throws Exception;

}
