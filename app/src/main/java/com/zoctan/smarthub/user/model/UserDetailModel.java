package com.zoctan.smarthub.user.model;

import com.zoctan.smarthub.beans.UserBean;

public interface UserDetailModel {
    void update(UserBean user, String token, Listener listener);

    interface Listener {
        void onSuccess(String msg);

        void onFailure(String msg);
    }
}
