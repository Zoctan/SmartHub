package com.zoctan.smarthub.user.model.impl;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.RequestType;
import com.okhttplib.callback.Callback;
import com.zoctan.smarthub.api.SmartApiUrls;
import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.response.ResponseUser;
import com.zoctan.smarthub.user.model.UserLoginModel;
import com.zoctan.smarthub.utils.JsonUtil;

import java.io.IOException;

public class UserLoginModelImpl implements UserLoginModel {
    @Override
    public void loginOrRegister(final Boolean login, final UserBean user,
                                final Listener listener) {
        String url = SmartApiUrls.USERS_TOKENS;
        if (!login) {
            url = SmartApiUrls.USERS;
        }
        OkHttpUtil.getDefault(this).doAsync(
                HttpInfo.Builder()
                        .setUrl(url)
                        .setRequestType(RequestType.POST)
                        .addParamJson(JsonUtil.serialize(user))
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
}
