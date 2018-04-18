package com.zoctan.smarthub.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.zoctan.smarthub.R;

public class NiftyDialogUtil {
    private NiftyDialog dialog;
    private Context mContext;

    public NiftyDialogUtil() {
    }

    public NiftyDialogUtil(final Context context) {
        mContext = context;
        dialog = NiftyDialog.getInstance(mContext);
        this.init();
    }

    public NiftyDialogUtil setView(final View view, final Context context) {
        mContext = context;
        dialog = NiftyDialog.getInstance(context);
        dialog.setCustomView(view, context);
        this.init();
        return this;
    }

    private void init() {
        dialog.withTitleColor(ContextCompat.getColor(mContext, R.color.white))
                .withMessageColor(ContextCompat.getColor(mContext, R.color.white))
                .withDividerColor(ContextCompat.getColor(mContext, R.color.divider))
                .withDialogColor(ContextCompat.getColor(mContext, R.color.primary))
                .withDuration(250)
                .withEffect(Effectstype.RotateBottom)
                .withButton2Text(mContext.getString(R.string.all_cancel))
                .setButton2Click(v -> dialog.dismiss());
    }

    public NiftyDialogUtil setIcon(final int icon) {
        @SuppressWarnings("ConstantConditions") final Drawable originBitmapDrawable = ContextCompat.getDrawable(mContext, icon).mutate();
        dialog.withIcon(ImgUtil.tintDrawable(originBitmapDrawable,
                ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.white))));
        return this;
    }

    public NiftyDialogUtil setTitle(final int title) {
        dialog.withTitle(mContext.getString(title));
        return this;
    }

    public NiftyDialogUtil setMessage(final int message) {
        dialog.withMessage(mContext.getString(message));
        return this;
    }

    public NiftyDialogUtil setMessage(final String message) {
        dialog.withMessage(message);
        return this;
    }

    public NiftyDialog setButton1Text(final int button1) {
        dialog.withButton1Text(mContext.getString(button1));
        return dialog;
    }
}
