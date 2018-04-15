package com.zoctan.smarthub.utils;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;

public class ImgUtil {
    /**
     * tint着色方法
     *
     * @param drawable x
     * @param color    x
     * @return x
     */
    public static Drawable tintDrawable(final Drawable drawable, final ColorStateList color) {
        final Drawable tempDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(tempDrawable, color);
        return tempDrawable;
    }
}
