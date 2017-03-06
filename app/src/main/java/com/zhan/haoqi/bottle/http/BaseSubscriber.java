package com.zhan.haoqi.bottle.http;

import android.app.ProgressDialog;
import android.content.Intent;

import com.zhan.haoqi.bottle.MyApplication;
import com.zhan.haoqi.bottle.util.NetworkUtil;
import com.zhan.haoqi.bottle.util.To;
import com.zhan.haoqi.bottle.view.HQNetworkErrorDialog;

import rx.Subscriber;

public abstract class BaseSubscriber<T> extends Subscriber<T> {
    @Override
    public void onStart() {
        super.onStart();
        if (!NetworkUtil.isConnect(MyApplication.getApp())) {
            Intent intent = new Intent(MyApplication.getApp(), HQNetworkErrorDialog.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.getApp().startActivity(intent);
            onError(new HttpError(HttpError.ERROR_NETWORK, "当前网络不可用"));
            if (!isUnsubscribed()) {
                unsubscribe();
            }
            return;
        }
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof HttpError) {
            HttpError he = (HttpError) e;
            switch (he.getCode()) {
                case HttpError.ERROR_HTTP:
                    To.show("网络错误");
                    break;
                case HttpError.ERROR_NOT_CONFIRM:
                    To.show("盗版应用，请下载正版");
                    break;
            }
        }
    }
}