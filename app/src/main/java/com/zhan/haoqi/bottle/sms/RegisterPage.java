/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2014年 mob.com. All rights reserved.
 */
package com.zhan.haoqi.bottle.sms;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.tools.FakeActivity;
import com.zhan.haoqi.bottle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.UserInterruptException;
import cn.smssdk.utils.SMSLog;

import static com.mob.tools.utils.R.getStringRes;

/**
 * 短信注册页面
 */
public class RegisterPage extends FakeActivity implements OnClickListener,
        TextWatcher {

    // 默认使用中国区号
    private EventHandler callback;
    // 手机号码
    private EditText inputPhone;
    private EditText validateCodeInput;
    // 国家编号
    // clear 号码
    private Button btnNext;
    private Button btnGetCode;
    private EventHandler handler;
    private Dialog pd;
    private OnSendMessageHandler osmHandler;

    public void setRegisterCallback(EventHandler callback) {
        this.callback = callback;
    }

    public void setOnSendMessageHandler(OnSendMessageHandler h) {
        osmHandler = h;
    }

    public void show(Context context) {
        super.show(context, null);
    }

    private static final int RETRY_INTERVAL = 60;
    private int time = RETRY_INTERVAL;
    boolean forgotPwd;
    public RegisterPage(boolean forgotPwd) {
      this.forgotPwd=forgotPwd;
    }

    private final String COUNTRY_CODE = "86";

    public void onCreate() {
        activity.setContentView(R.layout.sms_regist_page);
        inputPhone = (EditText) activity.findViewById(R.id.input_phone);
        validateCodeInput = (EditText) activity.findViewById(R.id.validate_code);
        TextView tv = (TextView) activity.findViewById(R.id.title);
        if(forgotPwd){
            tv.setText("找回密码");
        }else{
            tv.setText("手机号码注册");
        }

        btnNext = (Button) activity.findViewById(R.id.next);
        inputPhone.setText("");
        inputPhone.addTextChangedListener(this);
        inputPhone.requestFocus();
        activity.findViewById(R.id.clear_phone).setOnClickListener(this);
        activity.findViewById(R.id.back).setOnClickListener(this);
        btnGetCode = (Button) findViewById(R.id.get_sms);
        btnGetCode.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        handler = new EventHandler() {
            public void afterEvent(final int event, final int result,
                                   final Object data) {
                runOnUIThread(new Runnable() {
                    public void run() {
                        if (pd != null && pd.isShowing()) {
                            pd.dismiss();
                        }
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                // 请求验证码后，跳转到验证码填写页面
                                boolean smart = (Boolean) data;
                                afterSubmit(smart, result, data);
                            } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                                /** 提交验证码 */
                                afterSubmit(false, result, data);
                            }
                        } else {
                            //提前设置結束
                            if (time < RETRY_INTERVAL && time > 1) {
                                time = 0;
                            }
                            if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE
                                    && data != null
                                    && (data instanceof UserInterruptException)) {
                                // 由于此处是开发者自己决定要中断发送的，因此什么都不用做
                                return;
                            }

                            int status = 0;
                            // 根据服务器返回的网络错误，给toast提示
                            try {
                                ((Throwable) data).printStackTrace();
                                Throwable throwable = (Throwable) data;

                                JSONObject object = new JSONObject(
                                        throwable.getMessage());
                                String des = object.optString("detail");
                                status = object.optInt("status");
                                if (!TextUtils.isEmpty(des)) {
                                    Toast.makeText(activity, des, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (Exception e) {
                                SMSLog.getInstance().w(e);
                            }
                            // 如果木有找到资源，默认提示
                            int resId = 0;
                            if (status >= 400) {
                                resId = getStringRes(activity,
                                        "smssdk_error_desc_" + status);
                            } else {
                                resId = getStringRes(activity,
                                        "smssdk_network_error");
                            }

                            if (resId > 0) {
                                Toast.makeText(activity, resId, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        };
        SMSHelper.initSharedSms(getContext());
        validateCodeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btnNext.setEnabled(true);
                    activity.findViewById(R.id.clear_phone).setVisibility(View.VISIBLE);
                } else {
                    btnNext.setEnabled(false);
                    activity.findViewById(R.id.clear_phone).setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void onResume() {
        SMSSDK.registerEventHandler(handler);
    }

    public void onDestroy() {
        SMSSDK.unregisterEventHandler(handler);
    }

    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            btnGetCode.setEnabled(true);
        } else {
            btnGetCode.setEnabled(false);
        }
    }

    public void afterTextChanged(Editable s) {

    }

    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.back) {
            finish();
        } else if (id == R.id.get_sms) {
            hideSoftInputIsShow(getContext(), inputPhone);
            hideSoftInputIsShow(getContext(), validateCodeInput);
            // 请求发送短信验证码
            String phone = inputPhone.getText().toString().trim().replaceAll("\\s*", "");
            countDown();
            SMSSDK.getVerificationCode(COUNTRY_CODE, phone.trim(), osmHandler);
        } else if (id == R.id.next) {
            hideSoftInputIsShow(getContext(), inputPhone);
            hideSoftInputIsShow(getContext(), validateCodeInput);
            String phone = inputPhone.getText().toString().trim().replaceAll("\\s*", "");
            validateSMS(phone);
        } else if (id == R.id.clear_phone) {
            // 清除电话号码输入框
            inputPhone.getText().clear();
        }
    }


    /**
     * 倒数计时
     */
    private void countDown() {
        runOnUIThread(new Runnable() {
            public void run() {
                time--;
                if (time <= 0) {
                    btnGetCode.setEnabled(true);
                    btnGetCode.setText("获取验证码");
                    time = RETRY_INTERVAL;
                } else {
                    String unReceive = getContext().getString(R.string.time_count_down, time);
                    btnGetCode.setText(Html.fromHtml(unReceive));
                    btnGetCode.setEnabled(false);
                    runOnUIThread(this, 1000);
                }
            }
        }, 1000);
    }


    private void validateSMS(String phone) {
        // 提交验证码
        String verificationCode = validateCodeInput.getText().toString().trim();
        if (!TextUtils.isEmpty(verificationCode)) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            pd = progressDialog(getContext());
            if (pd != null) {
                pd.show();
            }
            SMSSDK.submitVerificationCode(COUNTRY_CODE, phone, verificationCode);
        } else {
            int resId = getStringRes(activity, "smssdk_write_identify_code");
            if (resId > 0) {
                Toast.makeText(getContext(), "请输入验证码", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 提交验证码成功后的执行事件
     *
     * @param result
     * @param data
     */
    private void afterSubmit(final boolean smart, final int result, final Object data) {
        runOnUIThread(new Runnable() {
                          public void run() {
                              if (pd != null && pd.isShowing()) {
                                  pd.dismiss();
                              }
                              Object phoneMap = null;
                              if (smart) {
                                  String phone = inputPhone.getText().toString().trim().replaceAll("\\s*", "");
                                  HashMap<String, String> hashMap = new HashMap<>();
                                  hashMap.put("phone", phone);
                                  phoneMap = hashMap;
                                  Toast.makeText(activity, "该手机号码为诚信号码，免验证！", Toast.LENGTH_LONG).show();
                              } else {
                                  if (result == SMSSDK.RESULT_COMPLETE) {
                                      phoneMap = data;
                                  } else {
                                      errorHandle(data);
                                  }
                              }
                              if (phoneMap != null && callback != null) {
                                  callback.afterEvent(
                                          SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE,
                                          SMSSDK.RESULT_COMPLETE, phoneMap);
                                  finish();
                              }
                          }
                      }

        );
    }


    private void errorHandle(Object data) {
        ((Throwable) data).printStackTrace();
        // 验证码不正确
        String message = ((Throwable) data).getMessage();
        int resId = 0;
        try {
            JSONObject json = new JSONObject(message);
            int status = json.getInt("status");
            resId = getStringRes(activity,
                    "smssdk_error_detail_" + status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (resId == 0) {
            resId = getStringRes(activity, "smssdk_virificaition_code_wrong");
        }
        if (resId > 0) {
            Toast.makeText(activity, resId, Toast.LENGTH_SHORT).show();
        }
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

    /**
     * 加载对话框
     */
    public static final Dialog progressDialog(Context context) {
        final Dialog dialog = new Dialog(context, R.style.CommonDialog);
        LinearLayout layout = ProgressDialogLayout.create(context);
        dialog.setContentView(layout);
        return dialog;
    }

}
