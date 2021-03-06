package com.robthecornallgmail.memarket.Canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

import com.robthecornallgmail.memarket.Views.MainSurfaceView;

/**
 * Created by rob on 13/02/17.
 */

public class WalkingFrog {

    int height, width;
    int currentFrame = 0;
    int x, y;
    Bitmap b;
    MainSurfaceView ov;
    public WalkingFrog(MainSurfaceView mainSurfaceView, Bitmap walkingFrog) {
        b = walkingFrog;
        ov = mainSurfaceView;
        height = b.getHeight();
        width = b.getWidth() / 11;
    }

    public void onDraw(Canvas canvas) {
        update();
        int srcX = currentFrame* width;
        Rect src = new Rect (srcX,0, srcX+width, height);
        Rect dst = new Rect (x,y, x+width, y+height);
        x = y = 0;
        canvas.drawBitmap(b, src, dst, null);

    }
    private void update() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        currentFrame = ++currentFrame % 11;
    }
}
