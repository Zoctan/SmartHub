package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.TimerBean;

import java.util.List;

import lombok.Data;

@Data
public class ResponseTimerList {
    private String msg;
    private String error;
    private List<TimerBean> result;
}