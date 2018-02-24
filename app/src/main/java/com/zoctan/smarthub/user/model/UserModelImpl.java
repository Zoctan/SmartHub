package com.zoctan.smarthub.user.model;

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
import com.zoctan.smarthub.api.QiNiuUrls;
import com.zoctan.smarthub.api.UserUrls;
import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.response.Response;
import com.zoctan.smarthub.response.ResponseUser;
import com.zoctan.smarthub.utils.JsonUtil;

import org.json.JSONObject;

import java.io.IOException;


public class UserModelImpl implements UserModel {
    @Override
    public void loginOrRegister(final Boolean login, final UserBean user,
                                final Listener listener) {
        String url = UserUrls.TOKENS;
        if (!login) {
            url = UserUrls.USERS;
        }
        OkHttpUtil.getDefault(this).doAsync(
                HttpInfo.Builder()
                        .setUrl(url)
                        .setRequestType(RequestType.POST)
                        .addParamJson(new Gson().toJson(user))
                        .build(),
                new Callback() {
                    @Override
                    public void onFailure(final HttpInfo info) throws IOException {
                        final String response = info.getRetDetail();
                        listener.onFailure(response);
                    }

                    @Override
                    public void onSuccess(final HttpInfo info) throws IOException {
                        // 将Json对象转换为User实体
                        final ResponseUser responseUser = JsonUtil.getObjectFromHttpInfo(info, ResponseUser.class);
                        if (responseUser.getMsg().equals("ok")) {
                            user.setId(responseUser.getResult().getId());
                            user.setAvatar(responseUser.getResult().getAvatar());
                            user.setToken(responseUser.getResult().getToken());
                            user.setPhone(responseUser.getResult().getPhone());
                            listener.onSuccess(user);
                        } else {
                            listener.onFailure(responseUser.getError());
                        }
                    }
                });
    }

    @Override
    public void update(final String url, final UserBean user,
                       final String token,
                       final Listener listener) {
        final String headerKey = "Authorization";
        final String headerValue = "Smart " + token;
        OkHttpUtil.getDefault(this).doAsync(
                HttpInfo.Builder()
                        .setUrl(url)
                        .setRequestType(RequestType.PUT)
                        .addParamJson(new Gson().toJson(user))
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
                            listener.onFailure(response.getError());
                        }
                    }
                });
    }

    @Override
    public void getQiNiuToken(final UserBean userBean, final Listener listener) {
        final String url = QiNiuUrls.QiNiu + "/" + userBean.getUsername();
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

    @Override
    public void uploadAvatar(final UserBean userBean, final String token, final String photoPath, final UploadAvatarListener listener) {
        // 上传图片到七牛云
        final UploadManager uploadManager = new UploadManager();
        uploadManager.put(photoPath, userBean.getUsername(), token, new UpCompletionHandler() {
            @Override
            public void complete(final String key, final ResponseInfo info, final JSONObject res) {
                // info.error中包含了错误信息，可打印调试
                if (info.isOK()) {
                    // 上传成功后将key值上传到自己的服务器
                    OkHttpUtil.getDefault(this).doAsync(
                            HttpInfo.Builder()
                                    .setUrl(UserUrls.AVATAR)
                                    .setRequestType(RequestType.PUT)
                                    .addParamJson(new Gson().toJson(userBean))
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
                                        listener.onSuccess(userBean.getAvatar(), response.getResult());
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
