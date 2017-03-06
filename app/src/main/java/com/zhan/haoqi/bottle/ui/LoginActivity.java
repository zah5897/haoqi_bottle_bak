package com.zhan.haoqi.bottle.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tencent.connect.common.Constants;
import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.data.AccountLoginListener;
import com.zhan.haoqi.bottle.data.User;
import com.zhan.haoqi.bottle.data.UserManager;
import com.zhan.haoqi.bottle.http.BaseSubscriber;
import com.zhan.haoqi.bottle.http.HttpHelper;
import com.zhan.haoqi.bottle.http.RequestParam;
import com.zhan.haoqi.bottle.sms.RegisterPage;
import com.zhan.haoqi.bottle.ui.user.RegistActivity;
import com.zhan.haoqi.bottle.util.AppUtils;
import com.zhan.haoqi.bottle.util.To;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.RequestBody;


/**
 * Created by zah on 2016/11/24.
 */

public class LoginActivity extends Activity {

    private final int REGIST_REQUEST = 1;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.action_bar_middle_text)
    TextView actionBarMiddleText;
    @BindView(R.id.to_regist_page)
    TextView toRegistPage;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        findViewById(R.id.action_bar).setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        actionBarMiddleText.setText(getString(R.string.login));
        toRegistPage.setText(Html.fromHtml(getString(R.string.to_regist_tip)));
    }

    @OnClick({R.id.back, R.id.login, R.id.qq_login, R.id.to_regist_page, R.id.forgot_pwd})
    public void onClick(View view) {
        closeSoftInput();
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.login:
                login();
                break;
            case R.id.qq_login:
                loginPopup = To.showPop(LoginActivity.this, R.id.qq_login, "QQ登录中...");
                UserManager.getInstance().qqLogin(this, new AccountLoginListener() {
                    @Override
                    public void onLogin(User account) {
                        To.show("登录成功");
                        finish();
                        To.dismiss(loginPopup);
                    }

                    @Override
                    public void onFailure(int errorCode, String msg) {
                        To.dismiss(loginPopup);
                        To.show("登录失败");
                    }
                });
                break;
            case R.id.to_regist_page:
// 打开注册页面
                RegisterPage registerPage = new RegisterPage();
                registerPage.setRegisterCallback(new EventHandler() {
                    public void afterEvent(int event, int result, Object data) {
                        // 解析注册结果
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                            String phone = (String) phoneMap.get("phone");
                            Intent intent = new Intent(getBaseContext(), RegistActivity.class);
                            intent.putExtra("phone", phone);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                registerPage.show(this);
                // startActivityForResult(new Intent(this, RegistActivity.class), REGIST_REQUEST);
                break;
            case R.id.forgot_pwd:
                startActivity(new Intent(this, RegistActivity.class));
                break;
        }
    }


    private void closeSoftInput() {
        AppUtils.hideSoftInputIsShow(this, username);
        AppUtils.hideSoftInputIsShow(this, password);
    }


    private PopupWindow loginPopup;

    private void login() {
        String mobile = username.getText().toString();
        String passwordStr = password.getText().toString();

        if (TextUtils.isEmpty(mobile)) {
            To.show("账号不能为空");
            return;
        }
        if (TextUtils.isEmpty(passwordStr)) {
            To.show("密码不能为空");
            return;
        }
        if (passwordStr.length() < 6) {
            To.show("密码太短");
            return;
        }

        RequestParam param = new RequestParam();
        param.put("mobile", mobile);
        param.put("password", passwordStr);

        HttpHelper.post(UserManager.USER_LOGIN, param).subscribe(new BaseSubscriber<JSONObject>() {
            @Override
            public void onCompleted() {
                if (UserManager.getInstance().isLogin()) {
                    To.show("登录成功");
                    finish();
                }
                To.dismiss(loginPopup);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                To.dismiss(loginPopup);
            }

            @Override
            public void onNext(JSONObject jsonObject) {
                UserManager.getInstance().praseAndSave(jsonObject);
                onCompleted();
            }

            @Override
            public void onStart() {
                super.onStart();
                loginPopup = To.showPop(LoginActivity.this, R.id.login, "正在登录中..");
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGIST_REQUEST && resultCode == RESULT_OK) {
            finish();
        } else if (requestCode == Constants.REQUEST_LOGIN && resultCode == RESULT_OK) {
            UserManager.getInstance().handleResultData(this, data);
        }
    }
}
