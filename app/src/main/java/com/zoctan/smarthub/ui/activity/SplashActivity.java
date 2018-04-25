package com.zoctan.smarthub.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.zoctan.smarthub.R;

/**
 * 启动屏
 */
public class SplashActivity extends Activity {
    private final SPUtils mSPUtil = SPUtils.getInstance();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 如果是第一次启动，则先进入功能引导页
        if (!mSPUtil.getBoolean("not_first_open")) {
            toActivity(GuideActivity.class);
            return;
        }
        // 判断是否登录, 用户登录才进行之后的操作
        if (!mSPUtil.getBoolean("login")) {
            toActivity(UserLoginActivity.class);
            return;
        }
        // 如果不是第一次启动app，则显示启动屏
        setContentView(R.layout.activity_splash);
        // 然后延迟1.5秒才进主界面
        new Handler().postDelayed(() -> toActivity(MainActivity.class), 1500);
    }

    private void toActivity(final Class cls) {
        ActivityUtils.startActivity(cls, android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
