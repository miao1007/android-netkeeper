package com.xinli.portalclient.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TouchPoint implements Parcelable {
    private long eventTime;
    private int x;
    private int y;

    public long getEventTime() {
        return this.eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String toString() {
        return new StringBuilder("TouchPoint [x=").append(this.x).append(", y=").append(this.y).append(", eventTime=").append(this.eventTime).append("]").toString();
    }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.eventTime);
    dest.writeInt(this.x);
    dest.writeInt(this.y);
  }

  public TouchPoint() {
  }

  private TouchPoint(Parcel in) {
    this.eventTime = in.readLong();
    this.x = in.readInt();
    this.y = in.readInt();
  }

  public static final Creator<TouchPoint> CREATOR = new Creator<TouchPoint>() {
    public TouchPoint createFromParcel(Parcel source) {
      return new TouchPoint(source);
    }

    public TouchPoint[] newArray(int size) {
      return new TouchPoint[size];
    }
  };
}
