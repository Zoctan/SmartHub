package com.zoctan.smarthub.zxing.decoding;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.zxing.activity.CaptureActivity;
import com.zoctan.smarthub.zxing.camera.CameraManager;
import com.zoctan.smarthub.zxing.camera.PlanarYUVLuminanceSource;

import java.util.Hashtable;


final class DecodeHandler extends Handler {
    private static final String TAG = DecodeHandler.class.getSimpleName();

    private final CaptureActivity activity;
    private final MultiFormatReader multiFormatReader;

    DecodeHandler(final CaptureActivity activity, final Hashtable<DecodeHintType, Object> hints) {
        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
        this.activity = activity;
    }

    @Override
    public void handleMessage(final Message message) {
        switch (message.what) {
            case R.id.decode:
                //Log.d(TAG, "Got decode message");
                decode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case R.id.quit:
                Looper.myLooper().quit();
                break;
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
     * reuse the same reader objects from one decode to the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    @SuppressWarnings("SuspiciousNameCombination")
    private void decode(final byte[] data, int width, int height) {
        final long start = System.currentTimeMillis();
        Result rawResult = null;

        //modify here
        final byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }
        final int tmp = width; // Here we are swapping, that's the difference to #11
        width = height;
        height = tmp;
        final PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(rotatedData, width, height);
        final BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            rawResult = multiFormatReader.decodeWithState(bitmap);
        } catch (final ReaderException re) {
            // continue
        } finally {
            multiFormatReader.reset();
        }

        if (rawResult != null) {
            final long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
            final Message message = Message.obtain(activity.getHandler(), R.id.decode_succeeded, rawResult);
            final Bundle bundle = new Bundle();
            bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
            message.setData(bundle);
            //Log.d(TAG, "Sending decode succeeded message...");
            message.sendToTarget();
        } else {
            final Message message = Message.obtain(activity.getHandler(), R.id.decode_failed);
            message.sendToTarget();
        }
    }

}
