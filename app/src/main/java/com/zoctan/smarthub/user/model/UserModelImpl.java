package com.zoctan.smarthub.user.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orhanobut.logger.Logger;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.zoctan.smarthub.api.UserUrls;
import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.response.ResponseToken;
import com.zoctan.smarthub.utils.JsonUtils;
import com.zoctan.smarthub.utils.OkHttpUtils;
import com.zoctan.smarthub.utils.QiNiuCloudAuth;

import org.json.JSONObject;


public class UserModelImpl implements UserModel {
    @Override
    public void login(final String username, final String password, final LoginUserListener listener) {
        String url = UserUrls.TOKENS;
        // json
        final Gson gson = new Gson();
        final UserBean[] userBean = {new UserBean()};
        userBean[0].setPassword(password);
        userBean[0].setUsername(username);
        // 接收响应数据
        OkHttpUtils.ResultCallback<String> loadUserCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                Logger.d(response);
                // 创建一个JsonParser
                JsonParser parser = new JsonParser();
                // 将res转换成Json对象
                JsonObject jsonObj = parser.parse(response).getAsJsonObject();
                // 将Json对象转换为User实体
                ResponseToken mResponseToken = JsonUtils.deserialize(jsonObj, ResponseToken.class);
                if (mResponseToken.getMsg().equals("ok")) {
                    userBean[0].setId(mResponseToken.getResult().get(0).getId());
                    userBean[0].setAvatar(mResponseToken.getResult().get(0).getAvatar());
                    // not store password but token
                    userBean[0].setPassword(mResponseToken.getResult().get(0).getToken());
                }else{
                    userBean[0] = null;
                }
                listener.onSuccess(userBean[0]);
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure("用户登录失败", e);
            }
        };
        // 调用OkHttp的post方法
        OkHttpUtils.postJson(url, gson.toJson(userBean[0]), loadUserCallback);
    }

    // 修改密码
    @Override
    public void modifyPwd(final String userName, final String password, final ModifyUserListener listener) {
        String url = UserUrls.PASSWORD;
        final Gson gson = new Gson();
        final UserBean[] userBean = {new UserBean()};
        userBean[0].setPassword(password);
        userBean[0].setUsername(userName);
        OkHttpUtils.ResultCallback<String> modifyPwdCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                listener.onSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure("密码修改失败", e);
            }
        };
        OkHttpUtils.postJson(url, gson.toJson(userBean[0]), modifyPwdCallback);
    }

    private static String AccessKey = "E-UOGVjHSKB59V8otdVUKHZtePBpW3jOJZH7SCbK";//此处填你自己的AccessKey
    private static String SecretKey = "cUfgICuOfZ5-WmOXg2O1h1TxQrdwUbHnn4XrS2bP";//此处填你自己的SecretKey
    @Override
    public void uploadAvatar(final String userName, final String photoPath, final UploadAvatarListener listener) {
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(photoPath, userName, QiNiuCloudAuth.create(AccessKey, SecretKey).uploadToken("smarthub"), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject res) {
                // info.error中包含了错误信息，可打印调试
                if (info.isOK()) {
                    Logger.i("token=" + QiNiuCloudAuth.create(AccessKey, SecretKey).uploadToken("photo"));
                    // 上传成功后将key值上传到自己的服务器
                    String url = UserUrls.AVATAR;
                    final Gson gson = new Gson();
                    final UserBean[] userBean = {new UserBean()};
                    final String avatarUrl = "http://p0qgwnuel.bkt.clouddn.com/" + key;
                    userBean[0].setAvatar(avatarUrl);
                    userBean[0].setUsername(userName);
                    OkHttpUtils.ResultCallback<String> modifyPwdCallback = new OkHttpUtils.ResultCallback<String>() {
                        @Override
                        public void onSuccess(String response) {
                            listener.onSuccess(avatarUrl);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            listener.onFailure("头像修改失败");
                        }
                    };
                    OkHttpUtils.postJson(url, gson.toJson(userBean[0]), modifyPwdCallback);
                }else {
                    listener.onFailure(info.error);
                }
            }
        }, null);
    }
}
