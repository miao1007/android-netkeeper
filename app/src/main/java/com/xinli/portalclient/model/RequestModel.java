package com.xinli.portalclient.model;

import android.graphics.Bitmap;

public class RequestModel {
    private Bitmap bitmap;
    private boolean forceUpdated;
    private String keyvalue;
    private String message;
    private String sessionId;
    private String username;

    public RequestModel(String username, String sessionId) {
        this.username = username;
        this.sessionId = sessionId;
    }

  public RequestModel() {

  }

    public boolean isForceUpdated() {
        return this.forceUpdated;
    }

    public void setForceUpdated(boolean forceUpdated) {
        this.forceUpdated = forceUpdated;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getKeyvalue() {
        return this.keyvalue;
    }

    public void setKeyvalue(String keyvalue) {
        this.keyvalue = keyvalue;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String toString() {
        return new StringBuilder("RequestModel [bitmap=").append(this.bitmap).append(", keyvalue=").append(this.keyvalue).append(", sessionId=").append(this.sessionId).append(", username=").append(this.username).append("]").toString();
    }
}
