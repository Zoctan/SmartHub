package com.zoctan.smarthub.presenter;

import com.github.mikephil.charting.data.Entry;
import com.zoctan.smarthub.model.api.ApiManger;
import com.zoctan.smarthub.model.bean.smart.MonthSpareBean;
import com.zoctan.smarthub.model.bean.smart.SmartResponseBean;
import com.zoctan.smarthub.ui.fragment.HubDetailSpareFragment;
import com.zoctan.smarthub.utils.JsonUtil;

import java.util.ArrayList;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.zoctan.smarthub.App.mSPUtil;

public class HubDetailSparePresenter extends BasePresenter {
    private HubDetailSpareFragment view;

    public HubDetailSparePresenter(final HubDetailSpareFragment view) {
        super();
        this.view = view;
    }

    public void listSpare(final String onenetId) {
        view.showLoading();
        ApiManger.getInstance()
                .getHubService()
                .listSpare(mSPUtil.getString("user_token"), onenetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResponseBean>() {
                    @Override
                    public void onSubscribe(final Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(final SmartResponseBean response) {
                        if (response.getError() > 0) {
                            view.showFailedMsg(response.getMsg());
                        } else {
                            final MonthSpareBean monthSpareBean = JsonUtil.deserialize(response.getResult(), MonthSpareBean.class);
                            final Double kwh = monthSpareBean.getWatt() / 1000.0;
                            final Double bill = kwh * monthSpareBean.getPrice();
                            // x轴、y轴的数据
                            final String[] x = new String[24];
                            final ArrayList<Entry> y = new ArrayList<>();
                            for (Integer i = 0; i < 24; i++) {
                                x[i] = i.toString();
                                y.add(new Entry(i, Float.parseFloat(String.format(Locale.CHINA, "%.1f", monthSpareBean.getHour().get(i)))));
                            }
                            view.setSpareData(String.format(Locale.CHINA, "%.3f", kwh),
                                    String.format(Locale.CHINA, "%.2f", bill), monthSpareBean.getCurrent_month());
                            view.setLineChartData(x, y);
                        }
                    }

                    @Override
                    public void onError(final Throwable e) {
                        view.showFailedMsg(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        view.hideLoading();
                    }
                });
    }

    @Override
    public void onDestroy() {
        view = null;
        System.gc();
    }
}
