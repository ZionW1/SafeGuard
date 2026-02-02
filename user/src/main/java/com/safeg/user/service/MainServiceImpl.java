package com.safeg.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.safeg.user.vo.BannerVO;
import com.safeg.user.vo.CampaignVO;
import com.safeg.user.vo.Option;
import com.safeg.user.vo.Page;
import com.safeg.user.vo.UserCampaignVO;
import com.safeg.user.mapper.MainMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MainServiceImpl implements MainService{

    @Autowired
    private MainMapper mainMapper;

    @Override
    public List<CampaignVO> campaignFavorite() throws Exception {
        // TODO Auto-generated method stub
        List<CampaignVO> campaignFavorite = mainMapper.campaignFavorite();
        log.info("campaignFavorite + " + campaignFavorite);

        return campaignFavorite;
    }

    @Override
    public List<CampaignVO> campaignWorkable() throws Exception {
        // TODO Auto-generated method stub
        List<CampaignVO> campaignWorkable = mainMapper.campaignWorkable();
        log.info("campaignWorkable + " + campaignWorkable);

        return campaignWorkable;
    }

    @Override
    public List<CampaignVO> campaignNew() throws Exception {
        // TODO Auto-generated method stub
        List<CampaignVO> campaignNew = mainMapper.campaignNew();
        log.info("campaignNew + ");

        return campaignNew;
    }

    @Override
    public List<BannerVO> bannerImage() throws Exception {
        // TODO Auto-generated method stub
        List<BannerVO> bannerImage = mainMapper.bannerImage();
        return bannerImage;
    }

    @Override
    public CampaignVO campaignSelect(String id) throws Exception{
        // TODO Auto-generated method stub

        CampaignVO campaignSelect = mainMapper.campaignSelect(id);

        return campaignSelect;
    }

    @Override
    public List<UserCampaignVO> campaignApply(String userId, String campaignId) throws Exception {
        // TODO Auto-generated method stub
        List<UserCampaignVO> campaignApply = mainMapper.campaignApply(userId, campaignId);

        return campaignApply;
    }

    @Override
    public List<CampaignVO> allView(Option option) throws Exception {
        // TODO Auto-generated method stub
        List<CampaignVO> allView = mainMapper.allView(option);
        return allView;
    }

    @Override
    public List<UserCampaignVO> userCampaignApply(String id) throws Exception {
        // TODO Auto-generated method stub
        List<UserCampaignVO> userCampaignApply = mainMapper.userCampaignApply(id);
        return userCampaignApply;
    }

    @Override
    public List<UserCampaignVO> appliedCampaign(String userId) throws Exception {
        // TODO Auto-generated method stub
        List<UserCampaignVO> appliedCampaign = mainMapper.appliedCampaign(userId);
        return appliedCampaign;
    }

    @Override
    public List<CampaignVO> campaignDeleted() throws Exception {
        // TODO Auto-generated method stub
        List<CampaignVO> campaignDeleted = mainMapper.campaignDeleted();

        return campaignDeleted;
    }

    @Override
    public int campaignCount() throws Exception {
        // TODO Auto-generated method stub
        int campaignCount = mainMapper.campaignCount();

        return campaignCount;
    }

    @Override
    public int totalCampaign() throws Exception {
        // TODO Auto-generated method stub
        int totalCampaign = mainMapper.totalCampaign();

        return totalCampaign;
    }
    
}
