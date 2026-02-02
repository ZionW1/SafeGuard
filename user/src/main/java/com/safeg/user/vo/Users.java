package com.safeg.user.vo;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Users {
    // private Long no;
    // private String username;
    // private String password;
    // private String name;
    // private String email;
    // private Date createdAt;
    // private Date updatedAt;
    // private int enabled;

    // private List<UserAuth> authList;

    private Long no;
    private String id;
    private String accountNumber;
    private String bank;
    private String depositor;
    private String name;
    private String email;
    private String isDeleted;
    private String isStopped;
    private String nickname;
    private String password;
    private String phone;
    private int point;
    private String role;
    private String roleCd;
    private Long addressId;
    private Date stoppedDate;
    private String memo;
    private String profileImage;
    private Date createdAt;
    private Date updatedAt;
    private String referrer;
    
    private int enabled;
    private List<UserAuth> authList;
}