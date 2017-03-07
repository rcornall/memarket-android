package com.robthecornallgmail.memarket.Views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.robthecornallgmail.memarket.Canvas.HouseCanvasDrawer;
import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Threads.HouseThread;
import com.robthecornallgmail.memarket.Util.InteractionMode;

/**
 * Created by rob on 03/03/17.
 */

public class HouseSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private String TAG = "HouseSurfaceView";

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    private int IMAGE_SIZE_X, IMAGE_SIZE_Y;
    private int MAX_X, MIN_X, MAX_Y, MIN_Y;

    private ScaleGestureDetector mScaleDetector; // for pinch zoom
    private InteractionMode mMode; // for touchevents
    Matrix mMatrix = new Matrix(); // for calculating where to put image after pan/zoom

    private SurfaceHolder holder;
    private HouseCanvasDrawer houseCanvasDrawer;
    private HouseThread houseThread;

    private Bitmap background;
    private class Movements{
        public float x = 0, lastY = 0, backupx = 0, dx = 0;
        public float y = 0, lastX = 0, backupy = 0, dy = 0;

        public void setXY(float x , float y) {
            this.x = x;
            this.y = y;
        }

        public void setdxdy(float dx, float dy) {
            this.dx = dx;
            this.dy = dy;
        }

        public void setBackupXY(float x, float y) {
            this.backupx = x;
            this.backupy = y;
        }

        public void setLastXY(float x, float y) {
            this.lastX = x;
            this.lastY = y;
        }
    }
    Movements moveObj;

    public HouseSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        SCREEN_WIDTH = displaymetrics.widthPixels;
        SCREEN_HEIGHT = displaymetrics.heightPixels;
        IMAGE_SIZE_X = SCREEN_WIDTH*3;
        IMAGE_SIZE_Y = (int)(SCREEN_HEIGHT*1.5);
        MAX_X = 0;
        MIN_X = -(IMAGE_SIZE_X - SCREEN_WIDTH);
        MAX_Y = 0;
        MIN_Y = -(IMAGE_SIZE_Y - SCREEN_HEIGHT);
        holder = getHolder();
        holder.addCallback(this);

        setFocusable(true);
        // scale the background to 1.5 times the height, this makes for a scrollable up/down screen
        background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.house_background), IMAGE_SIZE_X, IMAGE_SIZE_Y, true);
        moveObj = new Movements();
        mScaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener());

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        moveObj.lastX = - (float) ( (background.getWidth() - this.SCREEN_WIDTH)/2 );
        moveObj.lastY = - (float) (background.getHeight() - this.SCREEN_HEIGHT);
        Log.v(TAG, String.format("backgroundheight = %d, screenheight = %d", background.getHeight(), SCREEN_HEIGHT));
        Log.v(TAG, String.format("we place background initially at x = %f, y = %f", moveObj.lastX, moveObj.lastY));

        CalculateMatrix(true);
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
            houseCanvasDrawer.onDraw(canvas,mMatrix);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x, y;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.v(TAG, String.format("dimens are: x = %f, y = %f", moveObj.lastX, moveObj.lastY));
                x = event.getX(); y = event.getY();
                Log.v(TAG, String.format("PLZ, %f, %f", x,y));
                moveObj.setXY(x, y);
                moveObj.setBackupXY(moveObj.lastX, moveObj.lastY);
                mMode = InteractionMode.PAN;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == InteractionMode.PAN) {
                    float new_x = event.getX(); float new_y = event.getY();
                    Log.v(TAG, String.format("moveObj.x and y are: %f, %f. \nnewxy are: %f, %f", moveObj.x, moveObj.x, new_x, new_y));
                    moveObj.setdxdy( new_x - moveObj.x, new_y - moveObj.y);
                    Log.v(TAG, String.format("x and y are: %f, %f. \ndx and dy are: %f, %f", new_x, new_y, moveObj.dx, moveObj.dy));
                    if (moveObj.backupx+moveObj.dx > MAX_X) {
                        moveObj.lastX = MAX_X;
                    } else if (moveObj.backupx+moveObj.dx < MIN_X) {
                        moveObj.lastX = MIN_X;
                    } else {
                        moveObj.lastX = moveObj.backupx+moveObj.dx;
                    }
                    if (moveObj.backupy+moveObj.dy > MAX_Y) {
                        moveObj.lastY = MAX_Y;
                    } else if (moveObj.backupy+moveObj.dy < MIN_Y) {
                        moveObj.lastY = MIN_Y;
                    } else {
                        moveObj.lastY = moveObj.backupy+moveObj.dy;
                    }
                    CalculateMatrix(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                mMode = InteractionMode.NONE;
                // tap( calling tap method incase we decide to add in the future.);
                break;
        }
        return true;
    }

    void CalculateMatrix(boolean invalidate) {
        mMatrix.reset();

        mMatrix.postTranslate(moveObj.lastX, moveObj.lastY);

    }


}
