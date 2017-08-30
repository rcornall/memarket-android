package com.robthecornallgmail.memarket.Threads;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.robthecornallgmail.memarket.Views.MainSurfaceView;


/**
 * Created by rob on 15/02/17.
 */

public class MainThread extends  Thread {

    private static final int FRAMERATE = 10; //10 fps for pixel animations
    private static Canvas canvas;
    private final SurfaceHolder surfaceHolder;
    private boolean running;
    private MainSurfaceView mainSurfaceView;

    public MainThread(SurfaceHolder surfaceHolder, MainSurfaceView mainSurfaceView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.mainSurfaceView = mainSurfaceView;
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
                    this.mainSurfaceView.draw(canvas);
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

            // sleep the remaining amount of time left after processing
            // to get 90ms framerate, or 11 ish fps
            try {
                doneTime = System.currentTimeMillis();
                long timeDelta = doneTime-startTime;
                if (timeDelta >= 90) {
                    continue;
                } else {
                    this.sleep(90-timeDelta);
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
