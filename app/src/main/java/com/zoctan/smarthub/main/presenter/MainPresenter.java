package com.zoctan.smarthub.main.presenter;

import com.zoctan.smarthub.R;
import com.zoctan.smarthub.main.view.MainView;

/**
 * 主界面侧滑栏回调类
 */
public class MainPresenter {

    private MainView mMainView;

    public MainPresenter(MainView mainView) {
        this.mMainView = mainView;
    }

    // 侧滑栏选择
    public void switchNavigation(int id) {
        switch (id) {
            case R.id.navHub:
                mMainView.switch2Hub();
                break;
            case R.id.navUser:
                mMainView.switch2User();
                break;
            case R.id.navAbout:
                mMainView.switch2About();
                break;
            case R.id.navDayNight:
                mMainView.switch2DayNight();
                break;
            case R.id.navClear:
                mMainView.switch2Clear();
                break;
            default:
                mMainView.switch2Hub();
                break;
        }
    }
}
