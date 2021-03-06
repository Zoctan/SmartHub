package com.zoctan.smarthub.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gitonway.lee.niftymodaldialogeffects.lib.ColorUtils;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.effects.BaseEffects;

// 单例模式有问题，多个对话框会出现layout重用
@SuppressLint("StaticFieldLeak")
public class NiftyDialog extends Dialog implements DialogInterface {

    private static Context mContext;
    private static NiftyDialog instance;
    private final String defTextColor = "#FFFFFFFF";
    private final String defDividerColor = "#11000000";
    private final String defMsgColor = "#FFFFFFFF";
    private final String defDialogColor = "#FFE74C3C";
    private Effectstype type = null;
    private LinearLayout mLinearLayoutView;
    private RelativeLayout mRelativeLayoutView;
    private LinearLayout mLinearLayoutMsgView;
    private LinearLayout mLinearLayoutTopView;
    private FrameLayout mFrameLayoutCustomView;
    private View mDivider;
    private TextView mTitle;
    private TextView mMessage;
    private ImageView mIcon;
    private Button mButton1;
    private Button mButton2;
    private int mDuration = -1;
    private boolean isCancelable = true;

    public NiftyDialog(Context context) {
        super(context);
        init(context);
        this.mContext = context;
    }

    public NiftyDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public static NiftyDialog getInstance(Context context) {
        if (instance == null || !mContext.equals(context)) {
            synchronized (NiftyDialog.class) {
                if (instance == null || !mContext.equals(context)) {
                    instance = new NiftyDialog(context, com.gitonway.lee.niftymodaldialogeffects.lib.R.style.dialog_untran);
                }
            }
        }
        mContext = context;
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        @SuppressWarnings("ConstantConditions") WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
    }

    private void init(Context context) {

        View mDialogView = View.inflate(context, com.gitonway.lee.niftymodaldialogeffects.lib.R.layout.dialog_layout, null);

        mLinearLayoutView = mDialogView.findViewById(com.gitonway.lee.niftymodaldialogeffects.lib.R.id.parentPanel);
        mRelativeLayoutView = mDialogView.findViewById(com.gitonway.lee.niftymodaldialogeffects.lib.R.id.main);
        mLinearLayoutTopView = mDialogView.findViewById(com.gitonway.lee.niftymodaldialogeffects.lib.R.id.topPanel);
        mLinearLayoutMsgView = mDialogView.findViewById(com.gitonway.lee.niftymodaldialogeffects.lib.R.id.contentPanel);
        mFrameLayoutCustomView = mDialogView.findViewById(com.gitonway.lee.niftymodaldialogeffects.lib.R.id.customPanel);

        mTitle = mDialogView.findViewById(com.gitonway.lee.niftymodaldialogeffects.lib.R.id.alertTitle);
        mMessage = mDialogView.findViewById(com.gitonway.lee.niftymodaldialogeffects.lib.R.id.message);
        mIcon = mDialogView.findViewById(com.gitonway.lee.niftymodaldialogeffects.lib.R.id.icon);
        mDivider = mDialogView.findViewById(com.gitonway.lee.niftymodaldialogeffects.lib.R.id.titleDivider);
        mButton1 = mDialogView.findViewById(com.gitonway.lee.niftymodaldialogeffects.lib.R.id.button1);
        mButton2 = mDialogView.findViewById(com.gitonway.lee.niftymodaldialogeffects.lib.R.id.button2);

        setContentView(mDialogView);

        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                mLinearLayoutView.setVisibility(View.VISIBLE);
                if (type == null) {
                    type = Effectstype.Slidetop;
                }
                start(type);


            }
        });
        mRelativeLayoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCancelable) dismiss();
            }
        });
    }

    public void toDefault() {
        mTitle.setTextColor(Color.parseColor(defTextColor));
        mDivider.setBackgroundColor(Color.parseColor(defDividerColor));
        mMessage.setTextColor(Color.parseColor(defMsgColor));
        mLinearLayoutView.setBackgroundColor(Color.parseColor(defDialogColor));
    }

    public NiftyDialog withDividerColor(String colorString) {
        mDivider.setBackgroundColor(Color.parseColor(colorString));
        return this;
    }

    public NiftyDialog withDividerColor(int color) {
        mDivider.setBackgroundColor(color);
        return this;
    }


    public NiftyDialog withTitle(CharSequence title) {
        toggleView(mLinearLayoutTopView, title);
        mTitle.setText(title);
        return this;
    }

    public NiftyDialog withTitleColor(String colorString) {
        mTitle.setTextColor(Color.parseColor(colorString));
        return this;
    }

    public NiftyDialog withTitleColor(int color) {
        mTitle.setTextColor(color);
        return this;
    }

    public NiftyDialog withMessage(int textResId) {
        toggleView(mLinearLayoutMsgView, textResId);
        mMessage.setText(textResId);
        return this;
    }

    public NiftyDialog withMessage(CharSequence msg) {
        toggleView(mLinearLayoutMsgView, msg);
        mMessage.setText(msg);
        return this;
    }

    public NiftyDialog withMessageColor(String colorString) {
        mMessage.setTextColor(Color.parseColor(colorString));
        return this;
    }

    public NiftyDialog withMessageColor(int color) {
        mMessage.setTextColor(color);
        return this;
    }

    public NiftyDialog withDialogColor(String colorString) {
        mLinearLayoutView.getBackground().setColorFilter(ColorUtils.getColorFilter(Color
                .parseColor(colorString)));
        return this;
    }

    public NiftyDialog withDialogColor(int color) {
        mLinearLayoutView.getBackground().setColorFilter(ColorUtils.getColorFilter(color));
        return this;
    }

    public NiftyDialog withIcon(int drawableResId) {
        mIcon.setImageResource(drawableResId);
        return this;
    }

    public NiftyDialog withIcon(Drawable icon) {
        mIcon.setImageDrawable(icon);
        return this;
    }

    public NiftyDialog withDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

    public NiftyDialog withEffect(Effectstype type) {
        this.type = type;
        return this;
    }

    public NiftyDialog withButtonDrawable(int resid) {
        mButton1.setBackgroundResource(resid);
        mButton2.setBackgroundResource(resid);
        return this;
    }

    public NiftyDialog withButton1Text(CharSequence text) {
        mButton1.setVisibility(View.VISIBLE);
        mButton1.setText(text);

        return this;
    }

    public NiftyDialog withButton2Text(CharSequence text) {
        mButton2.setVisibility(View.VISIBLE);
        mButton2.setText(text);
        return this;
    }

    public NiftyDialog setButton1Click(View.OnClickListener click) {
        mButton1.setOnClickListener(click);
        return this;
    }

    public NiftyDialog setButton2Click(View.OnClickListener click) {
        mButton2.setOnClickListener(click);
        return this;
    }

    public NiftyDialog setCustomView(int resId, Context context) {
        View customView = View.inflate(context, resId, null);
        if (mFrameLayoutCustomView.getChildCount() > 0) {
            mFrameLayoutCustomView.removeAllViews();
        }
        mFrameLayoutCustomView.addView(customView);
        mFrameLayoutCustomView.setVisibility(View.VISIBLE);
        return this;
    }

    public NiftyDialog setCustomView(View view, Context context) {
        if (mFrameLayoutCustomView.getChildCount() > 0) {
            mFrameLayoutCustomView.removeAllViews();
        }
        mFrameLayoutCustomView.addView(view);
        mFrameLayoutCustomView.setVisibility(View.VISIBLE);
        return this;
    }

    public NiftyDialog isCancelableOnTouchOutside(boolean cancelable) {
        this.isCancelable = cancelable;
        this.setCanceledOnTouchOutside(cancelable);
        return this;
    }

    public NiftyDialog isCancelable(boolean cancelable) {
        this.isCancelable = cancelable;
        this.setCancelable(cancelable);
        return this;
    }

    private void toggleView(View view, Object obj) {
        if (obj == null) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void start(Effectstype type) {
        BaseEffects animator = type.getAnimator();
        if (mDuration != -1) {
            animator.setDuration(Math.abs(mDuration));
        }
        animator.start(mRelativeLayoutView);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        instance = null;
        mButton1.setVisibility(View.GONE);
        mButton2.setVisibility(View.GONE);
    }
}
