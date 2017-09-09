package com.robthecornallgmail.memarket.Threads;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.robthecornallgmail.memarket.Views.HouseSurfaceView;
import com.robthecornallgmail.memarket.Views.MainSurfaceView;

/**
 * Created by rob on 03/03/17.
 */

public class HouseThread extends Thread {
    String TAG = "HouseThread";
    private final SurfaceHolder surfaceHolder;
    private SurfaceView mHouseSurfaceView;
    private Canvas canvas;
    long startTime, doneTime;
    int frameTime = 18;
    private volatile boolean running = false;

    public HouseThread(SurfaceHolder surfaceHolder, SurfaceView houseSurfaceView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.mHouseSurfaceView = houseSurfaceView;
    }
    @Override
    public void run() {

        running = true;
        while(running){
            try {
                /* sleeping here fixes bug where UI gets blocked trying to join this thread some reason */
                sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            canvas = null;
            startTime = System.currentTimeMillis();
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.mHouseSurfaceView.draw(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(canvas != null)
                {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // sleep the remaining amount to get 33ms framerate, or 30 fps
            try {
                doneTime = System.currentTimeMillis();
                long timeDelta = doneTime-startTime;
                if (timeDelta >= frameTime) {
                    continue;
                } else {
                    sleep(frameTime-timeDelta);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

    public void setRunning(boolean b) {
        running = b;
        Log.v(TAG, "set running"+b);
    }
    public boolean isRunning() {
        return running;
    }
}
