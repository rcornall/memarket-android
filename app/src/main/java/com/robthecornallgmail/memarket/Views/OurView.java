package com.robthecornallgmail.memarket.Views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.robthecornallgmail.memarket.Activities.MainActivity;
import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Sprites.Clouds;
import com.robthecornallgmail.memarket.Threads.MainThread;

/**
 * Created by rob on 16/02/17.
 */

public class OurView extends SurfaceView implements SurfaceHolder.Callback {
    public static final int WIDTH = 512;
    public static final int HEIGHT = 768;
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    Boolean isItOK = false;
    private MainThread thread;
    SurfaceHolder holder;
    private Clouds clouds;



    public OurView(Context context)
    {
        super(context);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        SCREEN_WIDTH = displaymetrics.widthPixels;
        SCREEN_HEIGHT = displaymetrics.heightPixels;
        holder = getHolder();
//        holder.setFormat(PixelFormat.TRANSLUCENT);
        holder.addCallback(this);

        thread = new MainThread(holder, this);
    }
    public OurView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        SCREEN_WIDTH = displaymetrics.widthPixels;
        SCREEN_HEIGHT = displaymetrics.heightPixels;
        this.setBackgroundColor(Color.TRANSPARENT);
        this.setZOrderOnTop(true);

        holder = getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(this);

        thread = new MainThread(holder, this);
        setFocusable(true);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
        thread.setRunning(false);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        clouds = new Clouds(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.clouds),SCREEN_WIDTH,SCREEN_HEIGHT, true),
                Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.moving_mountains),SCREEN_WIDTH,SCREEN_HEIGHT, true),
                BitmapFactory.decodeResource(getResources(), R.drawable.main_frog_business_walking),
                SCREEN_WIDTH,SCREEN_HEIGHT);

        clouds.setVector(-4);

        thread.start();
        thread.setRunning(true);
    }

//        @Override
//        public void run() {
//            spriteClouds = new Clouds(this, movingClouds);
//            while (isItOK ) {
//                if (!holder.getSurface().isValid()) {
//                    continue;
//                }
//                Canvas c = holder.lockCanvas();
//                drawStuff(c);
//                holder.unlockCanvasAndPost(c);
//
//            }
//        }

//        public void pause(){
//            isItOK = false;
//            while (true){
//                try {
//                    t.join();
//                }catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
//            t = null;
//        }
//        public void resume() {
//            isItOK = true;
//            t = new Thread(this);
//            t.start();
//        }


    public void draw(Canvas canvas) {
        if (canvas != null) {
            clouds.onDraw(canvas);
        }
    }
}
