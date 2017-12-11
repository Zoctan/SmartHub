package com.zoctan.smarthub.user.view;

import com.zoctan.smarthub.beans.UserBean;

/**
 * 用户登录或注册视图接口
 */
public interface UserLoginView {
    void showLoading();
    void hideLoading();
    void showSuccessMsg(UserBean userBean);
    void showFailedMsg();
}
