package com.zhan.haoqi.bottle.data;

import android.util.Log;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhan.haoqi.bottle.util.To;


import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by zah on 2016/11/23.
 */
public class QQLoginListener implements IUiListener {
    Tencent currentTencent;
    boolean islogin;

    public QQLoginListener(Tencent currentTencent, boolean islogin) {
        this.currentTencent = currentTencent;
        this.islogin = islogin;
    }

    public void setIslogin(boolean islogin) {
        this.islogin = islogin;
    }

    @Override
    public void onComplete(Object o) {
        handleLogin(o);
    }

    @Override
    public void onError(UiError uiError) {

    }

    @Override
    public void onCancel() {

    }

    private void handleLogin(Object o) {
        if (o == null) {
            return;
        }
        try {
            JSONObject jo = (JSONObject) o;
            int ret = jo.getInt("ret");
            if (ret == 0) {
                if (islogin) {
                    String openID = jo.getString("openid");
                    String accessToken = jo.getString("access_token");
                    String expires = jo.getString("expires_in");
                    currentTencent.setOpenId(openID);
                    currentTencent.setAccessToken(accessToken, expires);
                } else {
                    String nickName = jo.getString("nickname");
                    String gender = jo.getString("gender");
                    String figureurl_qq_2 = jo.getString("figureurl_qq_2");
                    int intGender = 0;
                    if ("男".equals(gender)) {
                        intGender = 1;
                    } else if ("女".equals(gender)) {
                        intGender = 0;
                    } else {
                        intGender = 2;
                    }
                    UserManager.getInstance().setQQInfo(nickName, intGender, figureurl_qq_2);
                }
            }
        } catch (Exception e) {
            UserManager.getInstance().onQQError();
        }
    }

}
