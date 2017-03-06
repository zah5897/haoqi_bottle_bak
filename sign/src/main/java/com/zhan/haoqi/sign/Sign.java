package com.zhan.haoqi.sign;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


/**
 * Created by zah on 2016/12/6.
 */

public class Sign {

    private static String AES_KEY;
    private static String SIGN;

    static {
        System.loadLibrary("sign");
    }

    public static String getAesKey(Context context) {
        if (AES_KEY == null) {
            AES_KEY = getAESKey(context);
        }
        return AES_KEY;
    }

    public static String getSignKey(Context context) {
        if (SIGN == null) {
            SIGN = getSign(context);
        }
        return SIGN;
    }

    private static native String getSign(Context context);

    private static native String getAESKey(Context context);

}
