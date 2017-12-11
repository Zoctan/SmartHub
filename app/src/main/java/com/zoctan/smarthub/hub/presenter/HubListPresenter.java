package com.zoctan.smarthub.hub.presenter;

import com.zoctan.smarthub.beans.HubBean;
import com.zoctan.smarthub.hub.model.HubModel;
import com.zoctan.smarthub.hub.model.HubModelImpl;
import com.zoctan.smarthub.hub.view.HubListView;

import java.util.List;

public class HubListPresenter {
    private HubListView mHubView;
    private HubModel mHubModel;

    public HubListPresenter(HubListView hublistView) {
        this.mHubModel = new HubModelImpl();
        this.mHubView = hublistView;
    }

    public void loadHubList(final String token) {
        mHubView.showLoading();
        mHubModel.loadHubList(token, new HubListPresenter.OnLoadHubListListener());
    }

    private class OnLoadHubListListener implements HubModel.OnLoadHubListListener {
        @Override
        public void onSuccess(List<HubBean> list) {
            mHubView.hideLoading();
            mHubView.addHub(list);
        }

        @Override
        public void onFailure(String msg, Exception e) {
            mHubView.hideLoading();
            mHubView.showLoadingFailedMsg();
        }
    }
}
