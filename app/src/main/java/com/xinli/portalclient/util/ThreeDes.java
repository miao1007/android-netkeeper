package com.xinli.portalclient.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.dom4j.swing.XMLTableColumnDefinition;

// http://zhangzhaoaaa.iteye.com/blog/2145750
public class ThreeDes {
  private static final String Algorithm = "DESede/CBC/PKCS5Padding";
  static byte[] RADIUS_KEY;
  static byte[] XKJS_KEY_20;
  static byte[] XKJS_KEY_21;
  private static byte[] ivByte;

  static {
    ivByte = new byte[] {
        (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56
    };
    RADIUS_KEY = "XINLIAPSECRET01234567890".getBytes();
    XKJS_KEY_20 = "XINLIAPSECRET01234567891".getBytes();
    XKJS_KEY_21 = "XINLIAPSECRET01JIANGXI21".getBytes();
  }

  public static byte[] encryptMode(byte[] src, String key) {
    try {
      DESedeKeySpec dESedeKeySpec;
      DESedeKeySpec dks = new DESedeKeySpec(key.getBytes());

      IvParameterSpec iv = new IvParameterSpec(ivByte);
      SecretKey securekey = SecretKeyFactory.getInstance("DESede").generateSecret(dks);
      Cipher cipher = Cipher.getInstance(Algorithm);
      cipher.init(1, securekey, iv);
      dESedeKeySpec = dks;
      return cipher.doFinal(src);
    } catch (Exception e6) {

      return null;
    }
  }

  public static byte[] decryptMode(byte[] src, String key) {
    try {
      DESedeKeySpec dks = new DESedeKeySpec(key.getBytes());

      IvParameterSpec iv = new IvParameterSpec(ivByte);
      SecretKey securekey = SecretKeyFactory.getInstance("DESede").generateSecret(dks);
      Cipher cipher = Cipher.getInstance(Algorithm);
      cipher.init(XMLTableColumnDefinition.NUMBER_TYPE, securekey, iv);
      return cipher.doFinal(src);
    } catch (java.security.NoSuchAlgorithmException e1) {
      // TODO: handle exception
      e1.printStackTrace();
    } catch (javax.crypto.NoSuchPaddingException e2) {
      e2.printStackTrace();
    } catch (java.lang.Exception e3) {
      e3.printStackTrace();
    }
    return null;
  }

  public static String byte2hex(byte[] b) {
    String hs = "";
    String str = "";
    for (int n = 0; n < b.length; n++) {
      str = Integer.toHexString(b[n] & 255);
      if (str.length() == 1) {
        hs = new StringBuilder(String.valueOf(hs)).append("0").append(str).toString();
      } else {
        hs = new StringBuilder(String.valueOf(hs)).append(str).toString();
      }
      if (n < b.length - 1) {
        hs = new StringBuilder(String.valueOf(hs)).append(":").toString();
      }
    }
    System.out.println(hs.toUpperCase());
    return hs.toUpperCase();
  }

  public static void main(String[] args) {
    System.out.println("加密前的字符串:"
        + "Account=0791643455;SessionID=SHL-ME603006098000184f5e27c044211;ClientID=220.177.248.109;IP=59.52.1.131;");
    System.out.println("长度"
        + "Account=0791643455;SessionID=SHL-ME603006098000184f5e27c044211;ClientID=220.177.248.109;IP=59.52.1.131;"
        .length());
    byte[] arrayOfByte1 = encryptMode(
        "Account=0791643455;SessionID=SHL-ME603006098000184f5e27c044211;ClientID=220.177.248.109;IP=59.52.1.131;"
            .getBytes(), "RADIUS");
    byte2hex(arrayOfByte1);
    System.out.println("加密后的字符串:" + new String(arrayOfByte1));
    System.out.println(arrayOfByte1.length);
    byte[] arrayOfByte2 = decryptMode(arrayOfByte1, "RADIUS");
    System.out.println("解密后的字符串:" + new String(arrayOfByte2));
    System.out.println(
        "F0DBC06B30D350CC3CBC05ADC29F9FAEE34CCF36BCAFE9660C661AFB45FC0B95AD9CBB5796F00FCC8AFD326B335354C4A17B14AB8970C49E69D89F8A98944442CDFEFBBDB7E188E644C11AA44B6A346445BD13730A3BAF4B86E0248DEB898C16E50CCD116F112D44451F84B7EBD44D23"
            .getBytes().length);
  }
}
