package com.zoctan.smarthub.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.blankj.utilcode.util.SPUtils;
import com.gyf.barlibrary.ImmersionBar;
import com.zoctan.smarthub.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Activity基类
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected ImmersionBar mImmersionBar;
    protected SPUtils mSPUtil = SPUtils.getInstance();
    private InputMethodManager mInputMethodManager;
    private Unbinder unbinder;

    protected abstract int bindLayout();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindLayout());
        unbinder = ButterKnife.bind(this);
        // 初始化沉浸式
        if (isImmersionBarEnabled()) {
            initImmersionBar();
        }
        // 初始化数据
        initData();
        // view与数据绑定
        initView();
        setDayNightMode(false);
    }

    protected void setDayNightMode(Boolean isChange) {
        if (isChange) {
            mSPUtil.put("day", !mSPUtil.getBoolean("day"));
        }
        if (mSPUtil.getBoolean("day")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        this.mInputMethodManager = null;
        // 在BaseActivity里销毁
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
    }

    protected void initData() {
    }

    protected void initView() {
    }

    protected void initImmersionBar() {
        // 在BaseActivity里初始化
        mImmersionBar = ImmersionBar.with(this);

        mImmersionBar
                .statusBarColor(R.color.primary)
                .fitsSystemWindows(true)
                .transparentNavigationBar()
                .init();
    }

    /**
     * 是否可以使用沉浸式
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    public void finish() {
        super.finish();
        hideSoftKeyBoard();
    }

    public void hideSoftKeyBoard() {
        View localView = getCurrentFocus();
        if (this.mInputMethodManager == null) {
            this.mInputMethodManager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        }
        if (localView != null && this.mInputMethodManager != null) {
            this.mInputMethodManager.hideSoftInputFromWindow(localView.getWindowToken(), 2);
        }
    }
}