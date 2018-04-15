package com.zoctan.smarthub.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class GuideViewPagerAdapter extends PagerAdapter {
    private final List<View> views;

    public GuideViewPagerAdapter(final List<View> views) {
        super();
        this.views = views;
    }

    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    @Override
    public void destroyItem(@NonNull final ViewGroup container, final int position, @NonNull final Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public boolean isViewFromObject(@NonNull final View view, @NonNull final Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        container.addView(views.get(position), 0);
        return views.get(position);
    }
}
