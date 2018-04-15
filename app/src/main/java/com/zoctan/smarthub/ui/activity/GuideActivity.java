package com.zoctan.smarthub.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.SPUtils;
import com.zoctan.smarthub.R;
import com.zoctan.smarthub.ui.adapter.GuideViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 欢迎页
 */
public class GuideActivity extends Activity implements OnClickListener {

    @BindView(R.id.ViewPager_guide_pic)
    ViewPager mViewPagerGuidePic;
    @BindView(R.id.LinearLayout_guide_dot)
    LinearLayout mLayoutGuideDot;
    // 引导页图片资源
    private static final int[] pics = {R.layout.guid_view1,
            R.layout.guid_view2,
            R.layout.guid_view3};
    // 底部小点图片
    private static final ImageView[] dots = new ImageView[pics.length];
    // 记录当前选中位置
    private int currentIndex;
    private Unbinder unbinder;
    private final SPUtils mSPUtil = SPUtils.getInstance();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        unbinder = ButterKnife.bind(this);
        initView();
    }

    protected void initView() {
        mSPUtil.put("first_open", true);
        final List<View> views = new ArrayList<>();
        // 初始化引导页视图列表
        for (int i = 0; i < pics.length; i++) {
            final View view = LayoutInflater.from(this).inflate(pics[i], null);

            if (i == pics.length - 1) {
                final Button button = view.findViewById(R.id.Button_guide_enter);
                button.setTag("start");
                button.setOnClickListener(this);
            }

            views.add(view);
        }
        final GuideViewPagerAdapter adapter = new GuideViewPagerAdapter(views);
        mViewPagerGuidePic.setAdapter(adapter);
        mViewPagerGuidePic.addOnPageChangeListener(new PageChangeListener());
        initDots();
    }

    private void initDots() {
        // 循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            // 得到一个LinearLayout下面的每一个子元素
            dots[i] = (ImageView) mLayoutGuideDot.getChildAt(i);
            // 都设为灰色
            dots[i].setEnabled(false);
            dots[i].setOnClickListener(this);
            // 设置位置tag，方便取出与当前位置对应
            dots[i].setTag(i);
        }

        currentIndex = 0;
        // 设置primary color，即选中状态
        dots[currentIndex].setEnabled(true);
    }

    /**
     * 设置当前view
     *
     * @param position 当前位置
     */
    private void setCurView(final int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        mViewPagerGuidePic.setCurrentItem(position);
    }

    /**
     * 设置当前指示点
     *
     * @param position 当前位置
     */
    private void setCurDot(final int position) {
        if (position < 0 || position > pics.length || currentIndex == position) {
            return;
        }
        dots[position].setEnabled(true);
        dots[currentIndex].setEnabled(false);
        currentIndex = position;
    }

    @Override
    public void onClick(final View view) {
        if (view.getTag().equals("start")) {
            enterMainActivity();
            return;
        }

        final int position = (Integer) view.getTag();
        setCurView(position);
        setCurDot(position);
    }

    // 进入主界面
    private void enterMainActivity() {
        final Intent intent = new Intent(GuideActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    private class PageChangeListener implements OnPageChangeListener {

        /**
         * 当滑动状态改变时调用
         */
        @Override
        public void onPageScrollStateChanged(final int position) {
        }

        /**
         * 当前页面被滑动时调用
         *
         * @param position 当前位置
         * @param arg1     当前页面偏移的百分比
         * @param arg2     当前页面偏移的像素位置
         */
        @Override
        public void onPageScrolled(final int position, final float arg1, final int arg2) {
        }

        /**
         * 当新的页面被选中时调用
         */
        @Override
        public void onPageSelected(final int position) {
            // 设置底部小点选中状态
            setCurDot(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}