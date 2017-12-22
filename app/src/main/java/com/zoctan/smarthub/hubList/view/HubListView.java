package com.zoctan.smarthub.hubList.view;

import com.zoctan.smarthub.beans.HubBean;

import java.util.List;

public interface HubListView {
    void loadHubList(List<HubBean> hubList);

    void showSuccessMsg(String msg);

    void showFailedMsg(String msg);
}
