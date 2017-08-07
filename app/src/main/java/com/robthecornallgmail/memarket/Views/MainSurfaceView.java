package com.robthecornallgmail.memarket.Views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Canvas.MainCanvasDrawer;
import com.robthecornallgmail.memarket.Threads.MainThread;
import com.robthecornallgmail.memarket.Util.MyHelper;

import static java.lang.Thread.sleep;

/**
 * Created by rob on 16/02/17.
 */

public class MainSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "MainSurfaceView";
    public static final int WIDTH = 512;
    public static final int HEIGHT = 768;
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    Boolean isItOK = false;
    private MainThread thread;
    SurfaceHolder holder;
    private MainCanvasDrawer mainCanvasDrawer;
    private Bitmap background, moon, stars, clouds_front, clouds_back, mountains, bushes, walkingFrog;


    public MainSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        SCREEN_WIDTH = displaymetrics.widthPixels;
        SCREEN_HEIGHT = displaymetrics.heightPixels;
//        this.setBackgroundColor(Color.TRANSPARENT);
//        this.setZOrderOnTop(true);

        holder = getHolder();
//        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(this);

        setFocusable(true);

//        setting bitmaps:

    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(TAG, "MainSurfaceView surfaceDestroyed()");
        boolean retry = true;
        while(retry) {
            try {
                thread.setRunning(false);
                Log.v(TAG,"calling join");
                thread.join();
                thread = null;
                Log.v(TAG, "thread has joined");
            } catch (InterruptedException e) {
                Log.e(TAG, "failed to stop thread?");
                e.printStackTrace();
            }
            retry = false;
        }

        //free memory
        free();
        mainCanvasDrawer = null;

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "MainSurfaceView surfaceCreated()");
        background = MyHelper.decodeSampledBitmapFromResource(getResources(), R.drawable.start_background, SCREEN_HEIGHT);
        background = Bitmap.createScaledBitmap(background,SCREEN_WIDTH,SCREEN_HEIGHT, true);

        final int MOON_HEIGHT = SCREEN_HEIGHT/9;
        moon = MyHelper.decodeSampledBitmapFromResource(getResources(), R.drawable.moon,MOON_HEIGHT);
        float SCALE_FACTOR = (float)MOON_HEIGHT/(float)moon.getHeight();
        moon = Bitmap.createScaledBitmap(moon, (int)((float)moon.getWidth()*SCALE_FACTOR), MOON_HEIGHT, true);

        final int STARS_HEIGHT = (int) (SCREEN_HEIGHT*0.9);
        stars = MyHelper.decodeSampledBitmapFromResource(getResources(), R.drawable.moving_stars_cutoff, STARS_HEIGHT);
        SCALE_FACTOR = (float)STARS_HEIGHT/(float)stars.getHeight();
        stars = Bitmap.createScaledBitmap(stars, (int)((float)stars.getWidth()*SCALE_FACTOR), STARS_HEIGHT, true);

        clouds_front = MyHelper.decodeSampledBitmapFromResource(getResources(), R.drawable.clouds, SCREEN_HEIGHT);
        clouds_front = Bitmap.createScaledBitmap(clouds_front, SCREEN_WIDTH, SCREEN_HEIGHT, true);
        clouds_front = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.clouds),SCREEN_WIDTH,SCREEN_HEIGHT, true);


        clouds_back = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.clouds_back),SCREEN_WIDTH,SCREEN_HEIGHT, true);
        mountains = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.moving_mountains),SCREEN_WIDTH,SCREEN_HEIGHT, true);
        bushes = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.moving_bushes),SCREEN_WIDTH,SCREEN_HEIGHT, true);
        walkingFrog = BitmapFactory.decodeResource(getResources(), R.drawable.main_frog_business_walking_10_frames);


        mainCanvasDrawer = new MainCanvasDrawer(
                background,
                moon,
                stars,
                clouds_front,
                clouds_back,
                mountains,
                bushes,
                walkingFrog,
                SCREEN_WIDTH,SCREEN_HEIGHT);

        mainCanvasDrawer.setVector(-4);
        thread = new MainThread(holder, this);
        thread.start();
        Log.v(TAG, "Thread has started");
        thread.setRunning(true);
    }

//        @Override
//        public void run() {
//            spriteClouds = new MainCanvasDrawer(this, movingClouds);
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
            mainCanvasDrawer.onDraw(canvas);
        }
    }


    public void free() {
        // fix memory leak issues..

        if(background!=null) {
            background.recycle();
            background =null;
        }
        if(moon!=null) {
            moon.recycle();
            moon = null;
        }
        if(stars!=null) {
            stars.recycle();
            stars = null;
        }
        if(clouds_front!=null) {
            clouds_front.recycle();
            clouds_front = null;
        }
        if(clouds_back!=null) {
            clouds_back.recycle();
            clouds_back = null;
        }
        if(mountains!=null) {
            mountains.recycle();
            mountains = null;
        }
        if(bushes!=null) {
            bushes.recycle();
            bushes = null;
        }
        if(walkingFrog!=null) {
            walkingFrog.recycle();
            walkingFrog = null;
        }
    }
}
