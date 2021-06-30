package com.migi.toeic.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneUtils {
    private static final String PHONE_PATTERN = "(\\+84|84|0|)([9|3|7|8|5])+([0-9]{8})";
    private static Pattern pattern;
    private static Matcher matcher;

    public static boolean isPhone(String phone) {
        pattern = Pattern.compile(PHONE_PATTERN);
        matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    public static String formatPhoneNumber(String phone) {
        if (phone.startsWith("+84")) {
            return phone.substring(3);
        } else if (phone.startsWith("84")) {
            return phone.substring(2);
        } else if (phone.startsWith("0")) {
            return phone.substring(1);
        }
        return phone;
    }

}
