package com.zoctan.smarthub.hubDetail.widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ImageUtils;
import com.bumptech.glide.Glide;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.base.BaseFragment;
import com.zoctan.smarthub.beans.DeviceBean;
import com.zoctan.smarthub.beans.HubBean;
import com.zoctan.smarthub.hubDetail.presenter.HubDetailNowPresenter;
import com.zoctan.smarthub.hubDetail.view.HubDetailNowView;
import com.zoctan.smarthub.utils.AlerterUtil;
import com.zoctan.smarthub.utils.NiftyDialog;
import com.zoctan.smarthub.utils.NiftyDialogUtil;
import com.zyao89.view.zloading.ZLoadingView;

import java.io.File;
import java.util.Map;

import butterknife.BindView;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

import static android.app.Activity.RESULT_OK;
import static com.zoctan.smarthub.utils.ImgUtil.CHOOSE_PICTURE;
import static com.zoctan.smarthub.utils.ImgUtil.CROP_SMALL_PICTURE;
import static com.zoctan.smarthub.utils.ImgUtil.TAKE_PICTURE;

public class HubDetailNowFragment extends BaseFragment implements HubDetailNowView {

    @BindView(R.id.ImageView_hub_detail_now_appliances)
    ImageView mIvAppliances;
    @BindView(R.id.TextView_hub_detail_now_appliances_name)
    TextView mTvAppliances;
    @BindView(R.id.TextView_hub_detail_now_voltage)
    TextView mTvVoltage;
    @BindView(R.id.TextView_hub_detail_now_ampere)
    TextView mTvAmpere;
    @BindView(R.id.TextView_hub_detail_now_power_factor)
    TextView mTvPowerFactor;
    @BindView(R.id.TextView_hub_detail_now_power)
    TextView mTvPower;
    @BindView(R.id.FabSpeedDial_hub_detail)
    FabSpeedDial mFabSpeedDial;
    @BindView(R.id.ZLoadingView_hub_detail_now)
    ZLoadingView zLoadingView;
    private final Handler handler = new Handler();
    private final HubDetailNowPresenter mPresenter = new HubDetailNowPresenter(this);
    protected static Uri imageUri;
    protected HubBean hubBean = new HubBean();
    private DeviceBean deviceBean;

    public static HubDetailNowFragment newInstance() {
        return new HubDetailNowFragment();
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hubBean.setName(getArguments().getString("hub_name"));
            hubBean.setOnenet_id(getArguments().getString("hub_onenet_id"));
            hubBean.setIs_electric(getArguments().getBoolean("hub_is_electric"));
            hubBean.setConnected(getArguments().getBoolean("hub_connected"));
        }
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_hub_detail_now;
    }

    @Override
    protected void initView(final View view, final Bundle savedInstanceState) {
        // 插座在线即查询实时数据
        if (hubBean.getConnected()) {
            handler.postDelayed(runnableLoadHubNowList, 0);
        }
        // 继电器已通电
        final boolean flag;
        if (!hubBean.getIs_electric()) {
            flag = false;
            final DeviceBean deviceBean = new DeviceBean();
            deviceBean.setName(getString(R.string.msg_can_not_load_when_no_electric));
            setDevice(deviceBean);
        } else {
            flag = true;
            mPresenter.loadHubDevice(hubBean.getOnenet_id(), userToken);
        }
        mFabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(final MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_add_device:
                        // 空载不允许添加
                        // 已经识别到的也不添加
                        if (flag) {
                            if (!mTvAppliances.getText().equals(getString(R.string.find_device))) {
                                if (mTvAppliances.getText().equals(getString(R.string.none_device))) {
                                    AlerterUtil.showDanger(getHoldingActivity(), R.string.msg_do_when_none);
                                } else if (mTvAppliances.getText().equals(getString(R.string.msg_add_when_none))) {
                                    addDevice();
                                } else {
                                    AlerterUtil.showDanger(getHoldingActivity(), R.string.msg_update_when_error);
                                }
                            } else {
                                AlerterUtil.showDanger(getHoldingActivity(), R.string.find_device);
                            }
                        } else {
                            AlerterUtil.showDanger(getHoldingActivity(), R.string.msg_do_when_no_electric);
                        }
                        break;
                    case R.id.action_update_device:
                        if (flag) {
                            if (!mTvAppliances.getText().equals(getString(R.string.find_device))) {
                                if (mTvAppliances.getText().equals(getString(R.string.none_device))) {
                                    AlerterUtil.showDanger(getHoldingActivity(), R.string.msg_do_when_none);
                                } else if (mTvAppliances.getText().equals(getString(R.string.msg_add_when_none))) {
                                    AlerterUtil.showDanger(getHoldingActivity(), R.string.msg_add_when_none);
                                } else {
                                    updateDevice();
                                }
                            } else {
                                AlerterUtil.showDanger(getHoldingActivity(), R.string.find_device);
                            }
                        } else {
                            AlerterUtil.showDanger(getHoldingActivity(), R.string.msg_do_when_no_electric);
                        }
                        break;
                    case R.id.action_reset_hub:
                        resetHub();
                        break;
                }
                return false;
            }
        });
    }

    private final Runnable runnableLoadHubNowList = new Runnable() {
        @Override
        public void run() {
            mPresenter.loadHubNowList(hubBean.getOnenet_id(), "I,V,W,Q");
            // 间隔5秒
            handler.postDelayed(this, 5 * 1000);
        }
    };

    public void updateDevice() {
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.dialog_update_device, null);
        final TextInputEditText mEtDeviceName = view.findViewById(R.id.EditText_device_name);
        mEtDeviceName.setText(deviceBean.getName());
        mEtDeviceName.setSelection(mEtDeviceName.getText().length());

        final Button mBtnDeviceImg = view.findViewById(R.id.Button_device_img);

        mBtnDeviceImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showUpdateImgDialog();
            }
        });

        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.hub_detail_update_device,
                        null,
                        R.drawable.ic_update,
                        R.string.all_edit);
        dialog
                .setCustomView(view, getHoldingActivity())
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (mEtDeviceName.getText().length() > 0) {
                            getHoldingActivity().hideSoftKeyBoard(mEtDeviceName, getContext());
                            final String name = mEtDeviceName.getText().toString();
                            deviceBean.setName(name);
                            deviceBean.setAction("update");
                            mPresenter.doDevice(deviceBean, userToken);
                            dialog.dismiss();
                        }
                    }
                })
                .show();
    }


    public void showUpdateImgDialog() {
        @SuppressWarnings("ConstantConditions") final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("设置图片");
        final String[] items = {"选择本地照片", "拍照"};
        builder.setNegativeButton("取消", null);
        // 指定照片保存路径（应用本身的缓存目录）
        imageUri = Uri.fromFile(new File(getHoldingActivity().getCacheDir(),
                System.currentTimeMillis() + "device.png"));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                switch (which) {
                    // 选择本地照片
                    case CHOOSE_PICTURE:
                        final Intent openAlbumIntent = new Intent(Intent.ACTION_PICK, null);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    // 拍照
                    case TAKE_PICTURE:
                        final Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.create().show();
    }

    // 裁剪图片
    public void startPhotoZoom(final Uri uri) {
        if (uri == null) {
            AlerterUtil.showDanger(getHoldingActivity(), "图片路径不存在");
            return;
        }
        try {
            final Intent intent = new Intent("com.android.camera.action.CROP");
            // 打开图片类文件
            intent.setDataAndType(uri, "image/*");
            // 设置裁剪
            intent.putExtra("crop", "true");
            // aspectX aspectY 是宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // outputX outputY 是裁剪图片宽高
            intent.putExtra("outputX", 250);
            intent.putExtra("outputY", 250);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, CROP_SMALL_PICTURE);
        } catch (final ActivityNotFoundException e) {
            e.printStackTrace();
            AlerterUtil.showDanger(getHoldingActivity(), "设备不支持裁剪行为");
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // 如果返回码是可以的
            switch (requestCode) {
                // 选择本地照片
                case CHOOSE_PICTURE:
                    // 开始对图片进行裁剪处理
                    startPhotoZoom(data.getData());
                    break;
                // 拍照
                case TAKE_PICTURE:
                    startPhotoZoom(imageUri);
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        // 上传裁剪图片
                        final Bundle extras = data.getExtras();
                        if (extras != null) {
                            final Bitmap photo = extras.getParcelable("data");
                            // 图片处理成圆形再保存
                            ImageUtils.save(
                                    ImageUtils.toRound(photo),
                                    imageUri.getPath(),
                                    Bitmap.CompressFormat.PNG);
                            deviceBean.setImg(deviceBean.getHub_id() + deviceBean.getId() + ".png");
                            // 上传图片
                            mPresenter.qiNiuUpload(userToken, deviceBean, imageUri.getPath());
                        }
                    }
                    break;
            }
        }
    }

    public void resetHub() {
        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.nav_clear,
                        R.string.msg_hub_reset,
                        R.drawable.ic_clear,
                        R.string.all_ensure);
        dialog
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mPresenter.sendOrder(hubBean.getOnenet_id(), userToken, "reset");
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private TextView mTvStore = null;
    private TextView mTvMatch = null;
    private Button mBtnStore = null;
    private Button mBtnMatch = null;
    private ZLoadingView storeOrMatchLoading = null;
    private int list = 0;

    @Override
    public void showStoreOrMatchLoading() {
        mBtnStore.setVisibility(View.GONE);
        mBtnMatch.setVisibility(View.GONE);
        storeOrMatchLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideStoreOrMatchLoading() {
        mBtnStore.setVisibility(View.VISIBLE);
        mBtnMatch.setVisibility(View.VISIBLE);
        storeOrMatchLoading.setVisibility(View.GONE);
    }

    public void addDevice() {
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.dialog_new_device, null);
        final TextInputEditText mEtDeviceName = view.findViewById(R.id.EditText_device_name);
        mTvStore = view.findViewById(R.id.TextView_store);
        mTvMatch = view.findViewById(R.id.TextView_match);
        storeOrMatchLoading = view.findViewById(R.id.ZLoadingView_new_device);
        mBtnStore = view.findViewById(R.id.Button_store_device);
        mBtnMatch = view.findViewById(R.id.Button_match_device);

        mBtnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mPresenter.sendOrder(hubBean.getOnenet_id(), userToken, "store");
            }
        });

        mBtnMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mPresenter.sendOrder(hubBean.getOnenet_id(), userToken, "match");
            }
        });

        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.hub_detail_add_device,
                        null,
                        R.drawable.ic_update,
                        R.string.all_add);
        dialog
                .setCustomView(view, getHoldingActivity())
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        try {
                            if (mEtDeviceName.getText().length() > 0
                                    && mTvStore.getText().toString().equals("有效")
                                    && mTvMatch.getText().toString().equals("有效")
                                    && list != 0) {
                                getHoldingActivity().hideSoftKeyBoard(mEtDeviceName, getContext());
                                final String name = mEtDeviceName.getText().toString();
                                final DeviceBean deviceBean = new DeviceBean();
                                deviceBean.setName(name);
                                deviceBean.setEigenvalue(list);
                                deviceBean.setHub_id(hubBean.getOnenet_id());
                                deviceBean.setAction("add");
                                mPresenter.doDevice(deviceBean, userToken);
                                dialog.dismiss();
                            }
                        } catch (final NullPointerException ignored) {
                        }
                    }
                })
                .show();
    }

    @Override
    public void setHubStore(final boolean flag) {
        if (flag) {
            mTvStore.setText(R.string.is_validate);
        } else {
            mTvStore.setText(R.string.not_validate);
        }
    }

    @Override
    public void setHubMatch(final String list) {
        this.list = Integer.parseInt(list);
        if (this.list != 0) {
            mTvMatch.setText(R.string.is_validate);
        } else {
            mTvMatch.setText(R.string.not_validate);
        }
    }

    @Override
    public void setDevice(final DeviceBean device) {
        try {
            if (device.getImg() != null) {
                Glide.with(this)
                        .load(device.getImg())
                        .into(mIvAppliances);
            }
        } catch (final NullPointerException ignored) {

        }
        mTvAppliances.setText(device.getName());
        if (device.getHub_id() != null) {
            this.deviceBean = device;
        }
    }

    @Override
    public void setData(final Map<String, String> data) {
        mTvVoltage.setText(data.get("V"));
        mTvAmpere.setText(data.get("I"));
        mTvPowerFactor.setText(data.get("Q"));
        mTvPower.setText(data.get("W"));
    }

    @Override
    public void showUploadSuccessMsg(final String msg) {
        AlerterUtil.showInfo(getHoldingActivity(), msg);
    }

    @Override
    public void showDoDeviceSuccessMsg(final String msg) {
        AlerterUtil.showInfo(getHoldingActivity(), msg);
        mPresenter.loadHubDevice(hubBean.getOnenet_id(), userToken);
    }

    @Override
    public void showLoadDeviceLoading() {
        zLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadDeviceLoading() {
        zLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        AlerterUtil.showLoading(getHoldingActivity());
    }

    @Override
    public void hideLoading() {
        AlerterUtil.hideLoading();
    }

    @Override
    public void showSuccessMsg(final String msg) {
        AlerterUtil.showInfo(getHoldingActivity(), msg);
    }

    @Override
    public void showFailedMsg(final String msg) {
        AlerterUtil.showDanger(getHoldingActivity(), msg);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 停止刷新
        handler.removeCallbacks(runnableLoadHubNowList);
    }
}
