package com.zoctan.smarthub.presenter;

import com.zoctan.smarthub.model.api.ApiManger;
import com.zoctan.smarthub.model.api.HubApi;
import com.zoctan.smarthub.model.bean.smart.SmartResponseBean;
import com.zoctan.smarthub.model.bean.smart.SmartResponseListBean;
import com.zoctan.smarthub.model.bean.smart.TimerBean;
import com.zoctan.smarthub.ui.fragment.HubDetailTimerFragment;
import com.zoctan.smarthub.utils.JsonUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.zoctan.smarthub.App.SMART_TOKEN;

public class HubDetailTimerPresenter extends BasePresenter {
    private HubDetailTimerFragment view;

    public HubDetailTimerPresenter(final HubDetailTimerFragment view) {
        super();
        this.view = view;
    }

    public void crudTimer(final TimerBean timer, final String action) {
        view.showLoading();
        final HubApi api = ApiManger.getInstance().getHubService();
        final Observable<SmartResponseBean> observable;
        switch (action) {
            case "add":
                observable = api.addTimer(SMART_TOKEN, timer);
                break;
            case "update":
                observable = api.updateTimer(SMART_TOKEN, timer);
                break;
            case "delete":
                observable = api.deleteTimer(SMART_TOKEN, timer);
                break;
            default:
                return;
        }
        observable.subscribeOn(Schedulers.io())
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
                            view.showSuccessMsg(response.getMsg());
                            view.refreshTimerList();
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

    public void listTimer(final String onenetId) {
        view.showLoading();
        ApiManger.getInstance()
                .getHubService()
                .listTimer(SMART_TOKEN, onenetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResponseListBean>() {
                    @Override
                    public void onSubscribe(final Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(final SmartResponseListBean response) {
                        if (response.getError() > 0) {
                            view.showFailedMsg(response.getMsg());
                        } else {
                            if (response.getResult() != null) {
                                final List<TimerBean> list = JsonUtil.deserialize(response.getResult(), TimerBean.class);
                                view.loadTimerList(list);
                            }
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
