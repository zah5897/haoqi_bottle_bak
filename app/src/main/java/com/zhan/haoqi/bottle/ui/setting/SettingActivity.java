package com.zhan.haoqi.bottle.ui.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.data.UserManager;
import com.zhan.haoqi.bottle.sms.RegisterPage;
import com.zhan.haoqi.bottle.ui.user.ResetPasswordActiviy;
import com.zhan.haoqi.bottle.util.AppUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by zah on 2016/11/30.
 */

public class SettingActivity extends Activity {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.sys_msg)
    TextView sysMsg;
    @BindView(R.id.version)
    TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        title.setText("设置");
        String versionName = AppUtils.getVersionName();
        version.setText("检查更新" + (TextUtils.isEmpty(versionName) ? "" : "(" + versionName + ")"));
        if (!UserManager.getInstance().isLogin()) {
            findViewById(R.id.logout).setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.back, R.id.pwd_layout, R.id.notify_sound, R.id.vibrary, R.id.sys_msg, R.id.version, R.id.logout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.pwd_layout:


                // 打开注册页面
                RegisterPage forgotPage = new RegisterPage(false);
                forgotPage.setRegisterCallback(new EventHandler() {
                    public void afterEvent(int event, int result, Object data) {
                        // 解析注册结果
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                            String phone = (String) phoneMap.get("phone");
                            Intent intent = new Intent(getBaseContext(), ResetPasswordActiviy.class);
                            intent.putExtra("phone", phone);
                            intent.putExtra("modify", true);
                            startActivity(intent);
                        }
                    }
                });
                forgotPage.show(this);
                break;
            case R.id.notify_sound:
                break;
            case R.id.vibrary:
                break;
            case R.id.sys_msg:
                break;
            case R.id.version:
                break;
            case R.id.logout:
                UserManager.getInstance().logout();
                finish();
                break;
        }
    }
}
