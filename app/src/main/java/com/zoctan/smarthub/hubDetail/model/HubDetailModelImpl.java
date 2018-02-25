package com.zoctan.smarthub.hubDetail.model;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.RequestType;
import com.okhttplib.callback.Callback;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.zoctan.smarthub.api.DeviceUrls;
import com.zoctan.smarthub.api.HubUrls;
import com.zoctan.smarthub.api.OneNetUrls;
import com.zoctan.smarthub.api.QiNiuUrls;
import com.zoctan.smarthub.beans.DeviceBean;
import com.zoctan.smarthub.beans.OneNetDataPointsBean;
import com.zoctan.smarthub.beans.TimerBean;
import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.response.Response;
import com.zoctan.smarthub.response.ResponseDevice;
import com.zoctan.smarthub.response.ResponseOneNetDataPoints;
import com.zoctan.smarthub.response.ResponseOneNetDataStreams;
import com.zoctan.smarthub.response.ResponseTimerList;
import com.zoctan.smarthub.utils.JsonUtil;

import org.json.JSONObject;

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
                        listener.onNowFailure(response);
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
        final String url = DeviceUrls.DEVICE + "/" + oneNetId;
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
                        listener.onNowFailure(response);
                    }

                    @Override
                    public void onSuccess(final HttpInfo info) throws IOException {
                        final ResponseDevice responseDevice = JsonUtil.getObjectFromHttpInfo(info, ResponseDevice.class);
                        if (responseDevice.getMsg().equals("ok")) {
                            listener.onNowSuccess(responseDevice.getResult());
                        } else {
                            final DeviceBean deviceBean = new DeviceBean();
                            deviceBean.setName(responseDevice.getError());
                            listener.onNowSuccess(deviceBean);
                        }
                    }
                });
    }

    @Override
    public void doDevice(final DeviceBean deviceBean, final String token, final String action, final Listener listener) {
        final String url = HubUrls.HUBS + "/device/" + deviceBean.getHub_id();
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
                        listener.onNowFailure(response);
                    }

                    @Override
                    public void onSuccess(final HttpInfo info) throws IOException {
                        final Response response = JsonUtil.getObjectFromHttpInfo(info, Response.class);
                        if (response.getMsg().equals("ok")) {
                            listener.onDoDeviceSuccess(response.getResult());
                        } else {
                            listener.onNowFailure(response.getError());
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
                        listener.onNowFailure(response);
                    }

                    @Override
                    public void onSuccess(final HttpInfo info) throws IOException {
                        final Response response = JsonUtil.getObjectFromHttpInfo(info, Response.class);
                        listener.onNowSuccess(response.getMsg());
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
                        listener.onSpareFailure(response);
                    }

                    @Override
                    public void onSuccess(final HttpInfo info) throws IOException {
                        final ResponseOneNetDataPoints responseOneNetDataPoints = JsonUtil.getObjectFromHttpInfo(info, ResponseOneNetDataPoints.class);
                        if (responseOneNetDataPoints.getError().equals("succ")) {
                            final OneNetDataPointsBean oneNetDataPoints = responseOneNetDataPoints.getData();
                            listener.onSpareSuccess(oneNetDataPoints);
                        } else {
                            listener.onSpareFailure(responseOneNetDataPoints.getError());
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

    @Override
    public void getQiNiuToken(final UserBean userBean, final DeviceBean deviceBean, final UploadListener listener) {
        final String url = QiNiuUrls.QiNiu + "/" + deviceBean.getImg();
        final String headerKey = "Authorization";
        final String headerValue = "Smart " + userBean.getToken();
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
                        if (response.getMsg().equals("ok")) {
                            listener.onSuccess(response.getResult());
                        }
                    }
                });
    }

    // 上传图片到七牛云
    @Override
    public void uploadImg(final DeviceBean deviceBean, final String token, final String qiNiuToken, final String photoPath, final UploadListener listener) {
        final UploadManager uploadManager = new UploadManager();
        final String url = DeviceUrls.IMG + "/" + deviceBean.getHub_id();
        uploadManager.put(photoPath, deviceBean.getImg(), qiNiuToken, new UpCompletionHandler() {
            @Override
            public void complete(final String key, final ResponseInfo info, final JSONObject res) {
                // info.error中包含了错误信息，可打印调试
                if (info.isOK()) {
                    // 上传成功后将key值上传到自己的服务器
                    final String headerKey = "Authorization";
                    final String headerValue = "Smart " + token;
                    deviceBean.setImg("http://p0qgwnuel.bkt.clouddn.com/" + deviceBean.getImg());
                    OkHttpUtil.getDefault(this).doAsync(
                            HttpInfo.Builder()
                                    .setUrl(url)
                                    .setRequestType(RequestType.PUT)
                                    .addParamJson(new Gson().toJson(deviceBean))
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
                                    if (response.getMsg().equals("ok")) {
                                        listener.onSuccess(response.getResult());
                                    } else {
                                        listener.onFailure(response.getResult());
                                    }
                                }
                            });
                } else {
                    listener.onFailure(info.error);
                }
            }
        }, new UploadOptions(null, null, false,
                new UpProgressHandler() {
                    @Override
                    public void progress(final String key, final double percent) {
                        LogUtils.d(key + ": " + percent);
                    }
                }, null));
    }
}
