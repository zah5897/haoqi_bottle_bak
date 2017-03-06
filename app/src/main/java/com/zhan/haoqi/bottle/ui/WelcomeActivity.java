package com.zhan.haoqi.bottle.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.util.SharePrefreenceUtil;

/**
 * Created by zah on 2016/10/20.
 */
public class WelcomeActivity extends Activity {
    private static  final String HAS_OPENED="has_opened";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean hasOpened= SharePrefreenceUtil.getBoolean(HAS_OPENED);
        hasOpened=true;
        if(hasOpened){
            showWelcomeView();
        }else{
            showGuideView();
        }
    }
    private void showGuideView(){

    }
    private void showWelcomeView(){
       setContentView(R.layout.activity_welcome);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                  startActivity(new Intent(getBaseContext(),MainActivity.class));
                  finish();
            }
        },1000);
    }
}
