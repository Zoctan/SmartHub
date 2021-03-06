package com.zoctan.smarthub.hubDetail.model.impl;

import com.google.gson.Gson;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.RequestType;
import com.okhttplib.callback.Callback;
import com.zoctan.smarthub.api.SmartApiUrls;
import com.zoctan.smarthub.beans.TimerBean;
import com.zoctan.smarthub.hubDetail.model.HubDetailTimerModel;
import com.zoctan.smarthub.response.Response;
import com.zoctan.smarthub.response.ResponseTimerList;
import com.zoctan.smarthub.utils.JsonUtil;

import java.io.IOException;

public class HubDetailTimerModelImpl implements HubDetailTimerModel {
    @Override
    public void loadHubTimerList(final String token, final String hubOneNetId, final onLoadHubTimerListListener listener) {
        final String url = SmartApiUrls.TIMERS + "/" + hubOneNetId;

        final String headerKey = SmartApiUrls.HEADER_KEY;
        final String headerValue = SmartApiUrls.HEADER_VALUE + token;
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
                        final ResponseTimerList responseList = JsonUtil.getObjectFromHttpInfo(info, ResponseTimerList.class);
                        if (responseList.getMsg().equals("ok")) {
                            listener.onSuccess(responseList.getResult());
                        }
                    }
                });
    }

    @Override
    public void doHubTimer(final String token, final TimerBean timer, final onDoHubTimerListListener listener) {
        final String url = SmartApiUrls.TIMERS + "/" + timer.getHub_id();
        final String headerKey = SmartApiUrls.HEADER_KEY;
        final String headerValue = SmartApiUrls.HEADER_VALUE + token;
        final int requestType;
        switch (timer.getAction()) {
            case "add":
                requestType = RequestType.POST;
                break;
            case "close":
            case "open":
            case "update":
                requestType = RequestType.PUT;
                break;
            default:
                requestType = RequestType.DELETE;
                break;
        }
        OkHttpUtil.getDefault(this).doAsync(
                HttpInfo.Builder()
                        .setUrl(url)
                        .setRequestType(requestType)
                        .addHead(headerKey, headerValue)
                        .addParamJson(new Gson().toJson(timer))
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
}
