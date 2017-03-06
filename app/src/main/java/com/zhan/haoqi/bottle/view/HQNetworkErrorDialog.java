package com.zhan.haoqi.bottle.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.ui.listener.DialogCallBack;

/**
 * Created by zah on 2016/11/29.
 */

public class HQNetworkErrorDialog extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_material_layout);
        ((TextView) findViewById(R.id.msg)).setText("当前网络不可用");
        findViewById(R.id.cancel).setVisibility(View.GONE);
        findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
