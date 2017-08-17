package com.robthecornallgmail.memarket.Views;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.AsyncTask;
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
import com.robthecornallgmail.memarket.Util.Office;

import static java.lang.Thread.sleep;

/**
 * Created by rob on 03/03/17.
 */

public class HouseSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private String TAG = "HouseSurfaceView";

    public static int SCREEN_WIDTH,SCREEN_HEIGHT,CANVAS_HEIGHT;

    private int SKY_SIZE_X, SKY_SIZE_Y;
    private int MAX_X, MIN_X, MAX_Y, MIN_Y;
    private int VIEW_HEIGHT;
    boolean mFirst = true;

    private InteractionMode mMode; // for touchevents
    Matrix mMatrixSky = new Matrix(); // for calculating where to put image after pan/zoom
    Matrix mMatrixGround = new Matrix(); // for calculating where to put image after pan/zoom
    Matrix mMatrixGuy = new Matrix();

    private SurfaceHolder holder;
    private HouseCanvasDrawer houseCanvasDrawer;
    private HouseThread houseThread;

    private static int GROUND_HEIGHT, GUY_HEIGHT, HOUSE_HEIGHT, OFFICE_WIDTH, CLOUD_HEIGHT;
    private static float GROUND_SCALE_FACTOR, GUY_SCALE_FACTOR, HOUSE_SCALE_FACTOR, OFFICETOP_SCALE_FACTOR,OFFICEMID_SCALE_FACTOR,OFFICEBOT_SCALE_FACTOR, CLOUD_SCALE_FACTOR;
    private Bitmap background, ground, guy;
    private Bitmap house, officeTall;
    private Bitmap cloud1;
    private Office office = new Office();

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

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

    ObjectCoordinates backgroundCoordinates, groundCoordinates, guyCoordinates, houseCoordinates, officeTallCoordinates;
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
        Log.v(TAG, "surface view constructor, sleeping to let previous activity destroy");

        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        SCREEN_WIDTH = displaymetrics.widthPixels;
        SCREEN_HEIGHT = displaymetrics.heightPixels;
        SKY_SIZE_X = (int)(SCREEN_WIDTH*3.75);
        SKY_SIZE_Y = (int)(SCREEN_HEIGHT*1.4);
        MAX_X = 0;
        MIN_X = -(SKY_SIZE_X - SCREEN_WIDTH);
        MAX_Y = 0;
        MIN_Y = -(SKY_SIZE_Y - SCREEN_HEIGHT);
        holder = getHolder();
        holder.addCallback(this);

        setFocusable(true);


//        Resources res = getResources();
//        LoadBitmaps loadBitmaps = new LoadBitmaps(res);
//        loadBitmaps.execute((Void) null);


//        CalculateMatrix(true);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CANVAS_HEIGHT = this.getHeight();

        houseThread = new HouseThread(holder, this);

        LoadBitmaps reloadBitmaps = new LoadBitmaps(getResources());
        reloadBitmaps.execute((Void)null);


//        this.post(new Runnable() {
//            @Override
//            public void run() {
//                if (first) {
//                    first = false;
//               }
//                CalculateMatrix(true);
//            }
//        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}




    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(TAG, "SURFACE DESTROYED");
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
        free();
    }

    public void draw(Canvas canvas) {
        if (canvas != null) {
            canvas.save();
            canvas.scale(mScaleFactor, mScaleFactor);
            houseCanvasDrawer.onDraw(canvas, mScaleFactor,
                    backgroundCoordinates.lastX, backgroundCoordinates.lastY,
                    groundCoordinates.lastX, groundCoordinates.lastY,
                    guyCoordinates.lastX, guyCoordinates.lastY,
                    cloud1Coordinates.lastX, cloud1Coordinates.lastY,
                    houseCoordinates.lastX, houseCoordinates.lastY,
                    officeTallCoordinates.lastX, officeTallCoordinates.lastY);
            canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x, y;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
//                Log.v(TAG, String.format("dimens are: x = %f, y = %f", backgroundCoordinates.lastX, backgroundCoordinates.lastY));
                x = event.getX(); y = event.getY();
//                Log.v(TAG, String.format("PLZ, %f, %f", x,y));
                touchCoordinates.setXY(x, y);
                backgroundCoordinates.setBackupXY(backgroundCoordinates.lastX, backgroundCoordinates.lastY);
                groundCoordinates.setBackupXY(groundCoordinates.lastX,groundCoordinates.lastY);
                guyCoordinates.setBackupXY(guyCoordinates.lastX,guyCoordinates.lastY);
                houseCoordinates.setBackupXY(houseCoordinates.lastX,houseCoordinates.lastY);
                officeTallCoordinates.setBackupXY(officeTallCoordinates.lastX,officeTallCoordinates.lastY);
                cloud1Coordinates.setBackupXY(cloud1Coordinates.lastX,cloud1Coordinates.lastY);
                mMode = InteractionMode.PAN;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == InteractionMode.PAN) {
                    float new_x = event.getX(); float new_y = event.getY();
//                    Log.v(TAG, String.format("backgroundCoordinates.x and y are: %f, %f. \nnewxy are: %f, %f", touchCoordinates.x, touchCoordinates.x, new_x, new_y));
                    touchCoordinates.setdxdy( new_x - touchCoordinates.x, new_y - touchCoordinates.y);

                    MIN_Y = (int)-(SKY_SIZE_Y - CANVAS_HEIGHT/mScaleFactor);
                    MIN_X = (int)-(SKY_SIZE_X - SCREEN_WIDTH/mScaleFactor);

//                    Log.v(TAG, String.format("x and y are: %f, %f. \ndx and dy are: %f, %f", new_x, new_y, touchCoordinates.dx, touchCoordinates.dy));
//                    Log.v(TAG, String.format("scaleFactor is: %f", mScaleFactor));
//                    Log.v(TAG, String.format("NEW MIN_Y is: %d", MIN_Y));
//                    Log.v(TAG, String.format("MAXY*scaleFactor is: %d", MAX_Y));
//                    Log.v(TAG, String.format("backgroundCoordinates.backupy+touchCoordinates.dy: %f", backgroundCoordinates.backupy+touchCoordinates.dy));

                    //lock the surfaceview area to move around the size of the sky width and height.
                    if (backgroundCoordinates.backupx+touchCoordinates.dx > MAX_X) {
                        float diff = MAX_X-backgroundCoordinates.lastX;
                        backgroundCoordinates.lastX = backgroundCoordinates.lastX + diff;
                        groundCoordinates.lastX = groundCoordinates.lastX + diff;
                        guyCoordinates.lastX = guyCoordinates.lastX + diff;
                        houseCoordinates.lastX = houseCoordinates.lastX + diff;
                        officeTallCoordinates.lastX = officeTallCoordinates.lastX + diff;
                        cloud1Coordinates.lastX = cloud1Coordinates.lastX + diff;

                        backgroundCoordinates.backupx = backgroundCoordinates.lastX;
                        groundCoordinates.backupx = groundCoordinates.lastX;
                        guyCoordinates.backupx = guyCoordinates.lastX;
                        houseCoordinates.backupx = houseCoordinates.lastX;
                        officeTallCoordinates.backupx = officeTallCoordinates.lastX;
                        cloud1Coordinates.backupx = cloud1Coordinates.lastX;
                        touchCoordinates.x = new_x;
                    } else if (backgroundCoordinates.backupx+touchCoordinates.dx < MIN_X) {
                        float diff = MIN_X-backgroundCoordinates.lastX;
                        backgroundCoordinates.lastX = backgroundCoordinates.lastX + diff;
                        groundCoordinates.lastX = groundCoordinates.lastX + diff;
                        guyCoordinates.lastX = guyCoordinates.lastX + diff;
                        houseCoordinates.lastX = houseCoordinates.lastX + diff;
                        officeTallCoordinates.lastX = officeTallCoordinates.lastX + diff;
                        cloud1Coordinates.lastX = cloud1Coordinates.lastX + diff;

                        backgroundCoordinates.backupx = backgroundCoordinates.lastX;
                        groundCoordinates.backupx = groundCoordinates.lastX;
                        guyCoordinates.backupx = guyCoordinates.lastX;
                        houseCoordinates.backupx = houseCoordinates.lastX;
                        officeTallCoordinates.backupx = officeTallCoordinates.lastX;
                        cloud1Coordinates.backupx = cloud1Coordinates.lastX;
                        touchCoordinates.x = new_x;
                    } else {
                        backgroundCoordinates.lastX = backgroundCoordinates.backupx+touchCoordinates.dx;
                        groundCoordinates.lastX = groundCoordinates.backupx+touchCoordinates.dx;
                        guyCoordinates.lastX = guyCoordinates.backupx+touchCoordinates.dx;
                        houseCoordinates.lastX = houseCoordinates.backupx+touchCoordinates.dx;
                        officeTallCoordinates.lastX = officeTallCoordinates.backupx+touchCoordinates.dx;
                        cloud1Coordinates.lastX = cloud1Coordinates.backupx+touchCoordinates.dx;
                    }
//                    MAX_Y-(((float)MIN_Y*mScaleFactor)-MIN_Y)
                    if (backgroundCoordinates.backupy+touchCoordinates.dy > MAX_Y) {
                        float diff = MAX_Y-backgroundCoordinates.lastY;
                        backgroundCoordinates.lastY = backgroundCoordinates.lastY + diff;
                        groundCoordinates.lastY = groundCoordinates.lastY + diff;
                        guyCoordinates.lastY = guyCoordinates.lastY + diff;
                        houseCoordinates.lastY = houseCoordinates.lastY + diff;
                        officeTallCoordinates.lastY = officeTallCoordinates.lastY + diff;
                        cloud1Coordinates.lastY = cloud1Coordinates.lastY + diff;

                        backgroundCoordinates.backupy = backgroundCoordinates.lastY;
                        groundCoordinates.backupy = groundCoordinates.lastY;
                        guyCoordinates.backupy = guyCoordinates.lastY;
                        houseCoordinates.backupy = houseCoordinates.lastY;
                        officeTallCoordinates.backupy = officeTallCoordinates.lastY;
                        cloud1Coordinates.backupy = cloud1Coordinates.lastY;
                        touchCoordinates.y = new_y;
                    } else if (backgroundCoordinates.backupy+touchCoordinates.dy < MIN_Y) {
                        float diff = MIN_Y-backgroundCoordinates.lastY;
                        backgroundCoordinates.lastY = backgroundCoordinates.lastY + diff;
                        groundCoordinates.lastY = groundCoordinates.lastY + diff;
                        guyCoordinates.lastY = guyCoordinates.lastY + diff;
                        houseCoordinates.lastY = houseCoordinates.lastY + diff;
                        officeTallCoordinates.lastY = officeTallCoordinates.lastY + diff;
                        cloud1Coordinates.lastY = cloud1Coordinates.lastY + diff;

                        backgroundCoordinates.backupy = backgroundCoordinates.lastY;
                        groundCoordinates.backupy = groundCoordinates.lastY;
                        guyCoordinates.backupy = guyCoordinates.lastY;
                        houseCoordinates.backupy = houseCoordinates.lastY;
                        officeTallCoordinates.backupy = officeTallCoordinates.lastY;
                        cloud1Coordinates.backupy = cloud1Coordinates.lastY;
                        touchCoordinates.y = new_y;
                    } else {
                        backgroundCoordinates.lastY = backgroundCoordinates.backupy+touchCoordinates.dy;
                        groundCoordinates.lastY = groundCoordinates.backupy+touchCoordinates.dy;
                        guyCoordinates.lastY = guyCoordinates.backupy+touchCoordinates.dy;
                        houseCoordinates.lastY = houseCoordinates.backupy+touchCoordinates.dy;
                        officeTallCoordinates.lastY = officeTallCoordinates.backupy+touchCoordinates.dy;
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
        mScaleDetector.onTouchEvent(event);
        return true;
    }

    void CalculateMatrix(boolean invalidate) {
        mMatrixSky.reset(); mMatrixGround.reset(); mMatrixGuy.reset();
        mMatrixSky.postTranslate(backgroundCoordinates.lastX, backgroundCoordinates.lastY);
        mMatrixGround.postTranslate(groundCoordinates.lastX, groundCoordinates.lastY);
        mMatrixGuy.postTranslate(guyCoordinates.lastX, guyCoordinates.lastY);

    }

    private class LoadBitmaps extends AsyncTask<Void, Void, Void>
    {
        final Resources mResources;

        public LoadBitmaps(Resources res) {
            mResources = res;
        }

        @Override
        protected Void doInBackground(Void... params) {

            if(mFirst)
            {
                mFirst = false;
                // scale the background to 1.25 times the height, this makes for a scrollable up/down screen
                background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mResources, R.drawable.sky_gradient4), SKY_SIZE_X/100, SKY_SIZE_Y, true);

                // creating a scaled Bitmap reduces means reduced memory, since you dont decode the original full bitmap
                GROUND_HEIGHT = SCREEN_HEIGHT/16;
                ground = MyHelper.decodeSampledBitmapFromResource(mResources, R.drawable.ground2, HouseSurfaceView.GROUND_HEIGHT);
                GROUND_SCALE_FACTOR = (float)HouseSurfaceView.GROUND_HEIGHT/(float)ground.getHeight();
                ground = Bitmap.createScaledBitmap(ground,(int)((float)ground.getWidth()*GROUND_SCALE_FACTOR), GROUND_HEIGHT, true);


                GUY_HEIGHT = (int)((float)SCREEN_HEIGHT/10);
                guy = MyHelper.decodeSampledBitmapFromResource(mResources, R.drawable.ground_frog, GUY_HEIGHT);
                GUY_SCALE_FACTOR = (float)GUY_HEIGHT/(float)guy.getHeight();
                guy = Bitmap.createScaledBitmap(guy,(int)((float)guy.getWidth()*GUY_SCALE_FACTOR), GUY_HEIGHT, true);

                OFFICE_WIDTH = (int)((float)SCREEN_HEIGHT/5);
                office.officeTop = MyHelper.decodeSampledBitmapFromResource(mResources, R.drawable.office_top, OFFICE_WIDTH*3);
                office.officeMiddle = MyHelper.decodeSampledBitmapFromResource(mResources, R.drawable.office_middle, OFFICE_WIDTH*3);
                office.officeBottom = MyHelper.decodeSampledBitmapFromResource(mResources, R.drawable.office_bottom, OFFICE_WIDTH*3);
                OFFICETOP_SCALE_FACTOR = (float)OFFICE_WIDTH/(float)office.officeTop.getWidth();
                OFFICEMID_SCALE_FACTOR = (float)OFFICE_WIDTH/(float)office.officeMiddle.getWidth();
                OFFICEBOT_SCALE_FACTOR = (float)OFFICE_WIDTH/(float)office.officeBottom.getWidth();
                office.officeTop = Bitmap.createScaledBitmap(office.officeTop,OFFICE_WIDTH,(int)((float)office.officeTop.getHeight()*OFFICETOP_SCALE_FACTOR), true);
                office.officeMiddle = Bitmap.createScaledBitmap(office.officeMiddle,OFFICE_WIDTH, (int)((float)office.officeMiddle.getHeight()*OFFICEMID_SCALE_FACTOR), true);
                office.officeBottom = Bitmap.createScaledBitmap(office.officeBottom,OFFICE_WIDTH, (int)((float)office.officeBottom.getHeight()*OFFICEBOT_SCALE_FACTOR), true);

                CLOUD_HEIGHT = SCREEN_HEIGHT/12;
                cloud1 = MyHelper.decodeSampledBitmapFromResource(mResources, R.drawable.cloud_houseview, CLOUD_HEIGHT);
                CLOUD_SCALE_FACTOR = (float)CLOUD_HEIGHT/(float)cloud1.getHeight();
                cloud1 = Bitmap.createScaledBitmap(cloud1,(int)((float)cloud1.getWidth()*CLOUD_SCALE_FACTOR), CLOUD_HEIGHT, true);



                backgroundCoordinates = new ObjectCoordinates();
                groundCoordinates = new ObjectCoordinates();
                guyCoordinates = new ObjectCoordinates();
                houseCoordinates = new ObjectCoordinates();
                officeTallCoordinates = new ObjectCoordinates();
                cloud1Coordinates = new ObjectCoordinates();
                touchCoordinates = new movementCoordinates();

                backgroundCoordinates.lastX = - (float) ( (SKY_SIZE_X - HouseSurfaceView.SCREEN_WIDTH)/2 );
                groundCoordinates.lastX = - (float) ( (SKY_SIZE_X - HouseSurfaceView.SCREEN_WIDTH)/2 );
                guyCoordinates.lastX = SCREEN_WIDTH/4;
                houseCoordinates.lastX = SCREEN_WIDTH/2 - guy.getWidth()/3;
                officeTallCoordinates.lastX = 4;
                cloud1Coordinates.lastX = SCREEN_WIDTH-SCREEN_WIDTH/3;


                backgroundCoordinates.lastY = - (float) (SKY_SIZE_Y - CANVAS_HEIGHT);
                groundCoordinates.lastY = CANVAS_HEIGHT - ground.getHeight();
                guyCoordinates.lastY = CANVAS_HEIGHT - ground.getHeight() - guy.getHeight() + ground.getHeight() / 12;
                houseCoordinates.lastY = CANVAS_HEIGHT - ground.getHeight() - office.officeTop.getHeight() - office.officeMiddle.getHeight()*2 - office.officeBottom.getHeight();
                officeTallCoordinates.lastY = CANVAS_HEIGHT - ground.getHeight() - office.officeTop.getHeight() - office.officeMiddle.getHeight()*4 - office.officeBottom.getHeight();
                cloud1Coordinates.lastY = SCREEN_HEIGHT/18;
                Log.v(TAG, "HouseSurface constructor finished (loading bitmaps)");
            }
            else
            {
                if(background == null)
                {   Log.v(TAG, "bitmaps are getting reloaded");
                    background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mResources, R.drawable.sky_gradient4), SKY_SIZE_X/100, SKY_SIZE_Y, true);
                }
                if(ground == null)
                {
                    ground = MyHelper.decodeSampledBitmapFromResource(mResources, R.drawable.ground2, GROUND_HEIGHT);
                    ground = Bitmap.createScaledBitmap(ground,(int)((float)ground.getWidth()*GROUND_SCALE_FACTOR), GROUND_HEIGHT, true);
                }
                if(guy == null)
                {
                    guy = MyHelper.decodeSampledBitmapFromResource(mResources, R.drawable.ground_frog, GUY_HEIGHT);
                    guy = Bitmap.createScaledBitmap(guy,(int)((float)guy.getWidth()*GUY_SCALE_FACTOR), GUY_HEIGHT, true);
                }
                if(office.officeBottom == null)
                {
                    office.officeBottom = MyHelper.decodeSampledBitmapFromResource(mResources, R.drawable.office_bottom, OFFICE_WIDTH*3);
                    office.officeBottom = Bitmap.createScaledBitmap(office.officeBottom,OFFICE_WIDTH, (int)((float)office.officeBottom.getHeight()*OFFICEBOT_SCALE_FACTOR), true);
                }
                if(office.officeMiddle == null)
                {
                    office.officeMiddle = MyHelper.decodeSampledBitmapFromResource(mResources, R.drawable.office_middle, OFFICE_WIDTH*3);
                    office.officeMiddle = Bitmap.createScaledBitmap(office.officeMiddle,OFFICE_WIDTH, (int)((float)office.officeMiddle.getHeight()*OFFICEMID_SCALE_FACTOR), true);
                }
                if(office.officeTop == null)
                {
                    office.officeTop = MyHelper.decodeSampledBitmapFromResource(mResources, R.drawable.office_top, OFFICE_WIDTH*3);
                    office.officeTop = Bitmap.createScaledBitmap(office.officeTop,OFFICE_WIDTH,(int)((float)office.officeTop.getHeight()*OFFICETOP_SCALE_FACTOR), true);
                }
                if(cloud1 == null)
                {
                    cloud1 = MyHelper.decodeSampledBitmapFromResource(mResources, R.drawable.cloud_houseview, CLOUD_HEIGHT);
                    cloud1 = Bitmap.createScaledBitmap(cloud1,(int)((float)cloud1.getWidth()*CLOUD_SCALE_FACTOR), CLOUD_HEIGHT, true);
                }
                Log.v(TAG, "Done reloading bitmaps into ram");
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            houseCanvasDrawer = new HouseCanvasDrawer(background,
                                                      cloud1,
                                                      ground,
                                                      guy,
                                                      office,
                                                      SCREEN_WIDTH,CANVAS_HEIGHT
                                                      );
            if(!houseThread.running)
            {
                houseThread.start();
                Log.v(TAG, "Thread has started");
                houseThread.setRunning(true);
            }
        }
    }

    private class ReloadBitmaps extends AsyncTask<Void, Void, Void>
    {
        final Resources mResources;

        public ReloadBitmaps(Resources res) {
            mResources = res;
        }

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }

    }
    public void free() {
        // fix memory leak issues..
        Log.v(TAG, "free() bitmaps called");
        if(background!=null) {
            background.recycle();
            background =null;
        }
        if(ground!=null) {
            ground.recycle();
            ground = null;
        }
        if(guy!=null) {
            guy.recycle();
            guy = null;
        }
        if(house!=null) {
            house.recycle();
            house = null;
        }
        if(cloud1!=null) {
            cloud1.recycle();
            cloud1 = null;
        }
        if(office.officeMiddle!= null) {
            office.officeMiddle.recycle();
            office.officeMiddle = null;
        }
        if(office.officeTop!= null) {
            office.officeTop.recycle();
            office.officeTop = null;
        }
        if(office.officeBottom!= null) {
            office.officeBottom.recycle();
            office.officeBottom = null;
        }
    }

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.557f, Math.min(mScaleFactor, 1.75f));
            invalidate();
            return true;
        }
    }
}
