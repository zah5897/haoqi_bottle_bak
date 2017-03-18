package com.zhan.haoqi.bottle.http;

import android.text.TextUtils;
import android.util.Log;

import com.zhan.haoqi.bottle.MyApplication;
import com.zhan.haoqi.bottle.data.UserManager;
import com.zhan.haoqi.bottle.util.AppUtils;
import com.zhan.haoqi.sign.Sign;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zah on 2016/11/23.
 */
public class HttpHelper {

    public static String URL_IP = "117.143.221.190:8899/bottle";
    public static final String URL_HTTPS = "https://" + URL_IP;
    public static final String URL_HTTP = "http://" + URL_IP;

    public static String getHttpUrl() {
        return URL_HTTP;
    }

    public static String getHttpsUrl() {
        return URL_HTTPS;
    }


    public static final int HTTP_CONNECT_TIME_OUT = 10;
    public static final int HTTP_READ_TIME_OUT = 10;


    public static String getAvatarProFix(String imageName) {
        if (TextUtils.isEmpty(imageName)) {
            return imageName;
        }

        if (imageName.startsWith("http")) {
            return imageName;
        }
        return getHttpUrl() + "/images/user/" + imageName;
    }

    public static String getImgThumbFix(String imageName) {

        if (TextUtils.isEmpty(imageName)) {
            return imageName;
        }
        if (imageName.startsWith("http")) {
            return imageName;
        }
        return getHttpUrl() + "/images/bottle/thumb/" + imageName;
    }

    public static String getImgOriginFix(String imageName) {
        if (TextUtils.isEmpty(imageName)) {
            return imageName;
        }
        if (imageName.startsWith("http")) {
            return imageName;
        }
        return getHttpUrl() + "/images/bottle/origin/" + imageName;
    }

    private static RequestParam fifterParam(String url, RequestParam param) {
        if (param == null) {
            param = new RequestParam();
        }
        param.put("client_id", AppUtils.getUniqueID());
        if (url.startsWith("https://")) {
            param.put("sign", Sign.getSignKey(MyApplication.getApp()));
        }
        param.put("verion_code", AppUtils.getVersionCode());
        param.put("verion_name", AppUtils.getVersionName());
        if (UserManager.getInstance().isLogin()) {
            param.put("user_id", UserManager.getInstance().getUser().id);
            param.put("token", UserManager.getInstance().getUser().token);
        }
        return param;
    }


    public static Observable<JSONObject> postHttp(String url) {
        return postHttp(url, null);
    }

    public static Observable<JSONObject> postHttp(final String url, final RequestParam params) {
        return Observable.create(new Observable.OnSubscribe<JSONObject>() {
            @Override
            public void call(final Subscriber<? super JSONObject> subscriber) {
                Response response = http(url, params);
                handleResult(response, subscriber);
            }

        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    public static Observable<JSONObject> postHttps(final String url, final RequestParam params) {

        return Observable.create(new Observable.OnSubscribe<JSONObject>() {
            @Override
            public void call(final Subscriber<? super JSONObject> subscriber) {
                Response response = https(url, params);
                handleResult(response, subscriber);
            }

        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    private static void handleResult(Response response, final Subscriber<? super JSONObject> subscriber) {

        if(response==null){
            subscriber.onError(new HttpError(HttpError.ERROR_HTTP, "网络异常"));
            return;
        }
        try {
            int code = response.code();
            if (code == 200) {
                String result = response.body().string();
                JSONObject object = new JSONObject(result);
                int rc = object.optInt("code");
                if (rc == 0) {
                    subscriber.onNext(object);
                } else {
                    subscriber.onError(new HttpError(rc, object.optString("msg")));
                }
            } else {
                subscriber.onError(new HttpError(HttpError.ERROR_HTTP, "网络异常"));
            }
        } catch (Exception e) {
            subscriber.onError(new HttpError(HttpError.ERROR_HTTP, "网络异常"));
        }
    }

    private static Response http(final String url, RequestParam params) {
        String fullUrl = url;
        if (!url.startsWith("http://")) {
            fullUrl = getHttpUrl() + url;
        }
        RequestBody requestBody = fifterParam(fullUrl, params).prase();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(HTTP_CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(HTTP_READ_TIME_OUT, TimeUnit.SECONDS).writeTimeout(HTTP_READ_TIME_OUT * 2, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(fullUrl)
                .post(requestBody)
                .build();

        Response response = null;
        String result = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private static Response https(final String url, RequestParam params) {
        String fullUrl = url;
        if (!url.startsWith("https://")) {
            fullUrl = getHttpsUrl() + url;
        }
        RequestBody requestBody = fifterParam(fullUrl, params).prase();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(HTTP_CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(HTTP_READ_TIME_OUT, TimeUnit.SECONDS).writeTimeout(HTTP_READ_TIME_OUT * 2, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(fullUrl)
                .post(requestBody)
                .build();

        Response response = null;
        String result = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

}
