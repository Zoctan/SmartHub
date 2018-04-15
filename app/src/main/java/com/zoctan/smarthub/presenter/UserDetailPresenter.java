package com.zoctan.smarthub.presenter;

import com.blankj.utilcode.util.LogUtils;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.zoctan.smarthub.model.api.ApiManger;
import com.zoctan.smarthub.model.bean.smart.SmartResponseBean;
import com.zoctan.smarthub.model.bean.smart.UserBean;
import com.zoctan.smarthub.ui.fragment.UserDetailFragment;
import com.zoctan.smarthub.utils.JsonUtil;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.zoctan.smarthub.App.mSPUtil;

public class UserDetailPresenter extends BasePresenter {
    private UserDetailFragment view;

    public UserDetailPresenter(final UserDetailFragment view) {
        super();
        this.view = view;
    }

    public void getUserInfo() {
        view.showLoading();
        ApiManger.getInstance()
                .getHubService()
                .getUserInfo(mSPUtil.getString("user_token"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResponseBean>() {
                    @Override
                    public void onSubscribe(final Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(final SmartResponseBean response) {
                        if (response.getError() > 0) {
                            view.showFailedMsg(response.getMsg());
                        } else {
                            view.showSuccessMsg(response.getMsg());
                            final UserBean user = JsonUtil.deserialize(response.getResult(), UserBean.class);
                            mSPUtil.put("user_id", user.getId());
                            mSPUtil.put("user_avatar", user.getAvatar());
                            mSPUtil.put("user_phone", user.getPhone());
                            mSPUtil.put("user_name", user.getUsername());
                            mSPUtil.put("user_token", user.getToken());
                        }
                    }

                    @Override
                    public void onError(final Throwable e) {
                        view.showFailedMsg(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        view.hideLoading();
                    }
                });
    }

    public void updateUserInfo(final UserBean user) {
        view.showLoading();
        ApiManger.getInstance()
                .getHubService()
                .updateUserInfo(mSPUtil.getString("user_token"), user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResponseBean>() {
                    @Override
                    public void onSubscribe(final Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(final SmartResponseBean response) {
                        if (response.getError() > 0) {
                            view.showFailedMsg(response.getMsg());
                        } else {
                            view.showSuccessMsg(response.getMsg());
                            mSPUtil.put("user_name", user.getUsername());
                            mSPUtil.put("user_phone", user.getPhone());
                        }
                    }

                    @Override
                    public void onError(final Throwable e) {
                        view.showFailedMsg(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        view.hideLoading();
                    }
                });
    }

    public void updateUserPassword(final UserBean user) {
        view.showLoading();
        ApiManger.getInstance()
                .getHubService()
                .updateUserPassword(mSPUtil.getString("user_token"), user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResponseBean>() {
                    @Override
                    public void onSubscribe(final Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(final SmartResponseBean response) {
                        if (response.getError() > 0) {
                            view.showFailedMsg(response.getMsg());
                        } else {
                            view.showSuccessMsg(response.getMsg());
                        }
                    }

                    @Override
                    public void onError(final Throwable e) {
                        view.showFailedMsg(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        view.hideLoading();
                    }
                });
    }

    public void updateUserAvatar(final String localFilePath) {
        view.showLoading();
        final UserBean user = new UserBean();
        user.setAvatar(mSPUtil.getString("user_name") + ".png");
        ApiManger.getInstance()
                .getHubService()
                .getQiNiuToken(mSPUtil.getString("user_token"), user.getAvatar())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResponseBean>() {
                    @Override
                    public void onSubscribe(final Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(final SmartResponseBean response) {
                        if (response.getError() > 0) {
                            view.showFailedMsg(response.getMsg());
                        } else {
                            view.hideLoading();
                            view.showSuccessMsg(response.getMsg());
                            view.showLoading();
                            final String qiNiuToken = JsonUtil.deserialize(response.getResult(), String.class);
                            final UploadManager uploadManager = new UploadManager();
                            uploadManager.put(localFilePath, user.getAvatar(), qiNiuToken, (key, info, res) -> {
                                // info.error中包含了错误信息，可打印调试
                                if (info.isOK()) {
                                    // 上传成功后将key值上传到自己的服务器
                                    ApiManger.getInstance()
                                            .getHubService()
                                            .updateUserAvatar(mSPUtil.getString("user_token"), user)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<SmartResponseBean>() {
                                                @Override
                                                public void onSubscribe(final Disposable d) {
                                                    addDisposable(d);
                                                }

                                                @Override
                                                public void onNext(final SmartResponseBean response) {
                                                    if (response.getError() > 0) {
                                                        view.showFailedMsg(response.getMsg());
                                                    } else {
                                                        view.showSuccessMsg(response.getMsg());
                                                        final String avatar = JsonUtil.deserialize(response.getResult(), String.class);
                                                        mSPUtil.put("user_avatar", avatar);
                                                    }
                                                }

                                                @Override
                                                public void onError(final Throwable e) {
                                                    view.showFailedMsg(e.getMessage());
                                                }

                                                @Override
                                                public void onComplete() {
                                                    view.hideLoading();
                                                }
                                            });
                                } else {
                                    view.showFailedMsg(info.error);
                                }
                            }, new UploadOptions(null, null, false,
                                    (key, percent) -> LogUtils.d(key + ": " + percent), null));
                        }
                    }

                    @Override
                    public void onError(final Throwable e) {
                        view.showFailedMsg(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        view.hideLoading();
                    }
                });
    }

    @Override
    public void onDestroy() {
        view = null;
        System.gc();
    }
}
