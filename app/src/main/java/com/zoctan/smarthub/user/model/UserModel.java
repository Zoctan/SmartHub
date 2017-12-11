package com.zoctan.smarthub.user.model;

import com.zoctan.smarthub.beans.UserBean;

/**
 * 用户接口
 */
public interface UserModel {
    void login(String name, String password, LoginUserListener listener);

    void modifyPwd(String username, String password, ModifyUserListener listener);

    void uploadAvatar(String userName, String photoPath, UploadAvatarListener listener);

    interface LoginUserListener {
        void onSuccess(UserBean userBean);
        void onFailure(String msg, Exception e);
    }

    interface ModifyUserListener {
        void onSuccess();
        void onFailure(String msg, Exception e);
    }

    interface UploadAvatarListener {
        void onSuccess(String avatarUrl);
        void onFailure(String msg);
    }
}
