package com.zoctan.smarthub.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;

import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.StringUtils;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.model.bean.smart.FeedbackBean;
import com.zoctan.smarthub.presenter.BasePresenter;
import com.zoctan.smarthub.presenter.FeedbackPresenter;
import com.zoctan.smarthub.ui.base.BaseFragment;
import com.zoctan.smarthub.ui.custom.MyTextWatcher;
import com.zoctan.smarthub.utils.AlerterUtil;
import com.zyao89.view.zloading.ZLoadingView;

import butterknife.BindView;
import butterknife.OnClick;

public class FeedbackFragment extends BaseFragment implements FragmentUtils.OnBackClickListener {
    @BindView(R.id.EditText_email)
    TextInputEditText mEtEmail;
    @BindView(R.id.TextInputLayout_email)
    TextInputLayout mILayoutMail;
    @BindView(R.id.EditText_phone)
    TextInputEditText mEtPhone;
    @BindView(R.id.TextInputLayout_phone)
    TextInputLayout mILayoutPhone;
    @BindView(R.id.EditText_msg)
    TextInputEditText mEtMsg;
    @BindView(R.id.ZLoadingView_feedback)
    ZLoadingView zLoadingView;
    private final FeedbackPresenter mPresenter = new FeedbackPresenter(this);

    public static FeedbackFragment newInstance() {
        final Bundle args = new Bundle();
        final FeedbackFragment fragment = new FeedbackFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int bindLayout() {
        return R.layout.fragment_feedback;
    }

    @Override
    protected BasePresenter bindPresenter() {
        return mPresenter;
    }

    @Override
    protected void initView(final View view, final Bundle savedInstanceState) {
        mEtEmail.setText(mSPUtil.getString("user_email"));
        mEtPhone.setText(mSPUtil.getString("user_phone"));
        mEtEmail.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                if (!RegexUtils.isEmail(mEtEmail.getText().toString())) {
                    mILayoutMail.setErrorEnabled(true);
                    mEtEmail.setError(getString(R.string.error_mail_format));
                } else {
                    mEtEmail.setError(null);
                }
            }
        });
        mEtPhone.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                if (!RegexUtils.isMobileSimple(mEtPhone.getText().toString())) {
                    mILayoutPhone.setErrorEnabled(true);
                    mEtPhone.setError(getString(R.string.error_phone_format));
                } else {
                    mEtPhone.setError(null);
                }
            }
        });
    }

    @OnClick(R.id.Button_submit)
    public void submit() {
        final String email = mEtEmail.getText().toString();
        final String phone = mEtPhone.getText().toString();
        final String msg = mEtMsg.getText().toString();
        if (StringUtils.isEmpty(email) && StringUtils.isEmpty(phone)) {
            this.showFailedMsg("请输入邮箱或手机号");
            return;
        }
        if (StringUtils.isEmpty(msg)) {
            this.showFailedMsg("请输入建议/反馈信息");
            return;
        }
        if (mILayoutMail.getError() != null || mILayoutPhone.getError() != null) {
            this.showFailedMsg("请确保无误后再提交");
            return;
        }
        mPresenter.feedback(new FeedbackBean(email, phone, msg));
    }

    public void showLoading() {
        zLoadingView.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        zLoadingView.setVisibility(View.GONE);
    }

    public void showSuccessMsg(final String msg) {
        AlerterUtil.showInfo(getHoldingActivity(), msg);
    }

    public void showFailedMsg(final String msg) {
        AlerterUtil.showDanger(getHoldingActivity(), msg);
    }

    @Override
    public boolean onBackClick() {
        return false;
    }
}
