package com.zoctan.smarthub.hubDetail.view;

import java.util.Map;

public interface HubDetailNowView {
    void setData(Map<String, String> data);

    void showFailedMsg(String msg);
}
