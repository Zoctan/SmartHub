package com.zoctan.smarthub;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.okhttplib.OkHttpUtil;
import com.okhttplib.annotation.CacheType;
import com.okhttplib.annotation.Encoding;
import com.okhttplib.cookie.PersistentCookieJar;
import com.okhttplib.cookie.cache.SetCookieCache;
import com.okhttplib.cookie.persistence.SharedPrefsCookiePersistor;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreater;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreater;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.io.File;

public class App extends Application {

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Utils.init(instance);
        CrashUtils.init();
        initOkHttpUtil(instance);
        //registerActivityLifecycleCallbacks(mCallbacks);
    }

    private static final ActivityLifecycleCallbacks mCallbacks = new ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(final Activity activity, final Bundle savedInstanceState) {
            LogUtils.d("onActivityCreated() called with: activity = [" + activity + "], savedInstanceState = [" + savedInstanceState + "]");
        }

        @Override
        public void onActivityStarted(final Activity activity) {
            LogUtils.d("onActivityStarted() called with: activity = [" + activity + "]");
        }

        @Override
        public void onActivityResumed(final Activity activity) {
            LogUtils.d("onActivityResumed() called with: activity = [" + activity + "]");
        }

        @Override
        public void onActivityPaused(final Activity activity) {
            LogUtils.d("onActivityPaused() called with: activity = [" + activity + "]");
        }

        @Override
        public void onActivityStopped(final Activity activity) {
            LogUtils.d("onActivityStopped() called with: activity = [" + activity + "]");
        }

        @Override
        public void onActivitySaveInstanceState(final Activity activity, final Bundle outState) {
            LogUtils.d("onActivitySaveInstanceState() called with: activity = [" + activity + "], outState = [" + outState + "]");
        }

        @Override
        public void onActivityDestroyed(final Activity activity) {
            LogUtils.d("onActivityDestroyed() called with: activity = [" + activity + "]");
        }
    };

    // static 代码段可以防止内存泄露
    static {
        // 设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(final Context context, final RefreshLayout layout) {
                // 全局设置主题颜色
                layout.setPrimaryColorsId(R.color.primary, android.R.color.white);
                // 指定为经典Header，默认是贝塞尔雷达Header
                //.setTimeFormat(new DynamicTimeFormat("更新于 %s"));
                return new ClassicsHeader(context);
            }
        });
        // 设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(final Context context, final RefreshLayout layout) {
                // 指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    private static void initOkHttpUtil(final Application instance) {
        final String downloadFileDir = Environment.getExternalStorageDirectory().getPath() + "/smarthub_download/";
        final String cacheDir = Environment.getExternalStorageDirectory().getPath() + "/smarthub_cache";
        OkHttpUtil.init(instance)
                .setConnectTimeout(35)//连接超时时间
                .setWriteTimeout(35)//写超时时间
                .setReadTimeout(35)//读超时时间
                .setMaxCacheSize(10 * 1024 * 1024)//缓存空间大小
                .setCacheType(CacheType.FORCE_NETWORK)//缓存类型
                .setHttpLogTAG(">> OkHttpUtilLog >>")//设置请求日志标识
                .setIsGzip(false)//Gzip压缩，需要服务端支持
                .setShowHttpLog(true)//显示请求日志
                .setShowLifecycleLog(false)//显示Activity销毁日志
                .setRetryOnConnectionFailure(false)//失败后不自动重连
                .setCachedDir(new File(cacheDir))//设置缓存目录
                .setDownloadFileDir(downloadFileDir)//文件下载保存目录
                .setResponseEncoding(Encoding.UTF_8)//设置全局的服务器响应编码
                .setRequestEncoding(Encoding.UTF_8)//设置全局的请求参数编码
                .setCookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(instance)))//持久化cookie
                .build();

    }
}
