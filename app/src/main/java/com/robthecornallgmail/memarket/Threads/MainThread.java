package com.robthecornallgmail.memarket.Threads;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import com.robthecornallgmail.memarket.Sprites.Clouds;
import com.robthecornallgmail.memarket.Views.OurView;

/**
 * Created by rob on 15/02/17.
 */

public class MainThread extends  Thread {

    public static Canvas canvas;
    private SurfaceHolder surfaceHolder;
    public boolean running;
    private OurView ourView;

    public MainThread(SurfaceHolder surfaceHolder, OurView ourView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.ourView = ourView;
    }

    @Override
    public void run() {

        while(running){
            canvas = null;
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.ourView.draw(canvas);
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

            try {
                this.sleep(80);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void setRunning(boolean b) {
        running = b;
    }
}
