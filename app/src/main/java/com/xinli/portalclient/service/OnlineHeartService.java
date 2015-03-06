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
import com.xinli.portalclient.util.Config;
import com.xinli.portalclient.util.HttpUtils;
import com.xinli.portalclient.util.Sim_NetKeeperClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class OnlineHeartService extends Service {
    private static String clientVersion;
    private static final Logger logger;
    private static String phoneIp;
    private static String sessionId;
    private static String url;
    private static String username;
    private Binder binder;
    private Handler handler;
    private Handler proxyHandler;
    private Runnable proxyRunnable;
    private Runnable runable;
    private boolean sendHeart;

    public class LocalBinder extends Binder {
        public OnlineHeartService getService() {
            return OnlineHeartService.this;
        }
    }

    public OnlineHeartService() {
        this.sendHeart = true;
        this.handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        this.runable = new Runnable() {
            public void run() {
                OnlineHeartService.this.sendOnlineHeart();
                OnlineHeartService.this.handler.postDelayed(OnlineHeartService.this.runable, 120000);
            }
        };
        this.proxyHandler = new Handler();
        this.proxyRunnable = new Runnable() {
            public void run() {
            }
        };
        this.binder = new LocalBinder();
    }

    static {
        url = null;
        sessionId = null;
        phoneIp = null;
        clientVersion = null;
        username = null;
        logger = LoggerFactory.getLogger(OnlineHeartService.class);
    }

    public int getResult() {
        return this.sendHeart ? 0 : 1;
    }

    public IBinder onBind(Intent intent) {
        Log.i("OnlineHeartService", "onBind");
        url = intent.getStringExtra("url");
        sessionId = intent.getStringExtra(Config.SESSIONID);
        phoneIp = intent.getStringExtra("phoneIp");
        clientVersion = intent.getStringExtra("clientVersion");
        username = intent.getStringExtra(Config.USERNAME);
        return this.binder;
    }

    public void onCreate() {
        super.onCreate();
        Log.i("OnlineHeartService", "onCreate");
        this.handler.post(this.runable);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i("OnlineHeartService", "onDestroy");
    }

    public boolean onUnbind(Intent intent) {
        Log.i("OnlineHeartService", "onUnbind");
        this.handler.removeCallbacks(this.runable);
        return super.onUnbind(intent);
    }

    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.i("OnlineHeartService", "onRebind");
    }

    private void sendOnlineHeart() {
        List<NameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair(Config.USERNAME, username));
        try {
            logger.debug(new StringBuilder("\u5f00\u59cb\u53d1\u9001\u5728\u7ebf\u5fc3\u8df3\u62a5\u6587:").append(url).append("/wf.do?code=7  sessionId:").append(sessionId).append("time:").append(new Date()).toString());
            logger.info(new StringBuilder("\u53d1\u9001\u5728\u7ebf\u5fc3\u8df3\u62a5\u6587\u7ed3\u679c\uff1a").append(HttpUtils.sendContentByHttpClient(new StringBuilder(String.valueOf(url)).append("/wf.do?code=7").toString(), sessionId, params)).toString());
        } catch (Exception e) {
            onlineHeartBroadcast();
            logger.error(new StringBuilder("\u53d1\u9001\u5728\u7ebf\u5fc3\u8df3\u62a5\u6587\u53d1\u751f\u5f02\u5e38").append(e).toString());
            logger.error(new StringBuilder("\u53d1\u9001\u5728\u7ebf\u5fc3\u8df3\u62a5\u6587\u53d1\u751f\u5f02\u5e38message").append(e.getMessage()).toString());
            this.sendHeart = false;
        }
    }

    private void sendProxyHeart() {
        try {
            logger.debug(new StringBuilder("\u5f00\u59cb\u53d1\u9001\u9632\u4ee3\u7406\u5fc3\u8df3\u62a5\u6587: ip:").append(phoneIp).append(",version:").append(clientVersion).append("username:").append(username).toString());
            new Sim_NetKeeperClient().sendHeart(username, phoneIp, new StringBuilder("android").append(clientVersion).toString());
            logger.info("\u53d1\u9001\u9632\u4ee3\u7406\u5fc3\u8df3\u62a5\u6587\u6210\u529f");
        } catch (Exception e) {
            logger.error(new StringBuilder("\u53d1\u9001\u9632\u4ee3\u7406\u5fc3\u8df3\u53d1\u751f\u5f02\u5e38").append(e.getMessage()).toString());
            e.printStackTrace();
        }
    }

    public void onlineHeartBroadcast() {
        Intent intent = new Intent();
        intent.putExtra("onlineHeart", "Exception");
        intent.setAction("android.intent.onlineheart.exception");
        sendBroadcast(intent);
    }
}
