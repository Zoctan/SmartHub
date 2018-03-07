package com.zoctan.smarthub.hubDetail.model.impl;

import com.google.gson.Gson;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.RequestType;
import com.okhttplib.callback.Callback;
import com.zoctan.smarthub.api.OneNetUrls;
import com.zoctan.smarthub.api.SmartApiUrls;
import com.zoctan.smarthub.beans.DeviceBean;
import com.zoctan.smarthub.hubDetail.model.HubDetailNowModel;
import com.zoctan.smarthub.response.Response;
import com.zoctan.smarthub.response.ResponseDevice;
import com.zoctan.smarthub.response.ResponseOneNetDataStreams;
import com.zoctan.smarthub.utils.JsonUtil;

import java.io.IOException;

public class HubDetailNowModelImpl implements HubDetailNowModel {
    @Override
    public void loadHubNowList(final String oneNetId, final String dataStreamIds, final onLoadHubNowListListener listener) {
        final String headerKey = OneNetUrls.HEADER_KEY;
        final String headerValue = OneNetUrls.HEADER_VALUE;
        final String url = OneNetUrls.buildDataStreamsGet(oneNetId, dataStreamIds);

        OkHttpUtil.getDefault(this).doGetAsync(
                HttpInfo.Builder()
                        .setUrl(url)
                        .setRequestType(RequestType.GET)
                        .addHead(headerKey, headerValue)
                        .build(),
                new Callback() {
                    @Override
                    public void onFailure(final HttpInfo info) throws IOException {
                        final String response = info.getRetDetail();
                        listener.onFailure(response);
                    }

                    @Override
                    public void onSuccess(final HttpInfo info) throws IOException {
                        final ResponseOneNetDataStreams responseList = JsonUtil.getObjectFromHttpInfo(info, ResponseOneNetDataStreams.class);
                        if (responseList.getError().equals("succ")) {
                            listener.onSuccess(responseList.getData());
                        }
                    }
                });
    }

    @Override
    public void loadHubDevice(final String oneNetId, final String token, final onLoadHubDeviceListener listener) {
        final String url = SmartApiUrls.DEVICE + "/" + oneNetId;
        final String headerKey = SmartApiUrls.HEADER_KEY;
        final String headerValue = SmartApiUrls.HEADER_VALUE + token;
        OkHttpUtil.getDefault(this).doGetAsync(
                HttpInfo.Builder()
                        .setUrl(url)
                        .setRequestType(RequestType.GET)
                        .addHead(headerKey, headerValue)
                        .build(),
                new Callback() {
                    @Override
                    public void onFailure(final HttpInfo info) throws IOException {
                        final String response = info.getRetDetail();
                        listener.onFailure(response);
                    }

                    @Override
                    public void onSuccess(final HttpInfo info) throws IOException {
                        final ResponseDevice responseDevice = JsonUtil.getObjectFromHttpInfo(info, ResponseDevice.class);
                        if (responseDevice.getMsg().equals("ok")) {
                            listener.onSuccess(responseDevice.getResult());
                        } else {
                            final DeviceBean deviceBean = new DeviceBean();
                            deviceBean.setName(responseDevice.getError());
                            listener.onSuccess(deviceBean);
                        }
                    }
                });
    }

    @Override
    public void doDevice(final DeviceBean deviceBean, final String token, final onDoDeviceListener listener) {
        final String url = SmartApiUrls.DEVICE + "/" + deviceBean.getHub_id();
        final String headerKey = SmartApiUrls.HEADER_KEY;
        final String headerValue = SmartApiUrls.HEADER_VALUE + token;
        int requestType = RequestType.POST;
        if (deviceBean.getAction().equals("update")) {
            requestType = RequestType.PUT;
        }
        OkHttpUtil.getDefault(this).doAsync(
                HttpInfo.Builder()
                        .setUrl(url)
                        .setRequestType(requestType)
                        .addHead(headerKey, headerValue)
                        .addParamJson(new Gson().toJson(deviceBean))
                        .build(),
                new Callback() {
                    @Override
                    public void onFailure(final HttpInfo info) throws IOException {
                        final String response = info.getRetDetail();
                        listener.onFailure(response);
                    }

                    @Override
                    public void onSuccess(final HttpInfo info) throws IOException {
                        final Response response = JsonUtil.getObjectFromHttpInfo(info, Response.class);
                        if (response.getMsg().equals("ok")) {
                            listener.onSuccess(response.getResult());
                        } else {
                            listener.onFailure(response.getError());
                        }
                    }
                });
    }

    @Override
    public void sendOrder(final String oneNetId, final String token, final String order, final sendOrderListener listener) {
        final String headerKey = SmartApiUrls.HEADER_KEY;
        final String headerValue = SmartApiUrls.HEADER_VALUE + token;
        final String url = SmartApiUrls.setOrderUrl(oneNetId, order, 1);
        OkHttpUtil.getDefault(this).doAsync(
                HttpInfo.Builder()
                        .setUrl(url)
                        .setRequestType(RequestType.GET)
                        .addHead(headerKey, headerValue)
                        .build(),
                new Callback() {
                    @Override
                    public void onFailure(final HttpInfo info) throws IOException {
                        final String response = info.getRetDetail();
                        listener.onFailure(response);
                    }

                    @Override
                    public void onSuccess(final HttpInfo info) throws IOException {
                        final Response response = JsonUtil.getObjectFromHttpInfo(info, Response.class);
                        switch (order) {
                            case "store":
                                listener.setHubStore(response.getResult().equals("1"));
                                break;
                            case "match":
                                listener.setHubMatch(response.getResult());
                                break;
                            default:
                                listener.onSuccess(response.getResult());
                                break;
                        }
                    }
                });
    }
}
