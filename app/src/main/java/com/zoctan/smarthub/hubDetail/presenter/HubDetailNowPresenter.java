package com.zoctan.smarthub.hubDetail.presenter;

import com.zoctan.smarthub.api.SmartApiUrls;
import com.zoctan.smarthub.beans.DeviceBean;
import com.zoctan.smarthub.beans.OneNetDataStreamsBean;
import com.zoctan.smarthub.hubDetail.model.HubDetailNowModel;
import com.zoctan.smarthub.hubDetail.model.impl.HubDetailNowModelImpl;
import com.zoctan.smarthub.hubDetail.view.HubDetailNowView;
import com.zoctan.smarthub.utils.JsonUtil;
import com.zoctan.smarthub.utils.QiNiu.GetTokenListener;
import com.zoctan.smarthub.utils.QiNiu.QiNiuUtil;
import com.zoctan.smarthub.utils.QiNiu.UploadListener;

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
        mHubModel.loadHubNowList(hubOneNetId, dataStreamIds, new HubDetailNowModel.onLoadHubNowListListener() {
            @Override
            public void onSuccess(final List<OneNetDataStreamsBean> oneNetDataStreamList) {
                final Map<String, String> streams = new HashMap<>();
                for (final OneNetDataStreamsBean bean : oneNetDataStreamList) {
                    streams.put(bean.getId(), bean.getCurrent_value());
                }
                mNowView.setData(streams);
            }

            @Override
            public void onFailure(final String msg) {
                mNowView.showFailedMsg(msg);
            }
        });
    }

    public void loadHubDevice(final String hubOneNetId, final String token) {
        mNowView.showLoadDeviceLoading();
        mHubModel.loadHubDevice(hubOneNetId, token, new HubDetailNowModel.onLoadHubDeviceListener() {
            @Override
            public void onSuccess(final DeviceBean deviceBean) {
                mNowView.setDevice(deviceBean);
                mNowView.hideLoadDeviceLoading();
            }

            @Override
            public void onFailure(final String msg) {
                mNowView.showFailedMsg(msg);
                mNowView.hideLoadDeviceLoading();
            }
        });
    }

    public void doDevice(final DeviceBean deviceBean, final String token) {
        mNowView.showLoading();
        mHubModel.doDevice(deviceBean, token, new HubDetailNowModel.onDoDeviceListener() {
            @Override
            public void onSuccess(final String msg) {
                mNowView.showDoDeviceSuccessMsg(msg);
                mNowView.hideLoading();
            }

            @Override
            public void onFailure(final String msg) {
                mNowView.showFailedMsg(msg);
                mNowView.hideLoading();
            }
        });
    }

    public void sendOrder(final String hubOneNetId, final String token, final String order) {
        mNowView.showStoreOrMatchLoading();
        mHubModel.sendOrder(hubOneNetId, token, order, new HubDetailNowModel.sendOrderListener() {
            @Override
            public void onSuccess(final String msg) {
                mNowView.showSuccessMsg(msg);
                mNowView.hideStoreOrMatchLoading();
            }

            @Override
            public void setHubStore(final boolean flag) {
                mNowView.setHubStore(flag);
                mNowView.hideStoreOrMatchLoading();
            }

            @Override
            public void setHubMatch(final String list) {
                mNowView.setHubMatch(list);
                mNowView.hideStoreOrMatchLoading();
            }

            @Override
            public void onFailure(final String msg) {
                mNowView.showFailedMsg(msg);
                mNowView.hideStoreOrMatchLoading();
            }
        });
    }

    // 图片上传至七牛云
    public void qiNiuUpload(final String userToken,
                            final DeviceBean deviceBean,
                            final String localFilePath) {
        QiNiuUtil.getQiNiuTokenFromSmartApi(
                userToken,
                deviceBean.getImg(),
                new GetTokenListener() {
                    @Override
                    public void onSuccess(final String qiNiuToken) {
                        mNowView.hideLoading();
                        mNowView.showSuccessMsg("图片上传成功，正在设置数据库...");
                        mNowView.showLoading();
                        final DeviceBean device = new DeviceBean();
                        device.setImg(SmartApiUrls.QiNiuBucket + deviceBean.getImg());
                        QiNiuUtil.uploadImgAndStoreSmartApiDB(localFilePath,
                                deviceBean.getImg(),
                                qiNiuToken,
                                SmartApiUrls.DEVICE_IMG + deviceBean.getHub_id(),
                                userToken,
                                JsonUtil.serialize(device),
                                new UploadListener() {
                                    @Override
                                    public void onSuccess(final String msg) {
                                        mNowView.showUploadSuccessMsg(msg);
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
}
