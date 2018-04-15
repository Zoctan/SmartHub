package com.zoctan.smarthub.presenter;

import com.zoctan.smarthub.model.api.ApiManger;
import com.zoctan.smarthub.model.api.HubApi;
import com.zoctan.smarthub.model.bean.smart.SmartResponseBean;
import com.zoctan.smarthub.model.bean.smart.UserBean;
import com.zoctan.smarthub.ui.activity.UserLoginActivity;
import com.zoctan.smarthub.utils.JsonUtil;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.zoctan.smarthub.App.mSPUtil;

public class UserLoginPresenter extends BasePresenter {
    private UserLoginActivity view;

    public UserLoginPresenter(final UserLoginActivity view) {
        super();
        this.view = view;
    }

    /**
     * 登录或注册
     */
    public void loginRegister(final UserBean user, final String action) {
        view.showLoading();
        final HubApi api = ApiManger.getInstance().getHubService();
        final Observable<SmartResponseBean> observable;
        switch (action) {
            case "login":
                observable = api.login(user);
                break;
            case "register":
                observable = api.register(user);
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
                            final UserBean user = JsonUtil.deserialize(response.getResult(), UserBean.class);
                            // 将用户信息存在本地
                            mSPUtil.put("login", true);
                            mSPUtil.put("user_id", user.getId());
                            mSPUtil.put("user_avatar", user.getAvatar());
                            mSPUtil.put("user_phone", user.getPhone());
                            mSPUtil.put("user_name", user.getUsername());
                            mSPUtil.put("user_token", user.getToken());
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
