package com.safeg.admin.service;

import java.util.concurrent.CompletableFuture;

public interface AuthService {

    CompletableFuture<Boolean> sendAuthCode(String phoneNumber) throws Exception;

    boolean verifyAuthCode(String phoneNumber, String inputCode) throws Exception;

    CompletableFuture<Boolean> sendApply(String phoneNumber) throws Exception;

} 
