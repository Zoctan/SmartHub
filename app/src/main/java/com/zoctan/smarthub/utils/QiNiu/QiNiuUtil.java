package com.zoctan.smarthub.utils.QiNiu;

import com.blankj.utilcode.util.LogUtils;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.RequestType;
import com.okhttplib.callback.Callback;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.zoctan.smarthub.api.SmartApiUrls;
import com.zoctan.smarthub.response.Response;
import com.zoctan.smarthub.utils.JsonUtil;

import org.json.JSONObject;

import java.io.IOException;

public class QiNiuUtil {
    public static void getQiNiuTokenFromSmartApi(final String userToken,
                                                 final String fileName,
                                                 final GetTokenListener listener) {
        final String url = SmartApiUrls.QiNiu + fileName;
        final String headerKey = SmartApiUrls.HEADER_KEY;
        final String headerValue = SmartApiUrls.HEADER_VALUE + userToken;
        OkHttpUtil.getDefault(null).doAsync(
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
    public static void uploadImgAndStoreSmartApiDB(final String localFilePath,
                                                   final String fileName,
                                                   final String qiNiuToken,
                                                   final String smartApiUrl,
                                                   final String userToken,
                                                   final String json,
                                                   final UploadListener listener) {
        final UploadManager uploadManager = new UploadManager();
        uploadManager.put(localFilePath, fileName, qiNiuToken, new UpCompletionHandler() {
            @Override
            public void complete(final String key, final ResponseInfo info, final JSONObject res) {
                // info.error中包含了错误信息，可打印调试
                if (info.isOK()) {
                    // 上传成功后将key值上传到自己的服务器
                    final String headerKey = SmartApiUrls.HEADER_KEY;
                    final String headerValue = SmartApiUrls.HEADER_VALUE + userToken;
                    OkHttpUtil.getDefault(null).doAsync(
                            HttpInfo.Builder()
                                    .setUrl(smartApiUrl)
                                    .setRequestType(RequestType.PUT)
                                    .addParamJson(json)
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