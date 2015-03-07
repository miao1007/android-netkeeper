package com.xinli.portalclient.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class PrefUtils {
    private static final String TAG = "ShareUtil";
    Context mContext;
    private SharedPreferences sharedVerifyURL;

    public PrefUtils(Context c) {
        Log.d(TAG, " ShareUtil...");
        this.mContext = c;
    }

    public void setVerifyURL(String verifyURl) {
        try {
            this.sharedVerifyURL = this.mContext.getSharedPreferences("VERIFY_URL", 1);
            Editor edit = this.sharedVerifyURL.edit();
            edit.putString("verifyURl", verifyURl);
            edit.commit();
            Log.i(TAG, new StringBuilder("sharedCheckVerifyURL=====verifyURl==").append(verifyURl).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getVerifyURL() {
        try {
            Log.i(TAG, "sharedCheckVerifyURL======");
            this.sharedVerifyURL = this.mContext.getSharedPreferences("VERIFY_URL", 1);
            return this.sharedVerifyURL.getString("verifyURl", "");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
