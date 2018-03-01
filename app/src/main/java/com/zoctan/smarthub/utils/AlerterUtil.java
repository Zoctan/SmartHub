package com.zoctan.smarthub.utils;

import android.app.Activity;

import com.tapadoo.alerter.Alerter;
import com.zoctan.smarthub.R;

public class AlerterUtil {

    private static final int defaultIcon;

    static {
        defaultIcon = R.drawable.ic_robot;
    }

    public static void showInfo(final Activity activity, final int msg) {
        showInfo(activity, activity.getString(msg));
    }

    public static void showInfo(final Activity activity, final String msg) {
        show(activity, msg, R.color.accent);
    }

    public static void showWarning(final Activity activity, final int msg) {
        showWarning(activity, activity.getString(msg));
    }

    public static void showWarning(final Activity activity, final String msg) {
        show(activity, msg, R.color.orange);
    }

    public static void showDanger(final Activity activity, final int msg) {
        showDanger(activity, activity.getString(msg));
    }

    public static void showDanger(final Activity activity, final String msg) {
        show(activity, msg, R.color.danger);
    }

    public static void show(final Activity activity, final String msg, final int color) {
        Alerter.create(activity)
                .setText(msg)
                .setIcon(defaultIcon)
                .setBackgroundColorRes(color)
                .show();
    }

    public static void showLoading(final Activity activity) {
        Alerter.create(activity)
                .setText(R.string.msg_loading)
                .setIcon(defaultIcon)
                .setBackgroundColorRes(R.color.accent)
                .setDuration(15 * 1000)
                .enableProgress(true)
                .setProgressColorRes(R.color.orange)
                .show();
    }

    public static void hideLoading() {
        Alerter.hide();
    }
}
