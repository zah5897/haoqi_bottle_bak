package com.zhan.haoqi.bottle.sms;

import android.content.Context;

import cn.smssdk.SMSSDK;

/**
 * Created by zah on 2017/3/3.
 */

public class SMSHelper {

    public static final String APPKEY = "1963a3631b493";
    public static final String APPSECRET = "924dd1b35291402b1f242718eca281c3";

    public static void initSharedSms(Context application) {
        SMSSDK.initSDK(application, APPKEY, APPSECRET);
    }
}
