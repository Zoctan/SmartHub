package com.zoctan.smarthub.utils.QiNiu;

public interface GetTokenListener {
    void onSuccess(String qiNiuToken);

    void onFailure(String msg);
}
