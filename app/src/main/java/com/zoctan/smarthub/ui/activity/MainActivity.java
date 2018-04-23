package com.zoctan.smarthub.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.CacheUtils;
import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.presenter.BasePresenter;
import com.zoctan.smarthub.ui.base.BaseActivity;
import com.zoctan.smarthub.ui.custom.TitleBar;
import com.zoctan.smarthub.ui.fragment.FeedbackFragment;
import com.zoctan.smarthub.ui.fragment.HubListFragment;
import com.zoctan.smarthub.ui.fragment.UserDetailFragment;
import com.zoctan.smarthub.utils.AlerterUtil;

import butterknife.BindView;

import static com.zoctan.smarthub.App.mSPUtil;

public class MainActivity extends BaseActivity {
    @BindView(R.id.TitleBar_main)
    TitleBar mTitleBar;
    // 点击返回键的时间，时间短即退出APP
    private long exitTime = 0;
    private final int HUB_LIST = 0;
    private final int USER_CENTER = 1;
    private final int FEEDBACK = 2;
    private final int ABOUT = 3;
    private final int DAY_NIGHT = 4;
    private final int CLEAR = 5;
    private final int EXIT = 6;
    private Drawer drawer;
    private IProfile profile;//登录用户信息
    private AccountHeader headerResult;//head头布局

    private final Fragment[] mFragments = new Fragment[3];
    private int currentFragmentIndex;

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected BasePresenter bindPresenter() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setProfile();
        // 设置侧滑栏头布局用户信息
        setHeadLayout(savedInstanceState);
        setDrawerLayout(savedInstanceState);

        if (savedInstanceState != null) {
            currentFragmentIndex = savedInstanceState.getInt("currentFragmentIndex");
        }
        mFragments[0] = HubListFragment.newInstance();
        mFragments[1] = UserDetailFragment.newInstance();
        mFragments[2] = FeedbackFragment.newInstance();
        FragmentUtils.add(getSupportFragmentManager(), mFragments, R.id.FrameLayout_main_content, currentFragmentIndex);
    }

    @Override
    protected void initView() {
        mTitleBar.setCenterText(R.string.app);
        setSupportActionBar(mTitleBar);

        // 初始化抽屉图片加载器
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(final ImageView imageView, final Uri uri, final Drawable placeholder) {
                Picasso.get().load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(final ImageView imageView) {
                Picasso.get().cancelRequest(imageView);
            }
        });
    }

    private void setDrawerLayout(final Bundle savedInstanceState) {
        // 创建抽屉
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withSavedInstance(savedInstanceState)
                .withRootView(R.id.DrawerLayout_main)
                .withAccountHeader(headerResult)
                .withToolbar(mTitleBar)// 和toolbar关联
                .withShowDrawerOnFirstLaunch(false) // 默认开启抽屉
                .withDrawerGravity(Gravity.START) // 设置抽屉打开方向默认从左
                .withActionBarDrawerToggle(true) // 启用toolbar的ActionBarDrawerToggle动画
                .addDrawerItems(
                        setHubItem(),
                        setUserItem(),
                        setAboutItem(),
                        setSectionItem(),
                        setSwitchDayNight(),
                        setClearItem(),
                        setFeedbackItem()
                )// 给抽屉添加item布局
                .withSelectedItem(HUB_LIST)
                .build();
        // 页脚添加退出按钮
        drawer.addStickyFooterItem(setExitItem());
        // 去除阴影
        drawer.getDrawerLayout().setFitsSystemWindows(false);
        // 抽屉中item的监听事件
        drawer.setOnDrawerItemClickListener((view, position, drawerItem) -> {
            switch ((int) drawerItem.getIdentifier()) {
                case HUB_LIST:
                    showCurrentFragment(HUB_LIST);
                    mTitleBar.setCenterText(R.string.nav_hub);
                    break;
                case USER_CENTER:
                    showCurrentFragment(USER_CENTER);
                    mTitleBar.setCenterText(R.string.nav_user);
                    break;
                case ABOUT:
                    new LibsBuilder()
                            .withActivityTheme(R.style.AppTheme)
                            .withActivityTitle(getString(R.string.nav_about))
                            .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                            .withAboutAppName(getString(R.string.app))
                            .withVersionShown(false)
                            .withAboutIconShown(true)
                            .withAboutVersionShown(true)
                            .withAboutDescription(getString(R.string.about_app))
                            .withFields(R.string.class.getFields())
                            .withLicenseDialog(true)
                            .withLicenseShown(true)
                            .start(this);
                    break;
                case FEEDBACK:
                    showCurrentFragment(FEEDBACK);
                    mTitleBar.setCenterText(R.string.app);
                    break;
                case CLEAR:
                    clearApp();
                    break;
                case EXIT:
                    new MaterialDialog.Builder(this)
                            .title(R.string.nav_exit)
                            .iconRes(R.drawable.ic_alert)
                            .content("确定退出吗？")
                            .negativeText(R.string.all_cancel)
                            .positiveText(R.string.all_ensure)
                            .onPositive((dialog, which) -> finish()).show();
                    break;
            }
            return false;
        });
    }

    /**
     * 创建登录用户对象
     */
    public void setProfile() {
        profile = new ProfileDrawerItem()
                .withName(mSPUtil.getString("user_name"))
                .withEmail(mSPUtil.getString("user_email"))
                .withIcon(mSPUtil.getString("user_avatar"))
                .withIdentifier(USER_CENTER);//标识符，当设置监听事件时可以根据这个来区别对象
    }

    private void setHeadLayout(final Bundle savedInstanceState) {
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.primary)
                .withCompactStyle(true) // 横向布局
                .withTranslucentStatusBar(false) //半透明效果
                .withSelectionListEnabledForSingleProfile(false) // 只有一个用户时关闭下拉菜单
                .addProfiles(profile)
                .withSavedInstance(savedInstanceState)
                .build();
    }

    private PrimaryDrawerItem setHubItem() {
        return new PrimaryDrawerItem()
                .withName(R.string.nav_hub)
                .withIcon(R.drawable.ic_hub)
                .withIdentifier(HUB_LIST)
                .withSelectable(true);
    }

    private PrimaryDrawerItem setUserItem() {
        return new PrimaryDrawerItem()
                .withName(R.string.nav_user)
                .withIcon(R.drawable.ic_user_center)
                .withIdentifier(USER_CENTER)
                .withSelectable(true);
    }

    private PrimaryDrawerItem setAboutItem() {
        return new PrimaryDrawerItem()
                .withName(R.string.nav_about)
                .withIcon(R.drawable.ic_about)
                .withIdentifier(ABOUT)
                .withSelectable(false);
    }

    private PrimaryDrawerItem setFeedbackItem() {
        return new PrimaryDrawerItem()
                .withName(R.string.nav_feedback)
                .withIcon(R.drawable.ic_feedback)
                .withIdentifier(FEEDBACK)
                .withSelectable(true);
    }

    private PrimaryDrawerItem setClearItem() {
        return new PrimaryDrawerItem()
                .withName(R.string.nav_clear)
                .withIcon(R.drawable.ic_clear)
                .withIdentifier(CLEAR)
                .withSelectable(false);
    }

    private SectionDrawerItem setSectionItem() {
        return new SectionDrawerItem()
                .withName(R.string.nav_other)
                .withDivider(true)
                .withSelectable(false)
                .withIdentifier(-1);
    }

    private PrimaryDrawerItem setExitItem() {
        return new PrimaryDrawerItem()
                .withName(R.string.nav_exit)
                .withIcon(R.drawable.ic_exit)
                .withIdentifier(EXIT)
                .withSelectable(false);
    }

    private SwitchDrawerItem setSwitchDayNight() {
        return new SwitchDrawerItem()
                .withName(R.string.nav_day_night)
                .withIcon(R.drawable.ic_day_night)
                .withIdentifier(DAY_NIGHT)
                .withCheckable(!mSPUtil.getBoolean("day"))
                .withOnCheckedChangeListener(checkedChangeListener);
    }

    // 开关item的状态监听
    private final OnCheckedChangeListener checkedChangeListener = (drawerItem, buttonView, isChecked) -> {
        if (drawerItem instanceof Nameable) {
            switch ((int) drawerItem.getIdentifier()) {
                case DAY_NIGHT:
                    drawer.closeDrawer();
                    setDayNightMode(true);
                    recreate();
                    break;
            }
        }
    };

    public void clearApp() {
        final CacheUtils cacheUtils = CacheUtils.getInstance();
        new MaterialDialog.Builder(this)
                .title(R.string.nav_clear)
                .iconRes(R.drawable.ic_alert)
                .content("共 " + cacheUtils.getCacheSize() + " ，确定清理吗？")
                .negativeText(R.string.all_cancel)
                .positiveText(R.string.all_ensure)
                .onPositive((dialog, which) -> {
                    cacheUtils.clear();
                    // 点击“确认”后的操作
                    ToastUtils.showShort("缓存已清理");
                    // 重启APP
                    final Intent intent = getApplicationContext().getPackageManager()
                            .getLaunchIntentForPackage(getApplicationContext().getPackageName());
                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).show();
    }

    private void showCurrentFragment(final int index) {
        FragmentUtils.showHide(currentFragmentIndex = index, mFragments);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, final PersistableBundle outPersistentState) {
        // 保存抽屉状态
        outState = drawer.saveInstanceState(outState);
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("currentFragmentIndex", currentFragmentIndex);
    }

    /**
     * 双击退出
     */
    @Override
    public void onBackPressed() {
        // 如果抽屉是打开状态，点击返回键 -> 关闭抽屉
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
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
        }
    }
}