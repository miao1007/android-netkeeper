package com.xinli.portalclient;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;
import com.xinli.portalclient.model.TouchPoint;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.swing.XMLTableColumnDefinition;

public class MySurfaceView extends SurfaceView implements Callback, Runnable {
  private static final int MAX_TOUCHPOINTS = Toast.LENGTH_LONG;
  protected static final String TAG = "Client";
  public static final int TIME_IN_FRAME = 50;
  Canvas bakmCanvas;
  Bitmap bitmap;
  private int[] colors;
  private String keyvalue;
  final List<TouchPoint> list1;
  final List<TouchPoint> list2;
  Canvas mCanvas;
  private ProgressDialog mDialog;
  boolean mIsRunning;
  Paint mPaint;
  private Path[] mPath;
  boolean mRunning;
  SurfaceHolder mSurfaceHolder;
  private Handler mainHandler;
  MySurfaceView me;
  private float[] mposX;
  private float[] mposY;
  private boolean rememberPasswd;
  final List<ArrayList<TouchPoint>> resultList;
  private String sessionId;
  private SharedPreferences sp;
  private Paint[] touchPaints;

  public String getSessionId() {
    return this.sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getKeyvalue() {
    return this.keyvalue;
  }

  public void setKeyvalue(String keyvalue) {
    this.keyvalue = keyvalue;
  }

  public MySurfaceView(Context context) {
    super(context);
    this.mPaint = null;
    this.touchPaints = new Paint[10];
    this.colors = new int[10];
    this.mPath = new Path[10];
    this.mposX = new float[10];
    this.mposY = new float[10];
    this.mSurfaceHolder = null;
    this.mRunning = false;
    this.mCanvas = null;
    this.bakmCanvas = null;
    this.mIsRunning = false;
    this.list1 = new ArrayList();
    this.list2 = new ArrayList();
    this.resultList = new ArrayList();
    this.me = this;
  }

  public MySurfaceView(Context context, Bitmap bitMap, SharedPreferences sp, Handler mainHandler) {
    super(context);
    this.mPaint = null;
    this.touchPaints = new Paint[10];
    this.colors = new int[10];
    this.mPath = new Path[10];
    this.mposX = new float[10];
    this.mposY = new float[10];
    this.mSurfaceHolder = null;
    this.mRunning = false;
    this.mCanvas = null;
    this.bakmCanvas = null;
    this.mIsRunning = false;
    this.list1 = new ArrayList();
    this.list2 = new ArrayList();
    this.resultList = new ArrayList();
    this.me = this;
    setFocusable(true);
    setFocusableInTouchMode(true);
    this.mSurfaceHolder = getHolder();
    this.mSurfaceHolder.addCallback(this);
    this.mCanvas = new Canvas();
    this.mPaint = new Paint();
    this.mPaint.setColor(-16777216);
    this.bitmap = bitMap;
    init();
    this.sp = sp;
    this.mainHandler = mainHandler;
  }

  private void init() {
    this.colors[0] = -256;
    this.colors[1] = -256;
    this.colors[2] = -16711936;
    this.colors[3] = -256;
    this.colors[4] = -16711681;
    this.colors[5] = -65281;
    this.colors[6] = -12303292;
    this.colors[7] = -1;
    this.colors[8] = -3355444;
    this.colors[9] = -7829368;
    for (int i = 0; i < 10; i++) {
      this.touchPaints[i] = new Paint();
      this.touchPaints[i].setColor(this.colors[i]);
      this.touchPaints[i].setAntiAlias(true);
      this.touchPaints[i].setStyle(Style.STROKE);
      this.touchPaints[i].setStrokeCap(Cap.ROUND);
      this.touchPaints[i].setStrokeWidth(25.0f);
      this.touchPaints[i].setDither(true);
      this.touchPaints[i].setShadowLayer(5.0f, 8.0f, 8.0f, this.colors[i]);
      this.mPath[i] = new Path();
    }
  }

  public boolean onTouchEvent(MotionEvent event) {
    String username = ((MainActivity) getContext()).username.getText().toString();
    String password = this.sp.getString(username, "");
    this.rememberPasswd = false;
    Log.v(TAG, username);
    Log.v(TAG, password);
    if ("".equals(username) || username == null || "".equals(password) || password == null) {
      Toast.makeText(getContext(), "\u8bf7\u5148\u9009\u62e9\u5e10\u53f7", MAX_TOUCHPOINTS).show();
      return false;
    }
    int pointCount = event.getPointerCount();
    int i;
    int y;
    switch (event.getAction() & 255) {
      case XMLTableColumnDefinition.OBJECT_TYPE:
        Log.v(TAG, "\u7b2c\u4e00\u4e2a\u70b9\u6309\u4e0b\u89e6\u6478\u5c4f");
        for (i = 0; i < pointCount; i++) {
          y = (int) event.getY(i);
          Log.v(TAG, new StringBuilder("point(").append(i + 1)
              .append(") action_down:(")
              .append((int) event.getX(i))
              .append(",")
              .append(y)
              .append(")")
              .toString());
        }
        this.mPath[event.getPointerId(event.getPointerCount() - 1)].moveTo(event.getX(),
            event.getY());
        this.mposX[event.getPointerId(event.getPointerCount() - 1)] = event.getX();
        this.mposY[event.getPointerId(event.getPointerCount() - 1)] = event.getY();
        break;
      case XMLTableColumnDefinition.STRING_TYPE:
        Log.v(TAG, "\u6700\u540e\u4e00\u4e2a\u70b9\u79bb\u5f00\u89e6\u6478\u5c4f");
        for (i = 0; i < pointCount; i++) {
          y = (int) event.getY(i);
          Log.v(TAG, new StringBuilder("point(").append(i + 1)
              .append(") ACTION_UP:(")
              .append((int) event.getX(i))
              .append(",")
              .append(y)
              .append(")")
              .toString());
        }
        Log.w("\u753b\u56fe\u5b8c\u6bd5===",
            new StringBuilder(String.valueOf(getWidth())).append("\u00d7")
                .append(getHeight())
                .toString());
        Message msg = this.mainHandler.obtainMessage();
        if (this.list1.size() == 0 && this.list2.size() == 0) {
          this.me.setFocusableInTouchMode(false);
          Builder builder = new Builder(getContext());
          builder.setTitle("\u6d88\u606f\u63d0\u793a");
          builder.setMessage("\u89e6\u5c4f\u4e0d\u5408\u6cd5\uff01");
          builder.create().show();
        } else {
          Log.w("\u753b\u56fe\u5b8c\u6bd5=2==",
              new StringBuilder(String.valueOf(getWidth())).append("\u00d7")
                  .append(getHeight())
                  .toString());
          msg.what = 0;
          Bundle bundle = new Bundle();
          bundle.putParcelableArrayList("list1", (ArrayList) this.list1);
          bundle.putParcelableArrayList("list2", (ArrayList) this.list2);
          bundle.putInt("picwidth", getWidth());
          bundle.putInt("picheight", getHeight());
          Log.w("\u753b\u56fe\u5b8c\u6bd5=3==",
              new StringBuilder(String.valueOf(getWidth())).append("\u00d7")
                  .append(getHeight())
                  .toString());
          msg.setData(bundle);
          this.mainHandler.sendMessage(msg);
        }
        for (i = 0; i < 10; i++) {
          this.mPath[i].reset();
        }
        break;
      case XMLTableColumnDefinition.NUMBER_TYPE:
        Log.v(TAG, new StringBuilder("\u6709").append(pointCount)
            .append("\u4e2a\u70b9\u540c\u65f6\u79fb\u52a8")
            .toString());
        for (i = 0; i < pointCount; i++) {
          int x = (int) event.getX(i);
          y = (int) event.getY(i);
          TouchPoint tp = new TouchPoint();
          tp.setX(x);
          tp.setY(y);
          tp.setEventTime(event.getEventTime());
          TouchPoint touchPoint = new TouchPoint();
          if (i == 0) {
            if (this.list1.size() != 0) {
              touchPoint = this.list1.get(this.list1.size() - 1);
              if (((tp.getX() - touchPoint.getX()) * (tp.getX() - touchPoint.getX())) + ((tp.getY()
                  - touchPoint.getY()) * (tp.getY() - touchPoint.getY())) > 16) {
                this.list1.add(tp);
                Log.v(TAG,
                    "------------\u79fb\u52a8\u8ddd\u79bb\u8d85\u8fc7\u9884\u8bbe\u8ddd\u79bb\uff0c\u52a0\u5165list1");
              }
            } else {
              this.list1.add(tp);
            }
          } else if (i == 1) {
            if (this.list2.size() != 0) {
              touchPoint = this.list2.get(this.list2.size() - 1);
              if (((tp.getX() - touchPoint.getX()) * (tp.getX() - touchPoint.getX())) + ((tp.getY()
                  - touchPoint.getY()) * (tp.getY() - touchPoint.getY())) > 16) {
                this.list2.add(tp);
                Log.v(TAG,
                    "------------\u79fb\u52a8\u8ddd\u79bb\u8d85\u8fc7\u9884\u8bbe\u8ddd\u79bb\uff0c\u52a0\u5165list2");
              }
            } else {
              this.list2.add(tp);
            }
          }
          Log.v(TAG, new StringBuilder("point(").append(i + 1)
              .append(") ACTION_MOVE:(")
              .append(x)
              .append(",")
              .append(y)
              .append("),")
              .append(event.getPointerId(i))
              .toString());
        }
        for (i = 0; i < pointCount; i++) {
          this.mPath[event.getPointerId(i)].quadTo(this.mposX[event.getPointerId(i)],
              this.mposY[event.getPointerId(i)], event.getX(i), event.getY(i));
          this.mposX[event.getPointerId(i)] = event.getX(i);
          this.mposY[event.getPointerId(i)] = event.getY(i);
        }
        break;
      case SucessActivity.COUNT_DOWN_LOGOUT:
        Log.v(TAG,
            new StringBuilder("\u67091\u4e2a\u70b9\u6309\u4e0b\u89e6\u6478\u5c4f.count:").append(
                event.getPointerCount())
                .append(",")
                .append(event.getPointerId(event.getPointerCount() - 1))
                .toString());
        this.mPath[event.getPointerId(event.getPointerCount() - 1)].moveTo(
            event.getX(event.getPointerId(event.getPointerCount() - 1)),
            event.getY(event.getPointerId(event.getPointerCount() - 1)));
        this.mposX[event.getPointerId(event.getPointerCount() - 1)] =
            event.getX(event.getPointerId(event.getPointerCount() - 1));
        this.mposY[event.getPointerId(event.getPointerCount() - 1)] =
            event.getY(event.getPointerId(event.getPointerCount() - 1));
        break;
      case SucessActivity.GET_VERIFYCODE_SUCCESS:
        Log.v(TAG, "\u67091\u4e2a\u70b9\u79bb\u5f00\u89e6\u6478\u5c4f");
        break;
    }
    Log.w("\u4e00\u6b21\u7ed8\u5236\u7ed3\u675f", "\u4e00\u6b21\u7ed8\u5236\u7ed3\u675f");
    return true;
  }

  public void run() {
    while (this.mIsRunning) {
      long startTime = System.currentTimeMillis();
      synchronized (this.mSurfaceHolder) {
        this.mCanvas = this.mSurfaceHolder.lockCanvas();
        Matrix matrix = new Matrix();
        matrix.setScale(((float) getWidth()) / ((float) this.bitmap.getWidth()),
            ((float) getHeight()) / ((float) this.bitmap.getHeight()));
        matrix.postTranslate(0.0f, 0.0f);
        if (this.mCanvas != null) {
          this.mCanvas.drawBitmap(this.bitmap, matrix, this.mPaint);
          for (int i = 0; i < 2; i++) {
            this.mCanvas.drawPath(this.mPath[i], this.touchPaints[i]);
          }
          this.mSurfaceHolder.unlockCanvasAndPost(this.mCanvas);
        }
      }
      int diffTime = (int) (System.currentTimeMillis() - startTime);
      while (diffTime <= 50) {
        diffTime = (int) (System.currentTimeMillis() - startTime);
        Thread.yield();
      }
    }
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    drawBackground();
  }

  public void surfaceCreated(SurfaceHolder holder) {
    drawBackground();
    this.mIsRunning = true;
    new Thread(this).start();
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    this.mIsRunning = false;
  }

  private void drawBackground() {
    this.mCanvas = this.mSurfaceHolder.lockCanvas();
    Matrix matrix = new Matrix();
    Log.w("\u6e05\u7a7a\u753b\u5e03=====drawBackground===",
        new StringBuilder(String.valueOf(getWidth())).append("\u00d7")
            .append(getHeight())
            .toString());
    float widthRatio = ((float) getWidth()) / ((float) this.bitmap.getWidth());
    float heightRatio = ((float) getHeight()) / ((float) this.bitmap.getHeight());
    Log.w("\u6e05\u7a7a\u753b\u5e03===drawBackground===client==",
        new StringBuilder(String.valueOf(getWidth())).append("\u00d7")
            .append(getHeight())
            .toString());
    matrix.setScale(widthRatio, heightRatio);
    matrix.postTranslate(0.0f, 0.0f);
    this.mCanvas.drawBitmap(this.bitmap, matrix, this.mPaint);
    this.mSurfaceHolder.unlockCanvasAndPost(this.mCanvas);
  }
}
