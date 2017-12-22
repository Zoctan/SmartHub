package com.zoctan.smarthub.about;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zoctan.smarthub.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.blankj.utilcode.util.AppUtils.getAppVersionName;

public class AboutFragment extends Fragment {

    @BindView(R.id.TextView_about_version)
    public TextView mTvVersion;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_about, parent, false);
        unbinder = ButterKnife.bind(this, view);
        mTvVersion.setText(getAppVersionName());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
