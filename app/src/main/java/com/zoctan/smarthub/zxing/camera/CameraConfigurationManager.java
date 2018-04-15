package com.zoctan.smarthub.zxing.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.util.regex.Pattern;

final class CameraConfigurationManager {
    private static final String TAG = CameraConfigurationManager.class.getSimpleName();

    private static final int TEN_DESIRED_ZOOM = 27;
    private static final int DESIRED_SHARPNESS = 30;

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    private final Context context;
    private Point screenResolution;
    private Point cameraResolution;
    private int previewFormat;
    private String previewFormatString;

    CameraConfigurationManager(final Context context) {
        this.context = context;
    }

    private static Point getCameraResolution(final Camera.Parameters parameters,
                                             final Point screenResolution) {

        String previewSizeValueString = parameters.get("preview-size-values");
        // saw this on Xperia
        if (previewSizeValueString == null) {
            previewSizeValueString = parameters.get("preview-size-value");
        }

        Point cameraResolution = null;

        if (previewSizeValueString != null) {
            Log.d(TAG, "preview-size-values parameter: " + previewSizeValueString);
            cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
        }

        if (cameraResolution == null) {
            // Ensure that the camera resolution is a multiple of 8, as the screen may not be.
            cameraResolution = new Point(
                    (screenResolution.x >> 3) << 3,
                    (screenResolution.y >> 3) << 3);
        }

        return cameraResolution;
    }

    private static Point findBestPreviewSizeValue(final CharSequence previewSizeValueString,
                                                  final Point screenResolution) {
        int bestX = 0;
        int bestY = 0;
        int diff = Integer.MAX_VALUE;
        for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {

            previewSize = previewSize.trim();
            final int dimPosition = previewSize.indexOf('x');
            if (dimPosition < 0) {
                Log.w(TAG, "Bad preview-size: " + previewSize);
                continue;
            }

            final int newX;
            final int newY;
            try {
                newX = Integer.parseInt(previewSize.substring(0, dimPosition));
                newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
            } catch (final NumberFormatException nfe) {
                Log.w(TAG, "Bad preview-size: " + previewSize);
                continue;
            }

            final int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
            if (newDiff == 0) {
                bestX = newX;
                bestY = newY;
                break;
            } else if (newDiff < diff) {
                bestX = newX;
                bestY = newY;
                diff = newDiff;
            }

        }

        if (bestX > 0 && bestY > 0) {
            return new Point(bestX, bestY);
        }
        return null;
    }

    private static int findBestMotZoomValue(final CharSequence stringValues,
                                            final int tenDesiredZoom) {
        int tenBestValue = 0;
        for (String stringValue : COMMA_PATTERN.split(stringValues)) {
            stringValue = stringValue.trim();
            final double value;
            try {
                value = Double.parseDouble(stringValue);
            } catch (final NumberFormatException nfe) {
                return tenDesiredZoom;
            }
            final int tenValue = (int) (10.0 * value);
            if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom - tenBestValue)) {
                tenBestValue = tenValue;
            }
        }
        return tenBestValue;
    }

    public static int getDesiredSharpness() {
        return DESIRED_SHARPNESS;
    }

    /**
     * Reads, one time, values from the camera that are needed by the app.
     */
    @SuppressWarnings("SuspiciousNameCombination")
    void initFromCameraParameters(final Camera camera) {
        final Camera.Parameters parameters = camera.getParameters();
        previewFormat = parameters.getPreviewFormat();
        previewFormatString = parameters.get("preview-format");
        Log.d(TAG, "Default preview format: " + previewFormat + '/' + previewFormatString);
        final WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics dm = new DisplayMetrics();
        if (manager != null) {
            manager.getDefaultDisplay().getMetrics(dm);
        }
        screenResolution = new Point(dm.widthPixels, dm.heightPixels);
        Log.d(TAG, "Screen resolution: " + screenResolution);

        //图片拉伸
        final Point screenResolutionForCamera = new Point();
        screenResolutionForCamera.x = screenResolution.x;
        screenResolutionForCamera.y = screenResolution.y;
        // preview size is always something like 480*320, other 320*480
        if (screenResolution.x < screenResolution.y) {
            screenResolutionForCamera.x = screenResolution.y;
            screenResolutionForCamera.y = screenResolution.x;
        }

        cameraResolution = getCameraResolution(parameters, screenResolutionForCamera);
        Log.d(TAG, "Camera resolution: " + screenResolution);

    }

    /**
     * Sets the camera up to take preview images which are used for both preview and decoding.
     * We detect the preview format here so that buildLuminanceSource() can build an appropriate
     * LuminanceSource subclass. In the future we may want to force YUV420SP as it's the smallest,
     * and the planar Y can be used for barcode scanning without a copy in some cases.
     */
    void setDesiredCameraParameters(final Camera camera) {
        final Camera.Parameters parameters = camera.getParameters();
        Log.d(TAG, "Setting preview size: " + cameraResolution);
        parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
        setFlash(parameters);
        setZoom(parameters);
        //setSharpness(parameters);
        //modify here
        camera.setDisplayOrientation(90);
        camera.setParameters(parameters);
    }

    Point getCameraResolution() {
        return cameraResolution;
    }

    Point getScreenResolution() {
        return screenResolution;
    }

    int getPreviewFormat() {
        return previewFormat;
    }

    String getPreviewFormatString() {
        return previewFormatString;
    }

    private void setFlash(final Camera.Parameters parameters) {
        // FIXME: This is a hack to turn the flash off on the Samsung Galaxy.
        // And this is a hack-hack to work around a different value on the Behold II
        // Restrict Behold II check to Cupcake, per Samsung's advice
        //if (Build.MODEL.contains("Behold II") &&
        //    CameraManager.SDK_INT == Build.VERSION_CODES.CUPCAKE) {
        if (Build.MODEL.contains("Behold II") && CameraManager.SDK_INT == 3) { // 3 = Cupcake
            parameters.set("flash-value", 1);
        } else {
            parameters.set("flash-value", 2);
        }
        // This is the standard setting to turn the flash off that all devices should honor.
        parameters.set("flash-mode", "off");
    }

    private void setZoom(final Camera.Parameters parameters) {

        final String zoomSupportedString = parameters.get("zoom-supported");
        if (zoomSupportedString != null && !Boolean.parseBoolean(zoomSupportedString)) {
            return;
        }

        int tenDesiredZoom = TEN_DESIRED_ZOOM;

        final String maxZoomString = parameters.get("max-zoom");
        if (maxZoomString != null) {
            try {
                final int tenMaxZoom = (int) (10.0 * Double.parseDouble(maxZoomString));
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (final NumberFormatException nfe) {
                Log.w(TAG, "Bad max-zoom: " + maxZoomString);
            }
        }

        final String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
        if (takingPictureZoomMaxString != null) {
            try {
                final int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom;
                }
            } catch (final NumberFormatException nfe) {
                Log.w(TAG, "Bad taking-picture-zoom-max: " + takingPictureZoomMaxString);
            }
        }

        final String motZoomValuesString = parameters.get("mot-zoom-values");
        if (motZoomValuesString != null) {
            tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom);
        }

        final String motZoomStepString = parameters.get("mot-zoom-step");
        if (motZoomStepString != null) {
            try {
                final double motZoomStep = Double.parseDouble(motZoomStepString.trim());
                final int tenZoomStep = (int) (10.0 * motZoomStep);
                if (tenZoomStep > 1) {
                    tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
                }
            } catch (final NumberFormatException nfe) {
                // continue
            }
        }

        // Set zoom. This helps encourage the user to pull back.
        // Some devices like the Behold have a zoom parameter
        if (maxZoomString != null || motZoomValuesString != null) {
            parameters.set("zoom", String.valueOf(tenDesiredZoom / 10.0));
        }

        // Most devices, like the Hero, appear to expose this zoom parameter.
        // It takes on values like "27" which appears to mean 2.7x zoom
        if (takingPictureZoomMaxString != null) {
            parameters.set("taking-picture-zoom", tenDesiredZoom);
        }
    }

}
