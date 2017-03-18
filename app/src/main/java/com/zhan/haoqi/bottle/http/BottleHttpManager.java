package com.zhan.haoqi.bottle.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhan.haoqi.bottle.data.Bottle;
import com.zhan.haoqi.bottle.data.UserManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;

/**
 * Created by zah on 2016/12/1.
 */

public class BottleHttpManager {


    public static final int PAGE_SIZE = 20;

    private static final String BOTTLE_NET = "/bottle/get_one";
    private static final String BOTTLE_MAIN_REFRESH = "/bottle/main_refresh";
    private static final String BOTTLE_SEND = "/bottle/send";
    private static final String BOTTLE_COMMENT = "/comment/submit";
    private static final String BOTTLE_COMMENT_LIST = "/comment/list";


    public static void netBottle(BaseSubscriber subscriber) {
        HttpHelper.postHttp(BOTTLE_NET).subscribe(subscriber);
    }

    public static void sendBottle(File file, String contnet, int type, BaseSubscriber subscriber) {
        RequestParam param = new RequestParam();
        param.put("content", contnet);
        param.put("sender_id", UserManager.getInstance().getUser().id);
        param.put("type", type);
        if (file != null && file.exists()) {
            param.put("image", file);
        }
        HttpHelper.postHttp(BOTTLE_SEND, param).subscribe(subscriber);
    }

    public static Bottle praseBottle(JSONObject jsonObject) {
        JSONObject bottleObj = jsonObject.optJSONObject("bottle");
        Gson gson = new Gson();
        return gson.fromJson(bottleObj.toString(), Bottle.class);
    }

    public static void refreshBottles(Subscriber subscriber) {
        HttpHelper.postHttp(BOTTLE_MAIN_REFRESH).subscribe(subscriber);
    }

    public static List<Bottle> praseBottles(JSONObject jsonObject) {
        Gson gson = new Gson();
        JSONArray bottlesArray = jsonObject.optJSONArray("bottles");
        List<Bottle> ps = gson.fromJson(bottlesArray.toString(), new TypeToken<List<Bottle>>() {
        }.getType());
        return ps;
    }

    public static void submitComment(long bottle_id, long author_user_id, String comment, long at_comment_id, Subscriber subscriber) {
        RequestParam param = new RequestParam();
        param.put("bottle_id", bottle_id);
        param.put("author_user_id", author_user_id);
        param.put("content", comment);
        param.put("at_comment_id", at_comment_id);
        HttpHelper.postHttp(BOTTLE_COMMENT, param).subscribe(subscriber);
    }

    public static void reloadComments(long bottle_id, long last_comment_id, BaseSubscriber<JSONObject> subscriber) {
        RequestParam param = new RequestParam();
        param.put("bottle_id", bottle_id);
        param.put("last_id", last_comment_id);
        param.put("page_size", PAGE_SIZE);
        HttpHelper.postHttp(BOTTLE_COMMENT_LIST, param).subscribe(subscriber);
    }
}
