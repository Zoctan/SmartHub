package com.zoctan.smarthub.hubDetail.presenter;

import com.zoctan.smarthub.beans.OneNetDataStreamsBean;
import com.zoctan.smarthub.hubDetail.model.HubDetailModel;
import com.zoctan.smarthub.hubDetail.model.HubDetailModelImpl;
import com.zoctan.smarthub.hubDetail.view.HubDetailNowView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HubDetailNowPresenter {
    private final HubDetailNowView mHubView;
    private final HubDetailModel mHubModel;

    public HubDetailNowPresenter(HubDetailNowView hubDetailView) {
        this.mHubView = hubDetailView;
        this.mHubModel = new HubDetailModelImpl();
    }

    public void loadHubNowList(final String hubOneNetId, final String dataStreamIds) {
        mHubModel.loadHubNowList(hubOneNetId, dataStreamIds, new HubDetailNowPresenter.OnLoadHubDetailNowListener());
    }

    private class OnLoadHubDetailNowListener implements HubDetailModel.OnLoadHubDetailNowListener {

        @Override
        public void onSuccess(List<OneNetDataStreamsBean> oneNetDataStreamList) {
            Map<String, String> streams = new HashMap<>();
            for (OneNetDataStreamsBean bean : oneNetDataStreamList) {
                streams.put(bean.getId(), bean.getCurrent_value());
            }
            mHubView.setData(streams);
        }

        @Override
        public void onFailure(String msg) {
            mHubView.showFailedMsg(msg);
        }
    }
}
