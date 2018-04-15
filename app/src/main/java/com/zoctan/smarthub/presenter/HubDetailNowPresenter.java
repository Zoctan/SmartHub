package com.zoctan.smarthub.presenter;

import com.blankj.utilcode.util.LogUtils;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.zoctan.smarthub.model.api.ApiManger;
import com.zoctan.smarthub.model.bean.onenet.OneNetDataStreamsBean;
import com.zoctan.smarthub.model.bean.onenet.OneNetResponseListBean;
import com.zoctan.smarthub.model.bean.smart.DeviceBean;
import com.zoctan.smarthub.model.bean.smart.OrderBean;
import com.zoctan.smarthub.model.bean.smart.SmartResponseBean;
import com.zoctan.smarthub.model.bean.smart.SmartResponseListBean;
import com.zoctan.smarthub.ui.fragment.HubDetailNowFragment;
import com.zoctan.smarthub.utils.JsonUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.zoctan.smarthub.App.mSPUtil;

public class HubDetailNowPresenter extends BasePresenter {
    private HubDetailNowFragment view;

    public HubDetailNowPresenter(final HubDetailNowFragment view) {
        super();
        this.view = view;
    }

    public void listDatastreams(final String onenetId) {
        view.showLoading();
        ApiManger.getInstance()
                .getOneNetService()
                .listDatastreams(onenetId, "I,V,Q,W")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OneNetResponseListBean>() {
                    @Override
                    public void onSubscribe(final Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(final OneNetResponseListBean response) {
                        if (response.getErrno() > 0) {
                            view.showFailedMsg(response.getError());
                        } else {
                            final Map<String, String> streams = Stream.of(response.getData())
                                    .map(object -> JsonUtil.deserialize(object.toString(), OneNetDataStreamsBean.class))
                                    .collect(Collectors.toMap(OneNetDataStreamsBean::getId, OneNetDataStreamsBean::getCurrent_value));
                            view.setData(streams);
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


    public void listDevice(final String onenetId) {
        view.showListDeviceLoading();
        ApiManger.getInstance()
                .getHubService()
                .listDevice(mSPUtil.getString("user_token"), onenetId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResponseListBean>() {
                    @Override
                    public void onSubscribe(final Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(final SmartResponseListBean response) {
                        if (response.getError() > 0) {
                            view.showFailedMsg(response.getMsg());
                        } else {
                            final List<DeviceBean> list = JsonUtil.deserialize(response.getResult(), DeviceBean.class);
                            view.listDevice(list);
                        }
                    }

                    @Override
                    public void onError(final Throwable e) {
                        view.showFailedMsg(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        view.hideListDeviceLoading();
                    }
                });
    }

    public void addDevice(final DeviceBean device) {
        view.showLoading();
        ApiManger.getInstance()
                .getHubService()
                .addDevice(mSPUtil.getString("user_token"), device)
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
                            view.showDoDeviceSuccessMsg(response.getMsg());
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

    public void updateDevice(final DeviceBean device) {
        view.showLoading();
        ApiManger.getInstance()
                .getHubService()
                .updateDevice(mSPUtil.getString("user_token"), device)
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
                            view.showDoDeviceSuccessMsg(response.getMsg());
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

    public void sendOrder(final OrderBean order) {
        view.showStoreMatchLoading();
        ApiManger.getInstance()
                .getHubService()
                .orderHub(mSPUtil.getString("user_token"), order)
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
                            final String result = JsonUtil.deserialize(response.getResult(), String.class);
                            switch (order.getOrder()) {
                                case "store":
                                    view.setHubStore(result.equals("1"));
                                    break;
                                case "match":
                                    view.setHubMatch(Integer.parseInt(result) != 0);
                                    break;
                                default:
                                    view.showSuccessMsg(response.getMsg());
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onError(final Throwable e) {
                        view.showFailedMsg(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        view.hideStoreMatchLoading();
                    }
                });
    }


    public void updateDeviceImg(final DeviceBean device, final String localFilePath) {
        view.showLoading();
        device.setImg(device.getHub_id() + device.getId() + ".png");
        ApiManger.getInstance()
                .getHubService()
                .getQiNiuToken(mSPUtil.getString("user_token"), device.getImg())
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
                            uploadManager.put(localFilePath, device.getImg(), qiNiuToken, (key, info, res) -> {
                                // info.error中包含了错误信息，可打印调试
                                if (info.isOK()) {
                                    // 上传成功后将key值上传到自己的服务器
                                    ApiManger.getInstance()
                                            .getHubService()
                                            .updateDeviceImg(mSPUtil.getString("user_token"), device)
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
                                                        view.showDoDeviceSuccessMsg(response.getMsg());
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
