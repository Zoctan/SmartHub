package com.zoctan.smarthub.ui.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zoctan.smarthub.R;

public class TitleBar extends Toolbar {
    // 中心标题
    private TextView mCenterText;
    // 中心图标
    private ImageView mCenterIcon;
    // 左侧文字
    private TextView mLeftText;
    // 左侧图标
    private ImageButton mLeftIcon;
    // 右侧文字
    private TextView mRightText;
    // 右侧图标
    private ImageButton mRightIcon;

    public TitleBar(final Context context) {
        super(context);
    }

    public TitleBar(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public TitleBar(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 左侧文字
     */
    public void setLeftText(@StringRes final int Rid) {
        setLeftText(this.getContext().getText(Rid));
    }

    /**
     * ToolBar左侧有contentInsetStart 16Dp的空白，若需要可自己定义style修改
     * 详情请见 http://my.oschina.net/yaly/blog/502471
     */
    public void setLeftText(final CharSequence text) {
        final Context context = this.getContext();
        if (this.mLeftText == null) {
            this.mLeftText = new TextView(context);
            this.mLeftText.setGravity(Gravity.CENTER_VERTICAL);
            this.mLeftText.setSingleLine();
            this.mLeftText.setEllipsize(TextUtils.TruncateAt.END);
            setLeftTextAppearance(getContext(), R.style.TextAppearance_TitleBar_subTitle);
            //textView in left
            this.addMyView(this.mLeftText, Gravity.START);
        }
        mLeftText.setText(text);
    }

    public void setLeftTextAppearance(final Context context, @StyleRes final int resId) {
        if (this.mLeftText != null) {
            this.mLeftText.setTextAppearance(context, resId);
        }
    }

    public void setLeftTextColor(@ColorInt final int color) {
        if (this.mLeftText != null) {
            this.mLeftText.setTextColor(color);
        }
    }

    public void setLeftTextOnClickListener(final OnClickListener listener) {
        if (mLeftText != null) {
            mLeftText.setOnClickListener(listener);
        }
    }

    /**
     * 左侧图标
     */
    public void setLeftIcon(@DrawableRes final int resId) {
        setLeftIcon(ContextCompat.getDrawable(this.getContext(), resId));
    }

    public void setLeftIcon(final Drawable drawable) {
        final Context context = this.getContext();
        if (this.mLeftIcon == null) {
            this.mLeftIcon = new ImageButton(context);
            this.mLeftIcon.setBackground(null);
            //保持点击区域
            final int padding = (int) this.getContext().getResources().getDimension(R.dimen.title_left_margin);
            this.mLeftIcon.setPadding(padding, 0, padding, 0);

            this.mLeftIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            this.addMyView(this.mLeftIcon, Gravity.START);
        } else {
            if (mLeftIcon.getVisibility() != VISIBLE) {
                mLeftIcon.setVisibility(VISIBLE);
            }
        }
        if (mLeftText != null && mLeftText.getVisibility() != GONE) {
            mLeftText.setVisibility(GONE);
        }
        mLeftIcon.setImageDrawable(drawable);
    }

    public void setLeftIconOnClickListener(final OnClickListener listener) {
        if (mLeftIcon != null) {
            mLeftIcon.setOnClickListener(listener);
        }
    }

    /**
     * 居中标题
     */
    public void setCenterText(@StringRes final int Rid) {
        setCenterText(this.getContext().getText(Rid));
    }

    public void setCenterText(final CharSequence text) {
        final Context context = this.getContext();
        if (this.mCenterText == null) {
            this.mCenterText = new TextView(context);
            this.mCenterText.setGravity(Gravity.CENTER);
            this.mCenterText.setSingleLine();
            this.mCenterText.setEllipsize(TextUtils.TruncateAt.END);
            setCenterTextAppearance(getContext(), R.style.TextAppearance_TitleBar_Title);
            //textView in center
            this.addMyView(this.mCenterText, Gravity.CENTER);
        } else {
            if (this.mCenterText.getVisibility() != VISIBLE) {
                mCenterText.setVisibility(VISIBLE);
            }
        }
        if (mCenterIcon != null && mCenterIcon.getVisibility() != GONE) {
            mCenterIcon.setVisibility(GONE);
        }
        //隐藏toolbar自带的标题
        setTitle("");
        mCenterText.setText(text);
        mCenterText.setTextColor(getResources().getColor(R.color.white));
    }

    public void setCenterTextAppearance(final Context context, @StyleRes final int resId) {
        if (this.mCenterText != null) {
            this.mCenterText.setTextAppearance(context, resId);
        }
    }

    public void setCenterTextColor(@ColorInt final int color) {
        if (this.mCenterText != null) {
            this.mCenterText.setTextColor(color);
        }
    }

    /**
     * 居中图标
     */
    public void setCenterIcon(@DrawableRes final int resId) {
        setCenterIcon(ContextCompat.getDrawable(this.getContext(), resId));
    }

    public void setCenterIcon(final Drawable drawable) {
        final Context context = this.getContext();
        if (this.mCenterIcon == null) {
            this.mCenterIcon = new ImageView(context);
            this.mCenterIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            //textView in center
            this.addMyView(this.mCenterIcon, Gravity.CENTER);
        } else {
            if (mCenterIcon.getVisibility() != VISIBLE) {
                mCenterIcon.setVisibility(VISIBLE);
            }
        }
        if (mCenterText != null && mCenterText.getVisibility() != GONE) {
            mCenterText.setVisibility(GONE);
        }
        //隐藏toolbar自带的标题
        setTitle("");
        mCenterIcon.setImageDrawable(drawable);
    }

    /**
     * 右侧文字
     */
    public void setRightText(@StringRes final int Rid) {
        setRightText(this.getContext().getText(Rid));
    }

    public void setRightText(final CharSequence text) {
        final Context context = this.getContext();
        if (this.mRightText == null) {
            this.mRightText = new TextView(context);
            this.mRightText.setGravity(Gravity.CENTER);
            this.mRightText.setSingleLine();
            this.mRightText.setEllipsize(TextUtils.TruncateAt.END);
            setRightTextAppearance(getContext(), R.style.TextAppearance_TitleBar_subTitle);
            //textView in center
            final int padding = (int) this.getContext().getResources().getDimension(R.dimen.title_right_margin);
            this.mRightText.setPadding(padding, 0, padding, 0);

            this.addMyView(this.mRightText, Gravity.END);
        } else {
            if (mRightText.getVisibility() != VISIBLE) {
                mRightText.setVisibility(VISIBLE);
            }
        }
        if (mRightIcon != null && mRightIcon.getVisibility() != GONE) {
            mRightIcon.setVisibility(GONE);
        }
        mRightText.setText(text);
        mRightText.setTextColor(getResources().getColor(R.color.white));
    }

    public void setRightTextAppearance(final Context context, @StyleRes final int resId) {
        if (mRightText != null) {
            mRightText.setTextAppearance(context, resId);
        }
    }

    public void setRightTextColor(@ColorInt final int color) {
        if (mRightText != null) {
            mRightText.setTextColor(color);
        }
    }

    public void setRightTextOnClickListener(final OnClickListener listener) {
        if (mRightText != null) {
            mRightText.setOnClickListener(listener);
        }
    }

    /**
     * 右侧图标
     */
    public void setRightIcon(@DrawableRes final int resId) {
        setRightIcon(ContextCompat.getDrawable(this.getContext(), resId));
        //获取系统判定的最低华东距离
//        ViewConfiguration.get(this.getContext()).getScaledTouchSlop();
    }

    public void setRightIcon(final Drawable drawable) {
        final Context context = this.getContext();
        if (this.mRightIcon == null) {
            this.mRightIcon = new ImageButton(context);
            this.mRightIcon.setBackground(null);
            //保持点击区域
            final int padding = (int) this.getContext().getResources().getDimension(R.dimen.title_right_margin);
            this.mRightIcon.setPadding(padding, 0, padding, 0);

            this.mRightIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            //textView in center
            this.addMyView(this.mRightIcon, Gravity.END);
        } else {
            if (mRightIcon.getVisibility() != VISIBLE) {
                mRightIcon.setVisibility(VISIBLE);
            }
        }
        if (mRightText != null && mRightText.getVisibility() != GONE) {
            mRightText.setVisibility(GONE);
        }
        mRightIcon.setImageDrawable(drawable);
    }

    public void setRightIconOnClickListener(final OnClickListener listener) {
        if (mRightIcon != null) {
            mRightIcon.setOnClickListener(listener);
        }
    }

    private void addMyView(final View v, final int gravity) {
        addMyView(v, gravity, 0, 0, 0, 0);
    }

    private void addMyView(final View v, final int gravity, final int left, final int top, final int right, final int bottom) {
        final LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, gravity);
        lp.setMargins(left, top, right, bottom);
        this.addView(v, lp);
    }
}
