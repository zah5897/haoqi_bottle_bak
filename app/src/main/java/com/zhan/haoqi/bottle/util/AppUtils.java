package com.zhan.haoqi.bottle.util;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.zhan.haoqi.bottle.MyApplication;
import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.adapter.DialogAdapter;

import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by zah on 2016/11/7.
 */
public class AppUtils {

    private static String ID;

    public static String getUniqueID() {
        if (ID == null) {
            ID = getUniquePsuedoID();
        }
        return ID;
    }

    public static void hideSoftInputIsShow(Context context, EditText editText) {
        if ((context == null) || (editText == null)) {
            return;
        }
        InputMethodManager input_method = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (input_method.isActive()) {
            input_method.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public static void showSoftInputIsShow(Context context, EditText editText) {
        if ((context == null) || (editText == null)) {
            return;
        }
        InputMethodManager input_method = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (input_method.isActive()) {
            input_method.showSoftInput(editText, 0);
        }
    }

    public static Dialog getDialogListActionSheet(Context context, ArrayList<String> paramArrayList, final AdapterView.OnItemClickListener paramOnItemClickListener) {
        View layout = View.inflate(context, R.layout.dialog_list_selected_img, null);
        final Dialog dialog = getActionSheet(context, layout);
        ListView listView = (ListView) layout.findViewById(R.id.action_sheet_list_lv);
        listView.setAdapter(new DialogAdapter(context, paramArrayList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                paramOnItemClickListener.onItemClick(adapterView, view, i, l);
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public static Dialog getActionSheet(Context context, View contentView) {
        final Dialog localDialog = new Dialog(context, R.style.action_sheet);
        localDialog.setContentView(contentView);
        localDialog.setCanceledOnTouchOutside(true);
        View cancel = contentView.findViewById(R.id.action_sheet_cancel_bt);
        if (cancel != null) {
            cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View paramAnonymousView) {
                    localDialog.dismiss();
                }
            });
        }
        Window paramView = localDialog.getWindow();
        paramView.getAttributes().width = context.getResources().getDisplayMetrics().widthPixels;
        paramView.setGravity(80);
        paramView.setWindowAnimations(R.style.action_sheet_animation);
        return localDialog;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    private static String getUniquePsuedoID() {
        String serial = null;

        String m_szDevIDShort = "35" + Build.BOARD.length() % 10
                + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; // 13 位

        try {
            serial = android.os.Build.class.getField("SERIAL").get(null)
                    .toString();
            // API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode())
                    .toString();
        } catch (Exception exception) {
            // serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        // 使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode())
                .toString();
    }


    public static void requirePermision(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            int readPhone = activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            int receiveSms = activity.checkSelfPermission(Manifest.permission.RECEIVE_SMS);
            int readSms = activity.checkSelfPermission(Manifest.permission.READ_SMS);
            int readContacts = activity.checkSelfPermission(Manifest.permission.READ_CONTACTS);
            int readSdcard = activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            int requestCode = 0;
            ArrayList<String> permissions = new ArrayList<String>();
            if (readPhone != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 0;
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (receiveSms != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 1;
                permissions.add(Manifest.permission.RECEIVE_SMS);
            }
            if (readSms != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 2;
                permissions.add(Manifest.permission.READ_SMS);
            }
            if (readContacts != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 3;
                permissions.add(Manifest.permission.READ_CONTACTS);
            }
            if (readSdcard != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 4;
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (requestCode > 0) {
                String[] permission = new String[permissions.size()];
                activity.requestPermissions(permissions.toArray(permission), requestCode);
                return;
            }
        }
    }


    public static String getVersionName() {
        PackageManager pm = MyApplication.getApp().getPackageManager();//context为当前Activity上下文
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(MyApplication.getApp().getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getVersionCode() {
        PackageManager pm = MyApplication.getApp().getPackageManager();//context为当前Activity上下文
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(MyApplication.getApp().getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
