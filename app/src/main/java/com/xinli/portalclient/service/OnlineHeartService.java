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

  public static final String TAG = "OnlineHeartService";
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
      Log.d(TAG,
          "sendOnlineHeart:" + url + "/wf.do?code=7  ,sessionId:" + sessionId + ",time:" + new Date());
      String str =
          HttpUtils.sendContentByHttpPost(url + "/wf.do?code=7", sessionId, localArrayList);
      logger.info("sendOnlineHeart done:" + str);
      return;
    } catch (Exception localException) {
      onlineHeartBroadcast();
      Log.d(TAG,"sendOnlineHeart error!" + localException.getMessage());
      sendHeart = false;
    }
  }

  private void sendProxyHeart() {
    try {
      Log.d(TAG,
          "sendProxyHeart: ip:" + phoneIp + ",version:" + clientVersion + "username:" + username);
      new Sim_NetKeeperClient().sendHeart(username, phoneIp, "android" + clientVersion);
      Log.d(TAG,"sendProxyHeart success!");
      return;
    } catch (Exception localException) {
      Log.d(TAG,"sendProxyHeart exception!" + localException.getMessage());
      localException.printStackTrace();
    }
  }

  @Override
  public IBinder onBind(Intent paramIntent) {
    Log.i("OnlineHeartService", "onBind");
    url = paramIntent.getStringExtra("url");
    sessionId = paramIntent.getStringExtra("sessionId");
    phoneIp = paramIntent.getStringExtra("phoneIp");
    clientVersion = paramIntent.getStringExtra("clientVersion");
    username = paramIntent.getStringExtra("username");
    return binder;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.i("OnlineHeartService", "onCreate");
    handler.post(runable);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.i("OnlineHeartService", "onDestroy");
  }

  @Override
  public void onRebind(Intent paramIntent) {
    super.onRebind(paramIntent);
    Log.i("OnlineHeartService", "onRebind");
  }

  @Override
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
