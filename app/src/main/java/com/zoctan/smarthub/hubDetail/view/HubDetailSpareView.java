package com.zoctan.smarthub.hubDetail.view;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public interface HubDetailSpareView {
    void setSpareData(String electricalDegree, String electricalBill, int currentMonth);

    void setLineChartData(String[] x, ArrayList<Entry> y);

    void showFailedMsg(String msg);
}
