package com.zhan.haoqi.bottle;

import android.app.Application;
import android.view.inputmethod.InputMethodManager;

import com.zhan.haoqi.bottle.data.UserManager;
import com.zhan.haoqi.bottle.err.CrashApplication;
import com.zhan.haoqi.bottle.sms.SMSHelper;

/**
 * Created by zah on 2016/10/20.
 */
public class MyApplication extends Application {

    public static MyApplication application;

    public static MyApplication getApp() {
        return application;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        CrashApplication.getInstance(application).onCreate();
        //初始化
        UserManager.getInstance();
        SMSHelper.initSharedSms(application);

    }

}
