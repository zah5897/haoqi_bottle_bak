package com.zhan.haoqi.bottle.http;

/**
 * Created by zah on 2016/12/12.
 */

public class HttpError extends Exception {

    public static final int ERROR_HTTP = 4998;//http异常
    public static final int ERROR_OPERATE = 4999;//操作失败
    public static final int ERROR_NETWORK = 5000;
    public static final int ERROR_NOLOGIN = 5001; //当前没登录
    public static final int ERROR_PARAM = 5002; //参数错误
    public static final int ERROR_SYS = 5003; //系统错误
    public static final int ERROR_USER_ALREADY_EXIST = 5004; //用户已存在
    public static final int ERROR_FILE_UOLOAD = 5005; //文件上传失败
    public static final int ERROR_USER_NOT_EXIST = 5006; //用户不存在
    public static final int ERROR_PASS = 5007; //密码错误
    public static final int ERROR_BUSY = 5008; //操作频繁
    public static final int ERROR_NOT_CONFIRM = 5009; //非法请求
    public static final int ERROR_DATA = 5010; //非法请求

    private int code;
    private String msg;

    public HttpError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
