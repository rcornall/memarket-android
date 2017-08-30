package com.robthecornallgmail.memarket.Util;

/**
 * Created by rob on 29/08/17.
 */

public class ObjectCoordinates{
    public float lastY = 0, backupx = 0;
    public float lastX = 0, backupy = 0;

    public ObjectCoordinates(float x, float y)
    {
        lastX = x;
        lastY = y;
    }

    public void setBackupXY() {
        this.backupx = lastX;
        this.backupy = lastY;
    }

    public void setLastXY(float x, float y) {
        this.lastX = x;
        this.lastY = y;
    }
}