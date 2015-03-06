package com.xinli.portalclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.code.microlog4android.Level;
import com.google.code.microlog4android.format.command.CategoryFormatCommand;
import com.xinli.portalclient.model.RequestModel;
import com.xinli.portalclient.service.OnlineHeartService;
import com.xinli.portalclient.service.OnlineHeartService.LocalBinder;
import com.xinli.portalclient.util.Config;
import com.xinli.portalclient.util.HttpUtils;
import com.xinli.portalclient.util.MD5Builder;
import com.xinli.portalclient.util.ShareUtil;
import com.xinli.portalclient.util.SocketClientUDP;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.swing.XMLTableColumnDefinition;

@SuppressLint({ "NewApi" })
public class SucessActivity extends BaseActivity {
  public static final int COUNT_DOWN_ERROR = 2;
  private static int COUNT_DOWN_LIMIT = 0;
  public static final int COUNT_DOWN_LOGOUT = 5;
  public static final int COUNT_DOWN_RECORD = 1;
  public static final int COUNT_DOWN_SUCESS = 4;
  public static final int COUNT_DOWN_TIMEOUT = 3;
  public static final int GET_VERIFYCODE_SUCCESS = 6;
  protected static final int UPDATE_TEXT = 0;
  private static long loginTime;
  private static Handler mHandler;
  private static TextView textView;
  public static RequestModel userInfo;
  private final int NOTIFICATION_ID;
  private boolean authFlag;
  private Button btnCanel;
  private Button btnEnter;
  private SocketClientUDP client;
  private ServiceConnection conn;
  public int countDownCount;
  public Handler handler;
  private long id;
  Intent intent;
  boolean isBind;
  public boolean isCountDownFlag;
  private String key;
  private LinearLayout layVerify;
  OnClickListener listener;
  private SharedPreferences loginFile;
  private Intent loginIntent;
  private ProgressDialog mDialog;
  private Timer mTimer;
  private Timer mTimerCountDown;
  private TimerTask mTimerCountDownTask;
  MyReceiver receiver;
  private String resultXML;
  private SimpleDateFormat sdf;
  OnlineHeartService service;
  private ShareUtil shareUtil;
  private SharedPreferences sharedCheckState;
  private String[] strResultInfo;
  private boolean timeoutFlag;
  private TimerTask timerTask;
  private TextView tvHelp;
  private EditText tvVerifyCode;
  private TextView tvVerifyCountdown;
  private TextView tvVerifyS1;
  private TextView tvVerifyS2;
  private String verifyURL;

  //memory leak!
  private class GetVerifyCodeThread extends Thread {
    public GetVerifyCodeThread() {
      try {
        if (SucessActivity.this.client == null) {
          SucessActivity.this.client = new SocketClientUDP();
        }
      } catch (Exception e) {
        SucessActivity.this.logger.debug("client=init exception==");
      }
      SucessActivity.this.timeoutFlag = false;
    }

    public void run() {
      SucessActivity.this.logger.debug("call GetVerifyThread===");
      try {
        String resultXML;
        SucessActivity.this.shareUtil = new ShareUtil(SucessActivity.this);
        SucessActivity.this.verifyURL = SucessActivity.this.shareUtil.getVerifyURL();
        SucessActivity.this.logger.debug(
            new StringBuilder("call GetVerifyThread==step1=verifyURL==").append(
                SucessActivity.this.verifyURL).toString());
        SucessActivity.this.id = new Date().getTime();
        SucessActivity.this.logger.debug("call GetVerifyThread==step2=");
        SucessActivity.this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SS");
        SucessActivity.this.logger.debug("call GetVerifyThread==step3=");
        SucessActivity.this.key = MD5Builder.getMD5(
            new StringBuilder(String.valueOf(SucessActivity.this.getUsername())).append(
                "x%i^n&l*i)yixunsms").toString());
        SucessActivity.this.logger.debug(
            new StringBuilder("call GetVerifyThread==step4=getUsername==").append(
                SucessActivity.this.getUsername()).toString());
        String requestXML =
            new StringBuilder("<op><accountInfo login='").append(SucessActivity.this.getUsername())
                .append("' id='")
                .append(SucessActivity.this.id)
                .append("' checkCode='")
                .append(SucessActivity.this.key)
                .append("' /></op>")
                .toString();
        SucessActivity.this.logger.debug(
            new StringBuilder("\u8bf7\u6c42[").append(requestXML).toString());
        if (SucessActivity.this.client == null) {
          SucessActivity.this.logger.debug("client == null");
        } else {
          SucessActivity.this.logger.debug("client != null");
        }
        SucessActivity.this.logger.debug(
            new StringBuilder("==verifyURL==").append(SucessActivity.this.verifyURL).toString());
        if ("".equals(SucessActivity.this.verifyURL) || SucessActivity.this.verifyURL == "") {
          SucessActivity.this.logger.debug("==verifyURLAAA==");
          SucessActivity.this.client.send(Config.VERIFYCODE_AUTH_URL, Config.VERIFYCODE_AUTH_SORT,
              requestXML.getBytes());
          SucessActivity.this.logger.debug(
              new StringBuilder("\u5ba2\u6237\u7aef\u53d1\u9001\u6570\u636e\uff1a").append(
                  requestXML).toString());
          resultXML = SucessActivity.this.client.receive(Config.VERIFYCODE_AUTH_URL,
              Config.VERIFYCODE_AUTH_SORT);
        } else {
          SucessActivity.this.logger.debug(new StringBuilder("==verifyURL=BBB=").append(
              SucessActivity.this.verifyURL.split(":")[0])
              .append("==verifySORT==")
              .append(SucessActivity.this.verifyURL.split(":")[1])
              .toString());
          SucessActivity.this.client.send(SucessActivity.this.verifyURL.split(":")[0],
              Integer.parseInt(SucessActivity.this.verifyURL.split(":")[1]), requestXML.getBytes());
          SucessActivity.this.logger.debug(
              new StringBuilder("\u5ba2\u6237\u7aef\u53d1\u9001\u6570\u636e\uff1a").append(
                  requestXML).toString());
          resultXML =
              SucessActivity.this.client.receive(SucessActivity.this.verifyURL.split(":")[0],
                  Integer.parseInt(SucessActivity.this.verifyURL.split(":")[1]));
        }
        SucessActivity.this.logger.debug(
            new StringBuilder("\u63a5\u6536").append(resultXML).toString());
        SucessActivity.this.getResultInfo(resultXML);
        SucessActivity.this.logger.debug(new StringBuilder("\u63a5\u6536strResultInfo[0]==").append(
            SucessActivity.this.strResultInfo[0])
            .append("==strResultInfo[2]==")
            .append(SucessActivity.this.strResultInfo[2])
            .toString());
        if (("OK".equals(SucessActivity.this.strResultInfo[0])
            || SucessActivity.this.strResultInfo[0] == "OK") && ("Y".equals(
            SucessActivity.this.strResultInfo[2]) || SucessActivity.this.strResultInfo[2] == "Y")) {
          SucessActivity.this.logger.debug("GetVerifyCode==Client====use check======");
          SucessActivity.this.handler.sendEmptyMessage(GET_VERIFYCODE_SUCCESS);
          return;
        }
        SucessActivity.this.logger.debug("GetVerifyCode==Client====none use check======");
      } catch (Exception e) {
        SucessActivity.this.logger.debug(new StringBuilder("e====").append(e.fillInStackTrace())
            .append("==eee==")
            .append(e.toString())
            .toString());
        e.printStackTrace();
      }
    }
  }

  public class MyReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
      if ("Exception".equals(intent.getExtras().getString("onlineHeart"))) {
        SucessActivity.this.logger.debug("\u5e7f\u64ad\u63a5\u6536ok");
        SucessActivity.this.clearData();
        SucessActivity.this.startActivity(new Intent(SucessActivity.this, MainActivity.class));
        SucessActivity.this.logger.debug(
            "\u53d1\u9001\u5728\u7ebf\u5fc3\u8df3\u62a5\u6587\u53d1\u751f\u5f02\u5e38\uff0c\u8fd4\u56de\u767b\u5f55\u754c\u9762");
        Toast.makeText(SucessActivity.this.getApplicationContext(),
            "\u7f51\u7edc\u53d1\u751f\u5f02\u5e38\uff0c\u8bf7\u91cd\u65b0\u767b\u5f55", 0).show();
        SucessActivity.this.finish();
      }
    }
  }

  private class VerifyThread extends Thread {
    public VerifyThread() {
      try {
        if (SucessActivity.this.client == null) {
          SucessActivity.this.client = new SocketClientUDP();
        }
      } catch (Exception e) {
        SucessActivity.this.logger.debug("client=init exception==");
      }
      SucessActivity.this.timeoutFlag = false;
    }

    public void run() {
      Log.d(BaseActivity.TAG, "call VerifyThread");
      try {
        String resultXML;
        SucessActivity.this.id = new Date().getTime();
        SucessActivity.this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SS");
        SucessActivity.this.key = MD5Builder.getMD5(
            new StringBuilder(String.valueOf(SucessActivity.this.getUsername())).append(
                "x%i^n&l*i)yixunsms").toString());
        String requestXML = new StringBuilder("<op><validateAccount  login='").append(
            SucessActivity.this.getUsername())
            .append("' id='")
            .append(SucessActivity.this.id)
            .append("'  checkCode='")
            .append(SucessActivity.this.key)
            .append("' validateCode='")
            .append(SucessActivity.this.tvVerifyCode.getText().toString())
            .append("' /></op>")
            .toString();
        if ("".equals(SucessActivity.this.verifyURL)) {
          SucessActivity.this.client.send(Config.VERIFYCODE_AUTH_URL, Config.VERIFYCODE_AUTH_SORT,
              requestXML.getBytes());
          SucessActivity.this.logger.debug(
              new StringBuilder("\u5ba2\u6237\u7aef\u53d1\u9001\u6570\u636e\uff1a").append(
                  requestXML).toString());
          resultXML = SucessActivity.this.client.receive(Config.VERIFYCODE_AUTH_URL,
              Config.VERIFYCODE_AUTH_SORT);
        } else {
          SucessActivity.this.client.send(SucessActivity.this.verifyURL.split(":")[0],
              Integer.parseInt(SucessActivity.this.verifyURL.split(":")[1]), requestXML.getBytes());
          SucessActivity.this.logger.debug(
              new StringBuilder("\u5ba2\u6237\u7aef\u53d1\u9001\u6570\u636e\uff1a").append(
                  requestXML).toString());
          resultXML =
              SucessActivity.this.client.receive(SucessActivity.this.verifyURL.split(":")[0],
                  Integer.parseInt(SucessActivity.this.verifyURL.split(":")[1]));
        }
        SucessActivity.this.logger.debug(
            new StringBuilder("\u670d\u52a1\u7aef\u56de\u5e94\u6570\u636e\uff1a").append(
                new String(resultXML.getBytes(), "GBK")).toString());
        SucessActivity.this.logger.debug(new StringBuilder(
            "\u670d\u52a1\u7aef\u56de\u5e94\u6570\u636e=================\uff1a").append(resultXML)
            .toString());
        SucessActivity.this.getResultInfo(resultXML);
        SucessActivity.this.logger.debug(
            new StringBuilder("Verify===Client====strResultInfo[0]======").append(
                SucessActivity.this.strResultInfo[0]).toString());
        if ("OK".equals(SucessActivity.this.strResultInfo[0])
            || SucessActivity.this.strResultInfo[0] == "OK") {
          SucessActivity.this.logger.debug("Verify===Client====COUNT_DOWN_SUCESS=====");
          SucessActivity.this.handler.sendEmptyMessage(COUNT_DOWN_SUCESS);
        } else {
          SucessActivity.this.logger.debug("Verify===Client====COUNT_DOWN_ERROR=====");
          SucessActivity.this.handler.sendEmptyMessage(COUNT_DOWN_ERROR);
        }
        SucessActivity.this.stopCountDownTimer();
      } catch (Exception e) {
        SucessActivity.this.logger.debug(
            new StringBuilder("Verify===Client====exp=====").append(e.toString()).toString());
        if (!SucessActivity.this.timeoutFlag) {
          SucessActivity.this.strResultInfo[0] = "0";
          SucessActivity.this.strResultInfo[1] =
              "\u6821\u9a8c\u9a8c\u8bc1\u7801\u53d1\u751f\u5f02\u5e38";
          SucessActivity.this.handler.sendEmptyMessage(COUNT_DOWN_ERROR);
        }
      }
    }
  }

  static {
    loginTime = 0;
    COUNT_DOWN_LIMIT = 45;
    mHandler = new Handler() {
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case XMLTableColumnDefinition.OBJECT_TYPE:
            if (loginTime == -1 || loginTime == 0) {
              loginTime = System.currentTimeMillis();
            }
            long timeGap = System.currentTimeMillis() - loginTime;
            long second = ((timeGap % 3600000) % 60000) / 1000;
            textView.setText(
                new StringBuilder(String.valueOf(timeGap / 3600000)).append("\u5c0f\u65f6")
                    .append((timeGap % 3600000) / 60000)
                    .append("\u5206\u949f")
                    .append(second)
                    .append("\u79d2")
                    .toString());
          default:
            break;
        }
      }
    };
  }

  public SucessActivity() {
    this.mTimer = new Timer();
    this.NOTIFICATION_ID = 1;
    this.conn = new ServiceConnection() {
      public void onServiceConnected(ComponentName name, IBinder service) {
        LocalBinder binder = (LocalBinder) service;
        SucessActivity.this.service = binder.getService();
        SucessActivity.this.isBind = true;
      }

      public void onServiceDisconnected(ComponentName name) {
        SucessActivity.this.isBind = false;
      }
    };
    this.listener = new OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case CategoryFormatCommand.FULL_CLASS_NAME_SPECIFIER:
            SucessActivity.this.finish();
          default:
            break;
        }
      }
    };
    this.mTimerCountDown = null;
    this.mTimerCountDownTask = null;
    this.handler = new Handler() {
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case COUNT_DOWN_RECORD:
            SucessActivity.this.tvVerifyCountdown.setText(
                ((COUNT_DOWN_LIMIT - SucessActivity.this.countDownCount) + 1));
            break;
          case COUNT_DOWN_ERROR:
            SucessActivity.this.logger.debug("exit====COUNT_DOWN_ERROR=");
            SucessActivity.this.mDialog.dismiss();
            SucessActivity.this.btnEnter.setClickable(false);
            SucessActivity.this.btnCanel.setClickable(false);
            SucessActivity.this.tvVerifyS1.setText("");
            SucessActivity.this.tvVerifyS2.setText("");
            SucessActivity.this.tvVerifyCountdown.setText(
                new StringBuilder(String.valueOf(SucessActivity.this.strResultInfo[1])).append(
                    "\uff0c3\u79d2\u540e\u9000\u51fa\uff01").toString());
            SucessActivity.this.handler.sendEmptyMessageDelayed(COUNT_DOWN_LOGOUT, 3000);
            break;
          case COUNT_DOWN_TIMEOUT:
            SucessActivity.this.timeoutFlag = true;
            SucessActivity.this.logger.debug("exit====COUNT_DOWN_TIMEOUT=");
            if (SucessActivity.this.mDialog != null) {
              SucessActivity.this.mDialog.dismiss();
            }
            SucessActivity.this.logoutDo();
            break;
          case COUNT_DOWN_SUCESS:
            SucessActivity.this.mDialog.dismiss();
            SucessActivity.this.layVerify.setVisibility(Level.ERROR_INT);
            SucessActivity.this.setCheckState(true);
            break;
          case COUNT_DOWN_LOGOUT:
            SucessActivity.this.mDialog.dismiss();
            SucessActivity.this.layVerify.setVisibility(Level.ERROR_INT);
            SucessActivity.this.logoutDo();
            break;
          case GET_VERIFYCODE_SUCCESS:
            SucessActivity.this.layVerify.setVisibility(0);
            SucessActivity.this.layVerify.getBackground().setAlpha(150);
            SucessActivity.this.tvVerifyS1.setText(R.string.verify_s1);
            SucessActivity.this.tvVerifyS2.setText(R.string.verify_s2);
            if (!SucessActivity.this.isCountDownFlag) {
              SucessActivity.this.startCountDownTimer();
            }
            SucessActivity.this.initVerifyCode();
            break;
        }
        super.handleMessage(msg);
      }
    };
  }

  protected void onPause() {
    super.onPause();
    this.logger.info("onPause=====");
    try {
      this.handler.removeMessages(COUNT_DOWN_RECORD);
    } catch (Exception e) {
    }
    try {
      this.handler.removeMessages(COUNT_DOWN_ERROR);
    } catch (Exception e2) {
    }
    try {
      this.handler.removeMessages(COUNT_DOWN_TIMEOUT);
    } catch (Exception e3) {
    }
    try {
      this.handler.removeMessages(COUNT_DOWN_SUCESS);
    } catch (Exception e4) {
    }
    try {
      this.handler.removeMessages(COUNT_DOWN_LOGOUT);
    } catch (Exception e5) {
    }
    try {
      this.handler.removeMessages(GET_VERIFYCODE_SUCCESS);
    } catch (Exception e6) {
    }
    try {
      stopCountDownTimer();
    } catch (Exception e7) {
      e7.printStackTrace();
    }
    try {
      this.authFlag = false;
      this.layVerify.setVisibility(Level.ERROR_INT);
      if (this.client != null) {
        this.client.close();
      }
    } catch (Exception e72) {
      e72.printStackTrace();
    }
  }

  public void onCreate(Bundle savedInstanceState) {
    this.logger.debug("onCreate ====step1========");
    if (((double) Float.valueOf(VERSION.RELEASE.substring(0, COUNT_DOWN_TIMEOUT).trim())
        .floatValue()) > 2.3d) {
      StrictMode.setThreadPolicy(new Builder().detectDiskReads()
          .detectDiskWrites()
          .detectNetwork()
          .permitNetwork()
          .penaltyLog()
          .build());
    }
    super.onCreate(savedInstanceState);
    this.logger.debug("onCreate ====step2========");
    Intent loginIntent = getIntent();
    loginTime = loginIntent.getLongExtra("loginTime", loginTime);
    this.authFlag = loginIntent.getBooleanExtra("authFlag", false);
    requestWindowFeature(COUNT_DOWN_RECORD);
    this.logger.debug(
        new StringBuilder("onCreate ====step3======getUsername==").append(getUsername())
            .append("==authFlag==")
            .append(this.authFlag)
            .toString());
    this.receiver = new MyReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction("android.intent.onlineheart.exception");
    registerReceiver(this.receiver, filter);
    this.logger.debug("onCreate ====step4========");
    this.intent = new Intent(this, OnlineHeartService.class);
    this.intent.putExtra("url", getRealUrl());
    this.intent.putExtra(Config.SESSIONID, getSessionId());
    this.intent.putExtra("phoneIp", intToIp(getLocalIpAddress()));
    this.intent.putExtra("clientVersion", getVersionName());
    this.intent.putExtra(Config.USERNAME, getUsername());
    bindService(this.intent, this.conn, COUNT_DOWN_RECORD);
    this.isBind = true;
    this.logger.debug("onCreate ====step5========");
    setContentView(R.layout.success);
    this.strResultInfo = new String[4];
    this.isCountDownFlag = false;
    this.countDownCount = 0;
    this.layVerify = (LinearLayout) findViewById(R.id.verify_lay);
    this.tvHelp = (TextView) findViewById(R.id.verify_help);
    this.tvVerifyCountdown = (TextView) findViewById(R.id.verify_countdown);
    this.tvVerifyS1 = (TextView) findViewById(R.id.verify_s1);
    this.tvVerifyS2 = (TextView) findViewById(R.id.verify_s2);
    this.tvVerifyS1.setText(R.string.verify_s1);
    this.tvVerifyS2.setText(R.string.verify_s2);
    this.tvVerifyCode = (EditText) findViewById(R.id.verify_code);
    this.btnEnter = (Button) findViewById(R.id.btn_enter);
    this.btnCanel = (Button) findViewById(R.id.btn_canel);
    this.btnEnter.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        SucessActivity.this.logger.debug("GetVerifyCode==btnEnter==onClick===");
        SucessActivity.this.btnEnter.setClickable(false);
        SucessActivity.this.mDialog = new ProgressDialog(SucessActivity.this);
        SucessActivity.this.mDialog.setTitle("\u9a8c\u8bc1\u7801\u6821\u9a8c");
        SucessActivity.this.mDialog.setMessage(
            "\u6b63\u5728\u6821\u9a8c\u9a8c\u8bc1\u7801\uff0c\u8bf7\u7a0d\u540e...");
        SucessActivity.this.mDialog.show();
        ((InputMethodManager) SucessActivity.this.getSystemService("input_method")).toggleSoftInput(
            0, COUNT_DOWN_ERROR);
        new VerifyThread().start();
      }
    });
    this.btnCanel.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        SucessActivity.this.btnEnter.setClickable(true);
        SucessActivity.this.initVerifyCode();
      }
    });
    this.logger.debug(new StringBuilder("onCreate=====step1111===isBind==").append(this.isBind)
        .append("==authFlag==")
        .append(this.authFlag)
        .toString());
    if (!getCheckState()) {
      if ("ON".equals(Config.VERIFYCODE_SWITCH) || Config.VERIFYCODE_SWITCH == "ON") {
        new GetVerifyCodeThread().start();
      }
    }
    textView = (TextView) findViewById(R.id.login_time);
    ImageView logout = (ImageView) findViewById(R.id.logout);
    logout.setBackgroundResource(R.drawable.logout);
    ImageView exit = (ImageView) findViewById(R.id.exit);
    exit.setBackgroundResource(R.drawable.exit);
    this.logger.debug("onCreate ====step6========");
    this.loginFile = getSharedPreferences(Config.LOGININFOFILE, 0);
    this.logger.debug(new StringBuilder("loginTime========").append(loginTime).toString());
    this.logger.debug("onCreate ====step7========");
    timerTask();
    this.logger.debug("onCreate ====step8========");
    showIcoOnStatusBar();
    logouLisenert(logout);
    exitListener(exit);
    this.logger.debug("onCreate ====step9========");
  }

  protected void onStart() {
    super.onStart();
  }

  private void logouLisenert(ImageView logout) {
    this.logger.debug("logouLisenert========step11===");
    logout.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        SucessActivity.this.logger.debug(
            "logouLisenert==onClick==\u7528\u6237\u70b9\u51fb\u9000\u51fa\u767b\u5f55\u6309\u94ae");
        SucessActivity.this.logoutApp();
      }
    });
    logout.setOnTouchListener(new OnTouchListener() {
      public boolean onTouch(View arg0, MotionEvent arg1) {
        SucessActivity.this.logger.debug("logout.setOnTouchListener");
        if (arg1.getAction() == 0) {
          arg0.setBackgroundResource(R.drawable.logout_down);
        } else if (arg1.getAction() == 1) {
          arg0.setBackgroundResource(R.drawable.logout);
        }
        return false;
      }
    });
  }

  private void exitListener(ImageView exit) {
    this.logger.debug("exitListener========step11===");
    exit.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        SucessActivity.this.logger.debug("exitListener========onClick===");
        SucessActivity.this.exitApp();
      }
    });
    exit.setOnTouchListener(new OnTouchListener() {
      public boolean onTouch(View arg0, MotionEvent arg1) {
        SucessActivity.this.logger.debug("exitListener========setOnTouchListener===");
        if (arg1.getAction() == 0) {
          arg0.setBackgroundResource(R.drawable.exit_down);
        } else if (arg1.getAction() == 1) {
          arg0.setBackgroundResource(R.drawable.exit);
        }
        return false;
      }
    });
  }

  private void clearData() {
    try {
      List<Activity> tempList = activityList;
      activityList = new ArrayList();
      if (tempList.size() > 0) {
        for (Activity activity : tempList) {
          activity.finish();
        }
      }
      loginTime = 0;
      if (this.mTimer != null) {
        this.mTimer.cancel();
        this.mTimer = null;
      }
      if (this.timerTask != null) {
        this.timerTask.cancel();
        this.timerTask = null;
      }
      userInfo = null;
      removeIcoOnStatusBar();
      if (this.isBind) {
        unbindService(this.conn);
        this.isBind = false;
      }
    } catch (Exception e) {
      this.logger.debug(new StringBuilder("==clearData==").append(e.getMessage()).toString());
    }
  }

  private void showIcoOnStatusBar() {
    NotificationManager mNotificationManager =
        (NotificationManager) getSystemService("notification");
    long when = System.currentTimeMillis();
    Notification notification = new Notification();
    notification.icon = 2130837529;
    notification.tickerText = "\u7545\u901a\u65e0\u7ebf";
    notification.when = when;
    notification.flags = 32;
    notification.setLatestEventInfo(getApplicationContext(), "\u7545\u901a\u65e0\u7ebf",
        "\u7545\u901a\u65e0\u7ebf\u6b63\u5728\u8fd0\u884c\u2026\u2026",
        PendingIntent.getActivity(this, COUNT_DOWN_RECORD, new Intent(this, MainActivity.class),
            134217728));
    mNotificationManager.notify(COUNT_DOWN_RECORD, notification);
  }

  private void removeIcoOnStatusBar() {
    try {
      this.logger.debug("\u6e05\u9664\u72b6\u6001\u680f\u56fe\u6807");
      ((NotificationManager) getSystemService("notification")).cancel(COUNT_DOWN_RECORD);
    } catch (Exception e) {
      this.logger.debug(new StringBuilder("==e==").append(e.getMessage()).toString());
    }
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == 4) {
      Intent homeIntent = new Intent("android.intent.action.MAIN");
      homeIntent.addCategory("android.intent.category.HOME");
      startActivity(homeIntent);
    }
    return false;
  }

  protected void onDestroy() {
    unregisterReceiver(this.receiver);
    if (this.isBind) {
      unbindService(this.conn);
    }
    super.onDestroy();
  }

  public void timerTask() {
    this.timerTask = new TimerTask() {
      public void run() {
        mHandler.sendEmptyMessage(0);
      }
    };
    this.mTimer.schedule(this.timerTask, 1000, 1000);
  }

  public void logoutApp() {
    this.logger.debug("logoutApp==step111");
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("\u63d0\u793a");
    builder.setMessage("\u60a8\u786e\u5b9a\u8981\u9000\u51fa\u7a0b\u5e8f\u5417?");
    builder.setPositiveButton(R.string.logoutTip, new OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        String username = "";
        try {
          SucessActivity.this.logger.debug("logoutApp==step222");
          username = SucessActivity.this.getUsername();
          SucessActivity.this.logger.debug(
              new StringBuilder("logoutApp==step333").append(username).toString());
          SucessActivity.this.logger.debug(new StringBuilder(String.valueOf(username)).append(
              "logoutApp\u9000\u51fa\u65f6\uff0c\u68c0\u67e5\u7f51\u7edc\u8fde\u63a5\u60c5\u51b5")
              .toString());
          State wifi = ((ConnectivityManager) SucessActivity.this.getSystemService(
              "connectivity")).getNetworkInfo(COUNT_DOWN_RECORD).getState();
          if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
            SucessActivity.this.logger.debug(
                "logoutApp\u7f51\u7edc\u8fde\u63a5\u6b63\u5e38\uff0c\u5411\u670d\u52a1\u5668\u53d1\u51fa\u9000\u51fa\u767b\u5f55\u8bf7\u6c42");
            SucessActivity.this.logger.debug(new StringBuilder(
                "logoutApp\u5411\u670d\u52a1\u5668\u53d1\u51fa\u9000\u51fa\u767b\u5f55\u8bf7\u6c42\u7ed3\u679c\uff1a")
                .append(HttpUtils.requestHttpGet(
                    new StringBuilder(String.valueOf(SucessActivity.this.getRealUrl())).append(
                        "/wf.do?").toString(), SucessActivity.this.getSessionId(),
                    new StringBuilder("code=6&username=").append(username)
                        .append("&clientip=")
                        .append(
                            SucessActivity.this.intToIp(SucessActivity.this.getLocalIpAddress()))
                        .toString()))
                .toString());
            SucessActivity.this.setCheckState(false);
          }
        } catch (Exception e) {
          SucessActivity.this.logger.error(new StringBuilder(String.valueOf(username)).append(
              "logoutApp\u9000\u51fa\u767b\u5f55\u53d1\u751f\u5f02\u5e38")
              .append(e.getMessage())
              .toString());
        }
        SucessActivity.this.clearData();
        SucessActivity.this.logger.debug(new StringBuilder(String.valueOf(username)).append(
            "logoutApp\u76f4\u63a5\u9000\u51fa\u7a0b\u5e8f").toString());
        Process.killProcess(Process.myPid());
      }
    });
    builder.setNegativeButton("\u53d6\u6d88", null);
    builder.show();
  }

  private void backMain() {
    String username = "";
    try {
      this.logger.debug("exitApp========step22===");
      username = getUsername();
      this.logger.debug(
          new StringBuilder("exitApp========step22==username=").append(username).toString());
      this.logger.debug(new StringBuilder(String.valueOf(username)).append(
          "exitApp\u9000\u51fa\u767b\u5f55\u5e76\u8fd4\u56de\u767b\u5f55\u754c\u9762\u65f6\uff0c\u68c0\u67e5\u7f51\u7edc\u8fde\u63a5\u60c5\u51b5")
          .toString());
      State wifi =
          ((ConnectivityManager) getSystemService("connectivity")).getNetworkInfo(COUNT_DOWN_RECORD)
              .getState();
      if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
        this.logger.debug(
            "\u7f51\u7edc\u8fde\u63a5\u6b63\u5e38\uff0c\u5411\u670d\u52a1\u5668\u53d1\u51fa\u9000\u51fa\u767b\u5f55\u8bf7\u6c42\uff0c\u5e76\u8fd4\u56de\u5230\u754c\u9762\u8bf7\u6c42");
        this.logger.debug(
            new StringBuilder(String.valueOf(getRealUrl())).append("/wf.do?code=6&username=")
                .append(username)
                .append("&clientip=")
                .append(intToIp(getLocalIpAddress()))
                .append(",sessionId=")
                .append(getSessionId())
                .toString());
        this.logger.debug(new StringBuilder(String.valueOf(username)).append(
            "exitApp\u9000\u51fa\u7a0b\u5e8f\u8fd4\u56de\u767b\u5f55\u9875\u9762\u8c03\u7528\u670d\u52a1\u5668\u7ed3\u679c\uff1a")
            .append(HttpUtils.requestHttpGet(
                new StringBuilder(String.valueOf(getRealUrl())).append("/wf.do?").toString(),
                getSessionId(), new StringBuilder("code=6&username=").append(username)
                .append("&clientip=")
                .append(intToIp(getLocalIpAddress()))
                .toString()))
            .toString());
        setCheckState(false);
      }
    } catch (Exception e) {
      this.logger.error(new StringBuilder(String.valueOf(username)).append(
          "exitApp\u9000\u51fa\u7a0b\u5e8f\u8fd4\u56de\u767b\u5f55\u9875\u9762\u53d1\u751f\u5f02\u5e38")
          .append(e)
          .toString());
    }
    clearData();
    startActivity(new Intent(this, MainActivity.class));
    this.logger.debug(new StringBuilder(String.valueOf(username)).append(
        "exitApp\u7a0b\u5e8f\u76f4\u63a5\u9000\u51fa\u767b\u5f55\uff0c\u5e76\u8fd4\u56de\u5230\u754c\u9762")
        .toString());
    finish();
  }

  public void exitApp() {
    Log.d(TAG,"exitApp");
  }

  public String getRealUrl() {
    if (Config.realUrl != null && !Config.realUrl.isEmpty()) {
      return Config.realUrl;
    }
    if (this.loginFile == null) {
      this.loginFile = getSharedPreferences(Config.LOGININFOFILE, 0);
    }
    return this.loginFile.getString(Config.REDIRECTINFO, "");
  }

  public String getUsername() {
    if (userInfo != null && userInfo.getUsername() != null && !userInfo.getUsername().isEmpty()) {
      return userInfo.getUsername();
    }
    if (this.loginFile == null) {
      this.loginFile = getSharedPreferences(Config.LOGININFOFILE, 0);
    }
    return this.loginFile.getString(Config.USERNAME, "");
  }

  public String getSessionId() {
    if (userInfo != null && userInfo.getSessionId() != null && !userInfo.getSessionId().isEmpty()) {
      return userInfo.getSessionId();
    }
    if (this.loginFile == null) {
      this.loginFile = getSharedPreferences(Config.LOGININFOFILE, 0);
    }
    return this.loginFile.getString(Config.SESSIONID, "");
  }

  private void initVerifyCode() {
    try {
      this.tvVerifyCode.setText("");
      this.tvVerifyCode.setFocusable(true);
      this.tvVerifyCode.setFocusableInTouchMode(true);
      this.tvVerifyCode.requestFocus();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private String[] getResultInfo(String resultXml) {
    try {
      Element returnInfo =
          DocumentHelper.parseText(resultXml).getRootElement().element("returnInfo");
      this.strResultInfo[0] = returnInfo.attributeValue("resCode");
      this.strResultInfo[1] = returnInfo.attributeValue("resMsg");
      this.strResultInfo[2] = returnInfo.attributeValue("isBind");
      this.strResultInfo[3] = returnInfo.attributeValue("sendSuccess");
    } catch (Exception e) {
      try {
        e.printStackTrace();
        this.strResultInfo[0] = "0";
        this.strResultInfo[1] = "\u89e3\u6790\u7ed3\u679c\u4fe1\u606f\u5931\u8d25";
      } catch (Throwable th) {
      }
    }
    return this.strResultInfo;
  }

  private void startCountDownTimer() {
    if (this.mTimerCountDown == null) {
      this.mTimerCountDown = new Timer();
      this.isCountDownFlag = true;
    }
    if (this.mTimerCountDownTask == null) {
      this.mTimerCountDownTask = new TimerTask() {
        public void run() {
          Log.i(BaseActivity.TAG,
              new StringBuilder("CountDownCount===").append(SucessActivity.this.countDownCount)
                  .toString());
          if (SucessActivity.this.countDownCount >= COUNT_DOWN_LIMIT) {
            Log.i(BaseActivity.TAG,
                new StringBuilder("startCountDownTimer==CountDownCount:").append(
                    SucessActivity.this.countDownCount).toString());
            SucessActivity.this.handler.sendEmptyMessage(COUNT_DOWN_TIMEOUT);
          }
          SucessActivity.this.handler.sendEmptyMessage(COUNT_DOWN_RECORD);
          SucessActivity sucessActivity = SucessActivity.this;
          sucessActivity.countDownCount++;
        }
      };
    }
    if (this.mTimerCountDown != null && this.mTimerCountDown != null) {
      this.mTimerCountDown.schedule(this.mTimerCountDownTask, 0, 1000);
    }
  }

  public void stopCountDownTimer() {
    this.logger.debug("stopCountDownTimer====timer\u505c\u6b62==");
    if (this.mTimerCountDown != null) {
      this.mTimerCountDown.cancel();
      this.mTimerCountDown = null;
    }
    if (this.mTimerCountDownTask != null) {
      this.mTimerCountDownTask.cancel();
      this.mTimerCountDownTask = null;
    }
    this.isCountDownFlag = false;
    this.countDownCount = 0;
  }

  private void logoutDo() {
    this.logger.debug("exit====btn===step11111=");
    String username = getUsername();
    try {
      this.logger.debug(new StringBuilder(String.valueOf(username)).append(
          "\u9000\u51fa\u767b\u5f55\u5e76\u8fd4\u56de\u767b\u5f55\u754c\u9762\u65f6\uff0c\u68c0\u67e5\u7f51\u7edc\u8fde\u63a5\u60c5\u51b5")
          .toString());
      State wifi =
          ((ConnectivityManager) getSystemService("connectivity")).getNetworkInfo(COUNT_DOWN_RECORD)
              .getState();
      if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
        this.logger.debug(
            "\u7f51\u7edc\u8fde\u63a5\u6b63\u5e38\uff0c\u5411\u670d\u52a1\u5668\u53d1\u51fa\u9000\u51fa\u767b\u5f55\u8bf7\u6c42\uff0c\u5e76\u8fd4\u56de\u5230\u754c\u9762\u8bf7\u6c42");
        this.logger.debug(
            new StringBuilder(String.valueOf(getRealUrl())).append("/wf.do?code=6&username=")
                .append(username)
                .append("&clientip=")
                .append(intToIp(getLocalIpAddress()))
                .append(",sessionId=")
                .append(getSessionId())
                .toString());
        this.logger.info(new StringBuilder(String.valueOf(username)).append(
            "\u9000\u51fa\u7a0b\u5e8f\u8fd4\u56de\u767b\u5f55\u9875\u9762\u8c03\u7528\u670d\u52a1\u5668\u7ed3\u679c\uff1a")
            .append(HttpUtils.requestHttpGet(
                new StringBuilder(String.valueOf(getRealUrl())).append("/wf.do?").toString(),
                getSessionId(), new StringBuilder("code=6&username=").append(username)
                .append("&clientip=")
                .append(intToIp(getLocalIpAddress()))
                .toString()))
            .toString());
        setCheckState(false);
      }
    } catch (Exception e) {
      this.logger.error(new StringBuilder(String.valueOf(username)).append(
          "\u9000\u51fa\u7a0b\u5e8f\u8fd4\u56de\u767b\u5f55\u9875\u9762\u53d1\u751f\u5f02\u5e38")
          .append(e)
          .toString());
    }
    clearData();
    try {
      ComponentName comp =
          new ComponentName("com.xinli.portalclient", "com.xinli.portalclient.MainActivity");
      Intent intent = new Intent();
      intent.setComponent(comp);
      intent.setFlags(268435456);
      intent.setAction("android.intent.action.VIEW");
      startActivity(intent);
    } catch (Exception e2) {
      e2.printStackTrace();
    }
    finish();
  }

  public void setCheckState(boolean state) {
    try {
      this.sharedCheckState = getSharedPreferences("CHECK_STATE", COUNT_DOWN_RECORD);
      Editor edit = this.sharedCheckState.edit();
      edit.putBoolean("checked", state);
      edit.commit();
      Log.i(BaseActivity.TAG, new StringBuilder("setCheckState===\u8bbe\u7f6e\u72b6\u6001==state==")
          .append(state)
          .toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean getCheckState() {
    try {
      Log.i(BaseActivity.TAG, "getCheckState===\u67e5\u627e\u72b6\u6001====");
      this.sharedCheckState = getSharedPreferences("CHECK_STATE", COUNT_DOWN_RECORD);
      return this.sharedCheckState.getBoolean("checked", false);
    } catch (Exception e) {
      e.printStackTrace();
      return true;
    }
  }
}
