package com.zoctan.smarthub.hubDetail.view;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public interface HubDetailSpareView {
    void setLineChartData(String[] x, ArrayList<Entry> y);

    void showLoading();

    void hideLoading();

    void showFailedMsg(String msg);
}
