package com.xinli.portalclient.util;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.dom4j.swing.XMLTableColumnDefinition;

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
      try {
        IvParameterSpec iv = new IvParameterSpec(ivByte);
        SecretKey securekey = SecretKeyFactory.getInstance("DESede").generateSecret(dks);
        Cipher cipher = Cipher.getInstance(Algorithm);
        cipher.init(1, securekey, iv);
        dESedeKeySpec = dks;
        return cipher.doFinal(src);
      } catch (NoSuchAlgorithmException e) {
        NoSuchAlgorithmException e1 = e;
        dESedeKeySpec = dks;
      } catch (NoSuchPaddingException e2) {
        NoSuchPaddingException e22 = e2;
        dESedeKeySpec = dks;
        e22.printStackTrace();
        return null;
      } catch (Exception e3) {
        Exception e32 = e3;
        dESedeKeySpec = dks;
        e32.printStackTrace();
        return null;
      }
    } catch (NoSuchAlgorithmException e4) {
      e1 = e4;
      e1.printStackTrace();
      return null;
    } catch (NoSuchPaddingException e5) {
      e22 = e5;
      e22.printStackTrace();
      return null;
    } catch (Exception e6) {
      e32 = e6;
      e32.printStackTrace();
      return null;
    }
  }

  public static byte[] decryptMode(byte[] src, String key) {
    try {
      DESedeKeySpec dks = new DESedeKeySpec(key.getBytes());
      try {
        IvParameterSpec iv = new IvParameterSpec(ivByte);
        SecretKey securekey = SecretKeyFactory.getInstance("DESede").generateSecret(dks);
        Cipher cipher = Cipher.getInstance(Algorithm);
        cipher.init(XMLTableColumnDefinition.NUMBER_TYPE, securekey, iv);
        return cipher.doFinal(src);
      } catch (NoSuchAlgorithmException e) {
        NoSuchAlgorithmException e1 = e;
        DESedeKeySpec dESedeKeySpec = dks;
      } catch (NoSuchPaddingException e2) {
        NoSuchPaddingException e22 = e2;
        dESedeKeySpec = dks;
        e22.printStackTrace();
        return null;
      } catch (Exception e3) {
        Exception e32 = e3;
        dESedeKeySpec = dks;
        e32.printStackTrace();
        return null;
      }
    } catch (NoSuchAlgorithmException e4) {
      e1 = e4;
      e1.printStackTrace();
      return null;
    } catch (NoSuchPaddingException e5) {
      e22 = e5;
      e22.printStackTrace();
      return null;
    } catch (Exception e6) {
      e32 = e6;
      e32.printStackTrace();
      return null;
    }
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
    String str = "Account=adsl123;MAC=01:02:03:0a:0b:0c;IP=10.1.1.100;SESSIONID=SID0001;";
    str =
        "Account=07912780512;SessionID=SHL-ME60100600000300322fecd038564;ClientID=220.177.248.109;IP=59.52.1.155;";
    str =
        "Account=07913994850;SessionID=SHL-ME6020030753006670f2c4d086629;ClientID=220.177.248.109;IP=59.52.219.5;";
    str =
        "Account=0791643455;SessionID=SHL-ME603006098000184f5e27c044211;ClientID=220.177.248.109;IP=59.52.1.131;";
    System.out.println(
        new StringBuilder("\u52a0\u5bc6\u524d\u7684\u5b57\u7b26\u4e32:").append(str).toString());
    System.out.println(new StringBuilder("\u957f\u5ea6").append(str.length()).toString());
    byte[] encoded = encryptMode(str.getBytes(), "RADIUS");
    byte2hex(encoded);
    System.out.println(
        new StringBuilder("\u52a0\u5bc6\u540e\u7684\u5b57\u7b26\u4e32:").append(new String(encoded))
            .toString());
    System.out.println(encoded.length);
    System.out.println(new StringBuilder("\u89e3\u5bc6\u540e\u7684\u5b57\u7b26\u4e32:").append(
        new String(decryptMode(encoded, "RADIUS"))).toString());
    System.out.println(
        "F0DBC06B30D350CC3CBC05ADC29F9FAEE34CCF36BCAFE9660C661AFB45FC0B95AD9CBB5796F00FCC8AFD326B335354C4A17B14AB8970C49E69D89F8A98944442CDFEFBBDB7E188E644C11AA44B6A346445BD13730A3BAF4B86E0248DEB898C16E50CCD116F112D44451F84B7EBD44D23"
            .getBytes().length);
  }
}
