package com.zoctan.smarthub.beans;


import java.io.Serializable;

import lombok.Data;

@Data
public class UserBean implements Serializable {
    private String id;
    private String token;
    private String username;
    private String password;
    // 头像链接
    private String avatar;
    private String phone;

    private String action;
}
