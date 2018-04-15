package com.zoctan.smarthub.zxing.camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

final class PreviewCallback implements Camera.PreviewCallback {

    private static final String TAG = PreviewCallback.class.getSimpleName();

    private final CameraConfigurationManager configManager;
    private final boolean useOneShotPreviewCallback;
    private Handler previewHandler;
    private int previewMessage;

    PreviewCallback(final CameraConfigurationManager configManager,
                    final boolean useOneShotPreviewCallback) {
        this.configManager = configManager;
        this.useOneShotPreviewCallback = useOneShotPreviewCallback;
    }

    void setHandler(final Handler previewHandler, final int previewMessage) {
        this.previewHandler = previewHandler;
        this.previewMessage = previewMessage;
    }

    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera) {
        final Point cameraResolution = configManager.getCameraResolution();
        if (!useOneShotPreviewCallback) {
            camera.setPreviewCallback(null);
        }
        if (previewHandler != null) {
            final Message message = previewHandler.obtainMessage(previewMessage, cameraResolution.x,
                    cameraResolution.y, data);
            message.sendToTarget();
            previewHandler = null;
        } else {
            Log.d(TAG, "Got preview callback, but no handler for it");
        }
    }
}
