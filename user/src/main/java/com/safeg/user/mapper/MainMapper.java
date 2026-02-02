package com.safeg.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.safeg.user.vo.BannerVO;
import com.safeg.user.vo.CampaignVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.Page;
import com.safeg.user.vo.UserCampaignVO;

@Mapper
public interface MainMapper {

    public List<CampaignVO> campaignFavorite() throws Exception;

    public List<CampaignVO> campaignWorkable() throws Exception;

    public List<CampaignVO> campaignNew() throws Exception;

    public List<BannerVO> bannerImage() throws Exception;

    public CampaignVO campaignSelect(String id) throws Exception;

    public UserCampaignVO campaignAplly(String id);

    public List<UserCampaignVO> campaignApply(@Param("userId") String userId, @Param("campaignId") String campaignId) throws Exception;

    public List<CampaignVO> allView(@Param("option") Option option) throws Exception;

    public List<UserCampaignVO> userCampaignApply(String id) throws Exception;

    public List<UserCampaignVO> appliedCampaign(@Param("userId") String userId);

    public List<CampaignVO> campaignDeleted() throws Exception;

    public int campaignCount() throws Exception;

    public int totalCampaign() throws Exception;

}
