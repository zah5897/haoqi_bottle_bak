package com.zhan.haoqi.bottle.http;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by zah on 2016/12/13.
 */

public class RequestParam extends HashMap<String, Object> {
    public RequestBody prase() {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (String key : keySet()) {
            Object object = get(key);
            if (!(object instanceof File)) {
                String value = object.toString();
                builder.addFormDataPart(key, value);
            } else {
                File file = (File) object;
                builder.addFormDataPart("file", file.getName(), RequestBody.create(null, file));
            }
        }
        return builder.build();
    }
}
