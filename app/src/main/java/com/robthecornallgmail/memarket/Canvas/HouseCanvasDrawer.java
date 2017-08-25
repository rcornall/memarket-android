package com.robthecornallgmail.memarket.Canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.MyApplication;
import com.robthecornallgmail.memarket.Util.Office;

/**
 * Created by rob on 03/03/17.
 */

public class HouseCanvasDrawer {
    private String TAG = "HouseCanvasDrawer";
    private final Bitmap background, cloud1, ground, guy, silouette;
    private Office office;
    private  int ground_y;
    private Integer x_background_diff, y_background_diff;
    private int backgroundWidth;
    private int groundWidth;

    private int guyFrame = 0; //30 frame sprite animation, 12 x 5 x 8 x 5
    private int houseWidth, officeWidth, officeTopHeight, officeMiddleHeight, officeBottomHeight;
    float guyWidth, silhoutteWidth;
    int officeCapacitySpace;
    private int guyHeight,silouetteHeight;

    Paint textPaint = new Paint();
    Paint RectPaintGrey = new Paint();
    Paint RectPaintGreen = new Paint();
    Paint RectPaintBorder = new Paint();

    Rect guySrc = new Rect();
    Rect guyDst = new Rect();

    private int SCREEN_WIDTH, SCREEN_HEIGHT;
    private int VIEW_HEIGHT, CLOUD_LEFT_EDGE, CLOUD_RIGHT_EDGE;

    private int cloud1_dx = 0;

    public HouseCanvasDrawer(Bitmap background, Bitmap cloud1, Bitmap ground, Bitmap guy, Office office,
                             Bitmap silhouette, int SCREEN_WIDTH, int SCREEN_HEIGHT, Typeface pixelFont, int mTextColor) {
        this.background = background;
        this.cloud1 = cloud1;
        this.ground = ground;
        this.guy = guy;
        this.office = office;
        this.silouette = silhouette;
        this.SCREEN_WIDTH = SCREEN_WIDTH;
        this.SCREEN_HEIGHT = SCREEN_HEIGHT;

        ground_y = SCREEN_HEIGHT/10;

        backgroundWidth = background.getWidth();
        groundWidth = ground.getWidth();
        // guy is a sprite with 4 frames:
        guyWidth = guy.getWidth()/4;
        guyHeight = guy.getHeight();

        silhoutteWidth = (float)silhouette.getWidth()/4;
        silouetteHeight = silhouette.getHeight();

        officeWidth = office.officeTop.getWidth();
        officeTopHeight = office.officeTop.getHeight();
        officeMiddleHeight = office.officeMiddle.getHeight();
        officeBottomHeight = office.officeBottom.getHeight();
        officeCapacitySpace = officeWidth/13;


        this.y_background_diff = background.getHeight() - this.SCREEN_HEIGHT;
        Log.v(TAG, "y diff is " + this.y_background_diff.toString());
        Log.v(TAG, String.format("SCREEN HEIGHT=%d, groundy=%d", SCREEN_HEIGHT, ground_y));

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
        CLOUD_LEFT_EDGE =  (int)(SCREEN_WIDTH*3.75 - SCREEN_WIDTH)/2 + SCREEN_WIDTH-SCREEN_WIDTH/3+cloud1.getWidth();
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

    public void onDraw(Canvas canvas, float scaleFactor,
                       float background_lastX, float background_lastY,
                       float ground_lastX, float ground_lastY,
                       float guyLastX, float guyLastY,
                       float cloudLastX, float cloudLastY,
                       float houseLastX, float houseLastY,
                       float officeTallLastX, float officeTallLastY) {

//        Log.v(TAG, "scaleFactor:" + scaleFactor);
//        Log.v(TAG, "")
        // check if its out of screen bounds - dont waste resources drawing if texture is not in view
        float nextX = background_lastX;
        while (nextX < (int)((float)SCREEN_WIDTH/scaleFactor)) {
            if (nextX+backgroundWidth >= 0) {
                canvas.drawBitmap(background,nextX,background_lastY,null);
            }
            nextX+=backgroundWidth;
        }
        nextX = ground_lastX;
        while (nextX < (int)((float)SCREEN_WIDTH/scaleFactor)) {
            if (nextX+groundWidth >= 0) {
                canvas.drawBitmap(ground, nextX, ground_lastY, null);
            }
            nextX+=groundWidth;
        }
        // reset cloud back to the right

        if(cloud1_dx < -CLOUD_LEFT_EDGE) {
            cloud1_dx=CLOUD_RIGHT_EDGE;
        }

        canvas.drawBitmap(cloud1, cloudLastX+cloud1_dx, cloudLastY, null);

        drawOffice(canvas,office,officeTallLastX+officeWidth, houseLastY, "short");
        drawOffice(canvas,office,officeTallLastX,officeTallLastY,"tall");
        drawOffice(canvas,office,officeTallLastX+officeWidth*2,officeTallLastY, "tall");


        int guyX = calculateGuyOffsets(guyFrame,guyWidth);
        drawGuy(canvas,guy,guyX,(int)guyLastX,(int)guyLastY,(int)guyWidth,guyHeight);

//        Rect guySrc = new Rect(guyX, 0, guyX+guyWidth, 0+guyHeight);
//        Rect guyDst = new Rect((int)guyLastX, (int)guyLastY, (int)guyLastX+guyWidth, (int)guyLastY+guyHeight);
//        canvas.drawBitmap(guy,guySrc,guyDst,null);
//        guyDst.set((int)guyLastX-guyWidth, (int)guyLastY, (int)guyLastX, (int)guyLastY+guyHeight);
//        canvas.drawBitmap(guy,guySrc,guyDst,null);
//        guyDst.set((int)guyLastX-guyWidth-guyWidth/2, (int)guyLastY, (int)guyLastX-guyWidth/2, (int)guyLastY+guyHeight);
//        canvas.drawBitmap(guy,guySrc,guyDst,null);

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
        if (size == "tall") {
            for(int i = 0; i<5;i++) {
                if(i==4) {
                    canvas.drawBitmap(office.officeBottom,x,y+officeTopHeight+i*officeMiddleHeight,null);
                } else {
                    canvas.drawBitmap(office.officeMiddle,x,y+officeTopHeight+i*officeMiddleHeight,null);
                }
            }
            for(int i = 0; i<12; i++) {
                drawGuy(canvas,silouette,calculateGuyOffsets(guyFrame, silhoutteWidth),
                        x + officeCapacitySpace/2, y + officeTopHeight + i*silouetteHeight, (int)silhoutteWidth,silouetteHeight);
            }
            // draw the capacity bars beside buildings
            canvas.drawRect(x + officeCapacitySpace*2-10, y+officeTopHeight+10, x + officeCapacitySpace*2,
                    y + officeTopHeight + officeMiddleHeight*4 + officeBottomHeight, RectPaintGrey);
            canvas.drawRect(x + officeCapacitySpace*2-10, y+officeTopHeight+10+silouetteHeight*7, x + officeCapacitySpace*2,
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
                drawGuy(canvas,silouette,calculateGuyOffsets(guyFrame, silhoutteWidth),
                        x + officeCapacitySpace/2, y + officeTopHeight + i*silouetteHeight, (int)silhoutteWidth,silouetteHeight);
            }
            // draw the capacity bars beside buildings
            canvas.drawRect(x + officeCapacitySpace*2-10, y+officeTopHeight+10, x + officeCapacitySpace*2,
                    y + officeTopHeight + officeMiddleHeight*2 + officeBottomHeight, RectPaintGrey);
            canvas.drawRect(x + officeCapacitySpace*2-10, y+officeTopHeight+10+silouetteHeight*5, x + officeCapacitySpace*2,
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


