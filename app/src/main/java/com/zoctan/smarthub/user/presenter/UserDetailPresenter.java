package com.zoctan.smarthub.user.presenter;

import com.zoctan.smarthub.api.SmartApiUrls;
import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.user.model.UserDetailModel;
import com.zoctan.smarthub.user.model.impl.UserDetailModelImpl;
import com.zoctan.smarthub.user.view.UserDetailView;
import com.zoctan.smarthub.utils.JsonUtil;
import com.zoctan.smarthub.utils.QiNiu.GetTokenListener;
import com.zoctan.smarthub.utils.QiNiu.QiNiuUtil;
import com.zoctan.smarthub.utils.QiNiu.UploadListener;

public class UserDetailPresenter {
    private final UserDetailView mUserDetailView;
    private final UserDetailModel mUserModel;

    public UserDetailPresenter(final UserDetailView userDetailView) {
        this.mUserDetailView = userDetailView;
        this.mUserModel = new UserDetailModelImpl();
    }

    public void update(final UserBean user, final String token) {
        mUserDetailView.showLoading();
        mUserModel.update(user, token, new UserDetailModel.Listener() {
            @Override
            public void onSuccess(final String msg) {
                if (user.getAction().equals("password")) {
                    mUserDetailView.showSuccessMsg(msg);
                } else {
                    mUserDetailView.showUpdateInfoSuccessMsg(msg, user);
                }
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
    public void qiNiuUpload(final String userToken,
                            final UserBean userBean,
                            final String localFilePath) {
        mUserDetailView.showLoading();
        QiNiuUtil.getQiNiuTokenFromSmartApi(
                userToken,
                userBean.getAvatar(),
                new GetTokenListener() {
                    @Override
                    public void onSuccess(final String qiNiuToken) {
                        mUserDetailView.hideLoading();
                        mUserDetailView.showSuccessMsg("图片上传成功，正在设置数据库...");
                        mUserDetailView.showLoading();
                        final UserBean user = new UserBean();
                        user.setAvatar(SmartApiUrls.QiNiuBucket + userBean.getAvatar());
                        QiNiuUtil.uploadImgAndStoreSmartApiDB(localFilePath,
                                userBean.getAvatar(),
                                qiNiuToken,
                                SmartApiUrls.USERS_AVATAR,
                                userToken,
                                JsonUtil.serialize(user),
                                new UploadListener() {
                                    @Override
                                    public void onSuccess(final String msg) {
                                        mUserDetailView.showUploadSuccessMsg(msg, user);
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
