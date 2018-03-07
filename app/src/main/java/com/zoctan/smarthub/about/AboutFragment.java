package com.zoctan.smarthub.about;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.base.BaseFragment;

import butterknife.BindView;

public class AboutFragment extends BaseFragment {

    @BindView(R.id.TextView_about_version)
    public TextView mTvVersion;

    @Override
    protected int bindLayout() {
        return R.layout.fragment_about;
    }

    @Override
    protected void initView(final View view, final Bundle savedInstanceState) {
        mTvVersion.setText(AppUtils.getAppVersionName());
    }
}
