package com.zoctan.smarthub.zxing.view;

import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;

public final class ViewfinderResultPointCallback implements ResultPointCallback {
    private final ViewfinderView viewfinderView;

    public ViewfinderResultPointCallback(final ViewfinderView viewfinderView) {
        this.viewfinderView = viewfinderView;
    }

    @Override
    public void foundPossibleResultPoint(final ResultPoint point) {
        viewfinderView.addPossibleResultPoint(point);
    }

}
