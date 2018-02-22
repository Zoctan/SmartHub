package com.zoctan.smarthub.hubList.model;

import com.zoctan.smarthub.beans.HubBean;

import java.util.List;

public interface HubListModel {

    void loadHubList(String token, OnLoadHubListListener listener);

    void hubOpenClose(String oneNetId, String order, String token, OnListener listener);

    void doHub(String action, String token, HubBean hub, OnListener listener);

    interface OnLoadHubListListener {
        void onSuccess(List<HubBean> hubList);

        void onFailure(String msg);
    }

    interface OnListener {
        void onSuccess(String msg);

        void onFailure(String msg);
    }
}
