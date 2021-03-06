package com.zoctan.smarthub.api;

import java.util.Map;

public class OneNetUrls {
    public final static String HEADER_KEY = "api-key";
    public final static String HEADER_VALUE = "nJVyiaj5Y297Fc6Q=bUYVWnz2=0=";

    // https://open.iot.10086.cn/doc/art260.html#68
    /*
    datastream_id=a,b,c        // 查询的数据流，多个数据流之间用逗号分隔（可选）
    start=2015-01-10T08:00:35  // 提取数据点的开始时间（可选）
    end=2015-01-10T08:00:35    // 提取数据点的结束时间（可选）
    duration=3600              // 查询时间区间（可选，单位为秒）
                                start+duration：按时间顺序返回从start开始一段时间内的数据点
                                end+duration：按时间倒序返回从end回溯一段时间内的数据点
    limit=100                  // 限定本次请求最多返回的数据点数，0<n<=6000（可选，默认1440）
    cursor=                    // 指定本次请求继续从cursor位置开始提取数据（可选）
    sort=DESC | ASC            // 值为DESC|ASC时间排序方式，DESC:倒序，ASC升序，默认升序
     */
    // 查询设备数据点(指定时间段)
    public static String buildDataPointsGet(final String device_id, final String datastream_id, final Map params) {
        return "http://api.heclouds.com/devices/" +
                device_id +
                "/datapoints" +
                "?newadd=true&datastream_id=" + datastream_id +
                "&start=" + params.get("start") +
                "&end=" + params.get("end") +
                "&duration=" + params.get("duration") +
                "&limit=" + params.get("limit");
    }

    public static String buildDataStreamsGet(final String device_id, final String datastream_ids) {
        return "http://api.heclouds.com/devices/" +
                device_id +
                "/datastreams" +
                "?datastream_ids=" + datastream_ids;
    }

    public static String buildOrderSend(final String device_id) {
        return String.format("http://api.heclouds.com/cmds" +
                "?device_id=%s&qos=1&timeout=100&type=0", device_id);
    }
}
