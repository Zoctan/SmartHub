package com.zoctan.smarthub.user.presenter;

import com.zoctan.smarthub.api.UserUrls;
import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.user.model.UserDetailModel;
import com.zoctan.smarthub.user.model.impl.UserDetailModelImpl;
import com.zoctan.smarthub.user.view.UserDetailView;

public class UserDetailPresenter {
    private final UserDetailView mUserDetailView;
    private final UserDetailModel mUserModel;

    public UserDetailPresenter(final UserDetailView userDetailView) {
        this.mUserDetailView = userDetailView;
        this.mUserModel = new UserDetailModelImpl();
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
        mUserModel.update(url, user, token, new UserDetailModel.Listener() {
            @Override
            public void onSuccess(final String msg) {
                mUserDetailView.showUpdateSuccessMsg(msg);
                mUserDetailView.hideLoading();
            }

            @Override
            public void onFailure(final String msg) {
                mUserDetailView.showFailedMsg(msg);
                mUserDetailView.hideLoading();
            }
        });
    }

    // 图片上传至七牛云
    public void uploadAvatar(final UserBean userBean, final String photoPath) {
        mUserDetailView.showLoading();
        mUserModel.getQiNiuToken(userBean, new UserDetailModel.Listener() {
            @Override
            public void onSuccess(final String token) {
                mUserModel.uploadAvatar(userBean, token, photoPath, new UserDetailModel.UploadAvatarListener() {
                    @Override
                    public void onSuccess(final String avatarUrl, final String msg) {
                        mUserDetailView.showUpdateAvatarSuccessMsg(avatarUrl, msg);
                        mUserDetailView.hideLoading();
                    }

                    @Override
                    public void onFailure(final String msg) {
                        mUserDetailView.showFailedMsg(msg);
                        mUserDetailView.hideLoading();
                    }
                });
            }

            @Override
            public void onFailure(final String msg) {
                mUserDetailView.showFailedMsg(msg);
                mUserDetailView.hideLoading();
            }
        });
    }
}
