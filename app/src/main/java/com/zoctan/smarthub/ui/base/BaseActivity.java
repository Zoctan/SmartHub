package com.zoctan.smarthub.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.inputmethod.InputMethodManager;

import com.gyf.barlibrary.ImmersionBar;
import com.yinglan.keyboard.HideUtil;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.presenter.BasePresenter;
import com.zoctan.smarthub.utils.AlerterUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.zoctan.smarthub.App.mSPUtil;

/**
 * Activity基类
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected ImmersionBar mImmersionBar;
    private InputMethodManager mInputMethodManager;
    private Unbinder mUnbinder;
    private BasePresenter mPresenter;

    /**
     * 资源的布局
     */
    protected abstract int bindLayout();

    /**
     * 绑定presenter，主要用于销毁工作
     */
    protected abstract BasePresenter bindPresenter();

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindLayout());
        mUnbinder = ButterKnife.bind(this);
        // 初始化沉浸式
        if (isImmersionBarEnabled()) {
            initImmersionBar();
        }
        mPresenter = bindPresenter();
        // 初始化view
        initView();
        setDayNightMode(false);
        HideUtil.init(this);
    }

    /**
     * 设置日夜间模式
     *
     * @param isChange 是否改变模式
     */
    protected void setDayNightMode(final Boolean isChange) {
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
        mUnbinder.unbind();
        this.mInputMethodManager = null;
        // 在 BaseActivity 里销毁
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
        if (mPresenter != null) {
            mPresenter.dispose();
            mPresenter.onDestroy();
            mPresenter = null;
            System.gc();
        }
    }

    /**
     * 组件初始化操作
     */
    protected abstract void initView();

    /**
     * 初始化沉浸式
     */
    protected void initImmersionBar() {
        // 在BaseActivity里初始化
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarColor(R.color.primary)
                .fitsSystemWindows(true)
                .transparentNavigationBar()
                .init();
    }

    /**
     * 是否可以使用沉浸式
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    @Override
    public void finish() {
        super.finish();
    }


    public void showSuccessMsg(final int msg) {
        AlerterUtil.showInfo(this, msg);
    }

    public void showSuccessMsg(final String msg) {
        AlerterUtil.showInfo(this, msg);
    }

    public void showFailedMsg(final String msg) {
        AlerterUtil.showDanger(this, msg);
    }

    public void showFailedMsg(final int msg) {
        AlerterUtil.showDanger(this, msg);
    }
}