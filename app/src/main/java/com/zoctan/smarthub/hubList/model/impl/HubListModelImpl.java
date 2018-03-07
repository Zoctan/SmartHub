package com.zoctan.smarthub.hubList.model.impl;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.RequestType;
import com.okhttplib.callback.Callback;
import com.zoctan.smarthub.api.SmartApiUrls;
import com.zoctan.smarthub.beans.HubBean;
import com.zoctan.smarthub.hubList.model.HubListModel;
import com.zoctan.smarthub.response.Response;
import com.zoctan.smarthub.response.ResponseHubList;
import com.zoctan.smarthub.utils.JsonUtil;

import java.io.IOException;
import java.util.List;

public class HubListModelImpl implements HubListModel {

    @Override
    public void loadHubList(final String token,
                            final onLoadHubListListener listener) {
        final String url = SmartApiUrls.HUBS;
        final String headerKey = SmartApiUrls.HEADER_KEY;
        final String headerValue = SmartApiUrls.HEADER_VALUE + token;
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
    public void hubOpenClose(final HubBean hub,
                             final String token,
                             final onHubOpenCloseListener listener) {
        final String headerKey = SmartApiUrls.HEADER_KEY;
        final String headerValue = SmartApiUrls.HEADER_VALUE + token;
        final int status = hub.getAction().equals("off") ? 0 : 1;
        final String url = SmartApiUrls.setOrderUrl(hub.getOnenet_id(), "turn", status);
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
                        listener.onSuccess(response.getResult());
                    }
                });
    }

    @Override
    public void doHub(final HubBean hub,
                      final String token,
                      final onDoHubListener listener) {
        final String url;
        final int requestType;
        switch (hub.getAction()) {
            case "delete":
                url = SmartApiUrls.HUBS + "/" + hub.getOnenet_id();
                requestType = RequestType.DELETE;
                break;
            case "update":
                url = SmartApiUrls.HUBS + "/" + hub.getOnenet_id();
                requestType = RequestType.PUT;
                break;
            default:
                url = SmartApiUrls.HUBS;
                requestType = RequestType.POST;
                break;
        }
        final String headerKey = SmartApiUrls.HEADER_KEY;
        final String headerValue = SmartApiUrls.HEADER_VALUE + token;
        OkHttpUtil.getDefault(this).doAsync(
                HttpInfo.Builder()
                        .setUrl(url)
                        .setRequestType(requestType)
                        .addHead(headerKey, headerValue)
                        .addParamJson(JsonUtil.serialize(hub))
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
