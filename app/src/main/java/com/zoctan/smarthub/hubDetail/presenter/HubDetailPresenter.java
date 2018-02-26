package com.zoctan.smarthub.hubDetail.presenter;

import com.github.mikephil.charting.data.Entry;
import com.zoctan.smarthub.beans.DeviceBean;
import com.zoctan.smarthub.beans.MonthSpareBean;
import com.zoctan.smarthub.beans.OneNetDataStreamsBean;
import com.zoctan.smarthub.beans.TimerBean;
import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.hubDetail.model.HubDetailModel;
import com.zoctan.smarthub.hubDetail.model.HubDetailModelImpl;
import com.zoctan.smarthub.hubDetail.view.HubDetailNowView;
import com.zoctan.smarthub.hubDetail.view.HubDetailSpareView;
import com.zoctan.smarthub.hubDetail.view.HubDetailTimerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

    public void loadHubSpareList(final String hubOneNetId, final String token) {
        mSpareView.showLoading();
        mHubModel.loadHubSpareList(hubOneNetId, token, new HubDetailPresenter.Listener());
    }


    public void loadHubTimerList(final String token, final String hubOneNetId) {
        mHubModel.loadHubTimerList(token, hubOneNetId, new HubDetailPresenter.Listener());
    }

    public void doHubTimer(final String token, final TimerBean timer) {
        mHubModel.doHubTimer(token, timer, new HubDetailPresenter.Listener());
    }

    // 图片上传至七牛云
    public void uploadImg(final UserBean userBean, final DeviceBean deviceBean, final String photoPath) {
        mHubModel.getQiNiuToken(userBean, deviceBean, new HubDetailModel.UploadListener() {
            @Override
            public void onSuccess(final String qiNiuToken) {
                mHubModel.uploadImg(deviceBean, userBean.getToken(), qiNiuToken, photoPath, new HubDetailModel.UploadListener() {
                    @Override
                    public void onSuccess(final String msg) {
                        mNowView.showUpdateImgSuccessMsg(deviceBean.getImg(), msg);
                    }

                    @Override
                    public void onFailure(final String msg) {
                        mNowView.showFailedMsg(msg);
                    }
                });
            }

            @Override
            public void onFailure(final String msg) {
                mNowView.showFailedMsg(msg);
            }
        });
    }

    private class Listener implements HubDetailModel.Listener {
        @Override
        public void onDoDeviceSuccess(final String msg) {
            mNowView.showDoDeviceSuccessMsg(msg);
        }

        @Override
        public void onNowSuccess(final String msg) {
            mNowView.showSuccessMsg(msg);
        }

        @Override
        public void onTimerSuccess(final String msg) {
            mTimerView.showSuccessMsg(msg);
        }

        @Override
        public void onNowSuccess(final DeviceBean deviceBean) {
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
        public void onSpareSuccess(final MonthSpareBean monthSpareBean) {
            if (monthSpareBean == null) {
                return;
            }
            final Double kwh = monthSpareBean.getWatt() / 1000.0;
            final Double bill = kwh * monthSpareBean.getPrice();
            // x轴、y轴的数据
            final String[] x = new String[24];
            final ArrayList<Entry> y = new ArrayList<>();
            for (Integer i = 0; i < 24; i++) {
                x[i] = i.toString();
                y.add(new Entry(i, monthSpareBean.getHour().get(i)));
            }
            mSpareView.hideLoading();
            mSpareView.setSpareData(String.format(Locale.CHINA, "%.3f", kwh),
                    String.format(Locale.CHINA, "%.2f", bill), monthSpareBean.getCurrent_month());
            mSpareView.setLineChartData(x, y);
        }

        @Override
        public void onNowFailure(final String msg) {
            mNowView.showFailedMsg(msg);
        }

        @Override
        public void onSpareFailure(final String msg) {
            mSpareView.hideLoading();
            mSpareView.showFailedMsg(msg);
        }

        @Override
        public void onTimerFailure(final String msg) {
            mTimerView.showFailedMsg(msg);
        }
    }
}
