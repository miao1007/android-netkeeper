package com.xinli.portalclient.util;

import android.support.annotation.NonNull;
import android.util.Log;
import com.xinli.portalclient.model.TouchPoint;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class MessageContext {
  public static final String TAG = MessageContext.class.getSimpleName();

  public static List paramStrReturn(int picWidth, int picHeight, List<TouchPoint> list1,
      List<TouchPoint> list2, @NonNull String key, String username, String password,
      String clientIp) {
    StringBuilder outString = new StringBuilder();
    Log.w(TAG, "width=====" + String.valueOf(picWidth));
    Log.w(TAG, "height====" + String.valueOf(picHeight));
    outString.append("{").append(picWidth).append(",").append(picHeight).append("}@");
    for (TouchPoint tp : list1) {
      outString.append("{")
          .append(tp.getX())
          .append(",")
          .append(tp.getY())
          .append(",")
          .append(tp.getEventTime())
          .append("}")
          .append("#");
    }
    outString.append("@");
    for (TouchPoint tp2 : list2) {
      outString.append("{")
          .append(tp2.getX())
          .append(",")
          .append(tp2.getY())
          .append(",")
          .append(tp2.getEventTime())
          .append("}")
          .append("#");
    }
    List<NameValuePair> params = new ArrayList();
    params.add(new BasicNameValuePair("xypoints", outString.toString()));
    Log.v(TAG, key + " jyangzi5@163.com");
    params.add(new BasicNameValuePair("key", AESByKey.encryptToString(key, "jyangzi5@163.com")));
    params.add(new BasicNameValuePair(Config.USERNAME, username));
    params.add(
        new BasicNameValuePair("password", AESByKey.encryptToString(password, "pass012345678910")));
    params.add(new BasicNameValuePair("clientip", clientIp));
    params.add(new BasicNameValuePair("clientType", "android"));
    Log.v(TAG, AESByKey.encryptToString(key, "jyangzi5@163.com"));
    Log.v(TAG, outString.toString());
    return params;
  }

  public static List paramStrReturn(int picWidth, int picHeight, List<TouchPoint> list1,
      List<TouchPoint> list2, String key, String username, String password, String clientIp,
      String clientMAC) {
    StringBuilder outString = new StringBuilder();
    Log.w(TAG,
        "\u8bf7\u6c42\u624b\u52bf\u7ec4\u88c5\u53c2\u6570 width=====" + String.valueOf(picWidth));
    Log.w(TAG,
        "\u8bf7\u6c42\u624b\u52bf\u7ec4\u88c5\u53c2\u6570 height====" + String.valueOf(picHeight));
    outString.append("{").append(picWidth).append(",").append(picHeight).append("}@");
    for (TouchPoint tp : list1) {
      outString.append("{")
          .append(tp.getX())
          .append(",")
          .append(tp.getY())
          .append(",")
          .append(tp.getEventTime())
          .append("}")
          .append("#");
    }
    outString.append("@");
    for (TouchPoint tp2 : list2) {
      outString.append("{")
          .append(tp2.getX())
          .append(",")
          .append(tp2.getY())
          .append(",")
          .append(tp2.getEventTime())
          .append("}")
          .append("#");
    }
    List<NameValuePair> params = new ArrayList();
    params.add(new BasicNameValuePair("xypoints", outString.toString()));
    Log.v(TAG, new StringBuilder(String.valueOf(key)).append("jyangzi5@163.com").toString());
    params.add(new BasicNameValuePair("key", AESByKey.encryptToString(key, "jyangzi5@163.com")));
    params.add(new BasicNameValuePair(Config.USERNAME, username));
    params.add(
        new BasicNameValuePair("password", AESByKey.encryptToString(password, "pass012345678910")));
    params.add(new BasicNameValuePair("clientip", clientIp));
    params.add(new BasicNameValuePair("clientType", "android"));
    params.add(new BasicNameValuePair("clientmac", clientMAC));
    Log.v(TAG, AESByKey.encryptToString(key, "jyangzi5@163.com"));
    Log.v(TAG, outString.toString());
    return params;
  }

  public static List keyStrReturn(String key, String username, String password) {
    List<NameValuePair> params = new ArrayList();
    try {
      Log.v(TAG, new StringBuilder(String.valueOf(key)).append("jyangzi5@163.com").toString());
      params.add(new BasicNameValuePair("key", AESByKey.encryptToString(key, "jyangzi5@163.com")));
      params.add(new BasicNameValuePair(Config.USERNAME, username));
      params.add(new BasicNameValuePair("password",
          AESByKey.encryptToString(password, "pass012345678910")));
      Log.v(TAG, AESByKey.encryptToString(key, "jyangzi5@163.com"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return params;
  }
}
