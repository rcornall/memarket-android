package com.robthecornallgmail.memarket.Canvas;

import android.graphics.Bitmap;

import com.robthecornallgmail.memarket.Util.ObjectCoordinates;

/**
 * Created by rob on 29/08/17.
 */

public class DefaultBitmap {
    public Bitmap mBitmap;
    public Integer mType;
    public ObjectCoordinates mCoordinates;
    public DefaultBitmap(Bitmap bitmap, int type, float x, float y)
    {
        mBitmap = bitmap;
        mType = type;
        mCoordinates = new ObjectCoordinates(x,y);
    }
}
