package com.xinli.portalclient.util;

import android.util.Log;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpUtils {

  public static final String TAG = HttpUtils.class.getSimpleName();
  private static String USER_AGENT;
  private static final Logger logger;

  static {
    USER_AGENT = "wifikeeper/0.9.1 ( linux; u: android ; build :cs)";
    logger = LoggerFactory.getLogger(HttpUtils.class);
  }

  public static String getContentByHttp(String strUrl) {
    String result = "";
    try {
      URL url = new URL(strUrl);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      InputStreamReader in = new InputStreamReader(conn.getInputStream());
      BufferedReader br = new BufferedReader(in);
      while (true) {
        String readline = br.readLine();
        if (readline == null) {
          break;
        }
        result = new StringBuilder(String.valueOf(result)).append(readline).toString();
      }
      in.close();
      conn.disconnect();
      URL url2 = url;
    } catch (Exception e) {
    }
    return result;
  }

  public static String sendContentByHttpPost(String strUrl, String sessionid, List params) {
    try {
      Log.v(TAG, new StringBuilder("sendContentByHttpPost:").append(strUrl).toString());
      HttpPost httpPost = new HttpPost(strUrl);
      httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
      httpPost.setHeader("Cookie", sessionid);
      HttpClient httpClient = new DefaultHttpClient();
      httpClient.getParams()
          .setParameter("http.connection.timeout", Integer.valueOf(BitmapUtils.REQUEST_TIMEOUT));
      httpClient.getParams()
          .setParameter("http.socket.timeout", Integer.valueOf(BitmapUtils.SO_TIMEOUT));
      HttpResponse httpResponse = httpClient.execute(httpPost);
      if (httpResponse.getStatusLine().getStatusCode() == 200) {
        String result = EntityUtils.toString(httpResponse.getEntity(), "GBK");
        Log.v(TAG, "HttpPost return 200");
        return result;
      }

    } catch (IOException e) {
      Log.v(TAG, "HttpPost failed");
      return null; 
    }
    return null;
  }

  public static String requestHttpGet(String strUrl, String sessionid, String params) {
    Log.v("client", new StringBuilder(
        "\u6d4b\u8bd5\u8c03\u7528HTTPClient\u5411\u670d\u52a1\u5668\u53d1\u9001\u6570\u636e\uff01").append(
        strUrl).toString());
    try {
      HttpGet request =
          new HttpGet(new StringBuilder(String.valueOf(strUrl)).append(params).toString());
      request.setHeader("Cookie", sessionid);
      HttpClient httpClient = new DefaultHttpClient();
      httpClient.getParams()
          .setParameter("http.connection.timeout", Integer.valueOf(BitmapUtils.REQUEST_TIMEOUT));
      httpClient.getParams()
          .setParameter("http.socket.timeout", Integer.valueOf(BitmapUtils.SO_TIMEOUT));
      HttpResponse httpResponse = httpClient.execute(request);
      logger.debug(new StringBuilder("getStatusCode()====").append(
          httpResponse.getStatusLine().getStatusCode()).toString());
      if (httpResponse.getStatusLine().getStatusCode() == 200) {
        String result = EntityUtils.toString(httpResponse.getEntity(), "GBK");
        logger.info(
            new StringBuilder("\u8bf7\u6c42\u8fd4\u56de\u7ed3\u679c\u6210\u529f\uff1a").append(
                result).toString());
        Log.v("client", "HttpGet\u65b9\u5f0f\u8bf7\u6c42\u6210\u529f!");
        return result;
      }
      logger.info("\u8bf7\u6c42\u8fd4\u56de\u7ed3\u679c\u5931\u8d25");
      Log.v("client", "HttpGet\u65b9\u5f0f\u8bf7\u6c42\u5931\u8d25");
      return null;
    } catch (Exception e) {
      logger.error(
          new StringBuilder("\u8bf7\u6c42\u53d1\u751f\u5f02\u5e38\uff1a").append(e).toString());
      e.printStackTrace();
      throw new RuntimeException(e.getMessage());
    }
  }
}
