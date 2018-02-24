package com.zoctan.smarthub.user.view;

/**
 * 用户详情视图接口
 */
public interface UserDetailView {
    void showLoading();

    void hideLoading();

    void showUpdateSuccessMsg(String msg);

    void showUpdateAvatarSuccessMsg(String avatarUrl);

    void showFailedMsg(String msg);
}
