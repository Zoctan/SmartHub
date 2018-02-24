package com.zoctan.smarthub.hubDetail.model;

import com.google.gson.Gson;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.RequestType;
import com.okhttplib.callback.Callback;
import com.zoctan.smarthub.api.HubUrls;
import com.zoctan.smarthub.api.OneNetUrls;
import com.zoctan.smarthub.beans.DeviceBean;
import com.zoctan.smarthub.beans.OneNetDataPointsBean;
import com.zoctan.smarthub.beans.TimerBean;
import com.zoctan.smarthub.response.Response;
import com.zoctan.smarthub.response.ResponseDevice;
import com.zoctan.smarthub.response.ResponseOneNetDataPoints;
import com.zoctan.smarthub.response.ResponseOneNetDataStreams;
import com.zoctan.smarthub.response.ResponseTimerList;
import com.zoctan.smarthub.utils.JsonUtil;

import java.io.IOException;
import java.util.Map;

public class HubDetailModelImpl implements HubDetailModel {
    @Override
    public void loadHubNowList(final String oneNetId, final String dataStreamIds, final Listener listener) {
        final String headerKey = "api-key";
        final String headerValue = "nJVyiaj5Y297Fc6Q=bUYVWnz2=0=";
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
                            listener.onOneNetDataStreamSuccess(responseList.getData());
                        }
                    }
                });
    }

    @Override
    public void loadHubDevice(final String oneNetId, final String token, final Listener listener) {
        final String url = HubUrls.HUBS + "/device/" + oneNetId;
        final String headerKey = "Authorization";
        final String headerValue = "Smart " + token;
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
    public void doDevice(final DeviceBean deviceBean, final String token, final String action, final Listener listener) {
        final String url = HubUrls.HUBS + "/device/" + deviceBean.getOnenet_id();
        final String headerKey = "Authorization";
        final String headerValue = "Smart " + token;
        int requestType = RequestType.POST;
        if (action.equals("update")) {
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
                            listener.onDoDeviceSuccess(response.getResult());
                        } else {
                            listener.onFailure(response.getError());
                        }
                    }
                });
    }

    @Override
    public void resetHub(final String oneNetId, final String token, final Listener listener) {
        final String headerKey = "Authorization";
        final String headerValue = "Smart " + token;
        final String url = HubUrls.HUBS + "/" + oneNetId + "/order?order=reset&status=1";
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
    public void loadHubSpareList(final String oneNetId, final String dataStreamIds, final Map params, final Listener listener) {
        final String headerKey = "api-key";
        final String headerValue = "nJVyiaj5Y297Fc6Q=bUYVWnz2=0=";
        final String url = OneNetUrls.buildDataPointsGet(oneNetId, dataStreamIds, params);

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
                        final ResponseOneNetDataPoints responseOneNetDataPoints = JsonUtil.getObjectFromHttpInfo(info, ResponseOneNetDataPoints.class);
                        if (responseOneNetDataPoints.getError().equals("succ")) {
                            final OneNetDataPointsBean oneNetDataPoints = responseOneNetDataPoints.getData();
                            listener.onSuccess(oneNetDataPoints);
                        } else {
                            listener.onFailure(responseOneNetDataPoints.getError());
                        }
                    }
                });
    }

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
                        listener.onFailure(response);
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
