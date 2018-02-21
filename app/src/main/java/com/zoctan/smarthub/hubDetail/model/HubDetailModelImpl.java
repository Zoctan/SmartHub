package com.zoctan.smarthub.hubDetail.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.RequestType;
import com.okhttplib.callback.Callback;
import com.zoctan.smarthub.api.HubUrls;
import com.zoctan.smarthub.api.OneNetUrls;
import com.zoctan.smarthub.beans.OneNetDataPointsBean;
import com.zoctan.smarthub.beans.OneNetDataStreamsBean;
import com.zoctan.smarthub.beans.TimerBean;
import com.zoctan.smarthub.response.ResponseOneNet;
import com.zoctan.smarthub.response.ResponseOneNetDataStreams;
import com.zoctan.smarthub.response.ResponseTimer;
import com.zoctan.smarthub.utils.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HubDetailModelImpl implements HubDetailModel {
    @Override
    public void loadHubNowList(final String oneNetId, final String dataStreamIds, final OnLoadHubDetailNowListener listener) {
        String headerKey = "api-key";
        String headerValue = "nJVyiaj5Y297Fc6Q=bUYVWnz2=0=";
        String url = OneNetUrls.buildDataStreamsGet(oneNetId, dataStreamIds);

        OkHttpUtil.getDefault(this).doGetAsync(
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
                        ResponseOneNetDataStreams responseOneNetDataStreams = JsonUtil.deserialize(jsonObj, ResponseOneNetDataStreams.class);
                        if (responseOneNetDataStreams.getError().equals("succ")) {
                            final List<OneNetDataStreamsBean> oneNetDataStreamList = responseOneNetDataStreams.getData();
                            listener.onSuccess(oneNetDataStreamList);
                        } else {
                            listener.onSuccess(null);
                        }
                    }
                });
    }

    @Override
    public void loadHubSpareList(final String oneNetId, final String dataStreamIds, final Map params, final OnLoadHubSpareListListener listener) {
        String headerKey = "api-key";
        String headerValue = "nJVyiaj5Y297Fc6Q=bUYVWnz2=0=";
        String url = OneNetUrls.buildDataPointsGet(oneNetId, dataStreamIds, params);

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
                        ResponseOneNet responseOneNet = JsonUtil.deserialize(jsonObj, ResponseOneNet.class);
                        if (responseOneNet.getErrno() == 0) {
                            final OneNetDataPointsBean oneNetDataPoints = (OneNetDataPointsBean) responseOneNet.getData();
                            listener.onSuccess(oneNetDataPoints);
                        } else {
                            listener.onFailure(responseOneNet.getError());
                        }
                    }
                });
    }

    @Override
    public void loadHubTimerList(final String token, final String hubOneNetId, final OnLoadHubDetailTimerListener listener) {
        String url = HubUrls.TIMERS + "/" + hubOneNetId;
        String headerKey = "Authorization";
        String headerValue = "Smart " + token;
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
                        ResponseTimer responseTimer = JsonUtil.deserialize(jsonObj, ResponseTimer.class);
                        if (responseTimer.getMsg().equals("ok")) {
                            final List<TimerBean> timerList = responseTimer.getResult();
                            listener.onSuccess(timerList);
                        } else {
                            listener.onSuccess(null);
                        }
                    }
                });
    }

    @Override
    public void doHubTimer(final String token, final String hubOneNetId,
                           final TimerBean timer,
                           final OnDoHubTimerListener listener) {
        String url = HubUrls.TIMERS + "/" + hubOneNetId;
        String headerKey = "Authorization";
        String headerValue = "Smart " + token;
        OkHttpUtil.getDefault(this).doAsync(
                HttpInfo.Builder()
                        .setUrl(url)
                        .setRequestType(RequestType.PUT)
                        .addHead(headerKey, headerValue)
                        .addParamJson(new Gson().toJson(timer))
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
                        ResponseTimer responseTimer = JsonUtil.deserialize(jsonObj, ResponseTimer.class);
                        if (responseTimer.getMsg().equals("ok")) {
                            String msg;
                            switch (timer.getAction()) {
                                case "add":
                                    msg = "成功添加";
                                    break;
                                case "delete":
                                    msg = "成功修改";
                                    break;
                                default:
                                    msg = "成功删除";
                                    break;
                            }
                            listener.onSuccess(msg);
                        } else {
                            listener.onFailure(responseTimer.getError());
                        }
                    }
                });
    }

}
