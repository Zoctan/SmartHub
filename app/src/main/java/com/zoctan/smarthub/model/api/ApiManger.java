package com.zoctan.smarthub.model.api;

import com.zoctan.smarthub.model.url.OneNetUrl;
import com.zoctan.smarthub.model.url.SmartUrl;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API集中处理
 */
public class ApiManger {
    //超时时间60s
    private static final long DEFAULT_TIMEOUT = 60;
    private static ApiManger mApiManger;
    private HubApi mHubApi;
    private OneNetApi mOneNetApi;

    private ApiManger() {
    }

    public static ApiManger getInstance() {
        if (mApiManger == null) {
            synchronized (ApiManger.class) {
                if (mApiManger == null)
                    mApiManger = new ApiManger();
            }
        }
        return mApiManger;
    }

    public HubApi getHubService() {
        if (mHubApi == null) {
            final Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SmartUrl.API)
                    .client(getClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mHubApi = retrofit.create(HubApi.class);
        }
        return mHubApi;
    }

    public OneNetApi getOneNetService() {
        if (mOneNetApi == null) {
            final Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(OneNetUrl.API)
                    .client(getClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mOneNetApi = retrofit.create(OneNetApi.class);
        }
        return mOneNetApi;
    }

    private static OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())//使用自定义的Log拦截器
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    final Request.Builder requestBuilder = request.newBuilder();
                    request = requestBuilder
                            .addHeader("Content-Type", "application/json;charset=UTF-8")
                            .build();
                    return chain.proceed(request);
                })
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }
}
