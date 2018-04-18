package com.zoctan.smarthub.presenter;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.blankj.utilcode.util.LogUtils;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.zoctan.smarthub.model.api.ApiManger;
import com.zoctan.smarthub.model.api.HubApi;
import com.zoctan.smarthub.model.bean.smart.SmartResponseBean;
import com.zoctan.smarthub.model.bean.smart.UserBean;
import com.zoctan.smarthub.ui.fragment.UserDetailFragment;
import com.zoctan.smarthub.utils.JsonUtil;

import java.util.function.Consumer;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.zoctan.smarthub.App.SMART_TOKEN;
import static com.zoctan.smarthub.App.mSPUtil;

public class UserDetailPresenter extends BasePresenter {
    private UserDetailFragment view;

    public UserDetailPresenter(final UserDetailFragment view) {
        super();
        this.view = view;
    }

    public void crudUser(final UserBean user, final String action) {
        view.showLoading();
        final HubApi api = ApiManger.getInstance().getHubService();
        final Observable<SmartResponseBean> observable;
        final Consumer<SmartResponseBean> successAction;
        switch (action) {
            case "updatePassword":
                observable = api.updateUserPassword(SMART_TOKEN, user);
                successAction = response -> {
                };
                break;
            case "updateInfo":
                observable = api.updateUserInfo(SMART_TOKEN, user);
                successAction = response -> {
                    mSPUtil.put("user_name", user.getUsername());
                    mSPUtil.put("user_phone", user.getPhone());
                    mSPUtil.put("user_email", user.getEmail());
                };
                break;
            case "list":
                observable = api.getUserInfo(SMART_TOKEN);
                successAction = response -> {
                    final UserBean userBean = JsonUtil.deserialize(response.getResult(), UserBean.class);
                    mSPUtil.put("user_id", userBean.getId());
                    mSPUtil.put("user_avatar", userBean.getAvatar());
                    mSPUtil.put("user_phone", userBean.getPhone());
                    mSPUtil.put("user_email", userBean.getEmail());
                    mSPUtil.put("user_name", userBean.getUsername());
                    mSPUtil.put("user_token", userBean.getToken());
                };
                break;
            default:
                return;
        }
        observable.subscribe(new Observer<SmartResponseBean>() {
            @Override
            public void onSubscribe(final Disposable d) {
                addDisposable(d);
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onNext(final SmartResponseBean response) {
                if (response.getError() > 0) {
                    view.showFailedMsg(response.getMsg());
                } else {
                    view.showSuccessMsg(response.getMsg());
                    successAction.accept(response);
                    crudUser(null, "list");
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
                .getQiNiuToken(SMART_TOKEN, user.getAvatar())
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
                                            .updateUserAvatar(SMART_TOKEN, user)
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
                                                        crudUser(null, "list");
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
