package com.zoctan.smarthub.zxing.decoding;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.zxing.LuminanceSource;

import java.io.FileNotFoundException;

/**
 * This class is used to help decode images from files which arrive as RGB data
 * from Android bitmaps. It does not support cropping or rotation.
 */
public final class RGBLuminanceSource extends LuminanceSource {
    private final byte[] luminances;

    public RGBLuminanceSource(final String path) throws FileNotFoundException {
        this(loadBitmap(path));
    }

    public RGBLuminanceSource(final Bitmap bitmap) {
        super(bitmap.getWidth(), bitmap.getHeight());

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        final int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        // In order to measure pure decoding speed, we convert the entire image
        // to a greyscale array
        // up front, which is the same as the Y channel of the
        // YUVLuminanceSource in the real app.
        luminances = new byte[width * height];
        for (int y = 0; y < height; y++) {
            final int offset = y * width;
            for (int x = 0; x < width; x++) {
                final int pixel = pixels[offset + x];
                final int r = (pixel >> 16) & 0xff;
                final int g = (pixel >> 8) & 0xff;
                final int b = pixel & 0xff;
                if (r == g && g == b) {
                    // Image is already greyscale, so pick any channel.
                    luminances[offset + x] = (byte) r;
                } else {
                    // Calculate luminance cheaply, favoring green.
                    luminances[offset + x] = (byte) ((r + g + g + b) >> 2);
                }
            }
        }
    }

    private static Bitmap loadBitmap(final String path) throws FileNotFoundException {
        final Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap == null) {
            throw new FileNotFoundException("Couldn't open " + path);
        }
        return bitmap;
    }

    @Override
    public byte[] getRow(final int y, byte[] row) {
        if (y < 0 || y >= getHeight()) {
            throw new IllegalArgumentException("Requested row is outside the image: " + y);
        }
        final int width = getWidth();
        if (row == null || row.length < width) {
            row = new byte[width];
        }

        System.arraycopy(luminances, y * width, row, 0, width);
        return row;
    }

    // Since this class does not support cropping, the underlying byte array
    // already contains
    // exactly what the caller is asking for, so give it to them without a copy.
    @Override
    public byte[] getMatrix() {
        return luminances;
    }

}
