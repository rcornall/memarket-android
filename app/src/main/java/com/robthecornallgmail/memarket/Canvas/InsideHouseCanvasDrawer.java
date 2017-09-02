package com.robthecornallgmail.memarket.Canvas;

import android.graphics.Canvas;
import android.util.Log;

import com.robthecornallgmail.memarket.Util.Defines;

import java.util.ArrayList;

/**
 * Created by rob on 01/09/17.
 */

public class InsideHouseCanvasDrawer {
    String TAG = "InsideHouseCanvasDrawer";

    ArrayList<DefaultBitmap> mDefaultBitmaps;
    Integer SCREEN_WIDTH, SCREEN_HEIGHT;

    int mBackgroundWidth;
    int mBuildingHeight;

    public InsideHouseCanvasDrawer(ArrayList<DefaultBitmap> defaultBitmaps, int screenWidth, int screenHeight) {
        mDefaultBitmaps = defaultBitmaps;
        SCREEN_WIDTH = screenWidth;
        SCREEN_HEIGHT = screenHeight;

        for(DefaultBitmap bitmap : mDefaultBitmaps) {
            if (bitmap.mType == Defines.DEFAULT_TYPE.BACKGROUND) {
                mBackgroundWidth = bitmap.mBitmap.getWidth();
            }
            if (bitmap.mType == Defines.DEFAULT_TYPE.INSIDE_BUILDING) {
                mBuildingHeight = bitmap.mBitmap.getHeight();
                Log.v(TAG, "HEIGHT: " + mBuildingHeight);
            }
        }
    }

    public void onDraw(Canvas canvas, float scaleFactor) {
        for (DefaultBitmap bitmap : mDefaultBitmaps) {
            if (bitmap.mType == Defines.DEFAULT_TYPE.BACKGROUND)
            {
                float nextX = bitmap.mCoordinates.lastX;
                while (nextX < (int) ((float) SCREEN_WIDTH / scaleFactor)) {
                    if (nextX + mBackgroundWidth >= 0)
                    {
                        canvas.drawBitmap(bitmap.mBitmap, nextX, bitmap.mCoordinates.lastY, null);
                    }
                    nextX += mBackgroundWidth;
                }
            }
            if (bitmap.mType == Defines.DEFAULT_TYPE.INSIDE_BUILDING)
            {
                float nextY = bitmap.mCoordinates.lastY;
                for(int i = 0; i < bitmap.mFloors; i++) {
                    canvas.drawBitmap(bitmap.mBitmap, bitmap.mCoordinates.lastX, nextY, null);
                    nextY -= mBuildingHeight;
                }
            }
            if (bitmap.mType == Defines.DEFAULT_TYPE.INSIDE_BUILDING_TOP) {
                canvas.drawBitmap(bitmap.mBitmap,bitmap.mCoordinates.lastX,bitmap.mCoordinates.lastY,null);
            }
        }
    }
}
