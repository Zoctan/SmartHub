package com.zoctan.smarthub.ui.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.vansuita.library.Icon;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.presenter.BasePresenter;
import com.zoctan.smarthub.ui.base.BaseActivity;
import com.zoctan.smarthub.ui.custom.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;

public class ScannerActivity extends BaseActivity {
    @BindView(R.id.TitleBar_scanner)
    TitleBar mTitleBar;
    @BindView(R.id.DecoratedBarcodeView_scanner)
    DecoratedBarcodeView mDecoratedBarcodeView;
    @BindView(R.id.ImageView_torch)
    ImageView mIvTorch;
    // 扫描图片成功返回码
    public final static int RESULT_CODE_PICK_IMAGE = 0xA1;
    private CaptureManager mCaptureManager;
    private boolean isTorchOpen = false;

    @Override
    protected int bindLayout() {
        return R.layout.activity_scanner;
    }

    @Override
    protected BasePresenter bindPresenter() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCaptureManager = new CaptureManager(this, mDecoratedBarcodeView);
        mCaptureManager.initializeFromIntent(getIntent(), savedInstanceState);
        mCaptureManager.decode();
    }

    @Override
    protected void initView() {
        initToolBar();
        // 如果手机没有闪光灯
        if (!hasFlash()) {
            mIvTorch.setVisibility(View.GONE);
        }
    }

    private void initToolBar() {
        mTitleBar.setCenterText(R.string.qr_smart_hub);
        setSupportActionBar(mTitleBar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleBar.setNavigationOnClickListener(v -> onBackPressed());
        // FrameLayout 布局下，TooBar 需要置顶才能看到
        mTitleBar.bringToFront();
    }


    @OnClick(R.id.ImageView_torch)
    public void torch(final View view) {
        if (isTorchOpen) {
            isTorchOpen = false;
            // 关闪光灯
            mDecoratedBarcodeView.setTorchOff();
            Icon.on(mIvTorch).color(R.color.un_select).icon(R.drawable.ic_torch_off).put();
        } else {
            isTorchOpen = true;
            // 开闪光灯
            mDecoratedBarcodeView.setTorchOn();
            Icon.on(mIvTorch).color(R.color.yellow).icon(R.drawable.ic_torch_on).put();
        }
    }

    /**
     * 权限处理
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        mCaptureManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 按键处理
     */

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        return mDecoratedBarcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    /**
     * 检查手机有没有闪光灯
     */
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCaptureManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCaptureManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCaptureManager.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        mCaptureManager.onSaveInstanceState(outState);
    }
}