package com.robthecornallgmail.memarket.Canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

/**
 * Created by rob on 03/03/17.
 */

public class HouseCanvasDrawer {
    private String TAG = "HouseCanvasDrawer";
    private Bitmap background;
    private Integer x_background_diff, y_background_diff;

    private int SCREEN_WIDTH, SCREEN_HEIGHT;
    public HouseCanvasDrawer(Bitmap background, int SCREEN_WIDTH, int SCREEN_HEIGHT) {
        this.background = background;

        this.SCREEN_WIDTH = SCREEN_WIDTH;
        this.SCREEN_HEIGHT = SCREEN_HEIGHT;

        this.y_background_diff = background.getHeight() - this.SCREEN_HEIGHT;
        Log.v(TAG, "y diff is " + this.y_background_diff.toString());


    }

    public void onDraw(Canvas canvas, Matrix matrix) {
        canvas.drawBitmap(background,matrix,null);

    }
}
