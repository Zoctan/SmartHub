package com.zoctan.smarthub.hub.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zoctan.smarthub.api.HubUrls;
import com.zoctan.smarthub.beans.HubBean;
import com.zoctan.smarthub.response.ResponseHubList;
import com.zoctan.smarthub.utils.JsonUtils;
import com.zoctan.smarthub.utils.OkHttpUtils;

import java.util.List;

/**
 * 评测信息接口实现
 */
public class HubModelImpl implements HubModel {

    @Override
    public void loadHubList(final String token, final OnLoadHubListListener listener) {
        String url = HubUrls.HUBS;
        OkHttpUtils.ResultCallback<String> loadHubListCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                //Logger.d(response);
                // 创建一个JsonParser
                JsonParser parser = new JsonParser();
                // 将res转换成Json对象
                JsonObject jsonObj = parser.parse(response).getAsJsonObject();
                // 将Json对象转换为User实体
                ResponseHubList mResponseHubList = JsonUtils.deserialize(jsonObj, ResponseHubList.class);
                if (mResponseHubList.getMsg().equals("ok")) {
                    final List<HubBean> hubList = mResponseHubList.getResult();
                    listener.onSuccess(hubList);
                } else {
                    listener.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure("Loading Hub List failed", e);
            }
        };
        OkHttpUtils.get(url, token, loadHubListCallback);
    }

    // 加载详情
    @Override
    public void loadHubDetail(final String userId, final String hubId, final OnLoadHubDetailListener listener) {

    /*
        String url = getHubDetailUrl(userId, hubId);
        OkHttpUtils.ResultCallback<String> loadHubDetailCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                HubDetailBean hubDetailBean = HubJsonUtils.readJsonHubDetailBeans(response, hubId);
                listener.onSuccess(hubDetailBean);
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure("测评详情加载失败", e);
            }
        };
        // 调用OkHttp的get方法
        OkHttpUtils.get(url, loadHubDetailCallback);
    */
    }
}
