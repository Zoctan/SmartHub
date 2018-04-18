package com.zoctan.smarthub.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

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
            final Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        // 判断是否登录, 用户登录才进行之后的操作
        if (!mSPUtil.getBoolean("login")) {
            final Intent intent = new Intent(this, UserLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        // 如果不是第一次启动app，则显示启动屏
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(this::toMainActivity, 1500);
    }

    private void toMainActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
