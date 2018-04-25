package com.zoctan.smarthub.ui.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.StringUtils;
import com.squareup.picasso.Picasso;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.model.bean.smart.DeviceBean;
import com.zoctan.smarthub.model.bean.smart.HubBean;
import com.zoctan.smarthub.model.bean.smart.OrderBean;
import com.zoctan.smarthub.presenter.BasePresenter;
import com.zoctan.smarthub.presenter.HubDetailNowPresenter;
import com.zoctan.smarthub.ui.base.BaseFragment;
import com.zoctan.smarthub.ui.custom.MyTextWatcher;
import com.zoctan.smarthub.utils.AlerterUtil;
import com.zyao89.view.zloading.ZLoadingView;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import me.itangqi.waveloadingview.WaveLoadingView;
import mehdi.sakout.fancybuttons.FancyButton;

import static android.app.Activity.RESULT_OK;

public class HubDetailNowFragment extends BaseFragment {
    @BindView(R.id.ImageView_hub_detail_now_appliances)
    ImageView mIvAppliances;
    @BindView(R.id.TextView_hub_detail_now_appliances_name)
    TextView mTvAppliances;
    @BindView(R.id.FabSpeedDial_hub_detail)
    FabSpeedDial mFabSpeedDial;
    @BindView(R.id.ZLoadingView_hub_detail_now)
    ZLoadingView zLoadingView;
    @BindView(R.id.WaveLoadingView_V)
    WaveLoadingView mWvV;
    @BindView(R.id.TextView_V)
    TextView mTvV;
    @BindView(R.id.WaveLoadingView_I)
    WaveLoadingView mWvI;
    @BindView(R.id.TextView_I)
    TextView mTvI;
    @BindView(R.id.WaveLoadingView_Q)
    WaveLoadingView mWvQ;
    @BindView(R.id.TextView_Q)
    TextView mTvQ;
    @BindView(R.id.WaveLoadingView_P)
    WaveLoadingView mWvP;
    @BindView(R.id.TextView_P)
    TextView mTvP;
    private static final int CHOOSE_PICTURE = 0;
    private static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    private final Handler handler = new Handler();
    private final HubDetailNowPresenter mPresenter = new HubDetailNowPresenter(this);
    protected static Uri imageUri;
    protected static HubBean hubBean;
    private DeviceBean device;

    public static HubDetailNowFragment newInstance(final HubBean hub) {
        final Bundle args = new Bundle();
        final HubDetailNowFragment fragment = new HubDetailNowFragment();
        fragment.setArguments(args);
        hubBean = hub;
        return fragment;
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
        // 继电器已通电查询当前用电器
        if (hubBean.getIs_electric()) {
            mPresenter.listDevice(hubBean.getOnenet_id());
        } else {
            failedListDevice(R.string.msg_can_not_load_when_no_electric);
        }
        mFabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(final MenuItem menuItem) {
                // 空载不允许添加
                if (!hubBean.getIs_electric()) {
                    showFailedMsg(R.string.msg_do_when_no_electric);
                    return false;
                }
                if (mTvAppliances.getText().equals(getString(R.string.find_device))) {
                    showFailedMsg(R.string.find_device);
                    return false;
                }
                if (mTvAppliances.getText().equals(getString(R.string.none_device))) {
                    showFailedMsg(R.string.msg_do_when_none);
                    return false;
                }
                switch (menuItem.getItemId()) {
                    case R.id.action_add_device:
                        // 已经识别到的不添加
                        if (mTvAppliances.getText().equals(getString(R.string.msg_add_when_none))) {
                            addDevice();
                        } else {
                            showFailedMsg(R.string.msg_update_when_error);
                        }
                        break;
                    case R.id.action_update_device:
                        if (!mTvAppliances.getText().equals(getString(R.string.msg_add_when_none))) {
                            updateDevice();
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
        final TextInputEditText[] mEtDeviceName = new TextInputEditText[1];
        final TextInputLayout[] mLayoutDeviceName = new TextInputLayout[1];
        final FancyButton[] mBtnUpdate = new FancyButton[1];

        final MaterialDialog dialog = new MaterialDialog.Builder(getHoldingActivity())
                .title(R.string.hub_detail_update_device)
                .iconRes(R.drawable.ic_edit)
                .customView(R.layout.dialog_update_device, true)
                .negativeText(R.string.all_cancel)
                .positiveText(R.string.all_update)
                .onPositive((_dialog, which) -> {
                    String name = mEtDeviceName[0].getText().toString();
                    if (StringUtils.isEmpty(name)) {
                        this.showFailedMsg("用电器名称不能为空");
                        return;
                    }
                    final DeviceBean deviceBean = new DeviceBean(name);
                    mPresenter.crudDevice(deviceBean, "update");
                    _dialog.dismiss();
                })
                .build();

        final View view = dialog.getCustomView();
        if (view != null) {
            mLayoutDeviceName[0] = view.findViewById(R.id.TextInputLayout_device_name);
            mEtDeviceName[0] = view.findViewById(R.id.EditText_device_name);
            mEtDeviceName[0].setText(device.getName());
            mEtDeviceName[0].setSelection(mEtDeviceName[0].getText().length());
            mBtnUpdate[0] = view.findViewById(R.id.Button_device_img);
            mEtDeviceName[0].addTextChangedListener(new MyTextWatcher() {
                @Override
                public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                    if (s.length() > 12) {
                        mLayoutDeviceName[0].setErrorEnabled(true);
                        mEtDeviceName[0].setError(getString(R.string.all_max_name));
                    } else {
                        mEtDeviceName[0].setError(null);
                    }
                }
            });
            mBtnUpdate[0].setOnClickListener(v -> showUpdateImgDialog());
        }

        dialog.show();
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
                    // 先检查有没有相机权限
                    if (!PermissionUtils.isGranted(Manifest.permission.CAMERA)) {
                        this.showFailedMsg("没有相机权限");
                        return;
                    }
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
            this.showFailedMsg("图片路径不存在");
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
            LogUtils.e(e.getMessage());
            this.showFailedMsg("设备不支持裁剪行为");
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
        new MaterialDialog.Builder(getHoldingActivity())
                .title(R.string.msg_hub_reset)
                .iconRes(R.drawable.ic_alert)
                .content(R.string.hub_detail_reset_hub)
                .negativeText(R.string.all_cancel)
                .positiveText(R.string.all_ensure)
                .onPositive((dialog, which) -> {
                    final OrderBean resetOrder = new OrderBean(hubBean.getOnenet_id(), "reset", 1);
                    mPresenter.sendOrder(resetOrder);
                    dialog.dismiss();
                }).show();
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
        final TextInputEditText[] mEtDeviceName = new TextInputEditText[1];
        final TextInputLayout[] mLayoutDeviceName = new TextInputLayout[1];

        final MaterialDialog dialog = new MaterialDialog.Builder(getHoldingActivity())
                .title(R.string.hub_detail_add_device)
                .iconRes(R.drawable.ic_edit)
                .customView(R.layout.dialog_new_device, true)
                .negativeText(R.string.all_cancel)
                .positiveText(R.string.all_add)
                .onPositive((_dialog, which) -> {
                    String name = mEtDeviceName[0].getText().toString();
                    if (StringUtils.isEmpty(name)) {
                        showFailedMsg("用电器名称不能为空");
                        return;
                    }
                    if (mTvStore.getText() != "有效" || mTvMatch.getText() != "有效") {
                        showFailedMsg("插座没有保存到该电器特征值");
                        return;
                    }
                    if (mEtDeviceName[0].getError() == null) {
                        mPresenter.crudDevice(new DeviceBean.Builder()
                                .name(name)
                                .eigenvalue(list)
                                .hub_id(hubBean.getOnenet_id())
                                .build(), "add");
                        _dialog.dismiss();
                    }
                })
                .build();

        final View view = dialog.getCustomView();
        if (view != null) {
            mLayoutDeviceName[0] = view.findViewById(R.id.TextInputLayout_device_name);
            mEtDeviceName[0] = view.findViewById(R.id.EditText_device_name);

            mTvStore = view.findViewById(R.id.TextView_store);
            mTvMatch = view.findViewById(R.id.TextView_match);
            storeOrMatchLoading = view.findViewById(R.id.ZLoadingView_new_device);
            mBtnStore = view.findViewById(R.id.Button_store_device);
            mBtnMatch = view.findViewById(R.id.Button_match_device);

            mEtDeviceName[0].addTextChangedListener(new MyTextWatcher() {
                @Override
                public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                    if (s.length() > 12) {
                        mLayoutDeviceName[0].setErrorEnabled(true);
                        mEtDeviceName[0].setError(getString(R.string.all_max_name));
                    } else {
                        mEtDeviceName[0].setError(null);
                    }
                }
            });

            final OrderBean storeOrder = new OrderBean(hubBean.getOnenet_id(), "store", 1);
            mBtnStore.setOnClickListener(v -> mPresenter.sendOrder(storeOrder));

            final OrderBean matchOrder = new OrderBean(hubBean.getOnenet_id(), "match", 1);
            mBtnMatch.setOnClickListener(v -> mPresenter.sendOrder(matchOrder));
        }

        dialog.show();
    }

    public void setHubStore(final boolean flag) {
        mTvStore.setText(flag ? R.string.is_validate : R.string.not_validate);
    }

    public void setHubMatch(final boolean flag) {
        mTvMatch.setText(flag ? R.string.is_validate : R.string.not_validate);
    }

    public void successListDevice(final List<DeviceBean> list) {
        final DeviceBean device = list.get(0);
        try {
            if (device.getImg() != null) {
                Picasso.get()
                        .load(device.getImg())
                        .into(mIvAppliances);
            }
        } catch (final NullPointerException ignored) {
            return;
        }
        mTvAppliances.setText(device.getName());
        this.device = device;
    }

    public void failedListDevice(final int string) {
        mTvAppliances.setText(string);
    }

    public void failedListDevice() {
        mTvAppliances.setText(R.string.msg_add_when_none);
    }

    public void setData(final Map<String, Double> data) {
        for (final String id : data.keySet()) {
            switch (id) {
                case "V":
                    mWvV.setProgressValue((int) (data.get(id).intValue() / 230.0 * 100));
                    mTvV.setText(String.format(Locale.CHINA, "%.3f", data.get(id)));
                    break;
                case "I":
                    mWvI.setProgressValue((int) (data.get(id).intValue() / 0.8 * 100));
                    mTvI.setText(String.format(Locale.CHINA, "%.3f", data.get(id)));
                    break;
                case "W":
                    mWvP.setProgressValue((int) (data.get(id).intValue() / 200.0 * 100));
                    mTvP.setText(String.format(Locale.CHINA, "%.3f", data.get(id)));
                    break;
                case "Q":
                    mWvQ.setProgressValue((int) (data.get(id) * 100));
                    mTvQ.setText(String.format(Locale.CHINA, "%.3f", data.get(id)));
                    break;
            }
        }
    }

    public void showListDeviceLoading() {
        zLoadingView.setVisibility(View.VISIBLE);
    }

    public void hideListDeviceLoading() {
        zLoadingView.setVisibility(View.INVISIBLE);
    }

    public void showLoading() {
        AlerterUtil.showLoading(getHoldingActivity());
    }

    public void hideLoading() {
        AlerterUtil.hideLoading();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 停止刷新
        handler.removeCallbacks(runnableLoadHubNowList);
    }
}
