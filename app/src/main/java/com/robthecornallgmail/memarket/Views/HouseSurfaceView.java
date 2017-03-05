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

import com.robthecornallgmail.memarket.Canvas.HouseCanvasDrawer;
import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Threads.HouseThread;

/**
 * Created by rob on 03/03/17.
 */

public class HouseSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private String TAG = "HouseSurfaceView";

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    private SurfaceHolder holder;
    private HouseCanvasDrawer houseCanvasDrawer;
    private HouseThread houseThread;

    private Bitmap background;

    public HouseSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        SCREEN_WIDTH = displaymetrics.widthPixels;
        SCREEN_HEIGHT = displaymetrics.heightPixels;
        holder = getHolder();
        holder.addCallback(this);

        setFocusable(true);
        background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.house_background),SCREEN_WIDTH,SCREEN_HEIGHT, true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        houseCanvasDrawer = new HouseCanvasDrawer(background, SCREEN_WIDTH, SCREEN_HEIGHT);

        houseThread = new HouseThread(holder, this);
        houseThread.start();
        houseThread.setRunning(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry) {
            try {
                houseThread.setRunning(false);
                houseThread.join();
                houseThread = null;
                Log.v(TAG, "thread has joined");
            } catch (InterruptedException e) {
                Log.e(TAG, "failed to stop thread?");
                e.printStackTrace();
            }
            retry = false;
        }
    }

    public void draw(Canvas canvas) {
        if (canvas != null) {
            houseCanvasDrawer.onDraw(canvas);
        }
    }
}
