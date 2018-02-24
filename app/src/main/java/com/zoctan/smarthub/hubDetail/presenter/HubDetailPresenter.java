package com.zoctan.smarthub.hubDetail.presenter;

import com.github.mikephil.charting.data.Entry;
import com.zoctan.smarthub.beans.DeviceBean;
import com.zoctan.smarthub.beans.OneNetDataPointBean;
import com.zoctan.smarthub.beans.OneNetDataPointListBean;
import com.zoctan.smarthub.beans.OneNetDataPointsBean;
import com.zoctan.smarthub.beans.OneNetDataStreamsBean;
import com.zoctan.smarthub.beans.TimerBean;
import com.zoctan.smarthub.hubDetail.model.HubDetailModel;
import com.zoctan.smarthub.hubDetail.model.HubDetailModelImpl;
import com.zoctan.smarthub.hubDetail.view.HubDetailNowView;
import com.zoctan.smarthub.hubDetail.view.HubDetailSpareView;
import com.zoctan.smarthub.hubDetail.view.HubDetailTimerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HubDetailPresenter {
    private HubDetailNowView mNowView = null;
    private HubDetailSpareView mSpareView = null;
    private HubDetailTimerView mTimerView = null;
    private final HubDetailModel mHubModel = new HubDetailModelImpl();

    public HubDetailPresenter(final HubDetailNowView view) {
        this.mNowView = view;
    }

    public HubDetailPresenter(final HubDetailSpareView view) {
        this.mSpareView = view;
    }

    public HubDetailPresenter(final HubDetailTimerView view) {
        this.mTimerView = view;
    }

    public void loadHubNowList(final String hubOneNetId, final String dataStreamIds) {
        this.mHubModel.loadHubNowList(hubOneNetId, dataStreamIds, new HubDetailPresenter.Listener());
    }

    public void loadHubDevice(final String hubOneNetId, final String token) {
        this.mHubModel.loadHubDevice(hubOneNetId, token, new HubDetailPresenter.Listener());
    }

    public void doDevice(final DeviceBean deviceBean, final String token, final String action) {
        this.mHubModel.doDevice(deviceBean, token, action, new HubDetailPresenter.Listener());
    }

    public void resetHub(final String hubOneNetId, final String token) {
        this.mHubModel.resetHub(hubOneNetId, token, new HubDetailPresenter.Listener());
    }

    public void loadHubSpareList(final String hubOneNetId, final String dataStreamIds, final Map params) {
        mSpareView.showLoading();
        mHubModel.loadHubSpareList(hubOneNetId, dataStreamIds, params, new HubDetailPresenter.Listener());
    }


    public void loadHubTimerList(final String token, final String hubOneNetId) {
        mHubModel.loadHubTimerList(token, hubOneNetId, new HubDetailPresenter.Listener());
    }

    public void doHubTimer(final String token, final TimerBean timer) {
        mHubModel.doHubTimer(token, timer, new HubDetailPresenter.Listener());
    }

    private class Listener implements HubDetailModel.Listener {
        @Override
        public void onDoDeviceSuccess(final String msg) {
            mNowView.showDoDeviceSuccessMsg(msg);
        }

        @Override
        public void onSuccess(final String msg) {
            mNowView.showSuccessMsg(msg);
        }

        @Override
        public void onSuccess(final DeviceBean deviceBean) {
            mNowView.setDevice(deviceBean);
        }

        @Override
        public void onTimerListSuccess(final List<TimerBean> timerBean) {
            mTimerView.loadTimerList(timerBean);
        }

        @Override
        public void onOneNetDataStreamSuccess(final List<OneNetDataStreamsBean> oneNetDataStreamList) {
            final Map<String, String> streams = new HashMap<>();
            for (final OneNetDataStreamsBean bean : oneNetDataStreamList) {
                streams.put(bean.getId(), bean.getCurrent_value());
            }
            mNowView.setData(streams);
        }

        @Override
        public void onSuccess(final OneNetDataPointsBean oneNetDataPoints) {
            final List<OneNetDataPointListBean> oneNetDataPointList = oneNetDataPoints.getDatastreams();
            List<OneNetDataPointBean> dataWPointList = null;
            for (final OneNetDataPointListBean bean : oneNetDataPointList) {
                if (bean.getId().equals("W")) {
                    dataWPointList = bean.getDatapoints();
                    break;
                }
            }
            if (dataWPointList == null) {
                return;
            }
            final int size = dataWPointList.size();
            // x轴的数据, y轴的数据
            final String[] x = new String[size];
            final ArrayList<Entry> y = new ArrayList<>();
            OneNetDataPointBean bean;
            for (int i = 0; i < size; i++) {
                bean = dataWPointList.get(i);
                x[i] = bean.getHour();
                y.add(new Entry(i, bean.getValue()));
            }
            mSpareView.hideLoading();
            mSpareView.setLineChartData(x, y);
        }

        @Override
        public void onFailure(final String msg) {
            mSpareView.hideLoading();
            mSpareView.showFailedMsg(msg);
        }
    }
}
