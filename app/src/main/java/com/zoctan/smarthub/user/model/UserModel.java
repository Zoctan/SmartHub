package com.zoctan.smarthub.user.model;

import com.zoctan.smarthub.beans.UserBean;

public interface UserModel {
    void loginOrRegister(Boolean login, UserBean user, Listener listener);

    void update(String url, UserBean user, String token, Listener listener);

    void uploadAvatar(UserBean userBean, String photoPath, UploadAvatarListener listener);

    interface Listener {
        void onSuccess(String token);

        void onSuccess(UserBean userBean);

        void onFailure(String msg);
    }

    interface UploadAvatarListener {
        void onSuccess(String avatarUrl, String msg);

        void onFailure(String msg);
    }
}
