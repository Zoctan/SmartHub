package com.zoctan.smarthub.hubList.model.impl;

import com.google.gson.Gson;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.RequestType;
import com.okhttplib.callback.Callback;
import com.zoctan.smarthub.api.HubUrls;
import com.zoctan.smarthub.beans.HubBean;
import com.zoctan.smarthub.hubList.model.HubListModel;
import com.zoctan.smarthub.response.Response;
import com.zoctan.smarthub.response.ResponseHubList;
import com.zoctan.smarthub.utils.JsonUtil;

import java.io.IOException;
import java.util.List;

public class HubListModelImpl implements HubListModel {

    @Override
    public void loadHubList(final String token, final Listener listener) {
        final String url = HubUrls.HUBS;
        final String headerKey = "Authorization";
        final String headerValue = "Smart " + token;
        // https://github.com/MrZhousf/OkHttp3
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
                        final ResponseHubList responseList = JsonUtil.getObjectFromHttpInfo(info, ResponseHubList.class);
                        if (responseList.getMsg().equals("ok")) {
                            final List<HubBean> hubList = responseList.getResult();
                            listener.onSuccess(hubList);
                        }
                    }
                });
    }

    @Override
    public void hubOpenClose(final String oneNetId, final String order,
                             final String token, final Listener listener) {
        final String headerKey = "Authorization";
        final String headerValue = "Smart " + token;
        int status = 1;
        if (order.equals("off")) {
            status = 0;
        }
        final String url = HubUrls.HUBS + "/" + oneNetId + "/order?order=turn&status=" + status;
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
                        listener.onSuccess(response.getMsg());
                    }
                });
    }

    @Override
    public void doHub(final String action, final String token,
                      final HubBean hub, final Listener listener) {
        String url = HubUrls.HUBS;
        int requestType = RequestType.POST;
        switch (action) {
            case "delete":
                url += "/" + hub.getOnenet_id();
                requestType = RequestType.DELETE;
                break;
            case "update":
                url += "/" + hub.getOnenet_id();
                requestType = RequestType.PUT;
                break;
        }
        final String headerKey = "Authorization";
        final String headerValue = "Smart " + token;
        OkHttpUtil.getDefault(this).doAsync(
                HttpInfo.Builder()
                        .setUrl(url)
                        .setRequestType(requestType)
                        .addHead(headerKey, headerValue)
                        .addParamJson(new Gson().toJson(hub))
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
