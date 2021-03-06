package com.zhan.haoqi.bottle.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.Tencent;
import com.zhan.haoqi.bottle.MyApplication;
import com.zhan.haoqi.bottle.http.BaseSubscriber;
import com.zhan.haoqi.bottle.http.HttpError;
import com.zhan.haoqi.bottle.http.HttpHelper;
import com.zhan.haoqi.bottle.http.RequestParam;
import com.zhan.haoqi.bottle.ui.LoginActivity;
import com.zhan.haoqi.bottle.util.SharePrefreenceUtil;
import com.zhan.haoqi.bottle.util.To;
import com.zhan.haoqi.sign.Sign;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import haoqi.emoji.util.EmojiUtils;


/**
 * Created by zah on 2016/10/21.
 */
public class UserManager {
    public static final String TENCENT_APP_ID = "1105695571";
    public static final String CACHE_ACCOUNT_KEY = "account";


    public static final String USER_SUBMIT = "/user/submit";
    public static final String USER_LOGIN = "/user/login";
    public static final String USER_REGIST = "/user/regist";
    public static final String USER_MODIFY = "/user/modify_user_info";
    public static final String USER_MRESET_PWD = "/user/reset_pwd";
    private static UserManager userManager;
    private Tencent mTencent;

    private Map<String, User> userMap;

    private UserManager() {
        userMap=new HashMap<String, User>();
        Sign.getSignKey(MyApplication.getApp());
        EmojiUtils.getInstance();
    }

    private User user;

    public User getUser() {
        return user;
    }

    public Map<String, User> getUserMap() {
        return userMap;
    }

    public static UserManager getInstance() {
        if (userManager == null) {
            userManager = new UserManager();
            userManager.loadCacheAccount();
        }
        return userManager;
    }

    private void loadCacheAccount() {
        String cache_json = SharePrefreenceUtil.getString(CACHE_ACCOUNT_KEY);
        if (cache_json != null) {
            Gson gson = new Gson();
            user = gson.fromJson(cache_json, User.class);
        }

    }

    public boolean isLogin() {
        return user != null;
    }

    void setQQInfo(String nickName, int gender, String avatar) {
        user = new User();
        user.gender = gender;
        user.nick_name = nickName;
        user.avatar = avatar;
        user.mobile = mTencent.getOpenId();
        user.token = mTencent.getAccessToken();
        String param = submit();
    }

    void onQQError() {
        if (accountLoginListener != null) {
            accountLoginListener.onFailure(-1, "");
        }
    }

    private String submit() {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);

        RequestParam param = new RequestParam();
        param.put("account", userJson);
        HttpHelper.postHttp(USER_SUBMIT, param).subscribe(new BaseSubscriber<JSONObject>() {
            @Override
            public void onCompleted() {
                if (isLogin()) {
                    if (accountLoginListener != null) {
                        accountLoginListener.onLogin(user);
                    }
                } else {
                    if (accountLoginListener != null) {
                        accountLoginListener.onFailure(-1, "登录失败");
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (accountLoginListener != null) {
                    HttpError he = (HttpError) e;
                    accountLoginListener.onFailure(he.getCode(), he.getMsg());
                }
            }

            @Override
            public void onNext(JSONObject jsonObject) {
                praseAndSave(jsonObject);
                onCompleted();
            }
        });
        return userJson;
    }

    private QQLoginListener qqLoginListener;
    private AccountLoginListener accountLoginListener;

    public void qqLogin(final Activity activity, AccountLoginListener loginListener) {
        mTencent = Tencent.createInstance(TENCENT_APP_ID, activity.getApplicationContext());
        qqLoginListener = new QQLoginListener(mTencent, true);
        //scope ='all';
        mTencent.login(activity, "all", qqLoginListener);
        this.accountLoginListener = loginListener;
    }

    public void handleResultData(final Activity activity, Intent data) {
        mTencent.handleResultData(data, qqLoginListener);
        qqLoginListener.setIslogin(false);
        UserInfo userInfo = new UserInfo(activity, mTencent.getQQToken());
        userInfo.getUserInfo(qqLoginListener);
    }

    private void cacheAccount(String account) {
        SharePrefreenceUtil.saveString(CACHE_ACCOUNT_KEY, account);
    }

    public void logout() {
        SharePrefreenceUtil.saveString(CACHE_ACCOUNT_KEY, "");
        user = null;
    }

    public void regist(RequestParam param, BaseSubscriber baseSubscriber) {
        HttpHelper.postHttp(USER_REGIST, param).subscribe(baseSubscriber);
    }

    public void modifyUserInfo(RequestParam param, BaseSubscriber baseSubscriber) {
        HttpHelper.postHttp(USER_MODIFY, param).subscribe(baseSubscriber);
    }

    public void resetPwd(String phone, String oldPassword, String newPwd, BaseSubscriber baseSubscriber) {
        RequestParam param = new RequestParam();
        param.put("mobile", phone);
        param.put("old_password", oldPassword);
        param.put("password", newPwd);
        HttpHelper.postHttp(USER_MRESET_PWD, param).subscribe(baseSubscriber);
    }

    public void praseAndSave(JSONObject jsonObject) {
        JSONObject userObj = jsonObject.optJSONObject("user");
        Gson gson = new Gson();
        String accStr = userObj.toString();
        user = gson.fromJson(accStr, User.class);
        cacheAccount(accStr);
    }


    public void toLogin(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }
}
