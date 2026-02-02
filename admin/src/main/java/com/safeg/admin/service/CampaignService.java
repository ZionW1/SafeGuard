package com.safeg.admin.service;

import java.util.List;

// import com.safeg.admin.vo.CampaignTypesVO;
import com.safeg.admin.vo.CampaignVO;
import com.safeg.admin.vo.FilesVO;
import com.safeg.admin.vo.Option;
import com.safeg.admin.vo.Page;
import com.safeg.admin.vo.UserVO;

public interface CampaignService {

    public List<CampaignVO> campaignList(Option option, Page page) throws Exception;

    public int campaignCount(Option option) throws Exception;

    public CampaignVO campaignSelect(String id) throws Exception;

    public int campaignInsert(CampaignVO campaignsVO) throws Exception;

    public int campaignUpdate(CampaignVO campaign) throws Exception;

    public int campaignDelete(String id) throws Exception;

    public List<UserVO> leaderList() throws Exception;

    public List<CampaignVO> securityType() throws Exception;

    public List<CampaignVO> campaign07(Option option, Page page) throws Exception;

    public int updateExpiredCampaigns() throws Exception;

    
}
