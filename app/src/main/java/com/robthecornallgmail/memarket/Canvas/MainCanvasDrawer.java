package com.robthecornallgmail.memarket.Canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.robthecornallgmail.memarket.Views.MainSurfaceView;

/**
 * Created by rob on 15/02/17.
 */

public class MainCanvasDrawer {

    private int screenWidth, screenHeight, middle, bottomSpot;
    private int halfFrogWidth;
    private Bitmap background, moon, stars, clouds, clouds_back, mountains, bushes, walkingFrog;
    private int x_moon, y_moon;
    private int background_x, background_y;
    private int currentFrame = 0, starsFrame = 0;
    private int starHeight, starWidth;
    private int frogHeight, frogWidth;
    private int x_clouds, x_clouds_back, x_mountains, x_bushes, x_frog, y, dx;
    private MainSurfaceView ov;

    public MainCanvasDrawer(Bitmap background, Bitmap moon, Bitmap stars, Bitmap clouds, Bitmap clouds_back, Bitmap mountains, Bitmap bushes, Bitmap walkingFrog, int screenWidth, int screenHeight) {
        this.background = background;
        this.moon = moon;
        this.stars = stars;
        this.clouds = clouds;
        this.clouds_back = clouds_back;
        this.mountains = mountains;
        this.bushes = bushes;
        this.walkingFrog = walkingFrog;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        x_moon = (int) (0.7*screenWidth);
        y_moon = (int) (0.14*screenHeight);
        starHeight = stars.getHeight();
        starWidth = stars.getWidth()/3;
        frogHeight = walkingFrog.getHeight();
        frogWidth = walkingFrog.getWidth()/10;
        halfFrogWidth = frogWidth/2;
        middle =screenWidth/2-halfFrogWidth;
        bottomSpot = screenHeight-2*frogHeight;
    }

    public void onDraw(Canvas canvas) {
        x_clouds+=dx+1;
        x_clouds_back += dx+3;
        x_bushes += dx-1;
        x_mountains+=(dx+3);

        if(x_clouds<-MainSurfaceView.SCREEN_WIDTH) {
            x_clouds=0;
        }
        if(x_mountains<-MainSurfaceView.SCREEN_WIDTH) {
            x_mountains=0;
        }
        if(x_clouds_back<-MainSurfaceView.SCREEN_WIDTH) {
            x_clouds_back=0;
        }
        if(x_bushes<-MainSurfaceView.SCREEN_WIDTH) {
            x_bushes=0;
        }
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(background,0,0,null);
        if(starsFrame < 8)
        {
            Rect src_stars = new Rect(0, 0, starWidth, starHeight);
            Rect dst_stars = new Rect(-10, -10, 10+starWidth, 10+starHeight);
            canvas.drawBitmap(stars, src_stars, dst_stars, null);
        } else if(starsFrame < 11) {
            Rect src_stars = new Rect(starWidth, 0, starWidth*2, starHeight);
            Rect dst_stars = new Rect(-10, -10, 10+starWidth, 10+starHeight);
            canvas.drawBitmap(stars, src_stars, dst_stars, null);
        } else if (starsFrame > 17){
            Rect src_stars = new Rect(starWidth*2, 0, starWidth*3, starHeight);
            Rect dst_stars = new Rect(-10, -10, 10+starWidth, 10+starHeight);
            canvas.drawBitmap(stars, src_stars, dst_stars, null);
        }else{
            Rect src_stars = new Rect(0, 0, starWidth, starHeight);
            Rect dst_stars = new Rect(-10, -10, 10+starWidth, 10+starHeight);
            canvas.drawBitmap(stars, src_stars, dst_stars, null);
        }
        canvas.drawBitmap(moon,x_moon,y_moon,null);
        canvas.drawBitmap(clouds_back, x_clouds_back, y, null);
        if(x_clouds_back<0)
        {
            canvas.drawBitmap(clouds_back, x_clouds_back + MainSurfaceView.SCREEN_WIDTH, y, null);
        }
        canvas.drawBitmap(clouds, x_clouds, y, null);
        if(x_clouds<0)
        {
            canvas.drawBitmap(clouds, x_clouds + MainSurfaceView.SCREEN_WIDTH, y, null);
        }
        canvas.drawBitmap(mountains, x_mountains, y, null);
        if(x_mountains<0)
        {
            canvas.drawBitmap(mountains, x_mountains + MainSurfaceView.SCREEN_WIDTH, y, null);
        }
        int srcX_stars = starsFrame*starWidth;
        int srcY_stars = 0;
        int srcX_frog = currentFrame*frogWidth;
        int srcY_frog = 0;
        Rect src = new Rect(srcX_frog, srcY_frog, srcX_frog + frogWidth-2, srcY_frog+ frogHeight);
        Rect dst = new Rect(middle, bottomSpot, middle+frogWidth, screenHeight-frogHeight);
        canvas.drawBitmap(walkingFrog, src, dst, null);
        canvas.drawBitmap(bushes, x_bushes, y, null);
        if(x_bushes<0)
        {
            canvas.drawBitmap(bushes, x_bushes + MainSurfaceView.SCREEN_WIDTH, y, null);
        }
        currentFrame++; starsFrame++;
        if(currentFrame%10 == 0)
        {
            currentFrame = 0;
        }
        if(starsFrame%20 == 0)
        {
            starsFrame = 0;
        }
    }
    public void setVector(int dx)
    {
        this.dx = dx;
    }


}