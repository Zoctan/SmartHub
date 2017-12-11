package com.zoctan.smarthub.user.presenter;

import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.user.model.UserModel;
import com.zoctan.smarthub.user.model.UserModelImpl;
import com.zoctan.smarthub.user.view.UserLoginView;

/**
 * 用户登录回调实现类
 */
public class UserLoginPresenter {

    private UserLoginView mUserLoginView;
    private UserModel mUserModel;

    public UserLoginPresenter(UserLoginView userLoginView) {
        this.mUserLoginView = userLoginView;
        this.mUserModel = new UserModelImpl();
    }

    // 登录
    public void userAction(final String type, final String name, final String password) {
        mUserLoginView.showLoading();
        if (type.equals("login")) {
            mUserModel.login(name, password, new UserLoginPresenter.LoginUserListener());
        }
    }

    private class LoginUserListener implements UserModel.LoginUserListener {
        @Override
        public void onSuccess(UserBean userBean) {
            if (userBean == null) {
                // 显示失败信息
                mUserLoginView.showFailedMsg();
            } else {
                // 显示失败信息
                mUserLoginView.showSuccessMsg(userBean);
            }
            // 隐藏Loading圈圈
            mUserLoginView.hideLoading();
        }

        @Override
        public void onFailure(String msg, Exception e) {
            // 显示失败信息
            mUserLoginView.showFailedMsg();
            // 隐藏Loading圈圈
            mUserLoginView.hideLoading();
        }
    }
}
