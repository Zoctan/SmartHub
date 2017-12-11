package com.zoctan.smarthub.hub.widget;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zoctan.smarthub.R;
import com.zoctan.smarthub.utils.DashboardView;

import java.util.Random;

public class HubDetailNowFragment extends Fragment  implements View.OnClickListener {

    public static final String ARG_PAGE = "ARG_PAGE";
    private DashboardView mDashboardView;

    public static HubDetailNowFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        HubDetailNowFragment pageFragment = new HubDetailNowFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_hub_detail_now, container, false);
        mDashboardView = view.findViewById(R.id.mDashboardView);
        mDashboardView.setRealTimeValue(new Random().nextInt(600) + 350);
        mDashboardView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mDashboardView:
                mDashboardView.setRealTimeValue(new Random().nextInt(100));
        }
    }
}
