package com.zoctan.smarthub.user.model;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.RequestType;
import com.okhttplib.callback.Callback;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.zoctan.smarthub.api.UserUrls;
import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.response.ResponseUser;
import com.zoctan.smarthub.utils.JsonUtil;
import com.zoctan.smarthub.utils.QiNiuCloudAuth;

import org.json.JSONObject;

import java.io.IOException;


public class UserModelImpl implements UserModel {
    @Override
    public void loginOrRegister(final Boolean login, final UserBean user,
                                final LoginUserListener listener) {
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
                        ResponseUser mResponseUser = JsonUtil.deserialize(jsonObj, ResponseUser.class);
                        if (mResponseUser.getMsg().equals("ok")) {
                            user.setId(mResponseUser.getResult().get(0).getId());
                            user.setAvatar(mResponseUser.getResult().get(0).getAvatar());
                            // not store password but token
                            user.setPassword(mResponseUser.getResult().get(0).getToken());
                            user.setPhone(mResponseUser.getResult().get(0).getPhone());
                            listener.onSuccess(user);
                        } else {
                            listener.onFailure(mResponseUser.getError());
                        }
                    }
                });
    }

    // 修改
    @Override
    public void modify(final String url, final UserBean user,
                       final ModifyUserListener listener) {
        OkHttpUtil.getDefault(this).doAsync(
                HttpInfo.Builder()
                        .setUrl(url)
                        .setRequestType(RequestType.PUT)
                        .addParamJson(new Gson().toJson(user))
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
                        ResponseUser mResponseUser = JsonUtil.deserialize(jsonObj, ResponseUser.class);
                        if (mResponseUser.getMsg().equals("ok")) {
                            listener.onSuccess();
                        } else {
                            listener.onFailure(mResponseUser.getError());
                        }
                    }
                });
    }

    @Override
    public void uploadAvatar(final String userName, final String photoPath, final UploadAvatarListener listener) {
        // 此处填你自己的AccessKey
        final String accessKey = "2PQFWuKe3VZAsxFLN9LQXncHaRNgAQImcenvnVwy";
        // 此处填你自己的SecretKey
        final String secretKey = "KSK82UfPE1S5-ctIMWIWlV5ZHfRkQ8jZsfmt8k_k";
        LogUtils.d(photoPath);
        UploadManager uploadManager = new UploadManager();
        String token = QiNiuCloudAuth.create(accessKey, secretKey).uploadToken("smarthub");
        LogUtils.d(token);
        uploadManager.put(photoPath, userName, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, final JSONObject res) {
                LogUtils.i(info);
                // info.error中包含了错误信息，可打印调试
                if (info.isOK()) {
                    // 上传成功后将key值上传到自己的服务器
                    String url = UserUrls.AVATAR;
                    final UserBean userBean = new UserBean();
                    final String avatarUrl = "http://p0qgwnuel.bkt.clouddn.com/" + key;
                    userBean.setAvatar(avatarUrl);
                    userBean.setUsername(userName);
                    OkHttpUtil.getDefault(this).doAsync(
                            HttpInfo.Builder()
                                    .setUrl(url)
                                    .setRequestType(RequestType.PUT)
                                    .addParamJson(new Gson().toJson(userBean))
                                    .build(),
                            new Callback() {
                                @Override
                                public void onFailure(HttpInfo info) throws IOException {
                                    String response = info.getRetDetail();
                                    LogUtils.d(response);
                                    listener.onFailure(response);
                                }

                                @Override
                                public void onSuccess(HttpInfo info) throws IOException {
                                    String response = info.getRetDetail();
                                    listener.onSuccess(avatarUrl);
                                }
                            });
                } else {
                    listener.onFailure(info.error);
                }
            }
        }, new UploadOptions(null, null, false,
                new UpProgressHandler() {
                    public void progress(String key, double percent) {
                        LogUtils.i(key + ": " + percent);
                    }
                }, null));
    }
}
