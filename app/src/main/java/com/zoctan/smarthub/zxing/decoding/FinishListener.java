package com.zoctan.smarthub.zxing.decoding;

import android.app.Activity;
import android.content.DialogInterface;

/**
 * Simple listener used to exit the app in a few cases.
 */
public final class FinishListener
        implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener, Runnable {

    private final Activity activityToFinish;

    public FinishListener(final Activity activityToFinish) {
        this.activityToFinish = activityToFinish;
    }

    @Override
    public void onCancel(final DialogInterface dialogInterface) {
        run();
    }

    @Override
    public void onClick(final DialogInterface dialogInterface, final int i) {
        run();
    }

    @Override
    public void run() {
        activityToFinish.finish();
    }

}
