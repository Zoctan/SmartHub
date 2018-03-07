package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.MonthSpareBean;

import lombok.Data;

@Data
public class ResponseMonthSpare {
    private String msg;
    private String error;
    private MonthSpareBean result;
}