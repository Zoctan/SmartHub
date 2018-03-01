package com.zoctan.smarthub.user.model;

import com.zoctan.smarthub.beans.UserBean;

public interface UserLoginModel {
    void loginOrRegister(Boolean login, UserBean user, Listener listener);

    interface Listener {
        void onSuccess(UserBean userBean);

        void onFailure(String msg);
    }
}
