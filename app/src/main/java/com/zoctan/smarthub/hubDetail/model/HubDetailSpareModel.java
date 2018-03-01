package com.zoctan.smarthub.hubDetail.model;

import com.zoctan.smarthub.beans.MonthSpareBean;

public interface HubDetailSpareModel {
    void loadHubSpareList(String oneNetId, String token, Listener listener);

    interface Listener {
        void onSpareSuccess(MonthSpareBean monthSpareBean);

        void onSpareFailure(String msg);
    }
}
