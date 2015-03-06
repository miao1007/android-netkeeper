package com.xinli.portalclient.util;

public class Config {
  public static final String KEYVALUE = "keyValue";
  public static final String LOGININFOFILE = "loginInfo";
  public static String REDIRECTINFO = null;
  public static final String SESSIONID = "sessionId";
  public static final String USERNAME = "username";
  public static int VERIFYCODE_AUTH_SORT;
  public static String VERIFYCODE_AUTH_URL;
  public static String VERIFYCODE_SWITCH;
  public static String firstRreqUrl;
  //this is  Http redirect home page
  //http://gxprotal.online.cq.cn:8080/
  public static String realUrl;

  static {
    REDIRECTINFO = "redirectInfo";
    firstRreqUrl = "http://www.189.cn";
    VERIFYCODE_AUTH_URL = "117.34.65.39";
    VERIFYCODE_AUTH_SORT = 8099;
    VERIFYCODE_SWITCH = "OFF";
  }
}
