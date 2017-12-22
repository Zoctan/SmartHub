package com.zoctan.smarthub.hubList.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.RequestType;
import com.okhttplib.callback.Callback;
import com.zoctan.smarthub.api.HubUrls;
import com.zoctan.smarthub.api.OneNetUrls;
import com.zoctan.smarthub.beans.HubBean;
import com.zoctan.smarthub.response.ResponseHubList;
import com.zoctan.smarthub.response.ResponseOneNetCmds;
import com.zoctan.smarthub.utils.JsonUtil;

import java.io.IOException;
import java.util.List;

public class HubListModelImpl implements HubListModel {

    @Override
    public void loadHubList(final String token, final OnLoadHubListListener listener) {
        String url = HubUrls.HUBS;
        String headerKey = "Authorization";
        String headerValue = "Smart " + token;
        // https://github.com/MrZhousf/OkHttp3
        OkHttpUtil.getDefault(this).doAsync(
                HttpInfo.Builder()
                        .setUrl(url)
                        .setRequestType(RequestType.GET)
                        .addHead(headerKey, headerValue)
                        .build(),
                new Callback() {
                    @Override
                    public void onFailure(HttpInfo info) throws IOException {
                        String response = info.getRetDetail();
                        listener.onFailure(response);
                    }

                    @Override
                    public void onSuccess(HttpInfo info) throws IOException {
                        String response = info.getRetDetail();
                        // 创建一个JsonParser
                        JsonParser parser = new JsonParser();
                        // 将res转换成Json对象
                        JsonObject jsonObj = parser.parse(response).getAsJsonObject();
                        // 将Json对象转换为User实体
                        ResponseHubList responseHubList = JsonUtil.deserialize(jsonObj, ResponseHubList.class);
                        if (responseHubList.getMsg().equals("ok")) {
                            List<HubBean> hubList = responseHubList.getResult();
                            listener.onSuccess(hubList);
                        } else {
                            listener.onSuccess(null);
                        }
                    }
                });
    }

    @Override
    public void hubOpenClose(final String oneNetId, final String order,
                             final OnListener listener) {
        String headerKey = "api-key";
        String headerValue = "nJVyiaj5Y297Fc6Q=bUYVWnz2=0=";
        String url = OneNetUrls.buildOrderSend(oneNetId);
        final String msg;
        switch (order) {
            case "off":
                msg = "成功关闭";
                break;
            default:
                msg = "成功开启";
                break;
        }
        OkHttpUtil.getDefault(this).doAsync(
                HttpInfo.Builder()
                        .setUrl(url)
                        .setRequestType(RequestType.GET)
                        .addParam("xx", "xx")
                        .addHead(headerKey, headerValue)
                        .build(),
                new Callback() {
                    @Override
                    public void onFailure(HttpInfo info) throws IOException {
                        String response = info.getRetDetail();
                        listener.onFailure(response);
                    }

                    @Override
                    public void onSuccess(HttpInfo info) throws IOException {
                        String response = info.getRetDetail();
                        JsonParser parser = new JsonParser();
                        JsonObject jsonObj = parser.parse(response).getAsJsonObject();
                        ResponseOneNetCmds responseOneNetCmds = JsonUtil.deserialize(jsonObj, ResponseOneNetCmds.class);
                        if (responseOneNetCmds.getError().equals("succ")) {
                            listener.onSuccess(msg);
                        }
                    }
                });
    }

    @Override
    public void doHub(final String action, final String token,
                      final HubBean hub, final OnListener listener) {
        String url = HubUrls.HUBS;
        int requestType = RequestType.POST;
        final String msg;
        switch (action) {
            case "delete":
                url += "/" + hub.getOnenet_id();
                requestType = RequestType.DELETE;
                msg = "成功删除";
                break;
            case "update":
                url += "/" + hub.getOnenet_id();
                requestType = RequestType.PUT;
                msg = "成功修改";
                break;
            default:
                msg = "成功添加";
                break;
        }
        String headerKey = "Authorization";
        String headerValue = "Smart " + token;
        OkHttpUtil.getDefault(this).doAsync(
                HttpInfo.Builder()
                        .setUrl(url)
                        .setRequestType(requestType)
                        .addHead(headerKey, headerValue)
                        .addParamJson(new Gson().toJson(hub))
                        .build(),
                new Callback() {
                    @Override
                    public void onFailure(HttpInfo info) throws IOException {
                        String response = info.getRetDetail();
                        listener.onFailure(response);
                    }

                    @Override
                    public void onSuccess(HttpInfo info) throws IOException {
                        String response = info.getRetDetail();
                        // 创建一个JsonParser
                        JsonParser parser = new JsonParser();
                        // 将res转换成Json对象
                        JsonObject jsonObj = parser.parse(response).getAsJsonObject();
                        // 将Json对象转换为User实体
                        ResponseHubList responseHubList = JsonUtil.deserialize(jsonObj, ResponseHubList.class);
                        if (responseHubList.getMsg().equals("ok")) {
                            listener.onSuccess(msg);
                        } else {
                            listener.onFailure(responseHubList.getError());
                        }
                    }
                });
    }
}