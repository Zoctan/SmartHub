package com.zoctan.smarthub.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Toast统一管理类
 */
public class ToastUtils {

    private static Toast shortToast = null;

    @SuppressLint("ShowToast")
    private static void makeShortText(String msg, Context context) {
        if (context == null) {
            return;
        }

        if (shortToast == null) {
            shortToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            shortToast.setText(msg);
        }
        shortToast.show();
    }


    private static Toast longToast = null;

    @SuppressLint("ShowToast")
    private static void makeLongText(String msg, Context context) {
        if (context == null) {
            return;
        }

        if (longToast == null) {
            longToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            longToast.setText(msg);
        }
        longToast.show();
    }


    /**
     * 长时间显示Toast
     *
     * @param context 上下文
     * @param msg 消息
     */
    public static void showLong(Context context, String msg) {
        makeLongText(msg, context);
    }

    /**
     * 长时间显示Toast
     *
     * @param context 上下文
     * @param id 消息id
     */
    public static void showLong(Context context, int id) {
        makeLongText(context.getResources().getString(id), context);
    }

    /**
     * 短时间显示Toast
     *
     * @param context 上下文
     * @param msg 消息
     */
    public static void showShort(Context context, String msg) {
        makeShortText(msg, context);
    }

    /**
     * 短时间显示Toast
     *
     * @param context 上下文
     * @param id 消息id
     */
    public static void showShort(Context context, int id) {
        makeShortText(context.getResources().getString(id), context);
    }

    /**
     * 短时间居中显示Toast
     *
     * @param context 上下文
     * @param msg 消息
     */
    public static void showCenterToast(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
