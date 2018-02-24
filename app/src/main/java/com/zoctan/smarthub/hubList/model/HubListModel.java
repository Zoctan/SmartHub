package com.zoctan.smarthub.hubList.model;

import com.zoctan.smarthub.beans.HubBean;

import java.util.List;

public interface HubListModel {

    void loadHubList(String token, Listener listener);

    void hubOpenClose(String oneNetId, String order, String token, Listener listener);

    void doHub(String action, String token, HubBean hub, Listener listener);

    interface Listener {
        void onSuccess(List<HubBean> hubList);

        void onSuccess(String msg);

        void onFailure(String msg);
    }
}
