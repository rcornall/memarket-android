package com.robthecornallgmail.memarket.Canvas;

import android.graphics.Bitmap;
import android.util.Log;

import com.robthecornallgmail.memarket.Util.ObjectCoordinates;

/**
 * Created by rob on 29/08/17.
 */

public class DefaultBitmap {
    public Bitmap mBitmap;
    public Integer mType;
    public Integer mFloors;
    public ObjectCoordinates mCoordinates;
    public DefaultBitmap(Bitmap bitmap, int type, float x, float y)
    {
        mBitmap = bitmap;
        mType = type;
        mFloors = 0;
        mCoordinates = new ObjectCoordinates(x,y);
    }
    public DefaultBitmap(Bitmap bitmap, int type, int floors, float x, float y)
    {
        mBitmap = bitmap;
        mType = type;
        mFloors = floors;
        mCoordinates = new ObjectCoordinates(x,y);
    }
}
