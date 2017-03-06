package com.zhan.haoqi.sign;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by zah on 2016/12/6.
 */

public class JavaSign {
    public String getSignatureInfo(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            android.content.pm.Signature[] signs = packageInfo.signatures;
            android.content.pm.Signature sign = signs[0];
            byte[] signature = sign.toByteArray();

            X509Certificate cert = parseSignature(signature);

            String key = cert.getPublicKey().toString();

            int aa = key.indexOf("modulus");
            int bb = key.indexOf("publicExponent");
            return key.substring(aa + 8, bb - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getMessageDigest(String instance, byte[] signature) {
        String sinfo = null;
        try {
            MessageDigest md = MessageDigest.getInstance(instance);

            md.update(signature);

            byte[] digest = md.digest();

            sinfo = toHexString(digest);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sinfo;
    }

    public X509Certificate parseSignature(byte[] signature) {
        X509Certificate cert = null;
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return cert;
    }

    private void byte2hex(byte b, StringBuffer buf) {

        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8',

                '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        int high = ((b & 0xf0) >> 4);

        int low = (b & 0x0f);

        buf.append(hexChars[high]);

        buf.append(hexChars[low]);

    }


    /**
     * Converts a byte array to hex string
     */
    private String toHexString(byte[] block) {

        StringBuffer buf = new StringBuffer();


        int len = block.length;


        for (int i = 0; i < len; i++) {

            byte2hex(block[i], buf);

            if (i < len - 1) {

                buf.append(":");

            }

        }

        return buf.toString();

    }
}
