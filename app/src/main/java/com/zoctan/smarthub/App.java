package com.zoctan.smarthub;

import android.app.Application;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

public class App extends Application {
    public static SPUtils mSPUtil;
    public static String SMART_TOKEN;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        mSPUtil = SPUtils.getInstance();
        SMART_TOKEN = mSPUtil.getString("user_token");
    }

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
