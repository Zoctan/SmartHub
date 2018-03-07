package com.zoctan.smarthub.hubList.model;

import com.zoctan.smarthub.beans.HubBean;

import java.util.List;

public interface HubListModel {

    void loadHubList(String token, onLoadHubListListener listener);

    void hubOpenClose(HubBean hub, String token, onHubOpenCloseListener listener);

    void doHub(HubBean hub, String token, onDoHubListener listener);

    interface onLoadHubListListener {
        void onSuccess(List<HubBean> hubList);

        void onFailure(String msg);
    }

    interface onHubOpenCloseListener {
        void onSuccess(String msg);

        void onFailure(String msg);
    }

    interface onDoHubListener {
        void onSuccess(String msg);

        void onFailure(String msg);
    }
}
