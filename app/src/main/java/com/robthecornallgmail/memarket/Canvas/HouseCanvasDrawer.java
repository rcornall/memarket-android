package com.robthecornallgmail.memarket.Canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by rob on 03/03/17.
 */

public class HouseCanvasDrawer {
    private final Bitmap cloud1;
    private String TAG = "HouseCanvasDrawer";
    private final Bitmap background, ground, guy;
    private  int ground_y;
    private Integer x_background_diff, y_background_diff;

    private int guyFrame = 0; //30 frame sprite animation, 12 x 5 x 8 x 5
    private int guyWidth;
    private int guyHeight;

    private int SCREEN_WIDTH, SCREEN_HEIGHT;
    private int VIEW_HEIGHT;

    public HouseCanvasDrawer(Bitmap background, Bitmap cloud1, Bitmap ground, Bitmap guy, int SCREEN_WIDTH, int SCREEN_HEIGHT) {
        this.background = background;
        this.cloud1 = cloud1;
        this.ground = ground;
        this.guy = guy;
        this.SCREEN_WIDTH = SCREEN_WIDTH;
        this.SCREEN_HEIGHT = SCREEN_HEIGHT;

        ground_y = SCREEN_HEIGHT/10;

        // guy is a sprite with 4 frames:
        guyWidth = guy.getWidth()/4;
        guyHeight = guy.getHeight();

        this.y_background_diff = background.getHeight() - this.SCREEN_HEIGHT;
        Log.v(TAG, "y diff is " + this.y_background_diff.toString());
        Log.v(TAG, String.format("SCREEN HEIGHT=%d, groundy=%d", SCREEN_HEIGHT, ground_y));


    }

    public void onDraw(Canvas canvas,
                       float background_lastX, float background_lastY,
                       float ground_lastX, float ground_lastY,
                       float guyLastX, float guyLastY, float cloudLastX, float cloudLastY) {

        canvas.drawBitmap(background,background_lastX,background_lastY,null);
        canvas.drawBitmap(ground, ground_lastX, ground_lastY, null);
        canvas.drawBitmap(cloud1, cloudLastX, cloudLastY,null);

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

    }

    public void setHeight(int height) {
        this.VIEW_HEIGHT = height;
    }
}
