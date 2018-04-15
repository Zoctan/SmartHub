package com.zoctan.smarthub.ui.tab;

import com.flyco.tablayout.listener.CustomTabEntity;

public class TabEntity implements CustomTabEntity {
    private final String title;
    private final int selectedIcon;
    private final int unSelectedIcon;

    public TabEntity(final String title, final int selectedIcon, final int unSelectedIcon) {
        this.title = title;
        this.selectedIcon = selectedIcon;
        this.unSelectedIcon = unSelectedIcon;
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public int getTabSelectedIcon() {
        return selectedIcon;
    }

    @Override
    public int getTabUnselectedIcon() {
        return unSelectedIcon;
    }
}