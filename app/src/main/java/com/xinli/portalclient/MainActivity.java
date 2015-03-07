package com.xinli.portalclient;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.google.code.microlog4android.format.SimpleFormatter;
import com.xinli.portalclient.model.RequestModel;
import com.xinli.portalclient.model.ReturnMessage;
import com.xinli.portalclient.model.UpdataInfo;
import com.xinli.portalclient.util.BitmapUtils;
import com.xinli.portalclient.util.Config;
import com.xinli.portalclient.util.HttpUtils;
import com.xinli.portalclient.util.MessageContext;
import com.xinli.portalclient.util.PrefUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

/*
* 
* Draw a bitmap
* * */
@SuppressLint({ "NewApi" })
public class MainActivity extends BaseActivity {
  public static final int CHECK_DISCONNECT = 2;
  public static final int DISCOVER = 1;
  protected static final int DOWN_ERROR = 2;
  private static final int GET_UNDATAINFO_ERROR = Toast.LENGTH_LONG;
  public static final int INIT_RESET_WIFI = 3;
  protected static final String TAG = "MainActivity";
  private static final int UPDATA_CLIENT = Toast.LENGTH_SHORT;
  private static final int UPDATA_CLIENT_FORCE = 4;
  ArrayAdapter<String> _Adapter;
  Handler authHandler;
  private SharedPreferences defaultAccFile;
  private MyAdapter dropDownAdapter;
  ImageView errView;
  LinearLayout gestureLayout;
  static Handler handler;
  Handler handlerUpdate;
  private MySurfaceView imageView;
  private UpdataInfo info;
  private String ipConfig;
  private String keyvalue;
  private List list1;
  private List list2;
  private SharedPreferences loginFile;
  private int mConnMethod;
  private ProgressDialog mDialog;
  private String mPassword;
  private String mSsid;
  private WifiInfo mWifiInfo;
  private WifiManager mWifiManager;
  private WifiManagerUtil mWifiUtil;
  private ImageView moreSelect;
  public String password;
  private int picHeight;
  private int picWidth;
  private PopupWindow popView;
  BroadcastReceiver receiver;
  RequestModel reqResource;
  String[] resultArray;
  CheckBox savePasswordCB;
  private int screenHeight;
  private int screenWidth;
  private String sessionId;
  private SharedPreferences sp;
  public EditText username;
  private String usernametext;

  /*
  * Check for update
  * */
  class AnonymousClass_14 extends Thread {
    private final /* synthetic */ ProgressDialog val$pd;

    AnonymousClass_14(ProgressDialog progressDialog) {
      this.val$pd = progressDialog;
    }

    public void run() {
      try {

        File file = MainActivity.getFileFromServer(info.getUrl(), this.val$pd);
        sleep(3000);
        installApk(file);
        this.val$pd.dismiss();
      } catch (Exception e) {

        Message msg = new Message();
        msg.what = 2;
        handlerUpdate.sendMessage(msg);
        e.printStackTrace();
        this.val$pd.cancel();
      }
    }
  }

  //disconnect network
  class AnonymousClass_5 implements OnClickListener {
    private final /* synthetic */ EditText val$edtInput;

    AnonymousClass_5(EditText editText) {
      this.val$edtInput = editText;
    }

    public void onClick(DialogInterface dialog, int whichButton) {
      mPassword = this.val$edtInput.getText().toString().trim();
      mWifiManager.disableNetwork(mWifiInfo.getNetworkId());
      mWifiManager.disconnect();
      handler.sendEmptyMessage(DOWN_ERROR);
      dialog.dismiss();
    }
  }

  //clear editText
  class AnonymousClass_6 implements OnClickListener {
    private final /* synthetic */ EditText val$edtInput;

    AnonymousClass_6(EditText editText) {
      this.val$edtInput = editText;
    }

    public void onClick(DialogInterface dialog, int whichButton) {
      this.val$edtInput.setText("");
    }
  }

  //check for update apk
  // Config.realUrl + /app/version.xml
  public class CheckVersionTask implements Runnable {
    boolean force;

    public CheckVersionTask(boolean force) {
      this.force = false;
      this.force = force;
    }

    public void run() {
      Message msg;
      try {
        URL url = new URL(
            new StringBuilder(String.valueOf(Config.realUrl)).append("/apps/version.xml")
                .toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(BitmapUtils.REQUEST_TIMEOUT);
        info = MainActivity.getUpdataInfo(conn.getInputStream());
        //no need to update
        if (info.getVersion().equals(getVersionName())) {
          Log.d(TAG, "版本号相同无需升级");
          return;
        }
      } catch (Exception e) {
        msg = new Message();
        msg.what = 1;
        handlerUpdate.sendMessage(msg);
        e.printStackTrace();
      }
    }
  }

  class LoginThread implements Runnable {

    //private int picWidth;
    //private int picHeight;
    //private String keyvalue;
    //private List list1;
    //private List list2;
    //private String usernametext;
    //private String password;
    //private String ipaddr;

    private String endpoint;
    private String session;
    List<NameValuePair> postparas;

    LoginThread(String endpoint, String session, List<NameValuePair> postparas) {
      this.endpoint = endpoint;
      this.session = session;
      this.postparas = postparas;
    }

    @Override
    public void run() {
      Log.d(TAG, "LoginThread run");
      if (usernametext.isEmpty() || password.isEmpty()) {
        Log.e(TAG, "passwd or username is empty");
        return;
      }
      Message msg = handler.obtainMessage();
      Bundle bundle = new Bundle();

      String result = HttpUtils.sendContentByHttpPost(
          //host address
          endpoint + "/wf.do?code=8",
          //sessionId
          sessionId,
          //List<String> paras
          postparas);

      resultArray = result.split("#");
      list1.clear();
      list2.clear();
      Log.v(TAG, "result = " + resultArray[0].toString());
      if (ReturnMessage.AUTH_TRUE.equals(resultArray[0])) {
        msg.what = 0;
        PrefUtils prefUtils = new PrefUtils(MainActivity.this);
        if (resultArray.length < 6) {
          prefUtils.setVerifyURL("");
        } else if (resultArray[5].split("=").length > 1) {
          prefUtils.setVerifyURL(resultArray[5].split("=")[1]);
        }
        Log.i(TAG,
            new StringBuilder("LoginThread=======verifyURL==").append(prefUtils.getVerifyURL())
                .toString());
        msg.setData(bundle);
        authHandler.sendMessage(msg);
      }
      if (ReturnMessage.AUTH_FALSE.equals(resultArray[0])) {
        msg.what = 1;
        bundle.putString("resultDesc", resultArray[1]);
      } else if (ReturnMessage.KEY_FALSE.equals(resultArray[0])) {
        msg.what = 2;
        bundle.putString("resultDesc",
            "\u624b\u52bf\u5df2\u8fc7\u671f\uff0c\u8bf7\u91cd\u65b0\u64cd\u4f5c");
      } else if (ReturnMessage.PIC_FALSE.equals(resultArray[0])) {
        bundle.putString("resultDesc", "\u624b\u52bf\u9a8c\u8bc1\u5931\u8d25");
        msg.what = 3;
      } else {
        bundle.putString("resultDesc", "\u8ba4\u8bc1\u7ed3\u679c\u7801\u5f02\u5e38");
        msg.what = 100;
      }
      msg.setData(bundle);
      authHandler.sendMessage(msg);
    }
  }

  class MyAdapter extends SimpleAdapter {
    private List<HashMap<String, Object>> data;

    class AnonymousClass_1 implements View.OnClickListener {
      private final /* synthetic */ int val$position;

      AnonymousClass_1(int i) {
        this.val$position = i;
      }

      public void onClick(View v) {

        Log.d("点击下拉框", this.val$position + "");
        //mock password
        usernametext = "123_MyAdapter";
        password = "passwd_MyAdapter";
        username.setText(usernametext);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(usernametext, password);
        editor.apply();

        Log.d("username=", usernametext);
        popView.dismiss();
      }
    }

    public MyAdapter(Context context, List<HashMap<String, Object>> data, int resource,
        String[] from, int[] to) {
      super(context, data, resource, from, to);
      this.data = data;
    }

    public int getCount() {
      return this.data.size();
    }

    public Object getItem(int position) {
      return position;
    }

    public long getItemId(int position) {
      return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
      ViewHolder holder;
      if (convertView == null) {
        holder = new ViewHolder();
        convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dropdown_item, null);
        holder.tv = (TextView) convertView.findViewById(R.id.textview);
        convertView.setTag(holder);
      } else {
        holder = (ViewHolder) convertView.getTag();
      }
      holder.tv.setText(((HashMap) this.data.get(position)).get("name").toString());
      holder.tv.setOnClickListener(new AnonymousClass_1(position));
      return convertView;
    }
  }

  class ViewHolder {
    private TextView tv;

    ViewHolder() {
    }
  }

  public MainActivity() {
    this.handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        Log.d(TAG, "handler:msg.what = " + msg.what);
        switch (msg.what) {
          //called when last surfaceview touch left
          case UPDATA_CLIENT:
            list1 = msg.getData().getParcelableArrayList("list1");
            list2 = msg.getData().getParcelableArrayList("list2");
            picWidth = msg.getData().getInt("picwidth");
            picHeight = msg.getData().getInt("picheight");
            mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setTitle("\u767b\u5f55");
            mDialog.setMessage("正在登录服务器，请稍后...");
            mDialog.show();
            List<NameValuePair> postparas =
                MessageContext.paramStrReturn(picWidth, picHeight, list1, list2, keyvalue,
                    usernametext, password, intToIp(getLocalIpAddress()));

            new Thread(new LoginThread(Config.realUrl, sessionId, postparas)).start();
            break;
          case GET_UNDATAINFO_ERROR:
            sendDiscover();
            break;
          case DOWN_ERROR:
            if (wifiConnectState()) {
              handler.sendEmptyMessageDelayed(DOWN_ERROR, 1000);
              return;
            }
            handler.removeMessages(DOWN_ERROR);
            mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setTitle("\u767b\u5f55\u5237\u65b0");
            mDialog.setMessage("正在连接服务器，请稍后...");
            mDialog.show();
            mWifiUtil.connectToTargetWifi(mSsid, mPassword, mConnMethod);
            handler.sendEmptyMessage(GET_UNDATAINFO_ERROR);
            break;
          case INIT_RESET_WIFI:
            showDialog();
            break;
          default:
            break;
        }
      }
    };

    this.authHandler = new Handler() {
      public void handleMessage(Message msg) {
        String resultText = msg.getData().getString("resultDesc");
        Log.d(TAG, "authHandler:msg.what = " + msg.what);
        switch (msg.what) {
          case UPDATA_CLIENT:
            mDialog.cancel();
            SucessActivity.userInfo = new RequestModel(usernametext, sessionId);
            Editor editor = loginFile.edit();
            editor.putString(Config.USERNAME, usernametext);
            editor.putString(Config.SESSIONID, sessionId);
            if (!(Config.realUrl == null
                || Config.realUrl.isEmpty()
                || Config.firstRreqUrl.equalsIgnoreCase(Config.realUrl))) {

              editor.putString(Config.REDIRECTINFO, Config.realUrl);
              editor.commit();
            }

            //start SucessActivity
            //Intent intent =
            //    new Intent(getApplicationContext(), SucessActivity.class);
            //intent.putExtra("loginTime", System.currentTimeMillis());
            //intent.putExtra("authFlag", true);
            //startActivity(intent);
            break;

          case GET_UNDATAINFO_ERROR:
            mDialog.cancel();
            Toast.makeText(getApplicationContext(), resultText, UPDATA_CLIENT).show();
            break;

          //error == 2
          case DOWN_ERROR:
            Log.d(TAG, "挑战码过期,重新获取图片和key");
            try {
              if (isPrivateIpAdress()) {
                Log.e(TAG, "isPrivateIpAdress");
                return;
              }
              RequestModel reset =
                  BitmapUtils.getKey(getVersionName(), intToIp(getLocalIpAddress()));
              if (ReturnMessage.NATCHECK.equals(reset.getMessage())) {
                mDialog.cancel();
                Toast.makeText(getApplicationContext(),
                    "\u7f51\u7edc\u5f02\u5e38\uff0c\u8bf7\u68c0\u67e5\u7f51\u7edc\uff01",
                    UPDATA_CLIENT).show();
                exceptionView();
              } else if (ReturnMessage.VERSIONCHECK.equals(reset.getMessage())) {
                Toast.makeText(getApplicationContext(),
                    "\u60a8\u7684\u7248\u672c\u8fc7\u4f4e\uff0c\u8bf7\u6309\u63d0\u793a\u8fdb\u884c\u66f4\u65b0\uff01",
                    GET_UNDATAINFO_ERROR).show();
                new Thread(new CheckVersionTask(true)).start();
                mDialog.cancel();
              } else {
                imageView.setSessionId(reset.getSessionId());
                imageView.setKeyvalue(reset.getKeyvalue());
                sessionId = reset.getSessionId();
                keyvalue = reset.getKeyvalue();
                imageView.bitmap =
                    BitmapUtils.getPicture(reset.getSessionId(), screenWidth, screenHeight)
                        .getBitmap();

                reqResource.setSessionId(imageView.getSessionId());
                reqResource.setKeyvalue(imageView.getKeyvalue());
                reqResource.setBitmap(imageView.bitmap);
                mDialog.cancel();
                Toast.makeText(getApplicationContext(), resultText, UPDATA_CLIENT).show();
              }
            } catch (Exception e) {

              mDialog.cancel();
              Toast.makeText(getApplicationContext(),
                  "\u83b7\u53d6\u624b\u52bf\u5931\u8d25\uff0c\u8bf7\u70b9\u51fb\u6309\u94ae\u91cd\u65b0\u8bf7\u6c42\u624b\u52bf",
                  UPDATA_CLIENT).show();
            }
            break;
          case INIT_RESET_WIFI:
            mDialog.cancel();
            Toast.makeText(getApplicationContext(), resultText, UPDATA_CLIENT).show();
            break;
          case UPDATA_CLIENT_FORCE:
            mDialog.cancel();
            exceptionView();
            Toast.makeText(getApplicationContext(), resultText, UPDATA_CLIENT).show();
            break;
          case 100:
            mDialog.cancel();
            Toast.makeText(getApplicationContext(), resultText, UPDATA_CLIENT).show();
            break;
          default:
            break;
        }
      }
    };

    this.receiver = new BroadcastReceiver() {
      public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "intent.getAction().equals(\"mysurface\")");
        if (intent.getAction().equals("mysurface")) {
          //Intent mainIntent = new Intent(context, SucessActivity.class);
          //intent.putExtra("authFlag", true);
          //context.startActivity(mainIntent);
          //finish();
        }
      }
    };

    this.handlerUpdate = new Handler() {
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
          case UPDATA_CLIENT:
            showUpdataDialog(false);
          case DOWN_ERROR:
            Toast.makeText(getApplicationContext(), "\u4e0b\u8f7d\u65b0\u7248\u672c\u5931\u8d25",
                GET_UNDATAINFO_ERROR).show();
          case UPDATA_CLIENT_FORCE:
            showUpdataDialog(true);
          default:
            break;
        }
      }
    };
  }

  private void showDialog() {
    Log.v(TAG, "showDialog");
    this.mWifiUtil = new WifiManagerUtil(this, this.logger);
    this.mWifiUtil.getWifiInfo();
    this.mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    this.mWifiInfo = this.mWifiManager.getConnectionInfo();
    this.mSsid = this.mWifiInfo.getSSID().trim();
    this.mSsid = trim(this.mSsid);
    this.mConnMethod = 1;
    View textEntryView = LayoutInflater.from(this).inflate(R.layout.dialoglayout, null);
    EditText edtInput = (EditText) textEntryView.findViewById(R.id.edtInput);
    Builder builder = new Builder(this);
    builder.setCancelable(false);
    builder.setTitle("\u8f93\u5165WIFI\u5bc6\u7801");
    builder.setMessage(this.mSsid);
    builder.setView(textEntryView);
    builder.setPositiveButton("\u786e\u5b9a", new AnonymousClass_5(edtInput));
    builder.setNeutralButton("\u53d6\u6d88", new AnonymousClass_6(edtInput));
    builder.show();

  }

  private String getWifiMacAddress() {
    try {
      WifiInfo info = ((WifiManager) getSystemService(WIFI_SERVICE)).getConnectionInfo();
      String mac = "";
      if (info == null) {
        return mac;
      }
      mac = info.getMacAddress().replaceAll(":", SimpleFormatter.DEFAULT_DELIMITER);

      return mac;
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  private void sendDiscover() {
    Log.d(TAG, "sendDiscover");
    if (!wifiConnectState()) {
      handler.sendEmptyMessageDelayed(1, 1000L);
      return;
    }
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new BasicNameValuePair("username", usernametext));
    try {
      String str = HttpUtils.sendContentByHttpPost(Config.realUrl + "/wf.do?code=9", sessionId,
          localArrayList);

      if ("discover00".equals(str)) {
        SucessActivity.userInfo = new RequestModel(usernametext, sessionId);
        SharedPreferences.Editor localEditor = loginFile.edit();
        localEditor.putString("username", usernametext);
        localEditor.putString("sessionId", sessionId);
        if ((Config.realUrl != null)
            && (!Config.realUrl.isEmpty())
            && (!Config.firstRreqUrl.equalsIgnoreCase(Config.realUrl))) {
          localEditor.putString(Config.REDIRECTINFO, Config.realUrl);
          localEditor.commit();
        }
        //Intent localIntent = new Intent(getApplicationContext(), SucessActivity.class);
        //localIntent.putExtra("loginTime", System.currentTimeMillis());
        //localIntent.putExtra("authFlag", true);
        //startActivity(localIntent);
        return;
      }
    } catch (Exception localException) {

      return;
    }
    handler.sendEmptyMessageDelayed(1, 1000L);
  }

  private boolean wifiConnectState() {
    NetworkInfo wifiInfo =
        ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getNetworkInfo(
            GET_UNDATAINFO_ERROR);
    if (wifiInfo == null || !wifiInfo.isConnected() || wifiInfo.getState() != State.CONNECTED) {
      return false;
    }
    return true;
  }

  public String trim(String str) {
    int len = str.length();
    int st = UPDATA_CLIENT;
    char[] val = str.toCharArray();
    while (st < len && val[0 + st] <= '\"') {
      st++;
    }
    while (st < len && val[(0 + len) - 1] <= '\"') {
      len--;
    }
    return (st > 0 || len < str.length()) ? str.substring(st, len) : str;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    IntentFilter filter = new IntentFilter();
    filter.addAction("mysurface");
    registerReceiver(this.receiver, filter);
    if (((double) Float.valueOf(VERSION.RELEASE.substring(UPDATA_CLIENT, INIT_RESET_WIFI).trim())
        .floatValue()) > 2.3d) {
      StrictMode.setThreadPolicy(new ThreadPolicy.Builder().detectDiskReads()
          .detectDiskWrites()
          .detectNetwork()
          .permitNetwork()
          .penaltyLog()
          .build());
    }
    requestWindowFeature(GET_UNDATAINFO_ERROR);
    super.onCreate(savedInstanceState);

    if (activityList.size() > 1) {

      Log.d(TAG, "activityList.size>1");
      //Intent intent = new Intent();
      //intent.setClass(this, SucessActivity.class);
      //intent.putExtra("authFlag", true);
      //startActivity(intent);
      //finish();
      return;
    }
    setContentView(R.layout.activity_main);
    this.gestureLayout = (LinearLayout) findViewById(R.id.auth_pic_layout);
    DisplayMetrics displayMetrics = new DisplayMetrics();
    displayMetrics = getResources().getDisplayMetrics();
    this.screenWidth = displayMetrics.widthPixels;
    this.screenHeight = displayMetrics.heightPixels;
    this.username = (EditText) findViewById(R.id.username);
    this.defaultAccFile = getSharedPreferences(SetAccountActivity.DEFAULT_ACCOUNT, UPDATA_CLIENT);
    this.sp = getSharedPreferences("passwordFile", UPDATA_CLIENT);
    this.loginFile = getSharedPreferences(Config.LOGININFOFILE, UPDATA_CLIENT);
    initLoginUserName();
    ImageView accountSeting = (ImageView) findViewById(R.id.account_setting);
    accountSeting.setBackgroundResource(R.drawable.usermbk);
    accountSeting.setOnTouchListener(new OnTouchListener() {
      public boolean onTouch(View arg0, MotionEvent arg1) {
        if (arg1.getAction() == 0) {
          arg0.setBackgroundResource(R.drawable.usermbk_down);
        } else if (arg1.getAction() == 1) {
          arg0.setBackgroundResource(R.drawable.usermbk);
        }
        return false;
      }
    });
    accountSeting.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SetAccountActivity.class);
        startActivity(intent);
        finish();
      }
    });
    ((ImageView) findViewById(R.id.moreSelect)).setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        switch (v.getId()) {
          case R.id.moreSelect:
            if (popView == null) {
              String[] mItems = (String[]) sp.getAll().keySet().toArray(new String[0]);
              if (mItems.length > 0) {
                initPopView(mItems);
                if (popView.isShowing()) {
                  popView.dismiss();
                  return;
                } else {
                  popView.showAsDropDown(username);
                  return;
                }
              }
              Intent intent = new Intent();
              intent.setClass(MainActivity.this, SetAccountActivity.class);
              startActivity(intent);
              finish();
              Toast.makeText(getApplicationContext(), "\u8bf7\u5148\u6dfb\u52a0\u5e10\u53f7",
                  UPDATA_CLIENT).show();
            } else if (popView.isShowing()) {
              popView.dismiss();
            } else {
              popView.showAsDropDown(username);
            }
          default:
            break;
        }
      }
    });
    ImageView refreshText = (ImageView) findViewById(R.id.refresh_pic);
    refreshText.setBackgroundResource(R.drawable.refresh);
    refreshText.setOnTouchListener(new OnTouchListener() {
      public boolean onTouch(View arg0, MotionEvent arg1) {
        if (arg1.getAction() == 0) {
          arg0.setBackgroundResource(R.drawable.refresh_down);
        } else if (arg1.getAction() == 1) {
          arg0.setBackgroundResource(R.drawable.refresh);
        }
        return false;
      }
    });
    refreshText.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {

        if (!CheckNetworkState()) {
          shoWifiUnavaialableDialog();
          return;
        }

        if (isPrivateIpAdress()) {
          Log.e(TAG, "isPrivateIpAdress");
          return;
        }

        if (imageView == null) {
          Log.e(TAG, "imageView == null");
          Log.d(TAG, "refresh image and key");
          reqResource = getAuthPicAndKey();
          keyvalue = reqResource.getKeyvalue();
          Log.d(TAG, "refresh image and key done");
          return;
        } else {
          Log.d(TAG, "imageView != null");
          Log.d(TAG, "refresh image only!");
          imageView.bitmap =
              BitmapUtils.getPicture(reqResource.getSessionId(), screenWidth, screenHeight)
                  .getBitmap();
          Log.d(TAG, "refresh image done");
          return;
        }
      }
    });

    if (!CheckNetworkState()) {
      exceptionView();
      shoWifiUnavaialableDialog();
    } else if (isPrivateIpAdress()) {
      exceptionView();
      Toast.makeText(getApplicationContext(), "网络异常，请检查网络！", UPDATA_CLIENT).show();
    } else {
      //start http 
      try {
        if (!BitmapUtils.isHaveinitRealAddress(Config.firstRreqUrl)) {
          Log.d(TAG, "!BitmapUtils.isHaveinitRealAddress(Config.firstRreqUrl))");

          //Intent intent = new Intent();
          //intent.setClass(this, SucessActivity.class);
          //intent.putExtra("authFlag", true);
          //startActivity(intent);
          //finish();
          return;
        }

        this.reqResource = getAuthPicAndKey();

        if (!this.reqResource.isForceUpdated()) {
          new Thread(new CheckVersionTask(false)).start();
        }
      } catch (Exception e) {
        exceptionView();
        Toast.makeText(getApplicationContext(),
            "\u7f51\u7edc\u5f02\u5e38\uff0c\u8bf7\u7a0d\u5019\u518d\u8bd5", UPDATA_CLIENT).show();

      }
    }
  }

  //parse update.xml
  public static UpdataInfo getUpdataInfo(InputStream is) throws Exception {
    XmlPullParser parser = Xml.newPullParser();
    parser.setInput(is, "utf-8");
    UpdataInfo info = new UpdataInfo();
    for (int type = parser.getEventType(); type != 1; type = parser.next()) {
      switch (type) {
        case DOWN_ERROR:
          if ("version".equals(parser.getName())) {
            info.setVersion(parser.nextText());
          } else if ("url".equals(parser.getName())) {
            info.setUrl(parser.nextText());
          } else if ("description".equals(parser.getName())) {
            info.setDescription(parser.nextText());
          }
        default:
          break;
      }
    }
    return info;
  }

  //update apk from server

  public static File getFileFromServer(String path, ProgressDialog pd) throws Exception {
    if (!Environment.getExternalStorageState().equals("mounted")) {
      return null;
    }
    HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
    conn.setConnectTimeout(BitmapUtils.REQUEST_TIMEOUT);
    pd.setMax(conn.getContentLength());
    InputStream is = conn.getInputStream();
    File file = new File(Environment.getExternalStorageDirectory(), "portal_client_update.apk");
    FileOutputStream fos = new FileOutputStream(file);
    BufferedInputStream bis = new BufferedInputStream(is);
    byte[] buffer = new byte[1024];
    int total = UPDATA_CLIENT;
    while (true) {
      int len = bis.read(buffer);
      if (len == -1) {
        fos.close();
        bis.close();
        is.close();
        return file;
      }
      fos.write(buffer, UPDATA_CLIENT, len);
      total += len;
      pd.setProgress(total);
    }
  }

  protected void showUpdataDialog(boolean force) {
    Builder builer = new Builder(this);
    builer.setTitle("\u7248\u672c\u5347\u7ea7");
    try {
      builer.setMessage(new String(this.info.getDescription().getBytes(), "UTF-8"));
      builer.setPositiveButton("\u786e\u5b9a", new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {

          downLoadApk();
        }
      });

      if (!force) {

        builer.setNegativeButton("\u53d6\u6d88", new OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            Process.killProcess(Process.myPid());
          }
        });
      }
      AlertDialog dialog = builer.create();
      dialog.setCanceledOnTouchOutside(false);
      dialog.show();
    } catch (UnsupportedEncodingException e) {

    }
  }

  protected void downLoadApk() {
    ProgressDialog pd = new ProgressDialog(this);
    pd.setProgressStyle(GET_UNDATAINFO_ERROR);
    pd.setMessage("\u6b63\u5728\u4e0b\u8f7d\u66f4\u65b0");
    pd.show();
    new AnonymousClass_14(pd).start();
  }

  protected void installApk(File file) {
    Intent intent = new Intent();
    intent.addFlags(268435456);
    intent.setAction("android.intent.action.VIEW");
    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
    startActivity(intent);
  }

  private void initLoginUserName() {
    Log.d(TAG, "initLoginUserName");
    this.usernametext = "usernametext";
    this.password = "usernamepassword";
  }

  private void initPopView(String[] usernames) {
    List<HashMap<String, Object>> list = new ArrayList();
    for (int i = UPDATA_CLIENT; i < usernames.length; i++) {
      HashMap<String, Object> map = new HashMap();
      map.put("name", usernames[i]);
      list.add(map);
    }
    this.dropDownAdapter =
        new MyAdapter(this, list, 2130903043, new String[] { "name" }, new int[] { 2131296269 });
    ListView listView = new ListView(this);
    listView.setAdapter(this.dropDownAdapter);
    this.popView = new PopupWindow(listView, this.username.getWidth(), -2, false);
    this.popView.setBackgroundDrawable(getResources().getDrawable(R.drawable.white));
  }

  private RequestModel getAuthPicAndKey() {

    Log.v(TAG, "getAuthPicAndKey");
    if (isPrivateIpAdress()) {

      exceptionView();
      Log.e(TAG, "isPrivateIpAdress");
      return null;
    }

    this.gestureLayout.removeView(this.errView);
    try {
      RequestModel requestResult =
          BitmapUtils.requestBeforeLogin(Config.firstRreqUrl, this.screenWidth, this.screenHeight,
              getVersionName(), intToIp(getLocalIpAddress()));

      if (ReturnMessage.NATCHECK.equals(requestResult.getMessage())) {
        exceptionView();
      } else if (ReturnMessage.VERSIONCHECK.equals(requestResult.getMessage())) {
        Toast.makeText(getApplicationContext(), "update available!", GET_UNDATAINFO_ERROR).show();
        new Thread(new CheckVersionTask(true)).start();
        requestResult.setForceUpdated(true);
      } else {
        this.imageView = new MySurfaceView(this, requestResult.getBitmap(), this.sp, this.handler);
        LayoutParams params = new LayoutParams(-2, -2);
        this.imageView.setSessionId(requestResult.getSessionId());
        this.imageView.setFocusableInTouchMode(true);
        this.imageView.setKeyvalue(requestResult.getKeyvalue());
        this.gestureLayout.addView(this.imageView, params);
        this.sessionId = requestResult.getSessionId();
        this.keyvalue = requestResult.getKeyvalue();
        Log.d(TAG,
            "requestResult " + requestResult.getKeyvalue() + "/" + requestResult.getSessionId());
      }
      return requestResult;
    } catch (Exception e) {
      exceptionView();

      Toast.makeText(getApplicationContext(),
          "\u83b7\u53d6\u624b\u52bf\u5931\u8d25\uff0c\u8bf7\u68c0\u67e5\u7f51\u7edc\u6216\u7a0d\u5019\u518d\u8bd5",
          UPDATA_CLIENT).show();
      return null;
    }
  }

  private void exceptionView() {
    try {
      if (this.imageView != null) {
        this.gestureLayout.removeView(this.imageView);
      }
      this.errView = new ImageView(this);
      LayoutParams params = new LayoutParams(-2, -2);
      this.errView.setBackgroundResource(R.drawable.gesture_default);
      this.gestureLayout.addView(this.errView, params);
    } catch (Exception e) {
    }
  }

  public boolean CheckNetworkState() {
    State wifi = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getNetworkInfo(
        GET_UNDATAINFO_ERROR).getState();
    return wifi == State.CONNECTED || wifi == State.CONNECTING;
  }

  private void shoWifiUnavaialableDialog() {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
    localBuilder.setIcon(17301543);
    localBuilder.setTitle("没有可用Wifi网络");
    localBuilder.setMessage("当前Wifi网络不可用，是否设置Wifi网络？");
    localBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
        startActivity(new Intent("android.settings.WIFI_SETTINGS"));
      }
    });
    localBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
        paramAnonymousDialogInterface.cancel();
        finish();
      }
    });
    localBuilder.create();
    localBuilder.show();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(this.receiver);
  }

  private boolean isPrivateIpAdress() {
    boolean result;
    int netaddr = ntol(getLocalIpAddress());
    if (netaddr >= -1062731776 && netaddr <= -1062666241) {
      result = true;
    } else if (netaddr >= 167772160 && netaddr <= 184549375) {
      result = true;
    } else if (netaddr < -1408237568 || netaddr > -1400635393) {
      result = false;
    } else {
      result = true;
    }
    //mock 
    //return result;
    return false;
  }
}
