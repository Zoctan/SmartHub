package com.zoctan.smarthub.hubDetail.model.impl;

import com.google.gson.Gson;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.RequestType;
import com.okhttplib.callback.Callback;
import com.zoctan.smarthub.api.HubUrls;
import com.zoctan.smarthub.beans.TimerBean;
import com.zoctan.smarthub.hubDetail.model.HubDetailTimerModel;
import com.zoctan.smarthub.response.Response;
import com.zoctan.smarthub.response.ResponseTimerList;
import com.zoctan.smarthub.utils.JsonUtil;

import java.io.IOException;

public class HubDetailTimerModelImpl implements HubDetailTimerModel {
    @Override
    public void loadHubTimerList(final String token, final String hubOneNetId, final Listener listener) {
        final String url = HubUrls.TIMERS + "/" + hubOneNetId;
        final String headerKey = "Authorization";
        final String headerValue = "Smart " + token;
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
                        listener.onTimerFailure(response);
                    }

                    @Override
                    public void onSuccess(final HttpInfo info) throws IOException {
                        final ResponseTimerList responseList = JsonUtil.getObjectFromHttpInfo(info, ResponseTimerList.class);
                        if (responseList.getMsg().equals("ok")) {
                            listener.onTimerListSuccess(responseList.getResult());
                        }
                    }
                });
    }

    @Override
    public void doHubTimer(final String token, final TimerBean timer, final Listener listener) {
        final String url = HubUrls.TIMERS + "/" + timer.getHub_id();
        final String headerKey = "Authorization";
        final String headerValue = "Smart " + token;
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
                        listener.onTimerFailure(response);
                    }

                    @Override
                    public void onSuccess(final HttpInfo info) throws IOException {
                        final Response response = JsonUtil.getObjectFromHttpInfo(info, Response.class);
                        if (response.getMsg().equals("ok")) {
                            listener.onTimerSuccess(response.getResult());
                        } else {
                            listener.onTimerFailure(response.getError());
                        }
                    }
                });
    }
}
