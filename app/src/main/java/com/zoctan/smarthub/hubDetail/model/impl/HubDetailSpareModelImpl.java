package com.zoctan.smarthub.hubDetail.model.impl;

import com.okhttplib.HttpInfo;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.RequestType;
import com.okhttplib.callback.Callback;
import com.zoctan.smarthub.api.HubUrls;
import com.zoctan.smarthub.hubDetail.model.HubDetailSpareModel;
import com.zoctan.smarthub.response.ResponseMonthSpare;
import com.zoctan.smarthub.utils.JsonUtil;

import java.io.IOException;

public class HubDetailSpareModelImpl implements HubDetailSpareModel {
    @Override
    public void loadHubSpareList(final String oneNetId, final String token, final Listener listener) {
        final String headerKey = "Authorization";
        final String headerValue = "Smart " + token;
        final String url = HubUrls.SPARES + "/" + oneNetId;

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
                        final ResponseMonthSpare responseMonthSpare = JsonUtil.getObjectFromHttpInfo(info, ResponseMonthSpare.class);
                        if (responseMonthSpare.getMsg().equals("ok")) {
                            listener.onSpareSuccess(responseMonthSpare.getResult());
                        } else {
                            listener.onSpareFailure(responseMonthSpare.getError());
                        }
                    }
                });
    }
}
