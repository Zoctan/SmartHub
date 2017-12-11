package com.zoctan.smarthub.main.widget;

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

import com.zoctan.smarthub.R;
import com.zoctan.smarthub.main.GuideViewPagerAdapter;
import com.zoctan.smarthub.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 欢迎页
 */
public class WelcomeGuideActivity extends Activity implements OnClickListener {

    private ViewPager mViewPager;
    private SPUtils mSPUtils;
    // 引导页图片资源
    private static final int[] pics = {R.layout.guid_view1, R.layout.guid_view2, R.layout.guid_view3, R.layout.guid_view4};
    // 底部小点图片
    private ImageView[] dots;
    // 记录当前选中位置
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        mSPUtils = new SPUtils(this);

        List<View> views = new ArrayList<>();

        // 初始化引导页视图列表
        for (int i = 0; i < pics.length; i++) {
            View view = LayoutInflater.from(this).inflate(pics[i], null);

            if (i == pics.length - 1) {
                Button mBtnStart = view.findViewById(R.id.mBtnStart);
                mBtnStart.setTag("start");
                mBtnStart.setOnClickListener(this);
            }

            views.add(view);
        }

        mViewPager = findViewById(R.id.mVpGuide);
        // 初始化adapter
        GuideViewPagerAdapter adapter = new GuideViewPagerAdapter(views);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new PageChangeListener());

        initDots();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 如果切换到后台，就设置下次不进入功能引导页
        mSPUtils.putBoolean("isFirstOpen", true);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initDots() {
        LinearLayout ll = findViewById(R.id.ll);
        dots = new ImageView[pics.length];

        // 循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            // 得到一个LinearLayout下面的每一个子元素
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(false);// 都设为灰色
            dots[i].setOnClickListener(this);
            dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(true); // 设置为白色，即选中状态
    }

    /**
     * 设置当前view
     *
     * @param position 当前位置
     */
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        mViewPager.setCurrentItem(position);
    }

    /**
     * 设置当前指示点
     *
     * @param position 当前位置
     */
    private void setCurDot(int position) {
        if (position < 0 || position > pics.length || currentIndex == position) {
            return;
        }
        dots[position].setEnabled(true);
        dots[currentIndex].setEnabled(false);
        currentIndex = position;
    }

    @Override
    public void onClick(View view) {
        if (view.getTag().equals("start")) {
            enterMainActivity();
            return;
        }

        int position = (Integer) view.getTag();
        setCurView(position);
        setCurDot(position);
    }

    // 进入主界面
    private void enterMainActivity() {
        Intent intent = new Intent(WelcomeGuideActivity.this, SplashActivity.class);
        startActivity(intent);
        mSPUtils.putBoolean("isFirstOpen", true);
        finish();
    }

    private class PageChangeListener implements OnPageChangeListener {
        // 当滑动状态改变时调用
        @Override
        public void onPageScrollStateChanged(int position) {
            // arg0:1正在滑动
            // arg0:2滑动完毕了
            // arg0:0什么都没做。
        }

        // 当前页面被滑动时调用
        @Override
        public void onPageScrolled(int position, float arg1, int arg2) {
            // arg0:当前页面，及你点击滑动的页面
            // arg1:当前页面偏移的百分比
            // arg2:当前页面偏移的像素位置
        }

        // 当新的页面被选中时调用
        @Override
        public void onPageSelected(int position) {
            // 设置底部小点选中状态
            setCurDot(position);
        }
    }
}
