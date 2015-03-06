package edu.util;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class Secret
{
  private static final String DES = "DES";
  private static final String PASSWORD_CRYPT_KEY = "__jDlog_";
  
  public Secret() {}
  
  public static String byte2hex(byte[] paramArrayOfByte)
  {
    String str1 = "";
    int i = 0;
    if (i >= paramArrayOfByte.length) {
      return str1.toUpperCase();
    }
    String str2 = Integer.toHexString(0xFF & paramArrayOfByte[i]);
    if (str2.length() == 1) {}
    for (str1 = str1 + "0" + str2;; str1 = str1 + str2)
    {
      i++;
      break;
    }
  }
  
  public static final String decrypt(String paramString)
  {
    try
    {
      String str = new String(decrypt(hex2byte(paramString.getBytes()), "__jDlog_".getBytes()));
      return str;
    }
    catch (Exception localException) {}
    return null;
  }
  
  public static byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws Exception
  {
    SecureRandom localSecureRandom = new SecureRandom();
    DESKeySpec localDESKeySpec = new DESKeySpec(paramArrayOfByte2);
    SecretKey localSecretKey = SecretKeyFactory.getInstance("DES").generateSecret(localDESKeySpec);
    Cipher localCipher = Cipher.getInstance("DES");
    localCipher.init(2, localSecretKey, localSecureRandom);
    return localCipher.doFinal(paramArrayOfByte1);
  }
  
  public static final String encrypt(String paramString)
  {
    try
    {
      String str = byte2hex(encrypt(paramString.getBytes(), "__jDlog_".getBytes()));
      return str;
    }
    catch (Exception localException) {}
    return null;
  }
  
  public static byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws Exception
  {
    SecureRandom localSecureRandom = new SecureRandom();
    DESKeySpec localDESKeySpec = new DESKeySpec(paramArrayOfByte2);
    SecretKey localSecretKey = SecretKeyFactory.getInstance("DES").generateSecret(localDESKeySpec);
    Cipher localCipher = Cipher.getInstance("DES");
    localCipher.init(1, localSecretKey, localSecureRandom);
    return localCipher.doFinal(paramArrayOfByte1);
  }
  
  public static byte[] hex2byte(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length % 2 != 0) {
      throw new IllegalArgumentException("长度不是偶数");
    }
    byte[] arrayOfByte = new byte[paramArrayOfByte.length / 2];
    for (int i = 0;; i += 2)
    {
      if (i >= paramArrayOfByte.length) {
        return arrayOfByte;
      }
      String str = new String(paramArrayOfByte, i, 2);
      arrayOfByte[(i / 2)] = ((byte)Integer.parseInt(str, 16));
    }
  }
}
