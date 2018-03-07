package com.zoctan.smarthub.user.model.impl;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.RequestType;
import com.okhttplib.callback.Callback;
import com.zoctan.smarthub.api.SmartApiUrls;
import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.response.Response;
import com.zoctan.smarthub.user.model.UserDetailModel;
import com.zoctan.smarthub.utils.JsonUtil;

import java.io.IOException;

public class UserDetailModelImpl implements UserDetailModel {
    @Override
    public void update(final UserBean user,
                       final String token,
                       final Listener listener) {
        String url = SmartApiUrls.USERS;
        if (user.getAction().equals("password")) {
            url = SmartApiUrls.USERS_PASSWORD;
        }
        final String headerKey = SmartApiUrls.HEADER_KEY;
        final String headerValue = SmartApiUrls.HEADER_VALUE + token;
        OkHttpUtil.getDefault(this).doAsync(
                HttpInfo.Builder()
                        .setUrl(url)
                        .setRequestType(RequestType.PUT)
                        .addParamJson(JsonUtil.serialize(user))
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
}
