package com.zoctan.smarthub.response;

import com.zoctan.smarthub.beans.OneNetDataStreamsBean;

import java.util.List;

import lombok.Data;

@Data
public class ResponseOneNetDataStreams {
    private String errno;
    private String error;
    private List<OneNetDataStreamsBean> data;
}