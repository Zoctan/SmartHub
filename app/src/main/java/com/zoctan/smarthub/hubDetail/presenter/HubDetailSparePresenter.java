package com.zoctan.smarthub.hubDetail.presenter;

import com.github.mikephil.charting.data.Entry;
import com.zoctan.smarthub.beans.MonthSpareBean;
import com.zoctan.smarthub.hubDetail.model.HubDetailSpareModel;
import com.zoctan.smarthub.hubDetail.model.impl.HubDetailSpareModelImpl;
import com.zoctan.smarthub.hubDetail.view.HubDetailSpareView;

import java.util.ArrayList;
import java.util.Locale;

public class HubDetailSparePresenter {
    private final HubDetailSpareView mSpareView;
    private final HubDetailSpareModel mHubModel;

    public HubDetailSparePresenter(final HubDetailSpareView view) {
        mSpareView = view;
        mHubModel = new HubDetailSpareModelImpl();
    }

    public void loadHubSpareList(final String hubOneNetId, final String token) {
        mSpareView.showLoading();
        mHubModel.loadHubSpareList(hubOneNetId, token, new HubDetailSpareModel.onLoadHubSpareListListener() {
            @Override
            public void onSuccess(final MonthSpareBean monthSpareBean) {
                if (monthSpareBean == null) {
                    return;
                }
                final Double kwh = monthSpareBean.getWatt() / 1000.0;
                final Double bill = kwh * monthSpareBean.getPrice();
                // x轴、y轴的数据
                final String[] x = new String[24];
                final ArrayList<Entry> y = new ArrayList<>();
                for (Integer i = 0; i < 24; i++) {
                    x[i] = i.toString();
                    y.add(new Entry(i, Float.parseFloat(String.format(Locale.CHINA, "%.1f", monthSpareBean.getHour().get(i)))));
                }
                mSpareView.setSpareData(String.format(Locale.CHINA, "%.3f", kwh),
                        String.format(Locale.CHINA, "%.2f", bill), monthSpareBean.getCurrent_month());
                mSpareView.setLineChartData(x, y);
                mSpareView.hideLoading();
            }

            @Override
            public void onFailure(final String msg) {
                mSpareView.showFailedMsg(msg);
                mSpareView.hideLoading();
            }
        });
    }
}
