package com.zoctan.smarthub;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

public class App extends Application {
    public static SPUtils mSPUtil;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        mSPUtil = SPUtils.getInstance();
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
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            // 全局设置主题颜色
            layout.setPrimaryColorsId(R.color.primary, android.R.color.white);
            // 指定为经典Header，默认是贝塞尔雷达Header
            //.setTimeFormat(new DynamicTimeFormat("更新于 %s"));
            return new ClassicsHeader(context);
        });
        // 设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            // 指定为经典Footer，默认是 BallPulseFooter
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }
}
