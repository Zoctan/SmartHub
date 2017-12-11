package com.zoctan.smarthub.user.widget;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.orhanobut.logger.Logger;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.beans.UserBean;
import com.zoctan.smarthub.user.presenter.UserLoginPresenter;
import com.zoctan.smarthub.user.view.UserLoginView;
import com.zoctan.smarthub.utils.ActivityCollector;
import com.zoctan.smarthub.utils.CodeUtils;
import com.zoctan.smarthub.utils.SPUtils;
import com.zoctan.smarthub.utils.SwipeBackActivity;
import com.zoctan.smarthub.utils.ToastUtils;

import java.util.Objects;

import me.imid.swipebacklayout.lib.SwipeBackLayout;

public class UserLoginActivity extends SwipeBackActivity implements UserLoginView, View.OnClickListener {

    private EditText mEtName, mEtPassword, mEtPassword2, mEtProve;
    private Button mBtnLogin, mBtnRegister;
    private CheckBox mBtnEye;
    private Toolbar mToolbar;
    private ImageButton mBtnProve;
    private ProgressBar mPbLoading;
    private UserLoginPresenter mUserLoginPresenter;
    private SPUtils mSPUtils;
    private CodeUtils mCodeUtils;
    private FrameLayout mRegisterLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 如果为日间模式
        mSPUtils = new SPUtils(this);
        if (Objects.equals(mSPUtils.getString("toggle"), "day") || !mSPUtils.contains("toggle")) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // 设置登录要显示的视图
        setContentView(R.layout.activity_user_login);

        // 初始化控件
        initView();

        mUserLoginPresenter = new UserLoginPresenter(this);

        // 将该Activity添加到ActivityCollector管理器中
        ActivityCollector.addActivity(this);
    }

    // 初始化控件
    private void initView() {
        // 设置Toolbar
        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.setTitle(R.string.navUser);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mCodeUtils = CodeUtils.getInstance();

        // 初始化输入框和层
        mEtName = (EditText) findViewById(R.id.mEtLoginName);
        mEtPassword = (EditText) findViewById(R.id.mEtLoginPassword);
        mRegisterLayout = (FrameLayout) findViewById(R.id.mLayoutRegister);
        mRegisterLayout.setVisibility(View.GONE);
        mEtPassword2 = (EditText) findViewById(R.id.mEtRegisterPassword);
        mEtProve = (EditText) findViewById(R.id.mEtProve);
        mPbLoading = (ProgressBar) findViewById(R.id.mProgressBar);

        // 获得SwipeBackLayout对象
        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        // 设定可从上下左右滑动退出
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);

        // 初始化按钮
        mBtnLogin = (Button) findViewById(R.id.mBtnLogin);
        mBtnRegister = (Button) findViewById(R.id.mBtnRegister);
        mBtnEye = (CheckBox) findViewById(R.id.mBtnPwdEye);
        mBtnProve = (ImageButton) findViewById(R.id.mBtnProve);

        // 监听界面上的按钮
        mBtnLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mBtnEye.setOnClickListener(this);
        mBtnProve.setOnClickListener(this);

        refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnLogin:
                clickButton();
                break;
            case R.id.mBtnRegister:
                switchClick();
                break;
            case R.id.mBtnPwdEye:
                showHidePwd();
                break;
            case R.id.mBtnProve:
                refresh();
            default:
                break;
        }
    }

    // 显示密码
    private void showHidePwd() {
        if (mBtnEye.isChecked()) {
            //选择状态 显示明文--设置为可见的密码
            mEtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mEtPassword2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
            mEtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mEtPassword2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
       // 设置光标位置到行尾
        mEtPassword.setSelection(mEtPassword.getText().length());
        mEtPassword2.setSelection(mEtPassword2.getText().length());
    }

    // 通过交换按钮的text来交换位置
    private void switchClick() {
        if(mBtnLogin.getText() == "注册") {
            initView();
            mBtnLogin.setText("登录");
            mBtnRegister.setText("注册");
        }else {
            mBtnLogin.setText("注册");
            mBtnRegister.setText("登录");
            mRegisterLayout.setVisibility(View.VISIBLE);
        }
    }

    // 登录或注册点击
    private void clickButton() {
        String name = mEtName.getText().toString();
        String password = mEtPassword.getText().toString();
        String password2 = mEtPassword2.getText().toString();
        String codeStr = mEtProve.getText().toString().trim();
        if(mBtnLogin.getText() == "注册") {
            if(checkInput(name, password, password2) && checkProve(codeStr)) {
                mUserLoginPresenter.userAction("register", name, password);
            }
        }else {
            if(checkInput(name, password) && checkProve(codeStr)) {
                mUserLoginPresenter.userAction("login", name, password);
            }
        }
    }

    public void refresh() {
        Bitmap bitmap = mCodeUtils.createBitmap();
        mBtnProve.setImageBitmap(bitmap);
    }

    // 检查验证码
    public boolean checkProve(String codeStr) {
        if (codeStr == null || TextUtils.isEmpty(codeStr)) {
            ToastUtils.showShort(this, "请输入验证码");
            return false;
        }
        String code = mCodeUtils.getCode();
        if (code.equalsIgnoreCase(codeStr)) {
            return true;
        } else {
            ToastUtils.showShort(this, "验证码错误");
            return false;
        }
    }

    // 检查输入
    public boolean checkInput(String name, String password) {
        if (name == null || TextUtils.isEmpty(name)) {
            ToastUtils.showShort(this, "用户名不能为空");
        }else {
            if (password == null || TextUtils.isEmpty(password)) {
                ToastUtils.showShort(this, "密码不能为空");
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean checkInput(String name, String password, String password2) {
        if(checkInput(name, password)) {
            if (!Objects.equals(password, password2)) {
                ToastUtils.showShort(this, "两次密码不一致");
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void showLoading() {
        mPbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mPbLoading.setVisibility(View.GONE);
    }

    @Override
    public void showSuccessMsg(UserBean userBean) {
        if(mBtnLogin.getText() == "注册") {
            ToastUtils.showShort(this, "注册成功");
        }else {
            ToastUtils.showShort(this, "登录成功");
        }
        // 将login置为true
        mSPUtils.putBoolean("Login", true);
        // 将用户信息存在本地
        mSPUtils.putString("userID", userBean.getId());
        mSPUtils.putString("userAvatar", userBean.getAvatar());
        mSPUtils.putString("userName", userBean.getUsername());
        mSPUtils.putString("userPassword", userBean.getPassword());
        Logger.d(userBean);
        // 更新主界面侧滑栏的用户UI
        Intent intent = new Intent("userLogin");
        Bundle user = new Bundle();
        user.putString("name", userBean.getUsername());
        user.putString("image", userBean.getAvatar());
        intent.putExtras(user);
        sendBroadcast(intent);
        finish();
    }

    @Override
    public void showFailedMsg() {
        if(mBtnLogin.getText() == "注册") {
            ToastUtils.showLong(this, "注册失败, 用户名已存在");
        }else {
            ToastUtils.showShort(this, "登录失败");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 从管理器中移除该Activity
        ActivityCollector.removeActivity(this);
    }
}
