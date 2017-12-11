package com.zoctan.smarthub.user.presenter;

import com.zoctan.smarthub.user.model.UserModel;
import com.zoctan.smarthub.user.model.UserModelImpl;
import com.zoctan.smarthub.user.view.UserDetailView;

public class UserDetailPresenter {
    private UserDetailView mUserDetailView;
    private UserModel mUserModel;

    public UserDetailPresenter(UserDetailView userDetailView) {
        this.mUserDetailView = userDetailView;
        this.mUserModel = new UserModelImpl();
    }

    // 修改密码
    public void modifyPwd(final String username, final String password) {
        mUserDetailView.showLoading();
        mUserModel.modifyPwd(username, password, new UserModel.ModifyUserListener() {
            @Override
            public void onSuccess() {
                mUserDetailView.showSuccessMsg(null);
            }

            @Override
            public void onFailure(String msg, Exception e) {
                mUserDetailView.showFailedMsg(msg);
            }
        });
        mUserDetailView.hideLoading();
    }

    // 图片上传至七牛云
    public void uploadAvatar(final String userName, final String photoPath) {
        mUserDetailView.showLoading();
        mUserModel.uploadAvatar(userName, photoPath, new UserModel.UploadAvatarListener() {
            @Override
            public void onSuccess(String avatarUrl) {
                mUserDetailView.showSuccessMsg(avatarUrl);
            }

            @Override
            public void onFailure(String msg) {
                mUserDetailView.showFailedMsg(msg);
            }
        });
        mUserDetailView.hideLoading();
    }
}
