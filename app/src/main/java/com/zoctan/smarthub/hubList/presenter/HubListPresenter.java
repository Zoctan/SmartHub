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

    public void doHub(final HubBean hub, final String token) {
        mHubView.showLoading();
        mHubModel.doHub(hub, token, new HubListModel.onDoHubListener() {
            @Override
            public void onSuccess(final String msg) {
                mHubView.showSuccessMsg(msg);
                mHubView.hideLoading();
            }

            @Override
            public void onFailure(final String msg) {
                mHubView.showFailedMsg(msg);
                mHubView.hideLoading();
            }
        });
    }

    public void hubOpenClose(final HubBean hub, final String token) {
        mHubView.showLoading();
        mHubModel.hubOpenClose(hub, token, new HubListModel.onHubOpenCloseListener() {
            @Override
            public void onSuccess(final String msg) {
                mHubView.showSuccessMsg(msg);
                mHubView.hideLoading();
            }

            @Override
            public void onFailure(final String msg) {
                mHubView.showFailedMsg(msg);
                mHubView.hideLoading();
            }
        });
    }

    public void loadHubList(final String token, final boolean isShowLoading) {
        if (isShowLoading) {
            mHubView.showLoading();
        }
        mHubModel.loadHubList(token, new HubListModel.onLoadHubListListener() {
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
        });
    }
}
