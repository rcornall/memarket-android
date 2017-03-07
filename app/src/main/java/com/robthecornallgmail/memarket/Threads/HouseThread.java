package com.robthecornallgmail.memarket.Threads;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.robthecornallgmail.memarket.Views.HouseSurfaceView;
import com.robthecornallgmail.memarket.Views.MainSurfaceView;

/**
 * Created by rob on 03/03/17.
 */

public class HouseThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private HouseSurfaceView houseSurfaceView;
    private Canvas canvas;
    private boolean running;

    public HouseThread(SurfaceHolder surfaceHolder, HouseSurfaceView houseSurfaceView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.houseSurfaceView = houseSurfaceView;
    }
    @Override
    public void run() {
        long startTime, doneTime;
        while(running){
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
                if (timeDelta >= 16) {
                    continue;
                } else {
                    this.sleep(16-timeDelta);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

    public void setRunning(boolean b) {
        running = b;
    }
}
