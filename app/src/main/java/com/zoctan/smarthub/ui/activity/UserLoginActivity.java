package com.zoctan.smarthub.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.geetest.gt3unbindsdk.Bind.GT3GeetestBindListener;
import com.geetest.gt3unbindsdk.Bind.GT3GeetestUtilsBind;
import com.gyf.barlibrary.ImmersionBar;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.model.bean.smart.UserBean;
import com.zoctan.smarthub.presenter.BasePresenter;
import com.zoctan.smarthub.presenter.UserLoginPresenter;
import com.zoctan.smarthub.ui.base.BaseActivity;
import com.zoctan.smarthub.utils.AlerterUtil;
import com.zyao89.view.zloading.ZLoadingView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import mehdi.sakout.fancybuttons.FancyButton;

public class UserLoginActivity extends BaseActivity {
    @BindView(R.id.TextInputLayout_user_name)
    TextInputLayout mLayoutUserName;
    @BindView(R.id.TextInputLayout_user_password)
    TextInputLayout mLayoutUserPassword;
    @BindView(R.id.TextInputLayout_user_password2)
    TextInputLayout mLayoutUserPassword2;
    @BindView(R.id.EditText_user_name)
    TextInputEditText mEtUserName;
    @BindView(R.id.EditText_user_password)
    TextInputEditText mEtPassword;
    @BindView(R.id.EditText_user_password2)
    TextInputEditText mEtPassword2;
    @BindView(R.id.ImageView_app)
    ImageView mIvApp;
    @BindView(R.id.TextView_app)
    TextView mTvApp;
    @BindView(R.id.Button_user_login)
    FancyButton mBtnLogin;
    @BindView(R.id.Button_user_register)
    Button mBtnRegister;
    @BindView(R.id.ZLoadingView_user_login)
    ZLoadingView zLoadingView;
    @BindView(R.id.LinearLayout_user_password2)
    LinearLayout mLayoutRegister;
    @BindView(R.id.RelativeLayout_user_login)
    RelativeLayout mLayoutLogin;
    @BindView(R.id.View_user_register)
    View mViewRegisterLine;
    private GT3GeetestUtilsBind mGt3GeetestUtils;
    //设置获取id，challenge，success的URL,俗称api1，需替换成自己的服务器URL
    private static final String captchaURL = "http://www.geetest.com/demo/gt/register-click";
    // 设置二次验证的URL,俗称api2，需替换成自己的服务器URL
    private static final String validateURL = "http://www.geetest.com/demo/gt/validate-click";
    private final UserLoginPresenter mPresenter = new UserLoginPresenter(this);

    @Override
    protected int bindLayout() {
        return R.layout.activity_user_login;
    }

    @Override
    protected BasePresenter bindPresenter() {
        return mPresenter;
    }

    @Override
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();
    }

    @Override
    protected void initView() {
        mGt3GeetestUtils = new GT3GeetestUtilsBind(this);
    }

    @OnTextChanged(value = R.id.EditText_user_name, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void onUserNameChanged(final CharSequence s) {
        if (s.length() > 12) {
            mLayoutUserName.setErrorEnabled(true);
            mLayoutUserName.setError(getString(R.string.all_max_name));
        } else {
            mLayoutUserName.setError(null);
        }
    }

    @OnTextChanged(value = R.id.EditText_user_password2, callback = OnTextChanged.Callback.TEXT_CHANGED)
    void onPassword2Changed() {
        if (!mEtPassword.getText().toString().equals(mEtPassword2.getText().toString())) {
            mLayoutUserPassword2.setErrorEnabled(true);
            mLayoutUserPassword2.setError(getString(R.string.all_different_password));
        } else {
            mLayoutUserPassword2.setError(null);
        }
    }

    /**
     * 通过交换按钮的text来交换位置
     */
    @OnClick(R.id.Button_user_register)
    public void exchangeButton() {
        if (mBtnLogin.getText() == getString(R.string.user_register)) {
            mBtnLogin.setText(getString(R.string.user_login));
            mBtnRegister.setText(R.string.user_register);
            mViewRegisterLine.setVisibility(View.GONE);
            mLayoutRegister.setVisibility(View.GONE);
            mIvApp.setVisibility(View.VISIBLE);
            mTvApp.setTextSize(14);
        } else {
            mBtnLogin.setText(getString(R.string.user_register));
            mBtnRegister.setText(R.string.user_login);
            mViewRegisterLine.setVisibility(View.VISIBLE);
            mLayoutRegister.setVisibility(View.VISIBLE);
            mIvApp.setVisibility(View.GONE);
            mTvApp.setTextSize(18);
        }
    }

    private Boolean isValidate() {
        Boolean flag = false;
        if (mEtUserName.getText().length() > 0
                && mEtPassword.getText().length() > 0
                && mLayoutUserName.getError() == null
                && mLayoutUserPassword.getError() == null) {
            flag = true;
        }
        if (mBtnLogin.getText() == getString(R.string.user_register)
                && mEtPassword2.getText().length() == 0
                && mLayoutUserPassword2.getError() != null) {
            flag = false;
        }
        return flag;
    }

    /**
     * 登录或注册点击
     */
    @OnClick(R.id.Button_user_login)
    public void clickButton() {
        if (!isValidate()) {
            return;
        }
        mGt3GeetestUtils.getGeetest(this, captchaURL, validateURL, "zh", new GT3GeetestBindListener() {
            /**
             * 往API1请求中添加参数
             * 添加数据为Map集合
             * 添加的数据以get形式提交
             */
            @Override
            public Map<String, String> gt3CaptchaApi1() {
                return new HashMap<>();
            }

            /**
             * 往二次验证里面put数据
             * put类型是map类型
             * 注意map的键名不能是以下三个：geetest_challenge，geetest_validate，geetest_seccode
             */
            @Override
            public Map<String, String> gt3SecondResult() {
                final Map<String, String> map = new HashMap<>();
                map.put("testkey", "12315");
                return map;
            }

            /**
             * 二次验证完成的回调
             * result为验证后的数据
             * 根据二次验证返回的数据判断此次验证是否成功
             * 二次验证成功调用 mGt3GeetestUtils.gt3TestFinish();
             * 二次验证失败调用 mGt3GeetestUtils.gt3TestClose();
             */
            @Override
            public void gt3DialogSuccessResult(final String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        final JSONObject jobj = new JSONObject(result);
                        final String sta = jobj.getString("status");
                        if ("success".equals(sta)) {
                            mGt3GeetestUtils.gt3TestFinish();
                            final UserBean user = new UserBean();
                            user.setUsername(mEtUserName.getText().toString());
                            user.setPassword(mEtPassword.getText().toString());
                            if (mBtnLogin.getText() == getString(R.string.user_register)) {
                                mPresenter.loginRegister(user, "register");
                            } else {
                                mPresenter.loginRegister(user, "login");
                            }
                        } else {
                            mGt3GeetestUtils.gt3TestClose();
                        }
                    } catch (final JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mGt3GeetestUtils.gt3TestClose();
                }
            }

            /**
             * 验证过程错误
             * 返回的错误码为判断错误类型的依据
             */
            @Override
            public void gt3DialogOnError(final String error) {
                LogUtils.i("gt3DialogOnError");
            }
        });
        // 设置是否可以点击屏幕边缘关闭验证码
        mGt3GeetestUtils.setDialogTouch(true);
    }

    public void showSuccessMsg(final String msg) {
        AlerterUtil.showInfo(this, msg);
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        mGt3GeetestUtils.cancelUtils();
        finish();
    }

    public void showLoading() {
        zLoadingView.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        zLoadingView.setVisibility(View.GONE);
    }

    public void showFailedMsg(final String msg) {
        AlerterUtil.showDanger(this, msg);
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mGt3GeetestUtils.changeDialogLayout();
    }
}