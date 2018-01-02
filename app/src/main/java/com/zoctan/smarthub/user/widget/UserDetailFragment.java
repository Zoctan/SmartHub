package com.zoctan.smarthub.user.widget;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ImageUtils;
import com.bumptech.glide.Glide;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.base.BaseFragment;
import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.user.presenter.UserDetailPresenter;
import com.zoctan.smarthub.user.view.UserDetailView;
import com.zoctan.smarthub.utils.AlerterUtil;
import com.zoctan.smarthub.utils.NiftyDialog;
import com.zoctan.smarthub.utils.NiftyDialogUtil;

import java.io.File;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

import static android.app.Activity.RESULT_OK;


public class UserDetailFragment extends BaseFragment implements UserDetailView {

    @BindView(R.id.CircleImageView_user_avatar)
    CircleImageView mCircleImageView;
    @BindView(R.id.TextView_user_name)
    TextView mTvUserName;
    @BindView(R.id.TextView_user_phone)
    TextView mTvUserPhone;
    @BindView(R.id.FabSpeedDial_user_detail)
    FabSpeedDial mFabSpeedDial;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri imageUri;
    private final UserDetailPresenter mUserDetailPresenter = new UserDetailPresenter(this);
    private String userName, userPhone;

    @Override
    protected int bindLayout() {
        return R.layout.fragment_user_detail;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        userName = mSPUtil.getString("user_name");
        userPhone = mSPUtil.getString("user_phone");
        String userAvatarUrl = mSPUtil.getString("user_avatar");
        mTvUserName.setText(userName);
        mTvUserPhone.setText(userPhone);
        Glide.with(this).load(userAvatarUrl).into(mCircleImageView);

        mFabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_new_avatar:
                        modifyImg();
                        break;
                    case R.id.action_new_info:
                        modifyInfo();
                        break;
                    case R.id.action_new_password:
                        modifyPassword();
                        break;
                    case R.id.action_logout:
                        userLogout();
                        break;
                }
                return false;
            }
        });

        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    private final TextInputEditText[] mEtPassword = new TextInputEditText[2];
    private TextInputLayout mLayoutUserPassword2;

    public void modifyPassword() {
        @SuppressLint("InflateParams") final View view = this.getLayoutInflater().inflate(R.layout.dialog_new_password, null);
        mLayoutUserPassword2 = view.findViewById(R.id.TextInputLayout_user_password2);
        mEtPassword[0] = view.findViewById(R.id.EditText_user_password);
        mEtPassword[1] = view.findViewById(R.id.EditText_user_password2);
        mEtPassword[1].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mEtPassword[0].getText().toString().equals(mEtPassword[1].getText().toString())) {
                    mLayoutUserPassword2.setErrorEnabled(true);
                    mLayoutUserPassword2.setError(getString(R.string.all_different_password));
                } else {
                    mLayoutUserPassword2.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.user_detail_new_password,
                        null,
                        R.drawable.ic_modify,
                        R.string.all_modify);
        dialog
                .setCustomView(view, getHoldingActivity())
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mEtPassword[0].getText().length() > 0
                                && mEtPassword[1].getText().length() > 0
                                && mEtPassword[0].getError() == null
                                && mEtPassword[1].getError() == null) {
                            String password = mEtPassword[0].getText().toString();
                            // fixme
                            mUserDetailPresenter.modify("password", new UserBean(userName, password));
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private final TextInputEditText[] mEtUserInfo = new TextInputEditText[2];

    public void modifyInfo() {
        @SuppressLint("InflateParams") final View view = this.getLayoutInflater().inflate(R.layout.dialog_new_info, null);
        mEtUserInfo[0] = view.findViewById(R.id.EditText_user_username);
        mEtUserInfo[1] = view.findViewById(R.id.EditText_user_phone);

        mEtUserInfo[0].setText(userName);
        mEtUserInfo[1].setText(userPhone);
        mEtUserInfo[0].setSelection(mEtUserInfo[0].getText().length());
        final NiftyDialog dialog = new NiftyDialogUtil(getHoldingActivity())
                .init(R.string.user_detail_new_info,
                        null,
                        R.drawable.ic_modify,
                        R.string.all_modify);
        dialog
                .setCustomView(view, getHoldingActivity())
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mEtUserInfo[0].getText().length() > 0
                                && mEtUserInfo[1].getText().length() > 0) {
                            userName = mEtUserInfo[0].getText().toString();
                            userPhone = mEtUserInfo[1].getText().toString();
                            UserBean user = new UserBean(userName, mSPUtil.getString("user_password"));
                            user.setPhone(userPhone);
                            mUserDetailPresenter.modify("info", user);
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    // 显示修改头像的对话框
    private void modifyImg() {
        @SuppressWarnings("ConstantConditions") AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("设置头像");
        String[] items = {"选择本地照片", "拍照"};
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    // 选择本地照片
                    case CHOOSE_PICTURE:
                        Intent openAlbumIntent = new Intent(Intent.ACTION_PICK, null);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    // 拍照
                    case TAKE_PICTURE:
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "user.jpg"));
                        // 指定照片保存路径（SD卡），user.jpg为一个临时文件，每次拍照后这个图片都会被替换
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap photo = extras.getParcelable("data");
                            // 图片处理成圆形
                            photo = ImageUtils.toRound(photo);
                            ImageUtils.save(photo, imageUri.getPath(), Bitmap.CompressFormat.JPEG);
                            // 上传图片
                            mUserDetailPresenter.uploadAvatar(userName, imageUri.getPath());
                        }
                    }
                    break;
            }
        }
    }

    // 裁剪图片方法实现
    private void startPhotoZoom(Uri uri) {
        if (uri == null) {
            AlerterUtil.showDanger(getHoldingActivity(), "图片路径不存在");
            return;
        }
        try {
            imageUri = uri;
            Intent intent = new Intent("com.android.camera.action.CROP");
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
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            AlerterUtil.showDanger(getHoldingActivity(), R.string.all_cannot_crop);
        }
    }

    // 用户退出操作
    private void userLogout() {
        mSPUtil.put("login", false);
        //noinspection ConstantConditions
        getHoldingActivity().sendBroadcast(new Intent("user_login"));
    }

    @Override
    public void showSuccessMsg(String avatarUrl) {
        if (avatarUrl != null) {
            // 更新头像链接
            mSPUtil.put("user_avatar", avatarUrl);
            // 更新头像显示
            Glide.with(this).load(avatarUrl).into(mCircleImageView);
        }
        //noinspection ConstantConditions
        getActivity().sendBroadcast(new Intent("modify"));
        AlerterUtil.showInfo(getHoldingActivity(), R.string.user_modify_success);
    }

    @Override
    public void showFailedMsg(String msg) {
        AlerterUtil.showDanger(getHoldingActivity(), msg);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
