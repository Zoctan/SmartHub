package com.zoctan.smarthub.hubDetail.model;

import com.zoctan.smarthub.beans.MonthSpareBean;

public interface HubDetailSpareModel {
    void loadHubSpareList(String oneNetId, String token, onLoadHubSpareListListener listener);

    interface onLoadHubSpareListListener {
        void onSuccess(MonthSpareBean monthSpareBean);

        void onFailure(String msg);
    }
}
