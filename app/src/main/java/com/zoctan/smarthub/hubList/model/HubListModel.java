package com.zoctan.smarthub.hubList.model;

import com.zoctan.smarthub.beans.HubBean;

import java.util.List;

public interface HubListModel {

    void loadHubList(final String token, OnLoadHubListListener listener);

    void hubOpenClose(final String oneNetId, final String order, OnListener listener);

    void doHub(final String action, final String token, final HubBean hub, OnListener listener);

    interface OnLoadHubListListener {
        void onSuccess(List<HubBean> hubList);

        void onFailure(String msg);
    }

    interface OnListener {
        void onSuccess(String msg);

        void onFailure(String msg);
    }
}
