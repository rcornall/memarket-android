package com.robthecornallgmail.memarket.Canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by rob on 03/03/17.
 */

public class HouseCanvasDrawer {
    private Bitmap background;

    private int SCREEN_WIDTH, SCREEN_HEIGHT;
    public HouseCanvasDrawer(Bitmap background, int SCREEN_WIDTH, int SCREEN_HEIGHT) {
        this.background = background;

        this.SCREEN_WIDTH = SCREEN_WIDTH;
        this.SCREEN_HEIGHT = SCREEN_HEIGHT;
    }

    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(background,0,0,null);
    }
}
