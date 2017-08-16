package com.robthecornallgmail.memarket.Canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.robthecornallgmail.memarket.Util.Office;

/**
 * Created by rob on 03/03/17.
 */

public class HouseCanvasDrawer {
    private final Bitmap cloud1;
    private String TAG = "HouseCanvasDrawer";
    private final Bitmap background, ground, guy;
    private Office office;
    private  int ground_y;
    private Integer x_background_diff, y_background_diff;
    private int backgroundWidth;
    private int groundWidth;

    private int guyFrame = 0; //30 frame sprite animation, 12 x 5 x 8 x 5
    private int guyWidth, houseWidth, officeWidth;
    private int guyHeight;

    private int SCREEN_WIDTH, SCREEN_HEIGHT;
    private int VIEW_HEIGHT;

    private int cloud1_dx = 0;
    Bitmap test1;
    Bitmap test2;
    Bitmap test3;

    public HouseCanvasDrawer(Bitmap background, Bitmap cloud1, Bitmap ground, Bitmap guy, Office office, int SCREEN_WIDTH, int SCREEN_HEIGHT, Bitmap test1,
                             Bitmap test2,
                             Bitmap test3) {
        this.background = background;
        this.cloud1 = cloud1;
        this.ground = ground;
        this.guy = guy;
        this.office = office;
        this.SCREEN_WIDTH = SCREEN_WIDTH;
        this.SCREEN_HEIGHT = SCREEN_HEIGHT;
        this.test1 = test1 ;
        this.test2 = test2 ;
        this.test3 = test3 ;

        ground_y = SCREEN_HEIGHT/10;

        backgroundWidth = background.getWidth();
        groundWidth = ground.getWidth();
        // guy is a sprite with 4 frames:
        guyWidth = guy.getWidth()/4;
        guyHeight = guy.getHeight();

        officeWidth = office.officeTop.getWidth();

        this.y_background_diff = background.getHeight() - this.SCREEN_HEIGHT;
        Log.v(TAG, "y diff is " + this.y_background_diff.toString());
        Log.v(TAG, String.format("SCREEN HEIGHT=%d, groundy=%d", SCREEN_HEIGHT, ground_y));


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
        if(cloud1_dx<-(SCREEN_WIDTH*2-SCREEN_WIDTH/4)) {
            cloud1_dx=SCREEN_WIDTH-SCREEN_WIDTH/4;
        }

        canvas.drawBitmap(cloud1, cloudLastX+cloud1_dx, cloudLastY, null);

        drawOffice(canvas,office,houseLastX, houseLastY, "short");
        drawOffice(canvas,office,officeTallLastX,officeTallLastY,"tall");
        drawOffice(canvas,office,houseLastX+officeWidth,officeTallLastY, "tall");


        int guyX = 0;
        if (guyFrame < 12) {
            guyX = 0;
        } else if (guyFrame < 17) {
            guyX = guyWidth;
        } else if (guyFrame < 25) {
            guyX = guyWidth*2;
        } else {
            guyX = guyWidth*3;
        }
        Rect guySrc = new Rect(guyX, 0, guyX+guyWidth, 0+guyHeight);
        Rect guyDst = new Rect((int)guyLastX, (int)guyLastY, (int)guyLastX+guyWidth, (int)guyLastY+guyHeight);
        canvas.drawBitmap(guy,guySrc,guyDst,null);
        guyDst.set((int)guyLastX-guyWidth, (int)guyLastY, (int)guyLastX, (int)guyLastY+guyHeight);
        canvas.drawBitmap(guy,guySrc,guyDst,null);
        guyDst.set((int)guyLastX-guyWidth-guyWidth/2, (int)guyLastY, (int)guyLastX-guyWidth/2, (int)guyLastY+guyHeight);
        canvas.drawBitmap(guy,guySrc,guyDst,null);

        guyFrame = ++guyFrame % 30;
        cloud1_dx += -1;

    }

    public void setHeight(int height) {
        this.VIEW_HEIGHT = height;
    }

    private void drawOffice(Canvas canvas, Office office, float x, float y, String size) {
        canvas.drawBitmap(office.officeTop,x,y, null);
        int topHeight = office.officeTop.getHeight();
        int middleHeight = office.officeMiddle.getHeight();
        if (size == "tall") {
            for(int i = 0; i<5;i++) {
                if(i==4) {
                    canvas.drawBitmap(office.officeBottom,x,y+topHeight+i*middleHeight,null);
                } else {
                    canvas.drawBitmap(office.officeMiddle,x,y+topHeight+i*middleHeight,null);
                }
            }
        } else {
            for(int i = 0; i<3;i++) {
                if (i == 2) {
                    canvas.drawBitmap(office.officeBottom, x, y + topHeight + i * middleHeight, null);
                } else {
                    canvas.drawBitmap(office.officeMiddle, x, y + topHeight + i * middleHeight, null);
                }
            }
        }

    }
}


