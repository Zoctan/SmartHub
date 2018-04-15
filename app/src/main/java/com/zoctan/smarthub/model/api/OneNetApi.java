package com.zoctan.smarthub.model.api;

import com.zoctan.smarthub.model.bean.onenet.OneNetResponseListBean;
import com.zoctan.smarthub.model.url.OneNetUrl;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OneNetApi {
    /**
     * 数据流
     * https://open.iot.10086.cn/doc/art526.html#108
     */
    @Headers(OneNetUrl.HEADER)
    @GET("devices/{onenetId}/datastreams")
    Observable<OneNetResponseListBean> listDatastreams(@Path("onenetId") String onenetId,
                                                       @Query("datastream_ids") String datastreamIds);
}
