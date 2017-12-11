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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.user.presenter.UserDetailPresenter;
import com.zoctan.smarthub.user.view.UserDetailView;
import com.zoctan.smarthub.utils.ImageUtils;
import com.zoctan.smarthub.utils.SPUtils;

import java.io.File;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.RESULT_OK;

public class UserDetailFragment extends Fragment implements UserDetailView, View.OnClickListener {

    private Button mBtnSure;
    private CheckBox mBtnEye;
    private EditText mEtNewPwd, mEtNewPwd2;
    private UserDetailPresenter mUserDetailPresenter;
    private SPUtils mSPUtils;
    private CircleImageView userAvatar;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri imageUri;
    private ProgressBar mPbLoading;
    private FrameLayout mLayoutPwd;
    private LinearLayout mLayoutUserInfo;
    private String userName, userAvatarUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mUserDetailPresenter = new UserDetailPresenter(this);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_user, null);
        mSPUtils = new SPUtils(getContext());

        userName = mSPUtils.getString("userName");
        userAvatarUrl = mSPUtils.getString("userAvatar");
        TextView mTextView = view.findViewById(R.id.mTvName);
        mTextView.setText(userName);
        // 头像
        userAvatar = view.findViewById(R.id.mIvUserAvatar);
        Glide.with(this)
                // 加载图片的地址
                .load(userAvatarUrl)
                // 填充至view中
                .into(userAvatar);

        // 初始化密码输入框和层
        mEtNewPwd = view.findViewById(R.id.mEtNewPwd);
        mEtNewPwd2 = view.findViewById(R.id.mEtNewPwd2);
        mLayoutUserInfo = view.findViewById(R.id.mLayoutUserInfo);
        mLayoutPwd = view.findViewById(R.id.mLayoutPwd);
        mPbLoading = view.findViewById(R.id.mProgressBar);

        // 初始化按钮
        mBtnSure = view.findViewById(R.id.mBtnSure);
        mBtnEye = view.findViewById(R.id.mBtnPwdEye);

        // 监听界面上的按钮
        mBtnSure.setOnClickListener(this);
        mBtnEye.setOnClickListener(this);

        // 浮动按钮
        FabSpeedDial mFabSpeedDial = view.findViewById(R.id.mFabSpeedDial);
        mFabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                getActivity().getMenuInflater().inflate(R.menu.menu_user, navigationMenu);
                // 对菜单项目初始化
                // 如果不初始化就返回false
                return true;
            }
        });

        mFabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    // 修改头像
                    case R.id.modifyAvatar:
                        modifyImg();
                        break;
                    // 修改密码
                    case R.id.modifyPwd:
                        modifyPwd();
                        break;
                    // 退出
                    case R.id.logout:
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
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnSure:
                clickSure();
                break;
            case R.id.mBtnPwdEye:
                showHidePwd();
                break;
        }
    }

    // 修改密码操作
    public void modifyPwd() {
        if (mLayoutPwd.getVisibility() == View.GONE) {
            mLayoutUserInfo.setVisibility(View.GONE);
            mLayoutPwd.setVisibility(View.VISIBLE);
        } else {
            mLayoutUserInfo.setVisibility(View.VISIBLE);
            mLayoutPwd.setVisibility(View.GONE);
        }
    }

    // 修改密码确认点击
    private void clickSure() {
        String EtNewPwd = mEtNewPwd.getText().toString();
        String EtNewPwd2 = mEtNewPwd2.getText().toString();
        // 确认密码一致
        if (Objects.equals(EtNewPwd, EtNewPwd2)) {
            mUserDetailPresenter.modifyPwd(userName, EtNewPwd);
        } else {
            View view = getActivity().findViewById(R.id.mDrawerLayout);
            Snackbar.make(view, "两次密码不一致", Snackbar.LENGTH_SHORT).show();
        }
    }


    // 显示密码
    private void showHidePwd() {
        if (mBtnEye.isChecked()) {
            //选择状态 显示明文--设置为可见的密码
            mEtNewPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mEtNewPwd2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
            mEtNewPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mEtNewPwd2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        // 设置光标位置到行尾
        mEtNewPwd.setSelection(mEtNewPwd.getText().length());
        mEtNewPwd2.setSelection(mEtNewPwd2.getText().length());
    }

    // 显示修改头像的对话框
    public void modifyImg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        Logger.d(resultCode + " " + RESULT_OK);
        if (resultCode == RESULT_OK) {
            // 如果返回码是可以的
            switch (requestCode) {
                // 选择本地照片
                case CHOOSE_PICTURE:
                    // 开始对图片进行裁剪处理
                    Logger.d("crop");
                    startPhotoZoom(data.getData());
                    break;
                // 拍照
                case TAKE_PICTURE:
                    Logger.d("care crop");
                    startPhotoZoom(imageUri);
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        // 上传裁剪图片
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap photo = extras.getParcelable("data");
                            // 图片处理成圆形
                            photo = ImageUtils.toRoundBitmap(photo, imageUri);
                            ImageUtils.savePhoto(photo, imageUri.getPath(), "user.jpg");
                            // 上传图片
                            mUserDetailPresenter.uploadAvatar(userName, imageUri.getPath());
                        }
                    }
                    break;
            }
        }
    }

    // 裁剪图片方法实现
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Logger.e("图片路径不存在");
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
            View view = getActivity().findViewById(R.id.mDrawerLayout);
            Snackbar.make(view, "你的设备不支持裁剪行为！", Snackbar.LENGTH_SHORT).show();
        }
    }

    // 用户退出操作
    public void userLogout() {
        mSPUtils.clear();
        // 将login置为false
        mSPUtils.putBoolean("Login", false);
        View view = getActivity().findViewById(R.id.mDrawerLayout);
        Snackbar.make(view, "已退出", Snackbar.LENGTH_SHORT).show();

        //ToastUtils.showShort(getContext(), "已退出");
        getActivity().sendBroadcast(new Intent("userLogout"));
    }

    @Override
    public void showSuccessMsg(String avatarUrl) {
        if (avatarUrl != null) {
            // 更新头像链接
            mSPUtils.putString("userAvatar", avatarUrl);
            // 更新头像显示
            Glide.with(this)
                    // 加载图片的地址
                    .load(avatarUrl)
                    // 填充至view中
                    .into(userAvatar);
        }
        View view = getActivity().findViewById(R.id.mDrawerLayout);
        Snackbar.make(view, "修改成功", Snackbar.LENGTH_SHORT).show();
        //ToastUtils.showShort(getContext(), "修改成功");
    }

    @Override
    public void showFailedMsg(String msg) {
        View view = getActivity().findViewById(R.id.mDrawerLayout);
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
        //ToastUtils.showShort(getContext(), "修改失败");
    }

    @Override
    public void showLoading() {
        mPbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mPbLoading.setVisibility(View.INVISIBLE);
    }
}
