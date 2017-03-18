package com.zhan.haoqi.bottle.ui.user;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.zhan.haoqi.bottle.R;
import com.zhan.haoqi.bottle.data.Callback;
import com.zhan.haoqi.bottle.data.UserManager;
import com.zhan.haoqi.bottle.http.BaseSubscriber;
import com.zhan.haoqi.bottle.util.To;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zah on 2016/12/7.
 */

public class ResetPasswordActiviy extends Activity {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.old_password)
    EditText oldPassword;
    @BindView(R.id.old_password_layout)
    LinearLayout oldPasswordLayout;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.password_again)
    EditText passwordAgain;
    @BindView(R.id.next)
    Button btn;

    boolean is_modify_pwd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        is_modify_pwd = getIntent().getBooleanExtra("modify", false);
        String titleStr;
        if (is_modify_pwd) {
            oldPasswordLayout.setVisibility(View.VISIBLE);
            titleStr = "修改密码";
        } else {
            oldPasswordLayout.setVisibility(View.GONE);
            titleStr = "重置密码";
        }

        btn.setText(titleStr);
        title.setText(titleStr);
    }


    private void submit() {
        String oldPwd = oldPassword.getText().toString();
        String newPwd = password.getText().toString();
        String newPwdAgain = passwordAgain.getText().toString();

        if (is_modify_pwd) {
            if (TextUtils.isEmpty(oldPwd)) {
                To.show("当前密码不能为空");
                return;
            }
        }else{
            oldPwd="";
        }
        if (TextUtils.isEmpty(newPwd)) {
            To.show("新密码不能为空");
            return;
        }
        if (!newPwd.equals(newPwdAgain)) {
            To.show("两次新密码不一致");
            return;
        }
        BaseSubscriber baseSubscriber = new BaseSubscriber<JSONObject>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(JSONObject jsonObject) {
                UserManager.getInstance().praseAndSave(jsonObject);
                if (is_modify_pwd) {
                    To.show("修改成功");
                } else {
                    To.show("重置并登陆成功!");
                }
                finish();
            }
        };
        UserManager.getInstance().resetPwd(getIntent().getStringExtra("phone"),oldPwd, newPwd, baseSubscriber);
    }

    @OnClick({R.id.back, R.id.next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.next:
                submit();
                break;
        }
    }
}
