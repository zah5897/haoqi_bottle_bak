package com.zhan.haoqi.bottle.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zah on 2016/11/28.
 */

public class ValidateUtil {


    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        CharSequence inputStr = phoneNumber;
        //正则表达式

        String phone = "^1[34578]\\d{9}$";

        Pattern pattern = Pattern.compile(phone);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}
