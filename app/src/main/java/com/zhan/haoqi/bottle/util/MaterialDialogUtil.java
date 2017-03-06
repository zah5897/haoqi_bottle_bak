package com.zhan.haoqi.bottle.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.ui.listener.DialogCallBack;
import com.zhan.haoqi.bottle.view.HQMaterialDialog;

/**
 * Created by zah on 2016/11/28.
 */

public class MaterialDialogUtil {

    public static void showDialog(Context context, String[] content, DialogCallBack callBack) {
        HQMaterialDialog dialog = new HQMaterialDialog(context);
        dialog.setCallback(callBack);
        dialog.show();
        dialog.setTitle(content[0]);
        dialog.setMessage(content[1]);
    }

    public static void showDialogTip(Context context, String msg) {
        HQMaterialDialog dialog = new HQMaterialDialog(context);
        dialog.show();
        dialog.setMessage(msg);
    }
}
