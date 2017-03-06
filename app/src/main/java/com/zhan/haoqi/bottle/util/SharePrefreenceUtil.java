package com.zhan.haoqi.bottle.util;

import android.content.Context;

import com.zhan.haoqi.bottle.MyApplication;

/**
 * Created by zah on 2016/10/20.
 */
public class SharePrefreenceUtil {

    private static final String PREFREENCE_NAME = "haoqi_bottle_cfg";

    public static void saveBoolean(String key, boolean value) {
        MyApplication.application.getSharedPreferences(PREFREENCE_NAME, Context.MODE_PRIVATE).edit().putBoolean(key, value).commit();
    }

    public static void saveString(String key, String value) {
        MyApplication.application.getSharedPreferences(PREFREENCE_NAME, Context.MODE_PRIVATE).edit().putString(key, value).commit();
    }

    public static boolean getBoolean(String name) {
        return MyApplication.application.getSharedPreferences(PREFREENCE_NAME, Context.MODE_PRIVATE).getBoolean(name, false);
    }

    public static String getString(String name) {
        return MyApplication.application.getSharedPreferences(PREFREENCE_NAME, Context.MODE_PRIVATE).getString(name, null);
    }

}
