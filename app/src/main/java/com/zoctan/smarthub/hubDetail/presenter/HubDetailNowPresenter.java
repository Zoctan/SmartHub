package com.zoctan.smarthub.hubDetail.presenter;

import com.zoctan.smarthub.beans.DeviceBean;
import com.zoctan.smarthub.beans.OneNetDataStreamsBean;
import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.hubDetail.model.HubDetailNowModel;
import com.zoctan.smarthub.hubDetail.model.impl.HubDetailNowModelImpl;
import com.zoctan.smarthub.hubDetail.view.HubDetailNowView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HubDetailNowPresenter {
    private final HubDetailNowView mNowView;
    private final HubDetailNowModel mHubModel;

    public HubDetailNowPresenter(final HubDetailNowView view) {
        mNowView = view;
        mHubModel = new HubDetailNowModelImpl();
    }

    public void loadHubNowList(final String hubOneNetId, final String dataStreamIds) {
        mNowView.showLoading();
        mHubModel.loadHubNowList(hubOneNetId, dataStreamIds, new HubDetailNowPresenter.Listener());
    }

    public void loadHubDevice(final String hubOneNetId, final String token) {
        mNowView.showLoading();
        mHubModel.loadHubDevice(hubOneNetId, token, new HubDetailNowPresenter.Listener());
    }

    public void doDevice(final DeviceBean deviceBean, final String token, final String action) {
        mNowView.showLoading();
        mHubModel.doDevice(deviceBean, token, action, new HubDetailNowPresenter.Listener());
    }

    public void sendOrder(final String hubOneNetId, final String token, final String order) {
        mNowView.showLoading();
        mHubModel.sendOrder(hubOneNetId, token, order, new HubDetailNowPresenter.sendOrderListener());
    }

    // 图片上传至七牛云
    public void uploadImg(final UserBean userBean, final DeviceBean deviceBean, final String photoPath) {
        mHubModel.getQiNiuToken(userBean, deviceBean, new HubDetailNowModel.UploadListener() {
            @Override
            public void onSuccess(final String qiNiuToken) {
                mHubModel.uploadImg(deviceBean, userBean.getToken(), qiNiuToken, photoPath, new HubDetailNowModel.UploadListener() {
                    @Override
                    public void onSuccess(final String msg) {
                        mNowView.showUpdateImgSuccessMsg(deviceBean.getImg(), msg);
                        mNowView.hideLoading();
                    }

                    @Override
                    public void onFailure(final String msg) {
                        mNowView.showFailedMsg(msg);
                        mNowView.hideLoading();
                    }
                });
            }

            @Override
            public void onFailure(final String msg) {
                mNowView.showFailedMsg(msg);
                mNowView.hideLoading();
            }
        });
    }

    private class sendOrderListener implements HubDetailNowModel.sendOrderListener {
        @Override
        public void onNowSuccess(final String msg) {
            mNowView.showSuccessMsg(msg);
            mNowView.hideLoading();
        }

        @Override
        public void setHubStore(final boolean flag) {
            mNowView.setHubStore(flag);
            mNowView.hideLoading();
        }

        @Override
        public void setHubMatch(final String list) {
            mNowView.setHubMatch(list);
            mNowView.hideLoading();
        }

        @Override
        public void onNowFailure(final String msg) {
            mNowView.showFailedMsg(msg);
            mNowView.hideLoading();
        }
    }

    private class Listener implements HubDetailNowModel.Listener {
        @Override
        public void onDoDeviceSuccess(final String msg) {
            mNowView.showDoDeviceSuccessMsg(msg);
            mNowView.hideLoading();
        }

        @Override
        public void onNowSuccess(final String msg) {
            mNowView.showSuccessMsg(msg);
            mNowView.hideLoading();
        }

        @Override
        public void onNowSuccess(final DeviceBean deviceBean) {
            mNowView.setDevice(deviceBean);
            mNowView.hideLoading();
        }

        @Override
        public void onOneNetDataStreamSuccess(final List<OneNetDataStreamsBean> oneNetDataStreamList) {
            final Map<String, String> streams = new HashMap<>();
            for (final OneNetDataStreamsBean bean : oneNetDataStreamList) {
                streams.put(bean.getId(), bean.getCurrent_value());
            }
            mNowView.setData(streams);
            mNowView.hideLoading();
        }

        @Override
        public void onNowFailure(final String msg) {
            mNowView.showFailedMsg(msg);
            mNowView.hideLoading();
        }
    }
}
