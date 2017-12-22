package com.zoctan.smarthub.utils;

import android.app.Activity;

import com.tapadoo.alerter.Alerter;
import com.zoctan.smarthub.R;

public class AlerterUtil {

    private static int defaultIcon;

    static {
        defaultIcon = R.drawable.ic_robot;
    }

    public static void showInfo(Activity activity, int msg) {
        showInfo(activity, activity.getString(msg));
    }

    public static void showInfo(Activity activity, String msg) {
        show(activity, msg, R.color.accent);
    }

    public static void showWarning(Activity activity, int msg) {
        showWarning(activity, activity.getString(msg));
    }

    public static void showWarning(Activity activity, String msg) {
        show(activity, msg, R.color.orange);
    }

    public static void showDanger(Activity activity, int msg) {
        showDanger(activity, activity.getString(msg));
    }

    public static void showDanger(Activity activity, String msg) {
        show(activity, msg, R.color.danger);
    }

    public static void show(Activity activity, String msg, int color) {
        Alerter.create(activity)
                .setText(msg)
                .setIcon(defaultIcon)
                .setBackgroundColorRes(color)
                .show();
    }
}
