package com.zoctan.smarthub.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
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
    private InputMethodManager mInputMethodManager;
    private Unbinder unbinder;
    protected SPUtils mSPUtil = SPUtils.getInstance();

    // 获取布局文件ID
    protected abstract int bindLayout();

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
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

    /**
     * 设置日夜间模式
     *
     * @param isChange 是否改变模式
     */
    protected void setDayNightMode(final Boolean isChange) {
        // 改变
        if (isChange) {
            // 存下该模式
            // day: true 日间
            // day: false 夜间
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
        // 在 BaseActivity 里销毁
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

    @Override
    public void finish() {
        super.finish();
    }

    public void hideSoftKeyBoard(final TextInputEditText mEditText, final Context mContext) {
        this.mInputMethodManager = ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE));
        this.mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }
}