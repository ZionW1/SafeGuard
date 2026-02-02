package com.safeg.user.service;

import java.util.List;

import com.safeg.user.vo.CampaignVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.UserCampaignVO;

public interface CampaignService {

    public CampaignVO campaignSelect(String id) throws Exception;

    public int campaignApply(UserCampaignVO userCampaignVO) throws Exception;

    public List<UserCampaignVO> appliedCampaign(String userId) throws Exception;

    public List<UserCampaignVO> campaignApplied(String userId, String campaignId) throws Exception;

    public List<CampaignVO> allView(Option option) throws Exception;

    public List<CampaignVO> closedCampaign() throws Exception;

    public int updateCampaign(Long campaignId) throws Exception;

    public List<UserCampaignVO> userCampaignApply(String id) throws Exception;

}
