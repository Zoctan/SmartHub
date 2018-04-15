package com.zoctan.smarthub.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
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
import com.squareup.picasso.Picasso;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.model.bean.smart.DeviceBean;
import com.zoctan.smarthub.model.bean.smart.HubBean;
import com.zoctan.smarthub.model.bean.smart.OrderBean;
import com.zoctan.smarthub.presenter.BasePresenter;
import com.zoctan.smarthub.presenter.HubDetailNowPresenter;
import com.zoctan.smarthub.ui.base.BaseFragment;
import com.zoctan.smarthub.utils.AlerterUtil;
import com.zoctan.smarthub.utils.NiftyDialog;
import com.zoctan.smarthub.utils.NiftyDialogUtil;
import com.zyao89.view.zloading.ZLoadingView;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

import static android.app.Activity.RESULT_OK;

public class HubDetailNowFragment extends BaseFragment {
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
    protected HubBean hubBean;
    private DeviceBean device;
    private static final int CHOOSE_PICTURE = 0;
    private static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;

    public static HubDetailNowFragment newInstance() {
        return new HubDetailNowFragment();
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hubBean = new HubBean();
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
    protected BasePresenter bindPresenter() {
        return mPresenter;
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
            final List<DeviceBean> list = Collections.singletonList(new DeviceBean(getString(R.string.msg_can_not_load_when_no_electric)));
            listDevice(list);
        } else {
            flag = true;
            mPresenter.listDevice(hubBean.getOnenet_id());
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
            mPresenter.listDatastreams(hubBean.getOnenet_id());
            // 间隔5秒
            handler.postDelayed(this, 5 * 1000);
        }
    };

    public void updateDevice() {
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.dialog_update_device, null);
        final TextInputEditText mEtDeviceName = view.findViewById(R.id.EditText_device_name);
        mEtDeviceName.setText(device.getName());
        mEtDeviceName.setSelection(mEtDeviceName.getText().length());

        final Button mBtnDeviceImg = view.findViewById(R.id.Button_device_img);

        mBtnDeviceImg.setOnClickListener(v -> showUpdateImgDialog());

        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.hub_detail_update_device,
                        null,
                        R.drawable.ic_update,
                        R.string.all_edit);
        dialog
                .setCustomView(view, getHoldingActivity())
                .setButton1Click(v -> {
                    if (mEtDeviceName.getText().length() > 0) {
                        getHoldingActivity().hideSoftKeyBoard(mEtDeviceName, getContext());
                        device.setName(mEtDeviceName.getText().toString());
                        mPresenter.crudDevice(device, "update");
                        dialog.dismiss();
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
        builder.setItems(items, (dialog, which) -> {
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
                            // 上传图片
                            mPresenter.updateDeviceImg(device, imageUri.getPath());
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
                .setButton1Click(v -> {
                    final OrderBean resetOrder = new OrderBean(hubBean.getOnenet_id(), "reset", 1);
                    mPresenter.sendOrder(resetOrder);
                    dialog.dismiss();
                })
                .show();
    }

    private TextView mTvStore = null;
    private TextView mTvMatch = null;
    private Button mBtnStore = null;
    private Button mBtnMatch = null;
    private ZLoadingView storeOrMatchLoading = null;
    private final int list = 0;

    public void showStoreMatchLoading() {
        mBtnStore.setVisibility(View.GONE);
        mBtnMatch.setVisibility(View.GONE);
        storeOrMatchLoading.setVisibility(View.VISIBLE);
    }

    public void hideStoreMatchLoading() {
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

        final OrderBean storeOrder = new OrderBean(hubBean.getOnenet_id(), "store", 1);
        mBtnStore.setOnClickListener(v -> mPresenter.sendOrder(storeOrder));

        final OrderBean matchOrder = new OrderBean(hubBean.getOnenet_id(), "match", 1);
        mBtnMatch.setOnClickListener(v -> mPresenter.sendOrder(matchOrder));

        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.hub_detail_add_device,
                        null,
                        R.drawable.ic_update,
                        R.string.all_add);
        dialog
                .setCustomView(view, getHoldingActivity())
                .setButton1Click(v -> {
                    try {
                        if (mEtDeviceName.getText().length() > 0
                                && mTvStore.getText().toString().equals("有效")
                                && mTvMatch.getText().toString().equals("有效")
                                && list != 0) {
                            getHoldingActivity().hideSoftKeyBoard(mEtDeviceName, getContext());
                            final DeviceBean deviceBean = new DeviceBean();
                            deviceBean.setName(mEtDeviceName.getText().toString());
                            deviceBean.setEigenvalue(list);
                            deviceBean.setHub_id(hubBean.getOnenet_id());
                            mPresenter.crudDevice(deviceBean, "add");
                            dialog.dismiss();
                        }
                    } catch (final NullPointerException ignored) {
                    }
                })
                .show();
    }

    public void setHubStore(final boolean flag) {
        mTvStore.setText(flag ? R.string.is_validate : R.string.not_validate);
    }

    public void setHubMatch(final boolean flag) {
        mTvMatch.setText(flag ? R.string.is_validate : R.string.not_validate);
    }

    public void listDevice(final List<DeviceBean> list) {
        final DeviceBean device = list.get(0);
        try {
            if (device.getImg() != null) {
                Picasso.get()
                        .load(device.getImg())
                        .into(mIvAppliances);
            }
        } catch (final NullPointerException ignored) {

        }
        mTvAppliances.setText(device.getName());
        if (device.getHub_id() != null) {
            this.device = device;
        }
    }

    public void setData(final Map<String, String> data) {
        mTvVoltage.setText(data.get("V"));
        mTvAmpere.setText(data.get("I"));
        mTvPowerFactor.setText(data.get("Q"));
        mTvPower.setText(data.get("W"));
    }

    public void showDoDeviceSuccessMsg(final String msg) {
        AlerterUtil.showInfo(getHoldingActivity(), msg);
        mPresenter.listDevice(hubBean.getOnenet_id());
    }

    public void showListDeviceLoading() {
        zLoadingView.setVisibility(View.VISIBLE);
    }

    public void hideListDeviceLoading() {
        zLoadingView.setVisibility(View.GONE);
    }

    public void showLoading() {
        AlerterUtil.showLoading(getHoldingActivity());
    }

    public void hideLoading() {
        AlerterUtil.hideLoading();
    }

    public void showSuccessMsg(final String msg) {
        AlerterUtil.showInfo(getHoldingActivity(), msg);
    }

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
