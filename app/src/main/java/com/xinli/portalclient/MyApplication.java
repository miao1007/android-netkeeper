package com.xinli.portalclient;

import android.app.Application;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {
    protected final transient Logger logger;
    public Map<String, Object> myData;

    public MyApplication() {
        this.logger = LoggerFactory.getLogger(MyApplication.class);
        this.myData = new HashMap();
    }

    public Map<String, Object> getMyData() {
        return this.myData;
    }

    public void onCreate() {
        super.onCreate();
        this.logger.info("\u9352\u6d98\u7f13application");
    }

    public void onTerminate() {
        super.onTerminate();
    }
}
