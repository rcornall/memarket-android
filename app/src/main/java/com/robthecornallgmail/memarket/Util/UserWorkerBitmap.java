package com.robthecornallgmail.memarket.Util;

import android.graphics.Bitmap;

/**
 * Created by rob on 08/09/17.
 */

public class UserWorkerBitmap {
    public Integer mItemID;
    public String mName;
    public Bitmap mBitmap;
    public Integer mBitmapWidth, mBitmapHeight;
    public Integer mType;
    public Integer mSubtype;
    public Integer mWorkingAt;
    public Integer mWorkingPosition;
    public Integer mWorkerLevel;
    public ObjectCoordinates mCoordinates;

    public UserWorkerBitmap(Integer itemID, String name, Integer type, Integer subType, Integer workingAt, Integer workingPosition, Integer workerLevel, Integer bitmapWidth) {
        mItemID = itemID;
        mName = name;
        mType = type;
        mSubtype = subType;
        mWorkingAt = workingAt;
        mWorkingPosition = workingPosition;
        mWorkerLevel = workerLevel;
        mCoordinates = new ObjectCoordinates(0,0);
        mBitmapWidth = bitmapWidth;
    }

}
