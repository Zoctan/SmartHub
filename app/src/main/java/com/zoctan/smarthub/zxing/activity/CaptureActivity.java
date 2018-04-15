package com.zoctan.smarthub.zxing.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.presenter.BasePresenter;
import com.zoctan.smarthub.ui.base.BaseActivity;
import com.zoctan.smarthub.zxing.camera.CameraManager;
import com.zoctan.smarthub.zxing.decoding.CaptureActivityHandler;
import com.zoctan.smarthub.zxing.decoding.InactivityTimer;
import com.zoctan.smarthub.zxing.decoding.RGBLuminanceSource;
import com.zoctan.smarthub.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class CaptureActivity extends BaseActivity implements Callback {
    @BindView(R.id.Toolbar_all)
    Toolbar mToolbar;
    @BindView(R.id.SurfaceView_scanner)
    SurfaceView mSurfaceView;
    @BindView(R.id.ViewfinderView_content)
    ViewfinderView mViewfinderView;
    public static final int RESULT_CODE_QR_SCAN = 0xA1;
    public static final String INTENT_EXTRA_KEY_QR_SCAN = "qr_scan_result";
    private static final int REQUEST_CODE_SCAN_GALLERY = 100;
    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;
    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private boolean vibrate;
    private String photo_path;
    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = mediaPlayer -> mediaPlayer.seekTo(0);

    @Override
    protected int bindLayout() {
        return R.layout.activity_scanner;
    }

    @Override
    protected BasePresenter bindPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        mToolbar.setTitle(R.string.qr_smart_hub);
        mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
        setSupportActionBar(mToolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        CameraManager.init(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        //getMenuInflater().inflate(R.menu.scanner_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN_GALLERY:
                    //获取选中图片的路径
                    final Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                    if (cursor.moveToFirst()) {
                        photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    }
                    cursor.close();

                    final ProgressDialog mProgress = new ProgressDialog(CaptureActivity.this);
                    mProgress.setMessage("正在扫描...");
                    mProgress.setCancelable(false);
                    mProgress.show();

                    new Thread(() -> {
                        Result result = scanningImage(photo_path);
                        if (result != null) {
                            Intent resultIntent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString(INTENT_EXTRA_KEY_QR_SCAN, result.getText());
                            resultIntent.putExtras(bundle);
                            CaptureActivity.this.setResult(RESULT_CODE_QR_SCAN, resultIntent);

                        } else {
                            Message m = handler.obtainMessage();
                            m.what = R.id.decode_failed;
                            m.obj = "Scan failed!";
                            handler.sendMessage(m);
                        }
                    }).start();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 扫描二维码图片的方法
     *
     * @param path x
     * @return x
     */
    public Result scanningImage(final String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        final Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        final Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
        final RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        final BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        final QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        final SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        final AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService != null && audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result  x
     * @param barcode x
     */
    public void handleDecode(final Result result, final Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        final String resultString = result.getText();
        //FIXME
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            final Intent resultIntent = new Intent();
            final Bundle bundle = new Bundle();
            bundle.putString(INTENT_EXTRA_KEY_QR_SCAN, resultString);
            // 不能使用Intent传递大于40kb的bitmap，可以使用一个单例对象存储这个bitmap
            resultIntent.putExtras(bundle);
            this.setResult(RESULT_CODE_QR_SCAN, resultIntent);
        }
        CaptureActivity.this.finish();
    }

    private void initCamera(final SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(final SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return mViewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        mViewfinderView.drawViewfinder();
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            final AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (final IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(VIBRATE_DURATION);
            }
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}