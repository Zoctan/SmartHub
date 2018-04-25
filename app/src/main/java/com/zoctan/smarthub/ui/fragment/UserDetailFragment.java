package com.zoctan.smarthub.ui.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.CacheUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.StringUtils;
import com.squareup.picasso.Picasso;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.model.bean.smart.UserBean;
import com.zoctan.smarthub.presenter.BasePresenter;
import com.zoctan.smarthub.presenter.UserDetailPresenter;
import com.zoctan.smarthub.ui.base.BaseFragment;
import com.zoctan.smarthub.ui.custom.MyTextWatcher;
import com.zyao89.view.zloading.ZLoadingView;

import java.io.File;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

import static android.app.Activity.RESULT_OK;

public class UserDetailFragment extends BaseFragment implements FragmentUtils.OnBackClickListener {
    @BindView(R.id.CircleImageView_user_avatar)
    CircleImageView mCircleImageView;
    @BindView(R.id.TextView_user_name)
    TextView mTvUserName;
    @BindView(R.id.TextView_user_phone)
    TextView mTvUserPhone;
    @BindView(R.id.TextView_user_mail)
    TextView mTvUserMail;
    @BindView(R.id.FabSpeedDial_user_detail)
    FabSpeedDial mFabSpeedDial;
    @BindView(R.id.ZLoadingView_user_detail)
    ZLoadingView zLoadingView;
    protected static Uri imageUri;
    private final UserDetailPresenter mPresenter = new UserDetailPresenter(this);
    private static final int CHOOSE_PICTURE = 0;
    private static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;

    public static UserDetailFragment newInstance() {
        final Bundle args = new Bundle();
        final UserDetailFragment fragment = new UserDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_user_detail;
    }

    @Override
    protected BasePresenter bindPresenter() {
        return mPresenter;
    }

    @Override
    protected void initView(final View view, final Bundle savedInstanceState) {
        mTvUserName.setText(mSPUtil.getString("user_name"));
        mTvUserPhone.setText(mSPUtil.getString("user_phone"));
        mTvUserMail.setText(mSPUtil.getString("user_email"));
        Picasso.get()
                .load(mSPUtil.getString("user_avatar"))
                .into(mCircleImageView);

        mFabSpeedDial.setMenuListener(new MenuListener());

        // android 7.0系统解决拍照的问题
        final StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    @Override
    public boolean onBackClick() {
        return false;
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
                            // 上传图片
                            mPresenter.updateUserAvatar(imageUri.getPath());
                        }
                    }
                    break;
            }
        }
    }

    public void showUpdateInfoDialog() {
        final TextInputEditText[] mEtUserInfo = new TextInputEditText[3];
        final TextInputLayout[] mLayoutUserInfo = new TextInputLayout[3];
        final MaterialDialog dialog = new MaterialDialog.Builder(getHoldingActivity())
                .title(R.string.user_detail_new_info)
                .iconRes(R.drawable.ic_edit)
                .customView(R.layout.dialog_new_info, true)
                .negativeText(R.string.all_cancel)
                .positiveText(R.string.all_update)
                .onPositive((_dialog, which) -> {
                    String name = mEtUserInfo[0].getText().toString();
                    String phone = mEtUserInfo[1].getText().toString();
                    String mail = mEtUserInfo[2].getText().toString();
                    if (StringUtils.isEmpty(name)) {
                        this.showFailedMsg("请输入用户名");
                        return;
                    }
                    if (StringUtils.isEmpty(phone)) {
                        this.showFailedMsg("请输入手机号");
                        return;
                    }
                    if (StringUtils.isEmpty(mail)) {
                        this.showFailedMsg("请输入邮箱");
                        return;
                    }
                    if (mEtUserInfo[0].getError() == null
                            && mEtUserInfo[1].getError() == null
                            && mEtUserInfo[2].getError() == null) {
                        mPresenter.crudUser(new UserBean.Builder()
                                .username(name)
                                .phone(phone)
                                .build(), "updateInfo");
                        _dialog.dismiss();
                    }
                })
                .build();

        final View view = dialog.getCustomView();
        if (view != null) {
            mLayoutUserInfo[0] = view.findViewById(R.id.TextInputLayout_user_username);
            mLayoutUserInfo[1] = view.findViewById(R.id.TextInputLayout_user_phone);
            mLayoutUserInfo[2] = view.findViewById(R.id.TextInputLayout_user_mail);
            mEtUserInfo[0] = view.findViewById(R.id.EditText_user_username);
            mEtUserInfo[1] = view.findViewById(R.id.EditText_user_phone);
            mEtUserInfo[2] = view.findViewById(R.id.EditText_user_mail);
            mEtUserInfo[0].setText(mTvUserName.getText());
            mEtUserInfo[1].setText(mTvUserPhone.getText());
            mEtUserInfo[2].setText(mTvUserMail.getText());
            mEtUserInfo[0].setSelection(mEtUserInfo[0].getText().length());
            mEtUserInfo[0].addTextChangedListener(new MyTextWatcher() {
                @Override
                public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                    if (s.length() > 12) {
                        mLayoutUserInfo[0].setErrorEnabled(true);
                        mEtUserInfo[0].setError(getString(R.string.all_max_name));
                    } else {
                        mEtUserInfo[0].setError(null);
                    }
                }
            });
            mEtUserInfo[1].addTextChangedListener(new MyTextWatcher() {
                @Override
                public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                    if (!RegexUtils.isMobileSimple(mEtUserInfo[1].getText().toString())) {
                        mLayoutUserInfo[1].setErrorEnabled(true);
                        mEtUserInfo[1].setError(getString(R.string.error_phone_format));
                    } else {
                        mEtUserInfo[1].setError(null);
                    }
                }
            });
            mEtUserInfo[2].addTextChangedListener(new MyTextWatcher() {
                @Override
                public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                    if (!RegexUtils.isEmail(mEtUserInfo[2].getText().toString())) {
                        mLayoutUserInfo[2].setErrorEnabled(true);
                        mEtUserInfo[2].setError(getString(R.string.error_mail_format));
                    } else {
                        mEtUserInfo[2].setError(null);
                    }
                }
            });
        }

        dialog.show();
    }

    public void showModifyPasswordDialog() {
        final TextInputEditText[] mEtPassword = new TextInputEditText[2];
        final TextInputLayout[] mLayoutUserPassword2 = new TextInputLayout[1];
        final MaterialDialog dialog = new MaterialDialog.Builder(getHoldingActivity())
                .title(R.string.user_detail_new_password)
                .iconRes(R.drawable.ic_edit)
                .customView(R.layout.dialog_new_password, true)
                .negativeText(R.string.all_cancel)
                .positiveText(R.string.all_update)
                .onPositive((_dialog, which) -> {
                    String password1 = mEtPassword[0].getText().toString();
                    String password2 = mEtPassword[1].getText().toString();
                    if (StringUtils.isEmpty(password1)) {
                        this.showFailedMsg("请输入密码");
                        return;
                    }
                    if (StringUtils.isEmpty(password2)) {
                        this.showFailedMsg("请输入密码");
                        return;
                    }
                    if (mEtPassword[0].getError() == null) {
                        final UserBean userBean = new UserBean();
                        userBean.setPassword(password1);
                        mPresenter.crudUser(userBean, "updatePassword");
                        _dialog.dismiss();
                    }
                })
                .build();

        final View view = dialog.getCustomView();
        if (view != null) {
            mLayoutUserPassword2[0] = view.findViewById(R.id.TextInputLayout_user_password2);
            mEtPassword[0] = view.findViewById(R.id.EditText_user_password);
            mEtPassword[1] = view.findViewById(R.id.EditText_user_password2);
            mEtPassword[1].addTextChangedListener(new MyTextWatcher() {
                @Override
                public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                    if (!mEtPassword[0].getText().toString().equals(mEtPassword[1].getText().toString())) {
                        mLayoutUserPassword2[0].setErrorEnabled(true);
                        mLayoutUserPassword2[0].setError(getString(R.string.all_different_password));
                    } else {
                        mLayoutUserPassword2[0].setError(null);
                    }
                }
            });
        }

        dialog.show();
    }

    /**
     * 用户登出
     */
    private void userLogout() {
        mSPUtil.clear();
        mSPUtil.put("not_first_open", true);
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

    public void showLoading() {
        zLoadingView.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        zLoadingView.setVisibility(View.GONE);
    }
}
