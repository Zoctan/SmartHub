package com.zoctan.smarthub.response;

import lombok.Data;

@Data
public class Response {
    private String msg;
    private String error;
    private String result;
}