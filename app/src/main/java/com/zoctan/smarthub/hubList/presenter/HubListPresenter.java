package com.zoctan.smarthub.hubList.presenter;

import com.zoctan.smarthub.beans.HubBean;
import com.zoctan.smarthub.hubList.model.HubListModel;
import com.zoctan.smarthub.hubList.model.impl.HubListModelImpl;
import com.zoctan.smarthub.hubList.view.HubListView;

import java.util.List;

public class HubListPresenter {
    private final HubListView mHubView;
    private final HubListModel mHubModel;

    public HubListPresenter(final HubListView hublistView) {
        mHubModel = new HubListModelImpl();
        mHubView = hublistView;
    }

    public void doHub(final String action, final String token, final HubBean hub) {
        mHubView.showLoading();
        mHubModel.doHub(action, token, hub, new HubListPresenter.Listener());
    }

    public void hubOpenClose(final String oneNetId, final String order, final String token) {
        mHubView.showLoading();
        mHubModel.hubOpenClose(oneNetId, order, token, new HubListPresenter.Listener());
    }

    public void loadHubList(final String token) {
        mHubView.showLoading();
        mHubModel.loadHubList(token, new HubListPresenter.Listener());
    }

    private class Listener implements HubListModel.Listener {
        @Override
        public void onSuccess(final String msg) {
            mHubView.showSuccessMsg(msg);
            mHubView.hideLoading();
        }

        @Override
        public void onSuccess(final List<HubBean> list) {
            mHubView.loadHubList(list);
            mHubView.hideLoading();
        }

        @Override
        public void onFailure(final String msg) {
            mHubView.showFailedMsg(msg);
            mHubView.hideLoading();
        }
    }
}
