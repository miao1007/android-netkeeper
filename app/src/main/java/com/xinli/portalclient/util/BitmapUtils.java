package com.xinli.portalclient.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.Log;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.xinli.portalclient.model.RequestModel;
import com.xinli.portalclient.model.ReturnMessage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class BitmapUtils {
    public static final int REQUEST_TIMEOUT = 5000;
    public static final int SO_TIMEOUT = 10000;
    protected static final Logger logger;

    static {
        logger = LoggerFactory.getLogger(BitmapUtils.class);
    }

    public static boolean initRealAddress(String requestUrl) throws RuntimeException {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        boolean isredirect = true;
        try {
            Log.i("init redirect url=======", requestUrl);
            URL myFileUrl = new URL(requestUrl);
            try {
                httpURLConnection = (HttpURLConnection) myFileUrl.openConnection();
                httpURLConnection.setConnectTimeout(REQUEST_TIMEOUT);
                httpURLConnection.setReadTimeout(SO_TIMEOUT);
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                inputStream = httpURLConnection.getInputStream();
                String realUrl = httpURLConnection.getURL().toString().split("\\?")[0];
                System.out.println(new StringBuilder("realUrl====").append(realUrl).toString());
                if (requestUrl.equalsIgnoreCase(realUrl)) {
                    isredirect = false;
                    logger.info(new StringBuilder("URL no redirect  :").append(requestUrl).append("----->").append(realUrl).toString());
                } else {
                    Config.realUrl = realUrl;
                    logger.info(new StringBuilder("URL Redirect  is :").append(requestUrl).append("----->").append(realUrl).toString());
                }
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                closeStream(inputStream, null);
                return isredirect;
            } catch (IOException e) {
                e = e;
                URL url = myFileUrl;
                try {
                    IOException e2;
                    e2.printStackTrace();
                    Log.e("Cinit redirect url error:", e2.getMessage());
                    throw new RuntimeException(e2.getMessage());
                } catch (Throwable th) {
                    Throwable th2 = th;
                }
            } catch (Throwable th3) {
                th2 = th3;
                url = myFileUrl;
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                closeStream(inputStream, null);
                throw th2;
            }
        } catch (IOException e3) {
            e2 = e3;
            e2.printStackTrace();
            Log.e("Cinit redirect url error:", e2.getMessage());
            throw new RuntimeException(e2.getMessage());
        }
    }

    public static RequestModel requestBeforeLogin(String requestUrl, int screenWidth, int screenHeight, String clientVersion, String localIp) throws RuntimeException {
        RequestModel requestResult = new RequestModel();
        try {
            logger.debug(new StringBuilder("onCreate ==requestBeforeLogin=step1===Config.realUrl==").append(Config.realUrl).toString());
            if (Config.realUrl == null) {
                initRealAddress(Config.firstRreqUrl);
            }
            logger.debug(new StringBuilder("onCreate ==requestBeforeLogin=step2===requestUrl==").append(requestUrl).toString());
            Log.i("request key and pic=======", requestUrl);
            RequestModel keyResult = getKey(clientVersion, localIp);
            if (ReturnMessage.VERSIONCHECK.equals(keyResult.getMessage()) || ReturnMessage.NATCHECK.equals(keyResult.getMessage())) {
                logger.debug(new StringBuilder("onCreate ==requestBeforeLogin=step3===keyResult==").append(keyResult).toString());
                return keyResult;
            }
            logger.debug(new StringBuilder("onCreate ==requestBeforeLogin=step4===keyResult.getKeyvalue==").append(keyResult.getKeyvalue()).toString());
            logger.debug(new StringBuilder("onCreate ==requestBeforeLogin=step4===keyResult.getSessionId==").append(keyResult.getSessionId()).toString());
            requestResult.setKeyvalue(keyResult.getKeyvalue());
            requestResult.setSessionId(keyResult.getSessionId());
            logger.debug("onCreate ==requestBeforeLogin=step5=====");
            RequestModel picResult = getPicture(keyResult.getSessionId(), screenWidth, screenHeight);
            logger.debug("onCreate ==requestBeforeLogin=step6=====");
            requestResult.setBitmap(picResult.getBitmap());
            logger.debug("onCreate ==requestBeforeLogin=step7=====");
            return requestResult;
        } catch (Exception e) {
            logger.error("onCreate ==requestBeforeLogin=Exception=====", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Bitmap getLoacalBitmapByAssets(Context c, String url) {
        InputStream in = null;
        try {
            in = c.getResources().getAssets().open(url);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            closeStream(in, null);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            closeStream(in, null);
            return null;
        }
    }

    public static RequestModel getPicture(String sessionId, int screenWidth, int screenHeight) throws RuntimeException {
        Throwable th;
        RequestModel requestResult = new RequestModel();
        InputStream in = null;
        try {
            if (Config.realUrl == null) {
                initRealAddress(Config.firstRreqUrl);
            }
            Log.w("url", new StringBuilder(String.valueOf(Config.realUrl)).append("/wf.do?code=2").toString());
            URL myFileUrl = new URL(new StringBuilder(String.valueOf(Config.realUrl)).append("/wf.do?code=2&screen=").append(URLEncoder.encode(new StringBuilder(String.valueOf(screenWidth)).append("*").append(screenHeight).toString(), "UTF-8")).toString());
            try {
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setConnectTimeout(REQUEST_TIMEOUT);
                conn.setReadTimeout(SO_TIMEOUT);
                conn.setDoInput(true);
                conn.setRequestProperty("Cookie", sessionId);
                conn.connect();
                in = conn.getInputStream();
                requestResult.setBitmap(BitmapFactory.decodeStream(in));
                closeStream(in, null);
                return requestResult;
            } catch (IOException e) {
                e = e;
                URL url = myFileUrl;
                Log.e("\u8bf7\u6c42\u56fe\u7247\u5f02\u5e38", e.getMessage());
                throw new RuntimeException(new StringBuilder("\u8bf7\u6c42\u56fe\u7247\u5f02\u5e38\uff1a").append(e.getMessage()).toString());
            } catch (Throwable th2) {
                th = th2;
                url = myFileUrl;
                closeStream(in, null);
                throw th;
            }
        } catch (IOException e2) {
            e = e2;
            try {
                IOException e3;
                Log.e("\u8bf7\u6c42\u56fe\u7247\u5f02\u5e38", e3.getMessage());
                throw new RuntimeException(new StringBuilder("\u8bf7\u6c42\u56fe\u7247\u5f02\u5e38\uff1a").append(e3.getMessage()).toString());
            } catch (Throwable th3) {
                th = th3;
            }
        }
    }

    public static RequestModel getKey(String clientVersion, String localIp) throws RuntimeException {
        IOException e;
        URL url;
        Exception e2;
        Throwable th;
        RequestModel requestResult = new RequestModel();
        InputStream inputStream = null;
        String result = "";
        try {
            if (Config.realUrl == null) {
                initRealAddress(Config.firstRreqUrl);
            }
            StringBuffer urlStr = new StringBuffer(Config.realUrl).append("/wf.do?code=1&device=").append(URLEncoder.encode(new StringBuffer("Phone:").append(Build.MODEL).append("\\SDK:").append(VERSION.SDK_INT).toString(), "UTF-8")).append("&version=").append(URLEncoder.encode(clientVersion, "UTF-8")).append("&clientip=").append(URLEncoder.encode(localIp, "UTF-8"));
            logger.debug(new StringBuilder("\u8bf7\u6c42key url=========").append(urlStr.toString()).toString());
            URL myFileUrl = new URL(urlStr.toString());
            try {
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setConnectTimeout(REQUEST_TIMEOUT);
                conn.setReadTimeout(SO_TIMEOUT);
                conn.setDoInput(true);
                conn.connect();
                Log.w("Client key response", conn.getResponseMessage());
                inputStream = conn.getInputStream();
                while (true) {
                    int tempbyte = inputStream.read();
                    if (tempbyte == -1) {
                        break;
                    }
                    result = new StringBuilder(String.valueOf(result)).append((char) tempbyte).toString();
                }
                logger.debug(new StringBuilder("\u8bf7\u6c42key\u7ed3\u679c==========").append(result).toString());
                requestResult.setMessage(result);
                closeStream(inputStream, null);
                return requestResult;
            } catch (IOException e3) {
                e = e3;
                url = myFileUrl;
            } catch (Exception e4) {
                e2 = e4;
                url = myFileUrl;
            } catch (Throwable th2) {
                th = th2;
                url = myFileUrl;
            }
            if (!(ReturnMessage.VERSIONCHECK.equals(result) || ReturnMessage.NATCHECK.equals(result))) {
                String session_value = conn.getHeaderField("Set-Cookie");
                Log.w("Client key", new StringBuilder("pic\u4f1a\u8bddID\uff1a").append(session_value).toString());
                String[] session = session_value.split(";");
                RequestModel requestResult2 = new RequestModel();
                try {
                    requestResult2.setSessionId(session[0]);
                    requestResult2.setKeyvalue(result);
                    requestResult = requestResult2;
                } catch (IOException e5) {
                    e = e5;
                    url = myFileUrl;
                    requestResult = requestResult2;
                    logger.debug(new StringBuilder("\u8bf7\u6c42key\u5f02\u5e38").append(e.getMessage()).toString());
                    throw new RuntimeException(new StringBuilder("\u8bf7\u6c42key\u5f02\u5e38\uff1a").append(e.getMessage()).toString());
                } catch (Exception e6) {
                    e2 = e6;
                    url = myFileUrl;
                    requestResult = requestResult2;
                    logger.debug(new StringBuilder("\u8bf7\u6c42key\u5f02\u5e38").append(e2.getMessage()).toString());
                    throw new RuntimeException(new StringBuilder("\u8bf7\u6c42key\u5f02\u5e38\uff1a").append(e2.getMessage()).toString());
                } catch (Throwable th3) {
                    th = th3;
                    url = myFileUrl;
                    requestResult = requestResult2;
                    closeStream(inputStream, null);
                    throw th;
                }
            }
        } catch (IOException e7) {
            e = e7;
            logger.debug(new StringBuilder("\u8bf7\u6c42key\u5f02\u5e38").append(e.getMessage()).toString());
            throw new RuntimeException(new StringBuilder("\u8bf7\u6c42key\u5f02\u5e38\uff1a").append(e.getMessage()).toString());
        } catch (Exception e8) {
            e2 = e8;
            logger.debug(new StringBuilder("\u8bf7\u6c42key\u5f02\u5e38").append(e2.getMessage()).toString());
            throw new RuntimeException(new StringBuilder("\u8bf7\u6c42key\u5f02\u5e38\uff1a").append(e2.getMessage()).toString());
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void closeStream(java.io.InputStream r1_in, java.io.OutputStream r2_out) {
        throw new UnsupportedOperationException("Method not decompiled: com.xinli.portalclient.util.BitmapUtils.closeStream(java.io.InputStream, java.io.OutputStream):void");
        /*
        if (r1 == 0) goto L_0x0005;
    L_0x0002:
        r1.close();	 Catch:{ IOException -> 0x000b }
    L_0x0005:
        if (r2 == 0) goto L_0x000a;
    L_0x0007:
        r2.close();	 Catch:{ IOException -> 0x000b }
    L_0x000a:
        return;
    L_0x000b:
        r0 = move-exception;
        r0.printStackTrace();
        goto L_0x000a;
        */
    }

    public static void main(String[] args) {
        initRealAddress("http://192.168.3.198:8888");
    }
}
