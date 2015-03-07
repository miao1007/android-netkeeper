package com.xinli.portalclient.util;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import com.google.code.microlog4android.Level;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.dom4j.swing.XMLTableColumnDefinition;

@SuppressLint({ "DefaultLocale" })
public class AESByKey {
  private static final String AES = "AES";

  public static byte[] encrypt(byte[] src, @NonNull String key) throws Exception {
    Cipher cipher = Cipher.getInstance(AES);
    cipher.init(1, new SecretKeySpec(key.getBytes(), AES));
    return cipher.doFinal(src);
  }

  public static byte[] decrypt(byte[] src, String key) throws Exception {
    Cipher cipher = Cipher.getInstance(AES);
    cipher.init(XMLTableColumnDefinition.NUMBER_TYPE, new SecretKeySpec(key.getBytes(), AES));
    return cipher.doFinal(src);
  }

  public static String byte2hex(byte[] b) {
    String hs = "";
    String str = "";
    for (byte b2 : b) {
      str = Integer.toHexString(b2 & 255);
      if (str.length() == 1) {
        hs = new StringBuilder(String.valueOf(hs)).append("0").append(str).toString();
      } else {
        hs = new StringBuilder(String.valueOf(hs)).append(str).toString();
      }
    }
    return hs.toUpperCase();
  }

  public static byte[] hex2byte(byte[] b) {
    if (b.length % 2 != 0) {
      throw new IllegalArgumentException("\u957f\u5ea6\u4e0d\u662f\u5076\u6570");
    }
    byte[] b2 = new byte[(b.length / 2)];
    for (int n = 0; n < b.length; n += 2) {
      b2[n / 2] = (byte) Integer.parseInt(new String(b, n, 2), Level.FATAL_INT);
    }
    return b2;
  }

  public static final String decrypt(String data, String key) {
    try {
      return new String(decrypt(hex2byte(data.getBytes()), key));
    } catch (Exception e) {
      return null;
    }
  }

  public static final byte[] encrypt(String data, String key) {
    try {
      return encrypt(data.getBytes(), key);
    } catch (Exception e) {
      return null;
    }
  }

  public static final String encryptToString(String data, String key) {
    try {
      return byte2hex(encrypt(data.getBytes(), key));
    } catch (Exception e) {
      return null;
    }
  }

  public byte[] intToByte(int i) {
    return new byte[] {
        (byte) (i & 255), (byte) ((65280 & i) >> 8), (byte) ((16711680 & i) >> 16),
        (byte) ((-16777216 & i) >> 24)
    };
  }

  
  public static int bytesToInt(byte[] bytes) {
    return (((bytes[0] & 255) | ((bytes[1] << 8) & 65280)) | ((bytes[2] << 16) & 16711680)) | ((
        bytes[3]
            << 24) & -16777216);
  }

  //这个是外包的码农写的吧!!!???
  public static void main(String[] args) {
    System.out.println(new StringBuilder("enCode=").append(byte2hex(
        encrypt("TEL=15829350859&SEQ=411D23BB-6A71-44C3-B468-123B643ED8BF", "0123456789012345")))
        .toString());
  }
}
