package com.zhan.haoqi.bottle.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.zhan.haoqi.bottle.view.BottlePoolLayout;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by zah on 2016/12/9.
 */

public class TimerManager {

    public static void setBottleRefreshTimer(Context context) {
        Intent intent = new Intent();
        intent.setAction(BottlePoolLayout.BOTTLE_REFRESH);
        PendingIntent sender = PendingIntent
                .getBroadcast(context, 0, intent, 0);

        //开始时间
        long firstime = SystemClock.elapsedRealtime();

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        //5秒一个周期，不停的发送广播
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP
                , firstime, 5 * 1000, sender);
    }
}
