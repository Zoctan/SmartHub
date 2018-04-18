package com.zoctan.smarthub.model.api;

import com.zoctan.smarthub.model.bean.smart.DeviceBean;
import com.zoctan.smarthub.model.bean.smart.FeedbackBean;
import com.zoctan.smarthub.model.bean.smart.HubBean;
import com.zoctan.smarthub.model.bean.smart.OrderBean;
import com.zoctan.smarthub.model.bean.smart.SmartResponseBean;
import com.zoctan.smarthub.model.bean.smart.SmartResponseListBean;
import com.zoctan.smarthub.model.bean.smart.TimerBean;
import com.zoctan.smarthub.model.bean.smart.UserBean;
import com.zoctan.smarthub.model.url.SmartUrl;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface HubApi {
    /*********** 反馈 ************/
    @POST(SmartUrl.FEEDBACK)
    Observable<SmartResponseBean> feedback(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                           @Body FeedbackBean feedbackBean);

    /*********** 用户 ************/
    @GET(SmartUrl.USERS)
    Observable<SmartResponseBean> getUserInfo(@Header(SmartUrl.HEADER_AUTH_KEY) String token);

    @POST(SmartUrl.TOKEN)
    Observable<SmartResponseBean> login(@Body UserBean userBean);

    @POST(SmartUrl.USERS)
    Observable<SmartResponseBean> register(@Body UserBean userBean);

    @PUT(SmartUrl.USERS)
    Observable<SmartResponseBean> updateUserInfo(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                                 @Body UserBean userBean);

    @PUT(SmartUrl.USERS_PASSWORD)
    Observable<SmartResponseBean> updateUserPassword(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                                     @Body UserBean userBean);

    @PUT(SmartUrl.USERS_AVATAR)
    Observable<SmartResponseBean> updateUserAvatar(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                                   @Body UserBean userBean);

    /*********** 七牛云 ************/
    @GET(SmartUrl.QiNiu + "/{fileName}")
    Observable<SmartResponseBean> getQiNiuToken(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                                @Path("fileName") String fileName);

    /*********** 插座 ************/
    @GET(SmartUrl.HUBS)
    Observable<SmartResponseListBean> listHub(@Header(SmartUrl.HEADER_AUTH_KEY) String token);

    @POST(SmartUrl.ORDERS)
    Observable<SmartResponseBean> orderHub(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                           @Body OrderBean orderBean);

    @POST(SmartUrl.HUBS)
    Observable<SmartResponseBean> addHub(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                         @Body HubBean hubBean);

    // @DELETE 不支持向服务器传body
    @HTTP(method = "DELETE", path = SmartUrl.HUBS, hasBody = true)
    Observable<SmartResponseBean> deleteHub(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                            @Body HubBean hubBean);

    @PUT(SmartUrl.HUBS)
    Observable<SmartResponseBean> updateHub(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                            @Body HubBean hubBean);

    /*********** 用电器 ************/
    @GET(SmartUrl.DEVICE + "/{onenetId}")
    Observable<SmartResponseListBean> listDevice(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                                 @Path("onenetId") String onenetId);

    @POST(SmartUrl.DEVICE)
    Observable<SmartResponseBean> addDevice(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                            @Body DeviceBean deviceBean);

    @PUT(SmartUrl.DEVICE)
    Observable<SmartResponseBean> updateDevice(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                               @Body DeviceBean deviceBean);

    @PUT(SmartUrl.DEVICE_IMG)
    Observable<SmartResponseBean> updateDeviceImg(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                                  @Body DeviceBean deviceBean);

    /*********** 耗能 ************/
    @GET(SmartUrl.SPARES + "/{onenetId}")
    Observable<SmartResponseBean> listSpare(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                            @Path("onenetId") String onenetId);

    /*********** 定时器 ************/
    @GET(SmartUrl.TIMERS + "/{onenetId}")
    Observable<SmartResponseListBean> listTimer(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                                @Path("onenetId") String onenetId);

    @POST(SmartUrl.TIMERS)
    Observable<SmartResponseBean> addTimer(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                           @Body TimerBean timerBean);

    @PUT(SmartUrl.TIMERS)
    Observable<SmartResponseBean> updateTimer(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                              @Body TimerBean timerBean);

    @HTTP(method = "DELETE", path = SmartUrl.TIMERS, hasBody = true)
    Observable<SmartResponseBean> deleteTimer(@Header(SmartUrl.HEADER_AUTH_KEY) String token,
                                              @Body TimerBean timerBean);
}
