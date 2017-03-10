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
import com.robthecornallgmail.memarket.Util.MyHelper;

/**
 * Created by rob on 03/03/17.
 */

public class HouseSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private String TAG = "HouseSurfaceView";

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    private int SKY_SIZE_X, SKY_SIZE_Y;
    private int MAX_X, MIN_X, MAX_Y, MIN_Y;
    private int VIEW_HEIGHT;
    boolean first = true;

    private ScaleGestureDetector mScaleDetector; // for pinch zoom
    private InteractionMode mMode; // for touchevents
    Matrix mMatrixSky = new Matrix(); // for calculating where to put image after pan/zoom
    Matrix mMatrixGround = new Matrix(); // for calculating where to put image after pan/zoom
    Matrix mMatrixGuy = new Matrix();

    private SurfaceHolder holder;
    private HouseCanvasDrawer houseCanvasDrawer;
    private HouseThread houseThread;

    private Bitmap background, ground, guy;
    private Bitmap house;
    private Bitmap cloud1;

    private class ObjectCoordinates{
        public float lastY = 0, backupx = 0;
        public float lastX = 0, backupy = 0;

        public void setBackupXY(float x, float y) {
            this.backupx = x;
            this.backupy = y;
        }

        public void setLastXY(float x, float y) {
            this.lastX = x;
            this.lastY = y;
        }
    }
    private class movementCoordinates {
        public float x = 0, dx = 0;
        public float y = 0, dy = 0;
        public void setXY(float x , float y) {
            this.x = x;
            this.y = y;
        }
        public void setdxdy(float dx, float dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    ObjectCoordinates backgroundCoordinates, groundCoordinates, guyCoordinates, houseCoordinates;
    ObjectCoordinates cloud1Coordinates;
    movementCoordinates touchCoordinates;
    Bitmap test1;
    Bitmap test2;
    Bitmap test3;
    public HouseSurfaceView(Context context, AttributeSet attrs) {
        // If we scale everything to screen size of users phone,
        // then sprites and map size should all be scaled relatively to each other
        // but if screen size is not 16:9 then sprites may be stretched, which is ok.
        super(context, attrs);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        SCREEN_WIDTH = displaymetrics.widthPixels;
        SCREEN_HEIGHT = displaymetrics.heightPixels;
        SKY_SIZE_X = SCREEN_WIDTH*3;
        SKY_SIZE_Y = (int)(SCREEN_HEIGHT*1.5);
        MAX_X = 0;
        MIN_X = -(SKY_SIZE_X - SCREEN_WIDTH);
        MAX_Y = 0;
        MIN_Y = -(SKY_SIZE_Y - SCREEN_HEIGHT);
        holder = getHolder();
        holder.addCallback(this);

        setFocusable(true);

        // scale the background to 1.5 times the height, this makes for a scrollable up/down screen
        background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.house_background), SKY_SIZE_X/100, SKY_SIZE_Y, true);

        int newheight = SCREEN_HEIGHT/8;
        Bitmap tmpGround = BitmapFactory.decodeResource(getResources(), R.drawable.ground1);
        float scaleFactor = (float)newheight/(float)tmpGround.getHeight();
//        Log.v(TAG, String.format("scaling stuff, newheight=%d, tmpGround.getHeight=%d", newheight,tmpGround.getHeight()));
//        Log.v(TAG, String.format("scaling stuff, scalefactor=%f, tmpgroundwidth*scalefactor=%d", scaleFactor, (int) (tmpGround.getWidth()*scaleFactor)));
//        ground = Bitmap.createScaledBitmap(tmpGround,(int)(((float)tmpGround.getWidth())*scaleFactor),newheight,true);
        ground = Bitmap.createScaledBitmap(tmpGround,(int)(((float)tmpGround.getWidth())*scaleFactor),newheight,true);
        tmpGround.recycle();
        tmpGround = null;

        newheight = SCREEN_HEIGHT/6;
        Bitmap tmpGuy = BitmapFactory.decodeResource(getResources(), R.drawable.ground_frog);
        scaleFactor = (float)newheight/(float)tmpGuy.getHeight();
        guy = Bitmap.createScaledBitmap(tmpGuy,(int)(((float)tmpGuy.getWidth())*scaleFactor),newheight,true);
        tmpGuy.recycle();
        tmpGuy = null;

        newheight = SCREEN_HEIGHT/2;
        Bitmap tmpHouse = BitmapFactory.decodeResource(getResources(), R.drawable.house1);
        scaleFactor = (float)newheight/(float)tmpHouse.getHeight();
        house = Bitmap.createScaledBitmap(tmpHouse,(int)(((float)tmpHouse.getWidth())*scaleFactor),newheight,true);
        tmpHouse.recycle();
        tmpHouse = null;


        newheight = SCREEN_HEIGHT/8;
        Bitmap tmpCloud = BitmapFactory.decodeResource(getResources(), R.drawable.cloud_houseview);
        scaleFactor = (float)newheight/(float)tmpCloud.getHeight();
        cloud1 = Bitmap.createScaledBitmap(tmpCloud,(int)(((float)tmpCloud.getWidth())*scaleFactor),newheight,true);
        tmpCloud.recycle();
        tmpCloud = null;



        backgroundCoordinates = new ObjectCoordinates();
        groundCoordinates = new ObjectCoordinates();
        guyCoordinates = new ObjectCoordinates();
        houseCoordinates = new ObjectCoordinates();
        cloud1Coordinates = new ObjectCoordinates();
        touchCoordinates = new movementCoordinates();
        mScaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener());

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        backgroundCoordinates.lastX = - (float) ( (SKY_SIZE_X - this.SCREEN_WIDTH)/2 );
        backgroundCoordinates.lastY = - (float) (SKY_SIZE_Y - this.SCREEN_HEIGHT);
        groundCoordinates.lastX = - (float) ( (SKY_SIZE_X - this.SCREEN_WIDTH)/2 );
        groundCoordinates.lastY = SCREEN_HEIGHT-ground.getHeight()-100;
        guyCoordinates.lastX = SCREEN_WIDTH/4;
        guyCoordinates.lastY = SCREEN_HEIGHT-ground.getHeight()-100 -  guy.getHeight();
        houseCoordinates.lastX = SCREEN_WIDTH/2 - guy.getWidth()/5;
        houseCoordinates.lastY = SCREEN_HEIGHT-ground.getHeight()-100 - house.getHeight();
        cloud1Coordinates.lastX = SCREEN_WIDTH-SCREEN_WIDTH/3;
        cloud1Coordinates.lastY = SCREEN_HEIGHT/18;
        Log.v(TAG, String.format("backgroundheight = %d, screenheight = %d", background.getHeight(), SCREEN_HEIGHT));
        Log.v(TAG, String.format("we place background initially at x = %f, y = %f", backgroundCoordinates.lastX, backgroundCoordinates.lastY));

        CalculateMatrix(true);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        houseCanvasDrawer = new HouseCanvasDrawer(background,
                                                    cloud1,
                                                    ground,
                                                    guy,
                                                    house,
                                                    SCREEN_WIDTH,SCREEN_HEIGHT,test1,
                test2,
                test3
                                                    );

        houseThread = new HouseThread(holder, this);
        houseThread.start();
        houseThread.setRunning(true);

        this.post(new Runnable() {
            @Override
            public void run() {
                if (first) {
                    first = false;
                    VIEW_HEIGHT = getHeight();
                    houseCanvasDrawer.setHeight(VIEW_HEIGHT);
                    Log.v(TAG, String.format("HEIGHT OF VIEW= %d", VIEW_HEIGHT));
                    groundCoordinates.lastY = VIEW_HEIGHT - ground.getHeight();
                    guyCoordinates.lastY = VIEW_HEIGHT - ground.getHeight() - guy.getHeight() + ground.getHeight() / 14;
                    houseCoordinates.lastY = VIEW_HEIGHT - ground.getHeight() - house.getHeight() + ground.getHeight() / 14;
                }
//                CalculateMatrix(true);
            }
        });
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
//            if (first) {
//                VIEW_HEIGHT = canvas.getHeight(); //need to get height at runtime..
//                Log.v(TAG, String.format("HEIGHT OF VIEW(draw)= %d" , VIEW_HEIGHT));
//                first = false;
//
//            }
            houseCanvasDrawer.onDraw(canvas,
                    backgroundCoordinates.lastX, backgroundCoordinates.lastY,
                    groundCoordinates.lastX, groundCoordinates.lastY,
                    guyCoordinates.lastX, guyCoordinates.lastY,
                    cloud1Coordinates.lastX, cloud1Coordinates.lastY,
                    houseCoordinates.lastX, houseCoordinates.lastY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x, y;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.v(TAG, String.format("dimens are: x = %f, y = %f", backgroundCoordinates.lastX, backgroundCoordinates.lastY));
                x = event.getX(); y = event.getY();
                Log.v(TAG, String.format("PLZ, %f, %f", x,y));
                touchCoordinates.setXY(x, y);
                backgroundCoordinates.setBackupXY(backgroundCoordinates.lastX, backgroundCoordinates.lastY);
                groundCoordinates.setBackupXY(groundCoordinates.lastX,groundCoordinates.lastY);
                guyCoordinates.setBackupXY(guyCoordinates.lastX,guyCoordinates.lastY);
                houseCoordinates.setBackupXY(houseCoordinates.lastX,houseCoordinates.lastY);
                cloud1Coordinates.setBackupXY(cloud1Coordinates.lastX,cloud1Coordinates.lastY);
                mMode = InteractionMode.PAN;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == InteractionMode.PAN) {
                    float new_x = event.getX(); float new_y = event.getY();
                    Log.v(TAG, String.format("backgroundCoordinates.x and y are: %f, %f. \nnewxy are: %f, %f", touchCoordinates.x, touchCoordinates.x, new_x, new_y));
                    touchCoordinates.setdxdy( new_x - touchCoordinates.x, new_y - touchCoordinates.y);

                    Log.v(TAG, String.format("x and y are: %f, %f. \ndx and dy are: %f, %f", new_x, new_y, touchCoordinates.dx, touchCoordinates.dy));
                    //lock the surfaceview area to move around the size of the sky width and height.
                    if (backgroundCoordinates.backupx+touchCoordinates.dx > MAX_X) {
                        float diff = MAX_X-backgroundCoordinates.lastX;
                        backgroundCoordinates.lastX = backgroundCoordinates.lastX + diff;
                        groundCoordinates.lastX = groundCoordinates.lastX + diff;
                        guyCoordinates.lastX = guyCoordinates.lastX + diff;
                        houseCoordinates.lastX = houseCoordinates.lastX + diff;
                        cloud1Coordinates.lastX = cloud1Coordinates.lastX + diff;

                        backgroundCoordinates.backupx = backgroundCoordinates.lastX;
                        groundCoordinates.backupx = groundCoordinates.lastX;
                        guyCoordinates.backupx = guyCoordinates.lastX;
                        houseCoordinates.backupx = houseCoordinates.lastX;
                        cloud1Coordinates.backupx = cloud1Coordinates.lastX;
                        touchCoordinates.x = new_x;
                    } else if (backgroundCoordinates.backupx+touchCoordinates.dx < MIN_X) {
                        float diff = MIN_X-backgroundCoordinates.lastX;
                        backgroundCoordinates.lastX = backgroundCoordinates.lastX + diff;
                        groundCoordinates.lastX = groundCoordinates.lastX + diff;
                        guyCoordinates.lastX = guyCoordinates.lastX + diff;
                        houseCoordinates.lastX = houseCoordinates.lastX + diff;
                        cloud1Coordinates.lastX = cloud1Coordinates.lastX + diff;

                        backgroundCoordinates.backupx = backgroundCoordinates.lastX;
                        groundCoordinates.backupx = groundCoordinates.lastX;
                        guyCoordinates.backupx = guyCoordinates.lastX;
                        houseCoordinates.backupx = houseCoordinates.lastX;
                        cloud1Coordinates.backupx = cloud1Coordinates.lastX;
                        touchCoordinates.x = new_x;
                    } else {
                        backgroundCoordinates.lastX = backgroundCoordinates.backupx+touchCoordinates.dx;
                        groundCoordinates.lastX = groundCoordinates.backupx+touchCoordinates.dx;
                        guyCoordinates.lastX = guyCoordinates.backupx+touchCoordinates.dx;
                        houseCoordinates.lastX = houseCoordinates.backupx+touchCoordinates.dx;
                        cloud1Coordinates.lastX = cloud1Coordinates.backupx+touchCoordinates.dx;
                    }
                    if (backgroundCoordinates.backupy+touchCoordinates.dy > MAX_Y) {
                        float diff = MAX_Y-backgroundCoordinates.lastY;
                        backgroundCoordinates.lastY = backgroundCoordinates.lastY + diff;
                        groundCoordinates.lastY = groundCoordinates.lastY + diff;
                        guyCoordinates.lastY = guyCoordinates.lastY + diff;
                        houseCoordinates.lastY = houseCoordinates.lastY + diff;
                        cloud1Coordinates.lastY = cloud1Coordinates.lastY + diff;

                        backgroundCoordinates.backupy = backgroundCoordinates.lastY;
                        groundCoordinates.backupy = groundCoordinates.lastY;
                        guyCoordinates.backupy = guyCoordinates.lastY;
                        houseCoordinates.backupy = houseCoordinates.lastY;
                        cloud1Coordinates.backupy = cloud1Coordinates.lastY;
                        touchCoordinates.y = new_y;
                    } else if (backgroundCoordinates.backupy+touchCoordinates.dy < MIN_Y) {
                        float diff = MIN_Y-backgroundCoordinates.lastY;
                        backgroundCoordinates.lastY = backgroundCoordinates.lastY + diff;
                        groundCoordinates.lastY = groundCoordinates.lastY + diff;
                        guyCoordinates.lastY = guyCoordinates.lastY + diff;
                        houseCoordinates.lastY = houseCoordinates.lastY + diff;
                        cloud1Coordinates.lastY = cloud1Coordinates.lastY + diff;

                        backgroundCoordinates.backupy = backgroundCoordinates.lastY;
                        groundCoordinates.backupy = groundCoordinates.lastY;
                        guyCoordinates.backupy = guyCoordinates.lastY;
                        houseCoordinates.backupy = houseCoordinates.lastY;
                        cloud1Coordinates.backupy = cloud1Coordinates.lastY;
                        touchCoordinates.y = new_y;
                    } else {
                        backgroundCoordinates.lastY = backgroundCoordinates.backupy+touchCoordinates.dy;
                        groundCoordinates.lastY = groundCoordinates.backupy+touchCoordinates.dy;
                        guyCoordinates.lastY = guyCoordinates.backupy+touchCoordinates.dy;
                        houseCoordinates.lastY = houseCoordinates.backupy+touchCoordinates.dy;
                        cloud1Coordinates.lastY = cloud1Coordinates.backupy+touchCoordinates.dy;
                    }
//                    CalculateMatrix(true);
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
        mMatrixSky.reset(); mMatrixGround.reset(); mMatrixGuy.reset();
        mMatrixSky.postTranslate(backgroundCoordinates.lastX, backgroundCoordinates.lastY);
        mMatrixGround.postTranslate(groundCoordinates.lastX, groundCoordinates.lastY);
        mMatrixGuy.postTranslate(guyCoordinates.lastX, guyCoordinates.lastY);

    }


}
