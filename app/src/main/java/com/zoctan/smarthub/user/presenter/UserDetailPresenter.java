package com.zoctan.smarthub.user.presenter;

import com.zoctan.smarthub.api.UserUrls;
import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.user.model.UserModel;
import com.zoctan.smarthub.user.model.UserModelImpl;
import com.zoctan.smarthub.user.view.UserDetailView;

public class UserDetailPresenter {
    private final UserDetailView mUserDetailView;
    private final UserModel mUserModel;

    public UserDetailPresenter(final UserDetailView userDetailView) {
        this.mUserDetailView = userDetailView;
        this.mUserModel = new UserModelImpl();
    }

    public void update(final String action, final UserBean user, final String token) {
        mUserDetailView.showLoading();
        String url = null;
        if (action.equals("info")) {
            url = UserUrls.USERS;
        } else if (action.equals("password")) {
            // 修改密码
            url = UserUrls.PASSWORD;
        }
        mUserModel.update(url, user, token, new UserModel.Listener() {
            @Override
            public void onSuccess(final String msg) {
                mUserDetailView.showUpdateSuccessMsg(msg);
            }

            @Override
            public void onSuccess(final UserBean userBean) {

            }

            @Override
            public void onFailure(final String msg) {
                mUserDetailView.showFailedMsg(msg);
            }
        });
        mUserDetailView.hideLoading();
    }

    // 图片上传至七牛云
    public void uploadAvatar(final UserBean userBean, final String photoPath) {
        mUserDetailView.showLoading();
        mUserModel.uploadAvatar(userBean, photoPath, new UserModel.UploadAvatarListener() {
            @Override
            public void onSuccess(final String avatarUrl, final String msg) {
                mUserDetailView.showUpdateAvatarSuccessMsg(avatarUrl, msg);
            }

            @Override
            public void onFailure(final String msg) {
                mUserDetailView.showFailedMsg(msg);
            }
        });
        mUserDetailView.hideLoading();
    }
}
