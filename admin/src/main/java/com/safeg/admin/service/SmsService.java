package com.safeg.admin.service;

public interface SmsService {

    public void sendTestSms(String receiver, String message) throws Exception;

    String sendSms(String receiver, String message) throws Exception;
}
