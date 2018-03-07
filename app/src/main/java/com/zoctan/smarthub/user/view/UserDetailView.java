package com.zoctan.smarthub.user.view;

import com.zoctan.smarthub.beans.UserBean;

/**
 * 用户详情视图接口
 */
public interface UserDetailView {
    void showLoading();

    void hideLoading();

    void showSuccessMsg(String msg);

    void showUpdateInfoSuccessMsg(String msg, UserBean userBean);

    void showUploadSuccessMsg(String msg, UserBean userBean);

    void showFailedMsg(String msg);
}
