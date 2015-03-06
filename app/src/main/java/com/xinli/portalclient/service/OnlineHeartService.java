package com.xinli.portalclient.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.xinli.portalclient.util.HttpUtils;
import com.xinli.portalclient.util.Sim_NetKeeperClient;
import java.util.ArrayList;
import java.util.Date;
import org.apache.http.message.BasicNameValuePair;

public class OnlineHeartService extends Service {
  private static String clientVersion;
  private static final Logger logger = LoggerFactory.getLogger(OnlineHeartService.class);
  private static String phoneIp;
  private static String sessionId;
  private static String url = null;
  private static String username;
  private Binder binder = new LocalBinder();
  private Handler handler = new Handler() {
    public void handleMessage(Message paramAnonymousMessage) {
      super.handleMessage(paramAnonymousMessage);
    }
  };
  
  private Handler proxyHandler = new Handler();
  private Runnable proxyRunnable = new Runnable() {
    public void run() {
    }
  };
  private Runnable runable = new Runnable() {
    public void run() {
      OnlineHeartService.this.sendOnlineHeart();
      handler.postDelayed(runable, 120000L);
    }
  };
  private boolean sendHeart = true;

  static {
    sessionId = null;
    phoneIp = null;
    clientVersion = null;
    username = null;
  }

  public OnlineHeartService() {
  }

  private void sendOnlineHeart() {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new BasicNameValuePair("username", username));
    try {
      logger.debug(
          "开始发送在线心跳报文:" + url + "/wf.do?code=7  sessionId:" + sessionId + "time:" + new Date());
      String str =
          HttpUtils.sendContentByHttpClient(url + "/wf.do?code=7", sessionId, localArrayList);
      logger.info("发送在线心跳报文结果：" + str);
      return;
    } catch (Exception localException) {
      onlineHeartBroadcast();
      logger.error("发送在线心跳报文发生异常" + localException);
      logger.error("发送在线心跳报文发生异常message" + localException.getMessage());
      sendHeart = false;
    }
  }

  private void sendProxyHeart() {
    try {
      logger.debug(
          "开始发送防代理心跳报文: ip:" + phoneIp + ",version:" + clientVersion + "username:" + username);
      new Sim_NetKeeperClient().sendHeart(username, phoneIp, "android" + clientVersion);
      logger.info("发送防代理心跳报文成功");
      return;
    } catch (Exception localException) {
      logger.error("发送防代理心跳发生异常" + localException.getMessage());
      localException.printStackTrace();
    }
  }

  public int getResult() {
    if (sendHeart) {
      return 0;
    }
    return 1;
  }

  public IBinder onBind(Intent paramIntent) {
    Log.i("OnlineHeartService", "onBind");
    url = paramIntent.getStringExtra("url");
    sessionId = paramIntent.getStringExtra("sessionId");
    phoneIp = paramIntent.getStringExtra("phoneIp");
    clientVersion = paramIntent.getStringExtra("clientVersion");
    username = paramIntent.getStringExtra("username");
    return binder;
  }

  public void onCreate() {
    super.onCreate();
    Log.i("OnlineHeartService", "onCreate");
    handler.post(runable);
  }

  public void onDestroy() {
    super.onDestroy();
    Log.i("OnlineHeartService", "onDestroy");
  }

  public void onRebind(Intent paramIntent) {
    super.onRebind(paramIntent);
    Log.i("OnlineHeartService", "onRebind");
  }

  public boolean onUnbind(Intent paramIntent) {
    Log.i("OnlineHeartService", "onUnbind");
    handler.removeCallbacks(runable);
    return super.onUnbind(paramIntent);
  }

  public void onlineHeartBroadcast() {
    Intent localIntent = new Intent();
    localIntent.putExtra("onlineHeart", "Exception");
    localIntent.setAction("android.intent.onlineheart.exception");
    sendBroadcast(localIntent);
  }

  public class LocalBinder extends Binder {
    public LocalBinder() {
    }

    public OnlineHeartService getService() {
      return OnlineHeartService.this;
    }
  }
}
