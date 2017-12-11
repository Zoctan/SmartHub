package com.zoctan.smarthub.main.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.about.AboutFragment;
import com.zoctan.smarthub.hub.widget.HubListFragment;
import com.zoctan.smarthub.main.presenter.MainPresenter;
import com.zoctan.smarthub.main.view.MainView;
import com.zoctan.smarthub.user.widget.UserDetailFragment;
import com.zoctan.smarthub.utils.ActivityCollector;
import com.zoctan.smarthub.utils.CacheUtils;
import com.zoctan.smarthub.utils.SPUtils;
import com.zoctan.smarthub.utils.ToastUtils;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 主视图可视化界面
 */
public class MainActivity extends AppCompatActivity implements MainView {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private MainPresenter mMainPresenter;
    private SPUtils mSPUtils;
    // 保存点击的时间
    private long exitTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.addLogAdapter(new AndroidLogAdapter());
        // 设置主界面要显示的视图
        setContentView(R.layout.activity_main);
        // 初始化控件
        initView();
        // 主界面业务处理实体化
        mMainPresenter = new MainPresenter(this);
        // 将该Activity添加到ActivityCollector管理器中
        ActivityCollector.addActivity(this);
    }

    // 初始化控件
    private void initView() {
        // 设置Toolbar
        mToolbar = findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);

        // 设置DrawerLayout
        mDrawerLayout = findViewById(R.id.mDrawerLayout);
        // 创建侧滑键，并实现打开关/闭监听
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawerOpen, R.string.drawerClose);
        // 给抽屉Layout绑定切换器监听
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        // 自动和actionBar关联, 将开关的图片显示在了action上
        // 如果不设置，也可以有抽屉的效果，不过是默认的图标
        mDrawerToggle.syncState();

        // 设置NavigationView点击事件
        NavigationView mNavigationView = findViewById(R.id.mNavigationView);
        setupDrawerContent(mNavigationView);

        // switch hub
        mNavigationView.setCheckedItem(R.id.navHub);
        switch2Hub();

        // 获取侧滑栏头布局文件
        View mHeaderView = mNavigationView.getHeaderView(0);
        TextView mHeaderText = mHeaderView.findViewById(R.id.mTvUserName);
        CircleImageView mHeaderImage = mHeaderView.findViewById(R.id.mIvUserAvatar);

        // 判断是否登陆过
        mSPUtils = new SPUtils(this);
        if (!mSPUtils.getBoolean("Login") || !mSPUtils.contains("Login")) {
            mHeaderImage.setImageResource(R.mipmap.ic_user);
            mHeaderText.setText("未登录");
        } else {
            //ToastUtils.showShort(this, "已登录");
            String userAvatar = mSPUtils.getString("userAvatar");
            String userName = mSPUtils.getString("userName");
            // 显示头像
            Glide.with(this)
                    // 加载图片的地址
                    .load(userAvatar)
                    // 填充至view中
                    .into(mHeaderImage);
            // 显示用户名
            mHeaderText.setText(userName);
        }

        // 如果为日间模式
        if (Objects.equals(mSPUtils.getString("toggle"), "day") || !mSPUtils.contains("toggle")) {
            // 将toggle置为day
            mSPUtils.putString("toggle", "day");
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            mSPUtils.putString("toggle", "night");
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // 接收来自用户登录/退出/修改头像广播
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("userLogin");
        mIntentFilter.addAction("userLogout");
        mIntentFilter.addAction("modifyImg");
        // 动态注册广播
        registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    // 广播重写
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), "userLogin")
                    || Objects.equals(intent.getAction(), "userLogout")
                    || Objects.equals(intent.getAction(), "modifyImg")) {
                // 重新初始化界面
                initView();
            }
        }
    };

    // 设置NavigationView点击事件
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                mMainPresenter.switchNavigation(menuItem.getItemId());
                if (menuItem.getItemId() != R.id.navClear && menuItem.getItemId() != R.id.navDayNight) {
                    menuItem.setChecked(true);
                    // 关闭侧滑栏
                    mDrawerLayout.closeDrawers();
                }
                return true;
            }
        });
    }

    // 侧滑栏用户
    @Override
    public void switch2User() {
        if (mSPUtils.getBoolean("Login")) {
            replaceFragment(new UserDetailFragment(), "userFrame");
            mToolbar.setTitle(R.string.navUser);
        } else {
            Intent intent = new Intent();
            intent.setAction("toLogin");
            intent.addCategory("user");
            startActivity(intent);
        }
    }

    // 侧滑栏两个选项
    @Override
    public void switch2Hub() {
        // 排插栏
        replaceFragment(new HubListFragment(), "hubListFrame");
        mToolbar.setTitle(R.string.navHub);
    }

    @Override
    public void switch2Clear() {
        // 清理缓存
        new AlertDialog.Builder(this)
                .setTitle("共" + CacheUtils.getCacheSize(getDataDir()) + "，确定清理吗？")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        CacheUtils.clearApplicationData(getApplicationContext());
                        ToastUtils.showShort(getApplicationContext(), "缓存已清理");
                        // 重启APP
                        Intent intent = getApplicationContext().getPackageManager()
                                .getLaunchIntentForPackage(getApplicationContext().getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置任何操作
                    }
                }).show();
    }

    @Override
    public void switch2About() {
        // 关于栏
        replaceFragment(new AboutFragment(), "aboutFrame");
        mToolbar.setTitle(R.string.navAbout);
    }

    @Override
    public void switch2DayNight() {
        // 如果为日间模式
        if (Objects.equals(mSPUtils.getString("toggle"), "day")) {
            // 将toggle置为night
            mSPUtils.putString("toggle", "night");
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            // 将toggle置为day
            mSPUtils.putString("toggle", "day");
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        recreate();
    }

    private void replaceFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mLayoutContent, fragment, tag)
                // 显示fragment动画
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack("replace")
                .commit();
    }

    // 双击退出
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 当按下返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 如果按下的时间超过2秒
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Snackbar.make(findViewById(R.id.mDrawerLayout), "再按一次退出程序", Snackbar.LENGTH_SHORT).show();
                //ToastUtils.showShort(this, "再按一次退出程序");
                // 获取当前系统时间的毫秒数
                exitTime = System.currentTimeMillis();
            } else {
                // 关闭所有Activity
                ActivityCollector.finishAll();
                // 正常退出程序
                System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 从管理器中移除该Activity
        ActivityCollector.removeActivity(this);
        // 注销广播
        unregisterReceiver(mBroadcastReceiver);
    }
}
