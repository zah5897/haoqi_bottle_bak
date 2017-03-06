package com.zhan.haoqi.bottle.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhan.haoqi.bottle.data.Comment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by zah on 2017/2/16.
 */

public class JSONToBeanUtil<T> {
    public static List<Comment> toComments(JSONArray commons) {
        Gson gson = new Gson();
        List<Comment> ps = gson.fromJson(commons.toString(), new TypeToken<List<Comment>>() {
        }.getType());
        return ps;
    }

    public static Comment jsonToComment(JSONObject jsonObject) {
        Gson gson = new Gson();
        Comment bean = gson.fromJson(jsonObject.toString(),Comment.class);
        return bean;
    }

    /**
     public static <T> T jsonToBean(Class<T> clazz, JSONObject jsonObject) {
     return jsonToBean(clazz, jsonObject.toString());
     }

     public static <T> T jsonToBean(Class<T> clazz, String jsonStr) {
     Gson gson = new Gson();
     return gson.fromJson(jsonStr, clazz);
     }


     public static <T> List<T> jsonsToBean(Class<T> clazz, String jsonsStr) {
     Gson gson = new Gson();
     List<T> ps = gson.fromJson(jsonsStr, new TypeToken<List<T>>() {
     }.getType());
     return ps;
     }
     public static <T> List<T> jsonsToBean(Class<T> clazz, JSONArray jsonsArray) {
     return jsonsToBean(clazz,jsonsArray.toString());
     }
     */


}
