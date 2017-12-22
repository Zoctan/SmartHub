package com.zoctan.smarthub.user.view;

import com.zoctan.smarthub.beans.UserBean;

public interface UserLoginView {
    void showSuccessMsg(UserBean userBean);

    void showLoading();

    void hideLoading();

    void showFailedMsg(String msg);
}
