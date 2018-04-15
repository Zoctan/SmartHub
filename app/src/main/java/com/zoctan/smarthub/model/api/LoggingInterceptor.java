package com.zoctan.smarthub.model.api;

import com.blankj.utilcode.util.LogUtils;
import com.zoctan.smarthub.utils.CharSetUtil;

import java.io.IOException;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(final Chain chain) throws IOException {
        final Request request = chain.request();
        final long start = System.nanoTime();//请求发起的时间
        final String method = request.method();
        if ("POST".equals(method)
                || "DELETE".equals(method)
                || "PUT".equals(method)) {
            final StringBuilder sb = new StringBuilder();
            if (request.body() instanceof FormBody) {
                final FormBody body = (FormBody) request.body();
                if (body != null) {
                    for (int i = 0; i < body.size(); i++) {
                        sb.append(body.encodedName(i)).append("=").append(body.encodedValue(i)).append(",");
                    }
                }
                sb.delete(sb.length() - 1, sb.length());
                LogUtils.d(String.format(Locale.CHINA, "请求：%s on %s\n请求头：%s\n请求主体：%s",
                        request.url(), chain.connection(), request.headers(), sb.toString()));
            }
        } else {
            LogUtils.d(String.format(Locale.CHINA, "请求：%s on %s\n请求头：%s",
                    request.url(), chain.connection(), request.headers()));
        }
        final Response response = chain.proceed(request);
        // 收到响应的时间
        final long end = System.nanoTime();
        // 这里不能直接使用response.body().string()的方式输出日志
        // 因为response.body().string()之后，response中的流会被关闭，程序会报错
        // 我们需要创建出一个新的response给应用层处理
        final ResponseBody responseBody = response.peekBody(1024 * 1024);
        LogUtils.d(String.format(Locale.CHINA, "响应：\n%s\n花费时间：%.1fms\n响应头：%s",
                CharSetUtil.decodeUnicode(responseBody.string()),
                (end - start) / 1e6d, response.headers()));
        return response;
    }
}
