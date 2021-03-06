package com.robthecornallgmail.memarket.Util;

import android.graphics.Bitmap;

/**
 * Created by rob on 28/08/17.
 */
/* stores all info needed to draw on canvas at coordinates x,y */
public class UserBuildingBitmap {
    public Integer mItemID;
    public String mName;
    public Bitmap mBitmap;
    public Integer mXCoordinate;
    public Integer mWidth;
    public Integer mType;
    public Integer mSubtype;
    public ObjectCoordinates mCoordinates;
    public Integer mBitmapWidth;
    public Integer mBitmapHeight;

    public UserBuildingBitmap(Integer itemID, String name, Integer xCoord, Integer width, Integer bitmapWidth, Integer type, Integer subtype)
    {
        mItemID = itemID;
        mName = name;
        mXCoordinate = xCoord;
        mWidth = width;
        mBitmapWidth = bitmapWidth;
        mType = type;
        mSubtype = subtype;
        mCoordinates = new ObjectCoordinates(0,0);
    }
}
