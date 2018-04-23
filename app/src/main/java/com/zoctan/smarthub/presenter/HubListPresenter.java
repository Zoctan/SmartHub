package com.zoctan.smarthub.presenter;

import com.zoctan.smarthub.model.api.ApiManger;
import com.zoctan.smarthub.model.api.HubApi;
import com.zoctan.smarthub.model.bean.smart.HubBean;
import com.zoctan.smarthub.model.bean.smart.OrderBean;
import com.zoctan.smarthub.model.bean.smart.SmartResponseBean;
import com.zoctan.smarthub.model.bean.smart.SmartResponseListBean;
import com.zoctan.smarthub.ui.fragment.HubListFragment;
import com.zoctan.smarthub.utils.JsonUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.zoctan.smarthub.App.SMART_TOKEN;

public class HubListPresenter extends BasePresenter {
    private HubListFragment view;

    public HubListPresenter(final HubListFragment view) {
        super();
        this.view = view;
    }

    public void listHub() {
        view.showLoading();
        ApiManger.getInstance()
                .getHubService()
                .listHub(SMART_TOKEN)
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
                            view.showSuccessMsg(response.getMsg());
                            final List<HubBean> list = JsonUtil.deserialize(response.getResult(), HubBean.class);
                            view.loadHubList(list);
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

    public void openCloseHub(final OrderBean order) {
        view.showLoading();
        ApiManger.getInstance()
                .getHubService()
                .orderHub(SMART_TOKEN, order)
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
                            view.showSuccessMsg(response.getMsg());
                            listHub();
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

    public void crudHub(final HubBean hub, final String action) {
        view.showLoading();
        final HubApi api = ApiManger.getInstance().getHubService();
        final Observable<SmartResponseBean> observable;
        switch (action) {
            case "add":
                observable = api.addHub(SMART_TOKEN, hub);
                break;
            case "update":
                observable = api.updateHub(SMART_TOKEN, hub);
                break;
            case "updateRoom":
                observable = api.updateHubRoom(SMART_TOKEN, hub);
                break;
            case "delete":
                observable = api.deleteHub(SMART_TOKEN, hub);
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
