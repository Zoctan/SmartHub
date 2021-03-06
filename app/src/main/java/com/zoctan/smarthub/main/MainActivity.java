package com.zoctan.smarthub.main;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.CacheUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.about.AboutFragment;
import com.zoctan.smarthub.base.BaseActivity;
import com.zoctan.smarthub.hubList.widget.HubListFragment;
import com.zoctan.smarthub.user.widget.UserDetailFragment;
import com.zoctan.smarthub.utils.AlerterUtil;
import com.zoctan.smarthub.utils.NiftyDialog;
import com.zoctan.smarthub.utils.NiftyDialogUtil;

import java.util.Objects;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends BaseActivity {

    @BindView(R.id.Toolbar_all)
    Toolbar mToolbar;
    @BindView(R.id.NavigationView_main)
    NavigationView mNavigationView;
    @BindView(R.id.DrawerLayout_main)
    DrawerLayout mDrawerLayout;
    private TextView headerUserName;
    private CircleImageView headerUserAvatar;
    private final CacheUtils mCacheUtil = CacheUtils.getInstance();
    // 点击的时间
    private long exitTime = 0;

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        setSupportActionBar(mToolbar);
        // 创建侧滑键，并实现打开关/闭监听
        final ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.main_drawer_open, R.string.main_drawer_close);
        // 给抽屉Layout绑定切换器监听
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        // 自动和actionBar关联, 将开关的图片显示在了action上
        // 如果不设置，也可以有抽屉的效果，不过是默认的图标
        mActionBarDrawerToggle.syncState();

        // 设置NavigationView点击事件
        setupDrawerContent(mNavigationView);
        // 获取侧滑栏头布局文件
        final View headerView = mNavigationView.getHeaderView(0);
        headerUserName = headerView.findViewById(R.id.TextView_user_name);
        headerUserAvatar = headerView.findViewById(R.id.CircleImageView_user_avatar);

        // 设置侧滑栏头布局用户信息
        MainActivityPermissionsDispatcher.setHeaderUserWithPermissionCheck(this);
        // 设置广播接收
        setBroadcastReceiver();
        // 默认显示插座列表
        mNavigationView.setCheckedItem(R.id.item_hub_list);
        switch2Hub();
    }

    private void setBroadcastReceiver() {
        // 接收来自用户修改广播
        final IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("update_user_info_or_avatar");
        // 动态注册广播
        registerReceiver(broadcastReceiver, mIntentFilter);
    }

    // 广播
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            // 修改了用户名或头像都要更新主界面侧滑栏
            if (Objects.equals(intent.getAction(), "update_user_info_or_avatar")) {
                setHeaderUser();
            }
        }
    };

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void setHeaderUser() {
        // 显示头像
        Glide.with(this).load(mSPUtil.getString("user_avatar")).into(headerUserAvatar);
        // 显示用户名
        headerUserName.setText(mSPUtil.getString("user_name"));
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    // 给用户解释要请求什么权限，为什么需要此权限
    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void showRationale(final PermissionRequest request) {
        final NiftyDialog dialog = new NiftyDialogUtil(this)
                .init(R.string.permission_why,
                        R.string.permission_storage,
                        R.drawable.ic_alert,
                        R.string.all_ensure);
        dialog
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        dialog.dismiss();
                        request.proceed();//继续执行请求
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        dialog.dismiss();
                        request.cancel();//取消执行请求
                    }
                })
                .show();
    }

    // 一旦用户拒绝了
    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void permissionDenied() {
        AlerterUtil.showDanger(this, R.string.permission_denied);
    }

    // 用户选择的不再询问
    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void permissionDeniedNeverAsk() {
        AlerterUtil.showDanger(this, R.string.permission_denied_never_ask);
    }

    // 设置NavigationView点击事件
    public void setupDrawerContent(final NavigationView mNavigationView) {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {
                switchNavigation(menuItem.getItemId());
                if (menuItem.getItemId() != R.id.item_clear_cache
                        && menuItem.getItemId() != R.id.item_switch_day_or_night) {
                    menuItem.setChecked(true);
                    //MainActivityPermissionsDispatcher.
                    // 关闭侧滑栏
                    mDrawerLayout.closeDrawers();
                }
                return true;
            }
        });
    }

    private void switchNavigation(final int id) {
        switch (id) {
            case R.id.item_hub_list:
                switch2Hub();
                break;
            case R.id.item_user_center:
                switch2User();
                break;
            case R.id.item_about_app:
                switch2About();
                break;
            case R.id.item_switch_day_or_night:
                switch2DayNight();
                break;
            case R.id.item_clear_cache:
                switch2Clear();
                break;
        }
    }

    public void switch2User() {
        replaceFragment(new UserDetailFragment(), "user_detail_frame");
        mToolbar.setTitle(R.string.nav_user);
    }

    public void switch2Hub() {
        replaceFragment(new HubListFragment(), "hub_list_frame");
        mToolbar.setTitle(R.string.nav_hub);
    }

    public void switch2Clear() {
        final NiftyDialog dialog = new NiftyDialogUtil(this)
                .init(R.string.nav_clear,
                        "共 " + mCacheUtil.getCacheSize() + " ，确定清理吗？",
                        R.drawable.ic_clear,
                        R.string.all_ensure);
        dialog
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.get(getApplicationContext()).clearDiskCache();//清理磁盘缓存 需要在子线程中执行
                            }
                        }).start();
                        Glide.get(getApplicationContext()).clearMemory();//清理内存缓存
                        mCacheUtil.clear();
                        // 点击“确认”后的操作
                        ToastUtils.showShort("缓存已清理");
                        // 重启APP
                        final Intent intent = getApplicationContext().getPackageManager()
                                .getLaunchIntentForPackage(getApplicationContext().getPackageName());
                        if (intent != null) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                })
                .show();
    }

    public void switch2About() {
        replaceFragment(new AboutFragment(), "about_frame");
        mToolbar.setTitle(R.string.nav_about);
    }

    public void switch2DayNight() {
        setDayNightMode(true);
        mNavigationView.setCheckedItem(R.id.item_hub_list);
        recreate();
    }

    private void replaceFragment(final Fragment fragment, final String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.FrameLayout_main_content, fragment, tag)
                // 显示fragment动画
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack("replace")
                .commit();
    }

    // 双击退出
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        // 当按下返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 如果按下的时间超过2秒
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                AlerterUtil.showWarning(this, R.string.all_exit_app);
                // 获取当前系统时间的毫秒数
                exitTime = System.currentTimeMillis();
            } else {
                // 关闭所有Activity
                ActivityUtils.finishAllActivities();
                // 正常退出程序
                System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播
        unregisterReceiver(broadcastReceiver);
    }
}
