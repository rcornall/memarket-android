package com.robthecornallgmail.memarket.Sprites;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.SurfaceView;

import com.robthecornallgmail.memarket.Views.OurView;

/**
 * Created by rob on 15/02/17.
 */

public class Clouds {

    private int screenWidth, screenHeight;
    private Bitmap clouds, mountains, walkingFrog;
    private int currentFrame = 0;
    private int frogHeight, frogWidth;
    private int x_clouds, x_mountains, x_frog, y, dx;
    private OurView ov;

    public Clouds(Bitmap clouds, Bitmap mountains,Bitmap walkingFrog, int screenWidth, int screenHeight) {
        this.clouds = clouds;
        this.mountains = mountains;
        this.walkingFrog = walkingFrog;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        frogHeight = walkingFrog.getHeight();
        frogWidth = walkingFrog.getWidth()/11;
    }

    public void onDraw(Canvas canvas) {
        x_clouds+=dx;
        x_mountains+=(dx+2);
        if(x_clouds<-OurView.SCREEN_WIDTH) {
            x_clouds=0;
        }
        if(x_mountains<-OurView.SCREEN_WIDTH) {
            x_mountains=0;
        }
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(clouds, x_clouds, y, null);
        canvas.drawBitmap(mountains, x_mountains, y, null);
        if(x_clouds<0)
        {
            canvas.drawBitmap(clouds, x_clouds+ OurView.SCREEN_WIDTH, y, null);
        }
        if(x_mountains<0)
        {
            canvas.drawBitmap(mountains, x_mountains+ OurView.SCREEN_WIDTH, y, null);
        }
        int srcX = currentFrame*frogWidth;
        int srcY = 0;
        Rect src = new Rect(srcX, srcY, srcX + frogWidth, srcY+ frogHeight);
        Rect dst = new Rect(screenWidth/2-frogWidth/2, screenHeight-2*frogHeight, screenWidth/2+frogWidth/2, screenHeight-frogHeight);
        canvas.drawBitmap(walkingFrog, src, dst, null);
        currentFrame++;
        if(currentFrame%11 == 0)
        {
            currentFrame = 0;
        }
    }
    public void setVector(int dx)
    {
        this.dx = dx;
    }

}