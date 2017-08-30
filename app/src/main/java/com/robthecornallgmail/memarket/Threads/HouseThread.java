package com.robthecornallgmail.memarket.Threads;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import com.robthecornallgmail.memarket.Views.HouseSurfaceView;
import com.robthecornallgmail.memarket.Views.MainSurfaceView;

/**
 * Created by rob on 03/03/17.
 */

public class HouseThread extends Thread {
    String TAG = "HouseThread";
    private final SurfaceHolder surfaceHolder;
    private HouseSurfaceView houseSurfaceView;
    private Canvas canvas;
    long startTime, doneTime;
    private volatile boolean running = false;

    public HouseThread(SurfaceHolder surfaceHolder, HouseSurfaceView houseSurfaceView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.houseSurfaceView = houseSurfaceView;
    }
    @Override
    public void run() {

        running = true;
        while(running){
        Log.v(TAG, "run()");
            canvas = null;
            startTime = System.currentTimeMillis();
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.houseSurfaceView.draw(canvas);
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
                if (timeDelta >= 20) {
                    continue;
                } else {
                    sleep(20-timeDelta);
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
