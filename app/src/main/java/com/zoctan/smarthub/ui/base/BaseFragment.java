package com.zoctan.smarthub.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.SPUtils;
import com.zoctan.smarthub.presenter.BasePresenter;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";
    // 当前 Activity 渲染的视图 View
    protected View contentView;
    protected BaseActivity mActivity;
    private Unbinder unbinder;
    protected SPUtils mSPUtil = SPUtils.getInstance();
    protected String userToken = mSPUtil.getString("user_token");
    private BasePresenter mPresenter;

    /**
     * 资源的布局
     */
    protected abstract int bindLayout();

    /**
     * 绑定presenter，主要用于销毁工作
     */
    protected abstract BasePresenter bindPresenter();

    /**
     * 组件初始化操作
     *
     * @param view 父view
     */
    protected abstract void initView(View view, Bundle savedInstanceState);

    /**
     * 获取宿主Activity
     */
    protected BaseActivity getHoldingActivity() {
        return mActivity;
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            final boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            @SuppressWarnings("ConstantConditions") final FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (BaseActivity) getActivity();
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        setRetainInstance(true);
        mPresenter = bindPresenter();
        contentView = inflater.inflate(bindLayout(), null);
        unbinder = ButterKnife.bind(this, contentView);
        initView(contentView, savedInstanceState);
        return contentView;
    }

    @Override
    public void onDestroyView() {
        if (contentView != null) {
            ((ViewGroup) contentView.getParent()).removeView(contentView);
        }
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.dispose();
            mPresenter.onDestroy();
            mPresenter = null;
            System.gc();
        }
    }
}