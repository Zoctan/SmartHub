package com.zoctan.smarthub.hubList.presenter;

import com.zoctan.smarthub.beans.HubBean;
import com.zoctan.smarthub.hubList.model.HubListModel;
import com.zoctan.smarthub.hubList.model.HubListModelImpl;
import com.zoctan.smarthub.hubList.view.HubListView;

import java.util.List;

public class HubListPresenter {
    private final HubListView mHubView;
    private final HubListModel mHubModel;

    public HubListPresenter(HubListView hublistView) {
        this.mHubModel = new HubListModelImpl();
        this.mHubView = hublistView;
    }

    public void doHub(final String action, final String token,
                      final HubBean hub) {
        mHubModel.doHub(action, token, hub, new HubListPresenter.OnListener());
    }

    public void hubOpenClose(final String oneNetId, final String order) {
        mHubModel.hubOpenClose(oneNetId, order, new HubListPresenter.OnListener());
    }

    public void loadHubList(final String token) {
        mHubModel.loadHubList(token, new HubListPresenter.OnLoadHubListListener());
    }

    private class OnListener implements HubListModel.OnListener {
        @Override
        public void onSuccess(String msg) {
            mHubView.showSuccessMsg(msg);
        }

        @Override
        public void onFailure(String msg) {
            mHubView.showFailedMsg(msg);
        }
    }

    private class OnLoadHubListListener implements HubListModel.OnLoadHubListListener {
        @Override
        public void onSuccess(List<HubBean> list) {
            mHubView.loadHubList(list);
        }

        @Override
        public void onFailure(String msg) {
            mHubView.showFailedMsg(msg);
        }
    }
}
