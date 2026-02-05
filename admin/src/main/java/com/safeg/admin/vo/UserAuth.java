package com.safeg.admin.vo;

import lombok.Data;

@Data
public class UserAuth {
    private Long no;
    private Long id;
    private String name;
    private String authCd;
    private String auth;
}