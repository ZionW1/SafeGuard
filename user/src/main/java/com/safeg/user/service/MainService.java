package com.safeg.user.service;

import java.util.List;

import com.safeg.user.vo.BannerVO;
import com.safeg.user.vo.CampaignVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.Page;
import com.safeg.user.vo.UserCampaignVO;

public interface MainService {

    public List<CampaignVO> campaignFavorite() throws Exception;

    public List<CampaignVO> campaignWorkable() throws Exception;

    public List<CampaignVO> campaignNew() throws Exception;

    public List<BannerVO> bannerImage() throws Exception;

    public CampaignVO campaignSelect(String id) throws Exception;

    public List<UserCampaignVO> campaignApply(String userId, String campaignId) throws Exception;

    public List<UserCampaignVO> appliedCampaign(String userId) throws Exception;

    public List<CampaignVO> allView(Option option) throws Exception;

    public List<UserCampaignVO> userCampaignApply(String id) throws Exception;

    public List<CampaignVO> campaignDeleted() throws Exception;

    public int campaignCount() throws Exception;

    public int totalCampaign() throws Exception;

}
