package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.HubBean;

import java.util.List;

import lombok.Data;

@Data
public class ResponseHubList {
    private String msg;
    private String error;
    private List<HubBean> result;
}