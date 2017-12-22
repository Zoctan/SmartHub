package com.zoctan.smarthub.utils;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.zoctan.smarthub.R;

public class NiftyDialogUtil {

    private final NiftyDialog dialog;
    private final Activity mActivity;

    public NiftyDialogUtil(Activity activity) {
        mActivity = activity;
        dialog = NiftyDialog.getInstance(mActivity);
    }

    public NiftyDialog init(int title, String message, int icon, int button1) {
        return init(mActivity.getString(title), message, icon, button1);
    }

    public NiftyDialog init(String title, int message, int icon, int button1) {
        return init(title, mActivity.getString(message), icon, button1);
    }

    public NiftyDialog init(int title, int message, int icon, int button1) {
        return init(mActivity.getString(title), mActivity.getString(message), icon, button1);
    }

    public NiftyDialog init(String title, String message, int icon, int button1) {
        @SuppressWarnings("ConstantConditions") final Drawable originBitmapDrawable = ContextCompat.getDrawable(mActivity, icon).mutate();
        return dialog
                .withTitle(title)
                .withTitleColor(ContextCompat.getColor(mActivity, R.color.third_text))
                .withMessage(message)
                .withMessageColor(ContextCompat.getColor(mActivity, R.color.third_text))
                .withDividerColor(ContextCompat.getColor(mActivity, R.color.divider))
                .withDialogColor(ContextCompat.getColor(mActivity, R.color.primary))
                .withIcon(ImgUtil.tintDrawable(originBitmapDrawable, ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.icon))))
                .withDuration(250)
                .withEffect(Effectstype.RotateBottom)
                .withButton1Text(mActivity.getString(button1))
                .withButton2Text(mActivity.getString(R.string.all_cancel))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
    }
}
