package com.zoctan.smarthub.hubDetail.widget;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
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
import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.hubDetail.presenter.HubDetailPresenter;
import com.zoctan.smarthub.hubDetail.view.HubDetailNowView;
import com.zoctan.smarthub.utils.AlerterUtil;
import com.zoctan.smarthub.utils.NiftyDialog;
import com.zoctan.smarthub.utils.NiftyDialogUtil;

import java.io.File;
import java.util.Map;

import butterknife.BindView;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

import static android.app.Activity.RESULT_OK;

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
    private final Handler handler = new Handler();
    private final HubDetailPresenter mPresenter = new HubDetailPresenter(this);
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri imageUri;

    public static HubDetailNowFragment newInstance() {
        return new HubDetailNowFragment();
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_hub_detail_now;
    }

    @Override
    protected void initView(final View view, final Bundle savedInstanceState) {
        // 插座在线即查询实时数据
        if (mSPUtil.getBoolean("hub_connected")) {
            handler.postDelayed(runnable, 1000);
        }
        // 继电器已断电
        final boolean flag;
        if (!mSPUtil.getBoolean("hub_is_electric")) {
            flag = false;
        } else {
            flag = true;
            mPresenter.loadHubDevice(
                    mSPUtil.getString("hub_onenet_id"),
                    mSPUtil.getString("user_token")
            );
        }
        mFabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(final MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_add_device:
                        if (flag) {
                            addDevice();
                        } else {
                            AlerterUtil.showDanger(getHoldingActivity(), R.string.msg_do_when_no_electric);
                        }
                        break;
                    case R.id.action_update_device:
                        if (flag) {
                            updateDevice();
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

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateDetail();
            // 间隔5秒
            handler.postDelayed(this, 1000 * 5);
        }

        void updateDetail() {
            mPresenter.loadHubNowList(
                    mSPUtil.getString("hub_onenet_id"),
                    "I,V,W,Q");
        }
    };

    public void resetHub() {
        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.nav_clear,
                        "确定清除存储的所有用电器特征值吗？",
                        R.drawable.ic_clear,
                        R.string.all_ensure);
        dialog
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        mPresenter.resetHub(mSPUtil.getString("hub_onenet_id"), mSPUtil.getString("user_token"));
                    }
                })
                .show();
    }

    public void updateDevice() {
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.dialog_update_device, null);
        final TextInputEditText mEtDeviceName = view.findViewById(R.id.EditText_device_name);
        mEtDeviceName.setText(mSPUtil.getString("device_name"));
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
                            final String name = mEtDeviceName.getText().toString();
                            final DeviceBean deviceBean = new DeviceBean();
                            deviceBean.setId(mSPUtil.getString("device_id"));
                            deviceBean.setName(name);
                            deviceBean.setHub_id(mSPUtil.getString("hub_onenet_id"));
                            mPresenter.doDevice(deviceBean, mSPUtil.getString("user_token"), "update");
                            dialog.dismiss();
                        }
                    }
                })
                .show();
    }

    private void showUpdateImgDialog() {
        @SuppressWarnings("ConstantConditions") final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("设置用电器图片");
        // 指定照片保存路径（应用本身的缓存目录）
        imageUri = Uri.fromFile(new File(getHoldingActivity().getCacheDir(),
                System.currentTimeMillis() + "device.png"));
        final String[] items = {"选择本地照片", "拍照"};
        builder.setNegativeButton("取消", null);
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

    // 修改头像对话框操作处理
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
                            final DeviceBean deviceBean = new DeviceBean();
                            deviceBean.setId(mSPUtil.getString("device_id"));
                            deviceBean.setHub_id(mSPUtil.getString("device_hub_id"));
                            deviceBean.setImg(mSPUtil.getString("device_id") + mSPUtil.getString("device_hub_id") + ".png");
                            final UserBean userBean = new UserBean();
                            userBean.setToken(mSPUtil.getString("user_token"));
                            // 上传图片
                            mPresenter.uploadImg(userBean, deviceBean, imageUri.getPath());
                        }
                    }
                    break;
            }
        }
    }

    // 裁剪图片
    private void startPhotoZoom(final Uri uri) {
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
            AlerterUtil.showDanger(getHoldingActivity(), R.string.all_cannot_crop);
        }
    }

    public void addDevice() {
        @SuppressLint("InflateParams") final View view = getLayoutInflater().inflate(R.layout.dialog_new_device, null);
        final TextInputEditText mEtDeviceName = view.findViewById(R.id.EditText_device_name);

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
                        if (mEtDeviceName.getText().length() > 0) {
                            final String name = mEtDeviceName.getText().toString();
                            final DeviceBean deviceBean = new DeviceBean();
                            deviceBean.setName(name);
                            deviceBean.setHub_id(mSPUtil.getString("hub_onenet_id"));
                            mPresenter.doDevice(deviceBean, mSPUtil.getString("user_token"), "add");
                            dialog.dismiss();
                        }
                    }
                })
                .show();
    }

    @Override
    public void setDevice(final DeviceBean device) {
        if (device.getImg() != null) {
            Glide.with(this).load(device.getImg()).into(mIvAppliances);
        }
        mTvAppliances.setText(device.getName());
        if (device.getHub_id() != null) {
            mSPUtil.put("device_id", device.getId());
            mSPUtil.put("device_name", device.getName());
            mSPUtil.put("device_hub_id", device.getHub_id());
            mSPUtil.put("device_img", device.getImg());
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
    public void showUpdateImgSuccessMsg(final String url, final String msg) {
        mSPUtil.put("device_img", "http://p0qgwnuel.bkt.clouddn.com/" + url);
        AlerterUtil.showInfo(getHoldingActivity(), msg);
    }

    @Override
    public void showDoDeviceSuccessMsg(final String msg) {
        AlerterUtil.showInfo(getHoldingActivity(), msg);
        mPresenter.loadHubDevice(
                mSPUtil.getString("hub_onenet_id"),
                mSPUtil.getString("user_token")
        );
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
        handler.removeCallbacks(runnable);
    }
}
