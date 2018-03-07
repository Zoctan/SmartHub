package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.UserBean;

import lombok.Data;

@Data
public class ResponseUser {
    private String msg;
    private String error;
    private UserBean result;
}