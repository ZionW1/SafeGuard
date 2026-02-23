package com.safeg.user.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.safeg.user.vo.UserCampaignVO;

public interface AuthService {

    CompletableFuture<Boolean> sendAuthCode(String phoneNumber);

    boolean verifyAuthCode(String phoneNumber, String inputCode) throws Exception;

    CompletableFuture<Boolean> sendApply(Long campaignId, List<UserCampaignVO> userCampaignVO) throws Exception;

} 
