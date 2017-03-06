package com.zhan.haoqi.sign;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zah on 2016/12/6.
 */

public class MD5 {
    public static String getMd5(String plainText) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(plainText.getBytes());
        byte b[] = md.digest();

        int i;

        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }
        // 32位加密
        return buf.toString();
        // 16位的加密
        // return buf.toString().substring(8, 24);
    }

    public static String getMd5_16(String plainText) throws NoSuchAlgorithmException {
        return getMd5(plainText).substring(8, 24);
    }
}
