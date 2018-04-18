package com.zoctan.smarthub.presenter;

import com.zoctan.smarthub.model.api.ApiManger;
import com.zoctan.smarthub.model.bean.smart.FeedbackBean;
import com.zoctan.smarthub.model.bean.smart.SmartResponseBean;
import com.zoctan.smarthub.ui.fragment.FeedbackFragment;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.zoctan.smarthub.App.SMART_TOKEN;

public class FeedbackPresenter extends BasePresenter {
    private FeedbackFragment view;

    public FeedbackPresenter(final FeedbackFragment view) {
        super();
        this.view = view;
    }

    public void feedback(final FeedbackBean feedback) {
        view.showLoading();
        ApiManger.getInstance()
                .getHubService()
                .feedback(SMART_TOKEN, feedback)
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
