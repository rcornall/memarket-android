package com.robthecornallgmail.memarket.Canvas;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.robthecornallgmail.memarket.Util.Defines;
import com.robthecornallgmail.memarket.Util.UserBuildingBitmap;
import com.robthecornallgmail.memarket.Util.UserWorkerBitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rob on 01/09/17.
 */

public class InsideHouseCanvasDrawer {
    String TAG = "InsideHouseCanvasDrawer";

    ArrayList<DefaultBitmap> mDefaultBitmaps;
    HashMap<Integer,UserWorkerBitmap> mUserWorkerBitmaps;
    Map.Entry<Integer, UserBuildingBitmap> mUserBuildingBitmap;
    Integer SCREEN_WIDTH, SCREEN_HEIGHT;

    int mBackgroundWidth;
    int mBuildingHeight;
    Integer mWorkerWidth;
    Integer mWorkerHeight;
    Rect mWorkerSrc = new Rect(), mWorkerDst = new Rect();

    int mWorkerFrame;

    public InsideHouseCanvasDrawer(ArrayList<DefaultBitmap> defaultBitmaps, HashMap<Integer, UserWorkerBitmap> userWorkers, Map.Entry<Integer, UserBuildingBitmap> userBuildingBitmap, int screenWidth, int screenHeight) {
        mDefaultBitmaps = defaultBitmaps;
        mUserWorkerBitmaps = userWorkers;
        mUserBuildingBitmap = userBuildingBitmap;
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
        for(UserWorkerBitmap bitmap : mUserWorkerBitmaps.values()) {
            if(mWorkerWidth == null)
            {
                mWorkerWidth = bitmap.mBitmapWidth/5;
            }
            if(mWorkerHeight == null)
            {
                mWorkerHeight = bitmap.mBitmapHeight;
            }
            Log.v(TAG, " workerbitmap: " + bitmap.mWorkingAt.toString());
            Log.v(TAG, "BuildingID: "  + mUserBuildingBitmap.getKey());
            if(bitmap.mWorkingAt.equals(mUserBuildingBitmap.getKey()))
            {
                Log.v(TAG, bitmap.mWorkingPosition.toString());
                Log.v(TAG, String.format("%f, %f" , bitmap.mCoordinates.lastX, bitmap.mCoordinates.lastY));
                int guyX = calculateGuyOffsets(mWorkerFrame, mWorkerWidth);
                drawWorker(canvas, bitmap, guyX);
            }
        }


        mWorkerFrame = ++mWorkerFrame % 42;
    }
    void drawWorker(Canvas canvas, UserWorkerBitmap bitmap, int guyX)
    {
        mWorkerSrc.set(guyX, 0, guyX + mWorkerWidth, 0 + mWorkerHeight);
        mWorkerDst.set((int)bitmap.mCoordinates.lastX, (int)bitmap.mCoordinates.lastY, (int)bitmap.mCoordinates.lastX + mWorkerWidth, (int)bitmap.mCoordinates.lastY + mWorkerHeight);
        canvas.drawBitmap(bitmap.mBitmap, mWorkerSrc, mWorkerDst, null);

    }
    private int calculateGuyOffsets(int frame, float width) {
        int guyX = 0;
        if (frame < 7) {
            guyX = 0;
        } else if (frame < 10) {
            guyX = (int) width;
        } else if (frame < 12) {
            guyX = (int) width * 2;
        } else if (frame < 14){
            guyX = (int) width * 3;
        } else if (frame < 16){
            guyX = (int) width * 4;
        }else if (frame < 17){
            guyX = (int) width * 3;
        }else if (frame < 18){
            guyX = (int) width * 4;
        }else if (frame < 20){
            guyX = (int) width * 3;
        }else if (frame < 22){
            guyX = (int) width * 4;
        }else if (frame < 24){
            guyX = (int) width * 3;
        }else if (frame < 26){
            guyX = (int) width * 2;
        } else if (frame < 27){
            guyX = (int) width * 4;
        }else if (frame < 28){
            guyX = (int) width * 3;
        }else if (frame < 30){
            guyX = (int) width * 4;
        }else if (frame < 33){
            guyX = (int) width * 3;
        }else if (frame < 34){
            guyX = (int) width * 4;
        }else if (frame < 36){
            guyX = (int) width * 3;
        }else if (frame < 38){
            guyX = (int) width * 2;
        }else {
            guyX = (int) width * 1;
        }
        return guyX;
    }
}
