package com.robthecornallgmail.memarket.Canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.robthecornallgmail.memarket.Util.Defines;
import com.robthecornallgmail.memarket.Util.Office;
import com.robthecornallgmail.memarket.Util.UserBitmap;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rob on 03/03/17.
 */

public class HouseCanvasDrawer {
    String TAG = "HouseCanvasDrawer";

    HashMap<Integer, UserBitmap> mUserBitmaps;
    ArrayList<DefaultBitmap> mDefaultBitmaps;


    final Bitmap mSilhouette;
    Office office;
    int mBackgroundWidth;
    int mGroundWidth;
    int mCloud1Width;

    int guyFrame = 0; //30 frame sprite animation, 12 x 5 x 8 x 5
    int houseWidth, officeWidth, officeTopHeight, officeMiddleHeight, officeBottomHeight;
    float mGuyWidth, mSilhouetteWidth;
    int officeCapacitySpace;
    int mGuyHeight, mSilhouetteHeight;

    Paint textPaint = new Paint();
    Paint RectPaintGrey = new Paint();
    Paint RectPaintGreen = new Paint();
    Paint RectPaintBorder = new Paint();

    Rect guySrc = new Rect();
    Rect guyDst = new Rect();

    int SCREEN_WIDTH, SCREEN_HEIGHT;
    int VIEW_HEIGHT, CLOUD_LEFT_EDGE, CLOUD_RIGHT_EDGE;

    int cloud1_dx = 0;

    public HouseCanvasDrawer(HashMap<Integer, UserBitmap> userBitmaps, ArrayList<DefaultBitmap> defaultBitmaps, Bitmap silhouette,
                             int SCREEN_WIDTH, int SCREEN_HEIGHT, Typeface pixelFont, int mTextColor) {
        mUserBitmaps = userBitmaps;
        mDefaultBitmaps = defaultBitmaps;
        for(DefaultBitmap bitmap : mDefaultBitmaps) {
            if(bitmap.mType == Defines.DEFAULT_TYPE.BACKGROUND)
            {
                mBackgroundWidth = bitmap.mBitmap.getWidth();
            }
            else if(bitmap.mType == Defines.DEFAULT_TYPE.GROUND)
            {
                mGroundWidth = bitmap.mBitmap.getWidth();
            }
            else if(bitmap.mType == Defines.DEFAULT_TYPE.CLOUD1)
            {
                mCloud1Width = bitmap.mBitmap.getWidth();
            }
            else if(bitmap.mType == Defines.DEFAULT_TYPE.GUY)
            {
                mGuyWidth = bitmap.mBitmap.getWidth()/4;
                mGuyHeight = bitmap.mBitmap.getHeight();
            }
        }
        this.mSilhouette = silhouette;
        this.SCREEN_WIDTH = SCREEN_WIDTH;
        this.SCREEN_HEIGHT = SCREEN_HEIGHT;

        mSilhouetteWidth = (float)silhouette.getWidth()/4;
        mSilhouetteHeight = silhouette.getHeight();


//                             |----------------SCREEN_WIDTH*3.75-----------|
//                              ____________________________________________
//                             |                                            |
//                             |        (0,0)_________________              |
//                             |            |                 |             |
//                             |            |            ___  |             |
//                             |            |          (c l o u d)          |
//                             |            |                 |             |
//                             |            |                 |             |
//                             |            |                 |             |
//                             |____________|_________________|_____________|
//
//                                          |--SCREEN_WIDTH---|
//
// (cloud right edge) =                               |-------|-------------|
//
// (cloud left edge) = |(cloud)|------------|---------|
//                                   ||
//                                   ||
//                               ____/\____________________________
//                              /                                  \
        CLOUD_LEFT_EDGE =  (int)(SCREEN_WIDTH*3.75 - SCREEN_WIDTH)/2 + SCREEN_WIDTH-SCREEN_WIDTH/3+mCloud1Width;
        CLOUD_RIGHT_EDGE = (int)(SCREEN_WIDTH*3.75 - SCREEN_WIDTH)/2 + SCREEN_WIDTH/3;

        textPaint.setColor(mTextColor);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTypeface(pixelFont);
        textPaint.setTextSize(55);

        RectPaintGrey.setColor(Color.argb(148,29,29,29));
        RectPaintGreen.setColor(Color.rgb(27,231,0));
        RectPaintBorder.setColor(Color.BLACK);
        RectPaintBorder.setStyle(Paint.Style.STROKE);
        RectPaintBorder.setStrokeWidth(3);
    }

    public void onDraw(Canvas canvas, float scaleFactor) {

//        Log.v(TAG, "scaleFactor:" + scaleFactor);
//        Log.v(TAG, "")
        /* draw default bitmaps first */
        for (DefaultBitmap bitmap : mDefaultBitmaps) {
            if (bitmap.mType == Defines.DEFAULT_TYPE.BACKGROUND)
            {
                float nextX = bitmap.mCoordinates.lastX;
                while (nextX < (int)((float)SCREEN_WIDTH/scaleFactor)){
                    if (nextX+mBackgroundWidth >= 0)
                    {
                        canvas.drawBitmap(bitmap.mBitmap, nextX, bitmap.mCoordinates.lastY, null);
                    }
                    nextX+=mBackgroundWidth;
                }
            }
            else if (bitmap.mType == Defines.DEFAULT_TYPE.GROUND)
            {
                float nextX = bitmap.mCoordinates.lastX;
                while (nextX < (int)((float)SCREEN_WIDTH/scaleFactor)){
                    if (nextX+mGroundWidth >= 0)
                    {
                        canvas.drawBitmap(bitmap.mBitmap, nextX, bitmap.mCoordinates.lastY, null);
                    }
                    nextX+=mGroundWidth;
                }
            }
            else if(bitmap.mType == Defines.DEFAULT_TYPE.CLOUD1)
            {
                canvas.drawBitmap(bitmap.mBitmap, bitmap.mCoordinates.lastX + cloud1_dx, bitmap.mCoordinates.lastY, null);
            }
        }

        // reset cloud back to the right
        if(cloud1_dx < -CLOUD_LEFT_EDGE) {
            cloud1_dx=CLOUD_RIGHT_EDGE;
        }

        for (UserBitmap userBitmap : mUserBitmaps.values()) {
            canvas.drawBitmap(userBitmap.mBitmap, userBitmap.mCoordinates.lastX, userBitmap.mCoordinates.lastY, null);
        }

        int guyX = calculateGuyOffsets(guyFrame, mGuyWidth);
        drawGuy(canvas, mDefaultBitmaps.get(Defines.DEFAULT_TYPE.GUY).mBitmap, guyX,
                mDefaultBitmaps.get(Defines.DEFAULT_TYPE.GUY).mCoordinates.lastX,
                mDefaultBitmaps.get(Defines.DEFAULT_TYPE.GUY).mCoordinates.lastY, (int)mGuyWidth, mGuyHeight);



//        drawOffice(canvas,office,officeTallLastX+officeWidth, houseLastY, "short");
//        drawOffice(canvas,office,officeTallLastX,officeTallLastY,"tall");
//        drawOffice(canvas,office,officeTallLastX+officeWidth*2,officeTallLastY, "tall");



//        Rect guySrc = new Rect(guyX, 0, guyX+mGuyWidth, 0+mGuyHeight);
//        Rect guyDst = new Rect((int)guyLastX, (int)guyLastY, (int)guyLastX+mGuyWidth, (int)guyLastY+mGuyHeight);
//        canvas.drawBitmap(guy,guySrc,guyDst,null);
//        guyDst.set((int)guyLastX-mGuyWidth, (int)guyLastY, (int)guyLastX, (int)guyLastY+mGuyHeight);
//        canvas.drawBitmap(guy,guySrc,guyDst,null);
//        guyDst.set((int)guyLastX-mGuyWidth-mGuyWidth/2, (int)guyLastY, (int)guyLastX-mGuyWidth/2, (int)guyLastY+mGuyHeight);
//        canvas.drawBitmap(guy,guySrc,guyDst,null);

        /* do increments */
        guyFrame = ++guyFrame % 30;
        cloud1_dx += -1;

    }

    private void drawGuy(Canvas canvas, Bitmap bitmap, int x,
                         float lastX, float lastY,
                         int guyWidth, int guyHeight)
    {
        guySrc.set(x, 0, x+ guyWidth, 0+guyHeight);
        guyDst.set((int)lastX, (int)lastY, (int)lastX+ guyWidth, (int)lastY+ guyHeight);
        canvas.drawBitmap(bitmap,guySrc,guyDst,null);
    }

    public void setHeight(int height) {
        this.VIEW_HEIGHT = height;
    }

    private void drawOffice(Canvas canvas, Office office,
                            float x, float y, String size) {
        canvas.drawBitmap(office.officeTop,x,y, null);
        if (size.equals("tall")) {
            for(int i = 0; i<5;i++) {
                if(i==4) {
                    canvas.drawBitmap(office.officeBottom,x,y+officeTopHeight+i*officeMiddleHeight,null);
                } else {
                    canvas.drawBitmap(office.officeMiddle,x,y+officeTopHeight+i*officeMiddleHeight,null);
                }
            }
            for(int i = 0; i<12; i++) {
                drawGuy(canvas, mSilhouette,calculateGuyOffsets(guyFrame, mSilhouetteWidth),
                        x + officeCapacitySpace/2, y + officeTopHeight + i* mSilhouetteHeight, (int) mSilhouetteWidth, mSilhouetteHeight);
            }
            // draw the capacity bars beside buildings
            canvas.drawRect(x + officeCapacitySpace*2-10, y+officeTopHeight+10, x + officeCapacitySpace*2,
                    y + officeTopHeight + officeMiddleHeight*4 + officeBottomHeight, RectPaintGrey);
            canvas.drawRect(x + officeCapacitySpace*2-10, y+officeTopHeight+10+ mSilhouetteHeight *7, x + officeCapacitySpace*2,
                    y + officeTopHeight + officeMiddleHeight*4 + officeBottomHeight, RectPaintGreen);
            canvas.drawRect(x + officeCapacitySpace*2-10, y+officeTopHeight+10, x + officeCapacitySpace*2,
                    y + officeTopHeight + officeMiddleHeight*4 + officeBottomHeight, RectPaintBorder);
            canvas.drawText("5/12", x, y+officeTopHeight, textPaint);
        } else {
            for(int i = 0; i<3;i++) {
                if (i == 2) {
                    canvas.drawBitmap(office.officeBottom, x, y + officeTopHeight + i * officeMiddleHeight, null);
                } else {
                    canvas.drawBitmap(office.officeMiddle, x, y + officeTopHeight + i * officeMiddleHeight, null);
                }
            }
            for(int i = 0; i<6; i++) {
                drawGuy(canvas, mSilhouette,calculateGuyOffsets(guyFrame, mSilhouetteWidth),
                        x + officeCapacitySpace/2, y + officeTopHeight + i* mSilhouetteHeight, (int) mSilhouetteWidth, mSilhouetteHeight);
            }
            // draw the capacity bars beside buildings
            canvas.drawRect(x + officeCapacitySpace*2-10, y+officeTopHeight+10, x + officeCapacitySpace*2,
                    y + officeTopHeight + officeMiddleHeight*2 + officeBottomHeight, RectPaintGrey);
            canvas.drawRect(x + officeCapacitySpace*2-10, y+officeTopHeight+10+ mSilhouetteHeight *5, x + officeCapacitySpace*2,
                    y + officeTopHeight + officeMiddleHeight*2 + officeBottomHeight, RectPaintGreen);
            canvas.drawRect(x + officeCapacitySpace*2-10, y+officeTopHeight+10, x + officeCapacitySpace*2,
                    y + officeTopHeight + officeMiddleHeight*2 + officeBottomHeight, RectPaintBorder);
            canvas.drawText("1/6", x, y+officeTopHeight, textPaint);
        }

    }
    private int calculateGuyOffsets(int frame, float width) {
        int guyX = 0;
        if (frame < 12) {
            guyX = 0;
        } else if (frame < 17) {
            guyX = (int)width;
        } else if (frame < 25) {
            guyX = (int)width*2;
        } else {
            guyX = (int)width*3;
        }
        return guyX;
    }
}


