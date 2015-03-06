package com.xinli.portalclient.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Builder {
    static char[] hexDigits;

    static {
        hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    }

    public static String getMD5(String message) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return byteToHexString(MessageDigest.getInstance("MD5").digest(message.getBytes("GBK")));
    }

    private static String byteToHexString(byte[] tmp) {
        char[] str = new char[32];
        int i = 0;
        for (int i2 = 0; i2 < 16; i2++) {
            byte byte0 = tmp[i2];
            int i3 = i + 1;
            str[i] = hexDigits[(byte0 >>> 4) & 15];
            i = i3 + 1;
            str[i3] = hexDigits[byte0 & 15];
        }
        return new String(str);
    }
}
