package com.zhan.haoqi.bottle.util;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.zhan.haoqi.bottle.MyApplication;
import com.zhan.haoqi.bottle.R;

public class To {

    public static void show(String msg) {
        show(msg, Toast.LENGTH_SHORT);
    }

    private static void show(String msg, int duration) {
        Toast toast = Toast.makeText(MyApplication.getApp(), msg, duration);
        toast.show();
    }


    public static PopupWindow showPop(Activity activity, int parentID, String msg) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.view_alert_window_spot_layout, null);
        ((TextView) view.findViewById(R.id.message)).setText(msg);
        //创建PopupWindow，参数为显示对象，宽，高
        PopupWindow pop = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //PopupWindow的设置
        pop.setBackgroundDrawable(new BitmapDrawable());
        //点击外边消失
        pop.setOutsideTouchable(false);
        //设置此参数获得焦点，否则无法点击
        pop.setFocusable(true);
        pop.setAnimationStyle(R.anim.popup_animation);
        pop.showAtLocation(activity.findViewById(parentID), Gravity.CENTER, 0, 0);
        return pop;
    }

    public static PopupWindow showPop(Context context, View parent, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_alert_window_spot_layout, null);
        ((TextView) view.findViewById(R.id.message)).setText(msg);
        //创建PopupWindow，参数为显示对象，宽，高
        PopupWindow pop = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //PopupWindow的设置
        pop.setBackgroundDrawable(new BitmapDrawable());
        //点击外边消失
        pop.setOutsideTouchable(true);
        //设置此参数获得焦点，否则无法点击
        pop.setFocusable(true);
        pop.setAnimationStyle(R.anim.popup_animation);
        pop.showAtLocation(parent, Gravity.CENTER, 0, 0);
        return pop;
    }

    public static void dismiss(PopupWindow popupWindow) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
}