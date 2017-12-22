package com.zoctan.smarthub.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.zoctan.smarthub.App;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.user.widget.UserLoginActivity;

/**
 * 启动屏
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 如果是第一次启动，则先进入功能引导页
        if (!App.mSPUtil.getBoolean("first_open")) {
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        // 判断是否登录, 用户登录才进行之后的操作
        if (!App.mSPUtil.getBoolean("login")) {
            Intent intent = new Intent(this, UserLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        // 如果不是第一次启动app，则显示启动屏
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                enterMainActivity();
            }
        }, 1500);
    }

    private void enterMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
