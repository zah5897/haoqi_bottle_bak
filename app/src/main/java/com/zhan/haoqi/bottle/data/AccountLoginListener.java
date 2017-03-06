package com.zhan.haoqi.bottle.data;

/**
 * Created by zah on 2016/11/28.
 */

public interface AccountLoginListener {
    void onLogin(User user);

    void onFailure(int errorCode, String msg);
}
