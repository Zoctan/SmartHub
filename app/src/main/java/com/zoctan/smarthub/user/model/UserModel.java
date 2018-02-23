package com.zoctan.smarthub.user.model;

import com.zoctan.smarthub.beans.UserBean;

public interface UserModel {
    void loginOrRegister(Boolean login, UserBean user, LoginUserListener listener);

    void modify(String url, UserBean user, String token, ModifyUserListener listener);

    void uploadAvatar(String userName, String photoPath, UploadAvatarListener listener);

    interface LoginUserListener {
        void onSuccess(UserBean userBean);

        void onFailure(String msg);
    }

    interface ModifyUserListener {
        void onSuccess();

        void onFailure(String msg);
    }

    interface UploadAvatarListener {
        void onSuccess(String avatarUrl);

        void onFailure(String msg);
    }
}
