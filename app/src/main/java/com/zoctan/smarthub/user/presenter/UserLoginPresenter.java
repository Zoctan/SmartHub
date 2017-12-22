package com.zoctan.smarthub.user.presenter;

import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.user.model.UserModel;
import com.zoctan.smarthub.user.model.UserModelImpl;
import com.zoctan.smarthub.user.view.UserLoginView;

public class UserLoginPresenter {

    private final UserLoginView mUserLoginView;
    private final UserModel mUserModel;

    public UserLoginPresenter(UserLoginView userLoginView) {
        this.mUserLoginView = userLoginView;
        this.mUserModel = new UserModelImpl();
    }

    // 登录或注册
    public void userAction(final Boolean login, final UserBean user) {
        mUserLoginView.showLoading();
        mUserModel.loginOrRegister(login, user, new UserLoginPresenter.LoginUserListener());
    }

    private class LoginUserListener implements UserModel.LoginUserListener {
        @Override
        public void onSuccess(UserBean userBean) {
            mUserLoginView.hideLoading();
            if (userBean != null) {
                mUserLoginView.showSuccessMsg(userBean);
            } else {
                mUserLoginView.showFailedMsg("登录失败");
            }
        }

        @Override
        public void onFailure(String msg) {
            mUserLoginView.hideLoading();
            mUserLoginView.showFailedMsg(msg);
        }
    }
}
