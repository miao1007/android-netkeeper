package com.xinli.portalclient;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.google.code.microlog4android.config.PropertyConfigurator;
import java.util.ArrayList;

public class BaseActivity extends Activity {
  public static final String TAG = BaseActivity.class.getSimpleName();
  protected static ArrayList<Activity> activityList;
  protected final transient Logger logger;
  WifiManager wifiManager;

  public BaseActivity() {
    this.logger = LoggerFactory.getLogger(getClass());
  }

  static {
    activityList = new ArrayList();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    PropertyConfigurator.getConfigurator(this).configure();
    activityList.add(this);
    Log.i(TAG,"activityList count = " + activityList.size() );
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    activityList.remove(this);
  }

  @Override
  public void finish() {
    super.finish();
    if (activityList.contains(this)) {
      activityList.remove(this);
    }
  }

  public String getVersionName() {

    try {
      PackageManager packageManager = getPackageManager();
      PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
      return packInfo.versionName;
    } catch (Exception e) {
      e.printStackTrace();
      return "unknown";
    }
  }

  public int getLocalIpAddress() {

    int ipAddress = this.wifiManager.getConnectionInfo().getIpAddress();
    Log.d(TAG, "getLocalIpAddress " + intToIp(ipAddress));
    return ipAddress;
  }

  public String intToIp(int i) {
    return new StringBuilder(String.valueOf(i & 255)).append(".")
        .append((i >> 8) & 255)
        .append(".")
        .append((i >> 16) & 255)
        .append(".")
        .append((i >> 24) & 255)
        .toString();
  }

  public static int ntol(int n) {
    return ((((n & 255) << 24) | (((n >> 8) & 255) << 16)) | (((n >> 16) & 255) << 8)) | ((n >> 24)
        & 255);
  }
}
