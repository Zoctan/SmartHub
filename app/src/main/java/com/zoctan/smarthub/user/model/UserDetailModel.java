package com.zoctan.smarthub.user.model;

import com.zoctan.smarthub.beans.UserBean;

public interface UserDetailModel {
    void update(String url, UserBean user, String token, Listener listener);

    void uploadAvatar(UserBean userBean, String token, String photoPath, UploadAvatarListener listener);

    void getQiNiuToken(UserBean userBean, Listener listener);

    interface Listener {
        void onSuccess(String token);

        void onFailure(String msg);
    }

    interface UploadAvatarListener {
        void onSuccess(String avatarUrl, String msg);

        void onFailure(String msg);
    }
}
