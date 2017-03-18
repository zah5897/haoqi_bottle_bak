package com.zhan.haoqi.bottle.data;

/**
 * Created by zah on 2017/3/10.
 */

public interface Callback {
    void onSuccess(int code,Object obj);
    void onError(int code,Object obj);
}
