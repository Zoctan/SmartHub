package com.zoctan.smarthub.hub.view;

import com.zoctan.smarthub.beans.HubBean;

import java.util.List;

/**
 * 评测列表视图接口
 */
public interface HubListView {
    // 加载成功后，将加载得到的数据填充到RecyclerView展示给用户
    void addHub(List<HubBean> hubList);

    void showLoading();

    void hideLoading();

    // 若加载数据失败，如无网络连接，则需要给用户提示信息
    void showLoadingFailedMsg();
}
