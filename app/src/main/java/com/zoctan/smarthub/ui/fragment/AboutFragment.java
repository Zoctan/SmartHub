package com.zoctan.smarthub.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.presenter.BasePresenter;
import com.zoctan.smarthub.ui.base.BaseFragment;

import butterknife.BindView;

public class AboutFragment extends BaseFragment implements FragmentUtils.OnBackClickListener {
    @BindView(R.id.TextView_about_version)
    public TextView mTvVersion;

    public static AboutFragment newInstance() {
        final Bundle args = new Bundle();
        final AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int bindLayout() {
        return R.layout.fragment_about;
    }

    @Override
    protected BasePresenter bindPresenter() {
        return null;
    }

    @Override
    protected void initView(final View view, final Bundle savedInstanceState) {
        mTvVersion.setText(AppUtils.getAppVersionName());
    }

    @Override
    public boolean onBackClick() {
        return false;
    }
}
