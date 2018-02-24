package com.zoctan.smarthub.user.presenter;

import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.user.model.UserModel;
import com.zoctan.smarthub.user.model.UserModelImpl;
import com.zoctan.smarthub.user.view.UserLoginView;

public class UserLoginPresenter {

    private final UserLoginView mUserLoginView;
    private final UserModel mUserModel;

    public UserLoginPresenter(final UserLoginView userLoginView) {
        this.mUserLoginView = userLoginView;
        this.mUserModel = new UserModelImpl();
    }

    // 登录或注册
    public void userAction(final Boolean login, final UserBean user) {
        mUserLoginView.showLoading();
        mUserModel.loginOrRegister(login, user, new UserModel.Listener() {
            @Override
            public void onSuccess(final String msg) {

            }

            @Override
            public void onSuccess(final UserBean userBean) {
                mUserLoginView.hideLoading();
                mUserLoginView.showSuccessMsg(userBean);
            }

            @Override
            public void onFailure(final String msg) {
                mUserLoginView.hideLoading();
                mUserLoginView.showFailedMsg(msg);
            }
        });
    }
}
