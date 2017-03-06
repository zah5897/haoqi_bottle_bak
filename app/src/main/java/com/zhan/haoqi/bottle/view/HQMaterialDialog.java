package com.zhan.haoqi.bottle.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.ui.listener.DialogCallBack;

/**
 * Created by zah on 2016/11/29.
 */

public class HQMaterialDialog extends AlertDialog {
    public HQMaterialDialog(Context context) {
        super(context);
    }

    private DialogCallBack callback;

    public void setCallback(DialogCallBack callback) {
        this.callback = callback;
    }

    @Override
    public void setTitle(CharSequence title) {
        ((TextView) findViewById(R.id.title)).setText(title);
    }

    @Override
    public void setMessage(CharSequence message) {
        ((TextView) findViewById(R.id.msg)).setText(message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_material_layout);
        if (callback != null) {
            findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callback != null) {
                        callback.cancel();
                    }
                    dismiss();
                }
            });
            findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callback != null) {
                        callback.confirm();
                    }
                    dismiss();
                }
            });
        } else {
            findViewById(R.id.cancel).setVisibility(View.GONE);
            findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callback != null) {
                        callback.confirm();
                    }
                    dismiss();
                }
            });
        }


    }
}
