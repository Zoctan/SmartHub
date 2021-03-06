package com.zoctan.smarthub.user.widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.CacheUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.bumptech.glide.Glide;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.base.BaseFragment;
import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.user.presenter.UserDetailPresenter;
import com.zoctan.smarthub.user.view.UserDetailView;
import com.zoctan.smarthub.utils.AlerterUtil;
import com.zoctan.smarthub.utils.NiftyDialog;
import com.zoctan.smarthub.utils.NiftyDialogUtil;
import com.zyao89.view.zloading.ZLoadingView;

import java.io.File;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

import static android.app.Activity.RESULT_OK;
import static com.zoctan.smarthub.utils.ImgUtil.CHOOSE_PICTURE;
import static com.zoctan.smarthub.utils.ImgUtil.CROP_SMALL_PICTURE;
import static com.zoctan.smarthub.utils.ImgUtil.TAKE_PICTURE;


public class UserDetailFragment extends BaseFragment implements UserDetailView {

    @BindView(R.id.CircleImageView_user_avatar)
    CircleImageView mCircleImageView;
    @BindView(R.id.TextView_user_name)
    TextView mTvUserName;
    @BindView(R.id.TextView_user_phone)
    TextView mTvUserPhone;
    @BindView(R.id.FabSpeedDial_user_detail)
    FabSpeedDial mFabSpeedDial;
    @BindView(R.id.ZLoadingView_user_detail)
    ZLoadingView zLoadingView;
    protected static Uri imageUri;
    private final UserDetailPresenter mUserDetailPresenter = new UserDetailPresenter(this);
    private String userName, userPhone;

    @Override
    protected int bindLayout() {
        return R.layout.fragment_user_detail;
    }

    @Override
    protected void initView(final View view, final Bundle savedInstanceState) {
        userName = mSPUtil.getString("user_name");
        userPhone = mSPUtil.getString("user_phone");
        mTvUserName.setText(userName);
        mTvUserPhone.setText(userPhone);
        Glide.with(this).load(mSPUtil.getString("user_avatar")).into(mCircleImageView);

        mFabSpeedDial.setMenuListener(new MenuListener());

        // android 7.0系统解决拍照的问题
        final StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    private class MenuListener extends SimpleMenuListenerAdapter {
        @Override
        public boolean onMenuItemSelected(final MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_new_avatar:
                    showUpdateImgDialog();
                    break;
                case R.id.action_new_info:
                    showUpdateInfoDialog();
                    break;
                case R.id.action_new_password:
                    showModifyPasswordDialog();
                    break;
                case R.id.action_logout:
                    userLogout();
                    break;
            }
            return false;
        }
    }

    public void showUpdateImgDialog() {
        @SuppressWarnings("ConstantConditions") final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("设置图片");
        final String[] items = {"选择本地照片", "拍照"};
        builder.setNegativeButton("取消", null);
        // 指定照片保存路径（应用本身的缓存目录）
        imageUri = Uri.fromFile(new File(getHoldingActivity().getCacheDir(),
                System.currentTimeMillis() + "avatar.png"));
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

    // 修改头像操作处理
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
                            final UserBean userBean = new UserBean();
                            userBean.setAvatar(userName + ".png");
                            // 上传图片
                            mUserDetailPresenter.qiNiuUpload(userToken, userBean, imageUri.getPath());
                        }
                    }
                    break;
            }
        }
    }

    public void showUpdateInfoDialog() {
        @SuppressLint("InflateParams") final View view = this.getLayoutInflater().inflate(R.layout.dialog_new_info, null);
        final TextInputEditText[] mEtUserInfo = {view.findViewById(R.id.EditText_user_username), view.findViewById(R.id.EditText_user_phone)};
        mEtUserInfo[0].setText(userName);
        mEtUserInfo[1].setText(userPhone);
        mEtUserInfo[0].setSelection(mEtUserInfo[0].getText().length());
        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.user_detail_new_info,
                        null,
                        R.drawable.ic_update,
                        R.string.all_update);
        dialog
                .setCustomView(view, getHoldingActivity())
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (mEtUserInfo[0].getText().length() > 0
                                && mEtUserInfo[1].getText().length() > 0) {
                            userName = mEtUserInfo[0].getText().toString();
                            userPhone = mEtUserInfo[1].getText().toString();
                            if (RegexUtils.isMobileSimple(userPhone)) {
                                getHoldingActivity().hideSoftKeyBoard(mEtUserInfo[0], getContext());
                                final UserBean user = new UserBean();
                                user.setUsername(userName);
                                user.setPhone(userPhone);
                                user.setAction("info");
                                mUserDetailPresenter.update(user, userToken);
                                dialog.dismiss();
                            } else {
                                AlerterUtil.showDanger(getHoldingActivity(), R.string.msg_phone_error);
                            }
                        }
                    }
                })
                .show();
    }

    public void showModifyPasswordDialog() {
        @SuppressLint("InflateParams") final View view = this.getLayoutInflater().inflate(R.layout.dialog_new_password, null);
        final TextInputLayout mLayoutUserPassword2 = view.findViewById(R.id.TextInputLayout_user_password2);
        final TextInputEditText[] mEtPassword = {view.findViewById(R.id.EditText_user_password), view.findViewById(R.id.EditText_user_password2)};
        mEtPassword[1].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
            }

            @Override
            public void afterTextChanged(final Editable editable) {
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                if (!mEtPassword[0].getText().toString().equals(mEtPassword[1].getText().toString())) {
                    mLayoutUserPassword2.setErrorEnabled(true);
                    mLayoutUserPassword2.setError(getString(R.string.all_different_password));
                } else {
                    mLayoutUserPassword2.setError(null);
                }
            }
        });
        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.user_detail_new_password,
                        null,
                        R.drawable.ic_update,
                        R.string.all_update);
        dialog
                .setCustomView(view, getHoldingActivity())
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (mEtPassword[0].getText().length() > 0
                                && mEtPassword[1].getText().length() > 0
                                && mEtPassword[0].getError() == null
                                && mEtPassword[1].getError() == null) {
                            getHoldingActivity().hideSoftKeyBoard(mEtPassword[0], getContext());
                            final String password = mEtPassword[0].getText().toString();
                            final UserBean user = new UserBean();
                            user.setPassword(password);
                            user.setAction("password");
                            mUserDetailPresenter.update(user, userToken);
                            dialog.dismiss();
                        }
                    }
                })
                .show();
    }

    // 用户登出
    private void userLogout() {
        mSPUtil.clear();
        mSPUtil.put("first_open", true);
        CacheUtils.getInstance().clear();
        // 重启APP
        final Intent intent = getHoldingActivity()
                .getPackageManager()
                .getLaunchIntentForPackage(getHoldingActivity().getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void showUpdateInfoSuccessMsg(final String msg, final UserBean userBean) {
        mTvUserName.setText(userBean.getUsername());
        mTvUserPhone.setText(userBean.getPhone());
        mSPUtil.put("user_name", userBean.getUsername());
        mSPUtil.put("user_phone", userBean.getPhone());
        //noinspection ConstantConditions
        getHoldingActivity().sendBroadcast(new Intent("update_user_info_or_avatar"));
        AlerterUtil.showInfo(getHoldingActivity(), msg);
    }

    @Override
    public void showUploadSuccessMsg(final String msg, final UserBean userBean) {
        // 更新头像链接
        mSPUtil.put("user_avatar", userBean.getAvatar());
        // 更新头像显示
        Glide.with(this).load(userBean.getAvatar()).into(mCircleImageView);
        //noinspection ConstantConditions
        getHoldingActivity().sendBroadcast(new Intent("update_user_info_or_avatar"));
        AlerterUtil.showInfo(getHoldingActivity(), msg);
    }

    @Override
    public void showLoading() {
        zLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        zLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void showSuccessMsg(final String msg) {
        AlerterUtil.showInfo(getHoldingActivity(), msg);
    }

    @Override
    public void showFailedMsg(final String msg) {
        AlerterUtil.showDanger(getHoldingActivity(), msg);
    }
}
