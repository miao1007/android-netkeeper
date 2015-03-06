package com.xinli.portalclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class WELCOMEActivity extends BaseActivity {
  private final int SPLASH_DISPLAY_LENGHT;
  private ImageView image;

  public WELCOMEActivity() {
    this.SPLASH_DISPLAY_LENGHT = 5000;
    this.image = null;
  }

  public void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT,
        AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.welcome);
    String version = null;
    this.logger.debug("onCreate ====step1========");
    try {
      version = getVersionName();
    } catch (Exception e) {
      Log.e("\u6b22\u8fce\u9875\u9762\u83b7\u53d6\u7248\u672c\u53f7\u53d1\u751f\u5f02\u5e38",
          e.getMessage());
      this.logger.error(new StringBuilder(
          "\u6b22\u8fce\u9875\u9762\u83b7\u53d6\u7248\u672c\u53f7\u53d1\u751f\u5f02\u5e38").append(
          e.getMessage()).toString());
      e.printStackTrace();
    }
    this.logger.debug("onCreate ==step2==========");
    if (version != null) {
      ((TextView) findViewById(R.id.version)).setText(
          new StringBuilder("V ").append(version).toString());
    }
    //The progressbar is fake!!!!
    new Handler().postDelayed(new Runnable() {
      public void run() {
        WELCOMEActivity.this.startActivity(new Intent(WELCOMEActivity.this, MainActivity.class));
        WELCOMEActivity.this.finish();
      }
    }, 5000);
  }
}
