package com.zoctan.smarthub.user.presenter;

import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.user.model.UserLoginModel;
import com.zoctan.smarthub.user.model.impl.UserLoginModelImpl;
import com.zoctan.smarthub.user.view.UserLoginView;

public class UserLoginPresenter {

    private final UserLoginView mUserLoginView;
    private final UserLoginModel mUserModel;

    public UserLoginPresenter(final UserLoginView userLoginView) {
        this.mUserLoginView = userLoginView;
        this.mUserModel = new UserLoginModelImpl();
    }

    // 登录或注册
    public void userAction(final Boolean login, final UserBean user) {
        mUserLoginView.showLoading();
        mUserModel.loginOrRegister(login, user, new UserLoginModel.Listener() {
            @Override
            public void onSuccess(final UserBean userBean) {
                mUserLoginView.showSuccessMsg(userBean);
                mUserLoginView.hideLoading();
            }

            @Override
            public void onFailure(final String msg) {
                mUserLoginView.showFailedMsg(msg);
                mUserLoginView.hideLoading();
            }
        });
    }
}
