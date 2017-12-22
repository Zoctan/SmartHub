package com.zoctan.smarthub.hubDetail.presenter;

import com.github.mikephil.charting.data.Entry;
import com.zoctan.smarthub.beans.OneNetDataPointBean;
import com.zoctan.smarthub.beans.OneNetDataPointListBean;
import com.zoctan.smarthub.beans.OneNetDataPointsBean;
import com.zoctan.smarthub.hubDetail.model.HubDetailModel;
import com.zoctan.smarthub.hubDetail.model.HubDetailModelImpl;
import com.zoctan.smarthub.hubDetail.view.HubDetailSpareView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HubDetailSparePresenter {
    private final HubDetailSpareView mHubView;
    private final HubDetailModel mHubModel;

    public HubDetailSparePresenter(HubDetailSpareView hubDetailView) {
        this.mHubView = hubDetailView;
        this.mHubModel = new HubDetailModelImpl();
    }

    public void loadHubSpareList(final String hubOneNetId, final String dataStreamIds, final Map params) {
        mHubView.showLoading();
        mHubModel.loadHubSpareList(hubOneNetId, dataStreamIds, params, new HubDetailSparePresenter.OnLoadHubSpareListListener());
    }

    private class OnLoadHubSpareListListener implements HubDetailModel.OnLoadHubSpareListListener {

        @Override
        public void onSuccess(OneNetDataPointsBean oneNetDataPoints) {
            List<OneNetDataPointListBean> oneNetDataPointList = oneNetDataPoints.getDatastreams();
            List<OneNetDataPointBean> dataWPointList = null;
            for (OneNetDataPointListBean bean : oneNetDataPointList) {
                if (bean.getId().equals("W")) {
                    dataWPointList = bean.getDatapoints();
                    break;
                }
            }
            if (dataWPointList == null) {
                return;
            }
            int size = dataWPointList.size();
            // x轴的数据, y轴的数据
            String[] x = new String[size];
            ArrayList<Entry> y = new ArrayList<>();
            OneNetDataPointBean bean;
            for (int i = 0; i < size; i++) {
                bean = dataWPointList.get(i);
                x[i] = bean.getHour();
                y.add(new Entry(i, bean.getValue()));
            }
            mHubView.hideLoading();
            mHubView.setLineChartData(x, y);
        }

        @Override
        public void onFailure(String msg) {
            mHubView.hideLoading();
            mHubView.showFailedMsg(msg);
        }
    }
}
