package com.zoctan.smarthub.hub.model;

import com.zoctan.smarthub.beans.HubBean;
import com.zoctan.smarthub.beans.HubDetailBean;

import java.util.List;

/**
 * 信息接口
 */
public interface HubModel {

    // 载入Hub列表接口
    void loadHubList(final String token, OnLoadHubListListener listener);

    interface OnLoadHubListListener {
        void onSuccess(List<HubBean> hubList);

        void onFailure(String msg, Exception e);
    }

    // 载入详情接口
    void loadHubDetail(String userId, String hubId, OnLoadHubDetailListener listener);
    // 监听详情
    interface OnLoadHubDetailListener {
        void onSuccess(HubDetailBean hubDetailBean);
        void onFailure(String msg, Exception e);
    }
}
