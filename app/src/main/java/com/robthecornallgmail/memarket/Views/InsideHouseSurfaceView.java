package com.robthecornallgmail.memarket.Views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.robthecornallgmail.memarket.Activities.MainActivity;
import com.robthecornallgmail.memarket.Canvas.DefaultBitmap;
import com.robthecornallgmail.memarket.Canvas.InsideHouseCanvasDrawer;
import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Threads.HouseThread;
import com.robthecornallgmail.memarket.Util.Defines;
import com.robthecornallgmail.memarket.Util.InteractionMode;
import com.robthecornallgmail.memarket.Util.MovementCoordinates;
import com.robthecornallgmail.memarket.Util.UserBuildingBitmap;
import com.robthecornallgmail.memarket.Util.UserWorkerBitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.robthecornallgmail.memarket.Util.MyHelper.decodeSampledBitmapFromResourceByWidth;
import static com.robthecornallgmail.memarket.Views.HouseSurfaceView.CANVAS_HEIGHT;
import static com.robthecornallgmail.memarket.Views.HouseSurfaceView.SCREEN_HEIGHT;
import static com.robthecornallgmail.memarket.Views.HouseSurfaceView.SCREEN_WIDTH;

/**
 * Created by rob on 01/09/17.
 */

public class InsideHouseSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    static String TAG = "InsideHouseSurfaceView";
    private final SurfaceHolder mHolder;

    boolean mFirst = true;

    HouseThread mInsideHouseThread;
    InsideHouseCanvasDrawer mInsideHouseCanvasDrawer;

    Map.Entry<Integer, UserBuildingBitmap> mUserBuildingBitmap;

    ArrayList<DefaultBitmap> mDefaultBitmaps = new ArrayList<>();
    HashMap<Integer,UserWorkerBitmap> mUserWorkerBitmaps = new HashMap<>();

    int SKY_SIZE_X, SKY_SIZE_Y;
    int MAX_X, MIN_X, MAX_Y, MIN_Y;
    InteractionMode mMode; // for touchevents

    MovementCoordinates mTouchCoordinates = new MovementCoordinates();

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    public InsideHouseSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SKY_SIZE_X = (int)(SCREEN_WIDTH*1);;
        SKY_SIZE_Y = (int)(SCREEN_HEIGHT*2.4);
        MAX_X = 0;
        MIN_X = -(SKY_SIZE_X - SCREEN_WIDTH);
        MAX_Y = 0;
        MIN_Y = -(SKY_SIZE_Y - SCREEN_HEIGHT);

        mHolder = getHolder();
        mHolder.addCallback(this);

        setFocusable(true);

        mDefaultBitmaps.clear();

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mInsideHouseThread = new HouseThread(holder, this);
        if(!mFirst)
        {
            Log.e(TAG, "SDLKFJSDLKFJSDLKFJ");
            LoadBitmaps reloadBitmaps = new LoadBitmaps(getResources());
            reloadBitmaps.execute((Void)null);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(TAG, "SURFACE DESTROYED");
        boolean retry = true;
        mInsideHouseThread.setRunning(false);
        while(retry) {
            try {
                mInsideHouseThread.join();
                mInsideHouseThread = null;
                Log.v(TAG, "thread has joined");
                retry = false;
            } catch (InterruptedException e) {
                Log.e(TAG, "failed to stop thread?");
                e.printStackTrace();
            }
        }
        mDefaultBitmaps.clear();
    }
    public void draw(Canvas canvas) {
        if (canvas != null) {
            canvas.save();
//            canvas.scale(mScaleFactor, mScaleFactor);
            mInsideHouseCanvasDrawer.onDraw(canvas, mScaleFactor);
            canvas.restore();
        }
    }

    public void updateUserBitmap(Map.Entry<Integer, UserBuildingBitmap> userBuildingBitmap, HashMap<Integer, UserWorkerBitmap> userWorkers) {
        mFirst = true;
        mUserBuildingBitmap = userBuildingBitmap;
        mUserWorkerBitmaps = userWorkers;
        LoadBitmaps reloadBitmaps = new LoadBitmaps(getResources());
        reloadBitmaps.execute((Void)null);
    }

    private class LoadBitmaps extends AsyncTask<Void, Void, Void>
    {
        final Resources mResources;

        public LoadBitmaps(Resources res) {
            mResources = res;
        }

        @Override
        protected Void doInBackground(Void... params) {

            if(true)
            {
                mFirst = false;


            /* Load and add default bitmaps into list */
            /* scale the background to 1.25 times the height, this makes for a scrollable up/down screen */
                Bitmap background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mResources, R.drawable.sky_gradient_inside), SKY_SIZE_X/100, SKY_SIZE_Y, true);


                String name = mUserBuildingBitmap.getValue().mName;
                name = name.toLowerCase();
                int floors;
                if(name.contains("small"))
                {
                    Log.v(TAG, "small");
                    floors = 2;
                }
                else if(name.contains("medium"))
                {
                    Log.v(TAG, "medium");
                    floors = 4;
                }
                else if (name.contains("tall"))
                {
                    Log.v(TAG, "tall");
                    floors = 6;
                }
                else
                {
                    Log.v(TAG, "11111111");
                    floors = 1;
                }

                Log.v(TAG, "floors: " + floors);

                name = "inside_" + name.replaceAll(" ", "_").replaceAll("_small", "").replaceAll("_medium", "").replaceAll("_tall", "");
                Log.v(TAG, name.toString());

                Bitmap insideHouseBitmap = decodeSampledBitmapFromResourceByWidth(mResources, mResources.getIdentifier(name, "drawable", MainActivity.PACKAGE_NAME), SCREEN_WIDTH);
                float scaleFactor = (float)SCREEN_WIDTH/(float)insideHouseBitmap.getWidth();
                insideHouseBitmap = Bitmap.createScaledBitmap(insideHouseBitmap, SCREEN_WIDTH, (int)((float)insideHouseBitmap.getHeight()*scaleFactor), true);

                mDefaultBitmaps.add(new DefaultBitmap(background, Defines.DEFAULT_TYPE.BACKGROUND, - (float) ( (SKY_SIZE_X - SCREEN_WIDTH)/2 ), - (float) (SKY_SIZE_Y - CANVAS_HEIGHT) ));
                mDefaultBitmaps.add(new DefaultBitmap(insideHouseBitmap, Defines.DEFAULT_TYPE.INSIDE_BUILDING, floors, 0, CANVAS_HEIGHT - insideHouseBitmap.getHeight()));


                try {
                    name = name + "_top";
                    Bitmap insideHouseTopBitmap = decodeSampledBitmapFromResourceByWidth(mResources, mResources.getIdentifier(name, "drawable", MainActivity.PACKAGE_NAME), SCREEN_WIDTH);
                    scaleFactor = (float)SCREEN_WIDTH/(float)insideHouseTopBitmap.getWidth();
                    insideHouseTopBitmap = Bitmap.createScaledBitmap(insideHouseTopBitmap, SCREEN_WIDTH, (int)((float)insideHouseTopBitmap.getHeight()*scaleFactor), true);

                    mDefaultBitmaps.add(new DefaultBitmap(insideHouseTopBitmap, Defines.DEFAULT_TYPE.INSIDE_BUILDING_TOP, 0, CANVAS_HEIGHT - insideHouseBitmap.getHeight()*floors-insideHouseTopBitmap.getHeight() ));
                } catch (Exception e) {
                /* no top needed for this particular building */
                    Log.v(TAG, "NO top for: " + name.toString() + "... " + e.toString());
                }


                Log.v(TAG, "HouseSurface constructor finished (loading bitmaps)");
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            /* check we havent freed bitmaps (they arent null) before proceeding */
            mInsideHouseCanvasDrawer = new InsideHouseCanvasDrawer(mDefaultBitmaps, mUserWorkerBitmaps, mUserBuildingBitmap, SCREEN_WIDTH, SCREEN_HEIGHT);
            if(mInsideHouseThread != null)
            {
                if(!mInsideHouseThread.isRunning())
                {
                    mInsideHouseThread.start();
                    Log.v(TAG, "Thread has started");
                    mInsideHouseThread.setRunning(true);
                }
            }
            else
            {
                Log.v(TAG, "mInsideHouseThread is null, probably left houseview previously");
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x, y;
        float backgroundCoordinates_backupX, backgroundCoordinates_backupY;
        float backgroundCoordinates_lastX, backgroundCoordinates_lastY;
        try {
            backgroundCoordinates_backupX = mDefaultBitmaps.get(Defines.DEFAULT_TYPE.BACKGROUND).mCoordinates.backupx;
            backgroundCoordinates_backupY = mDefaultBitmaps.get(Defines.DEFAULT_TYPE.BACKGROUND).mCoordinates.backupy;
            backgroundCoordinates_lastX = mDefaultBitmaps.get(Defines.DEFAULT_TYPE.BACKGROUND).mCoordinates.lastX;
            backgroundCoordinates_lastY = mDefaultBitmaps.get(Defines.DEFAULT_TYPE.BACKGROUND).mCoordinates.lastY;
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, e.toString());
            return false;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX(); y = event.getY();
                mTouchCoordinates.setXY(x, y);

                /* remember the last coordinates */
                for(DefaultBitmap bitmap : mDefaultBitmaps)
                {
                    bitmap.mCoordinates.setBackupXY();
                }
                for(UserWorkerBitmap bitmap : mUserWorkerBitmaps.values())
                {
                    bitmap.mCoordinates.setBackupXY();
                }
                mMode = InteractionMode.PAN;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == InteractionMode.PAN) {
                    float new_x = event.getX(); float new_y = event.getY();
                    mTouchCoordinates.setdxdy( new_x - mTouchCoordinates.x, new_y - mTouchCoordinates.y);

                    /* calculate new MIN_X/MIN_Y (skyarea max/min) based on scaling. */
                    MIN_Y = (int)-(SKY_SIZE_Y - CANVAS_HEIGHT/mScaleFactor);
                    MIN_X = (int)-(SKY_SIZE_X - SCREEN_WIDTH/mScaleFactor);


                    //lock the surfaceview area to move around the size of the sky width and height.
                    if (backgroundCoordinates_backupX+ mTouchCoordinates.dx > MAX_X)
                    {
                        float diff = MAX_X-backgroundCoordinates_lastX;

                        for(DefaultBitmap bitmap : mDefaultBitmaps)
                        {
                            bitmap.mCoordinates.lastX = bitmap.mCoordinates.lastX + diff;
                            bitmap.mCoordinates.backupx = bitmap.mCoordinates.lastX;
                        }
                        for(UserWorkerBitmap bitmap : mUserWorkerBitmaps.values())
                        {
                            bitmap.mCoordinates.lastX = bitmap.mCoordinates.lastX + diff;
                            bitmap.mCoordinates.backupx = bitmap.mCoordinates.lastX;
                        }
                        mTouchCoordinates.x = new_x;
                    }
                    else if (backgroundCoordinates_backupX+ mTouchCoordinates.dx < MIN_X)
                    {
                        float diff = MIN_X-backgroundCoordinates_lastX;

                        for(DefaultBitmap bitmap : mDefaultBitmaps)
                        {
                            bitmap.mCoordinates.lastX = bitmap.mCoordinates.lastX + diff;
                            bitmap.mCoordinates.backupx = bitmap.mCoordinates.lastX;
                        }
                        for(UserWorkerBitmap bitmap : mUserWorkerBitmaps.values())
                        {
                            bitmap.mCoordinates.lastX = bitmap.mCoordinates.lastX + diff;
                            bitmap.mCoordinates.backupx = bitmap.mCoordinates.lastX;
                        }

                        mTouchCoordinates.x = new_x;
                    }
                    else
                    {
                        for(DefaultBitmap bitmap : mDefaultBitmaps)
                        {
                            bitmap.mCoordinates.lastX = bitmap.mCoordinates.backupx + mTouchCoordinates.dx;
                        }
                        for(UserWorkerBitmap bitmap : mUserWorkerBitmaps.values())
                        {
                            bitmap.mCoordinates.lastX = bitmap.mCoordinates.backupx + mTouchCoordinates.dx;
                        }
                    }

                    /* Y coordinates */
                    if (backgroundCoordinates_backupY+ mTouchCoordinates.dy > MAX_Y)
                    {
                        float diff = MAX_Y-backgroundCoordinates_lastY;

                        for(DefaultBitmap bitmap : mDefaultBitmaps)
                        {
                            bitmap.mCoordinates.lastY = bitmap.mCoordinates.lastY + diff;
                            bitmap.mCoordinates.backupy = bitmap.mCoordinates.lastY;
                        }
                        for(UserWorkerBitmap bitmap : mUserWorkerBitmaps.values())
                        {
                            bitmap.mCoordinates.lastY = bitmap.mCoordinates.lastY + diff;
                            bitmap.mCoordinates.backupy = bitmap.mCoordinates.lastY;
                        }
                        mTouchCoordinates.y = new_y;
                    }
                    else if (backgroundCoordinates_backupY+ mTouchCoordinates.dy < MIN_Y)
                    {
                        float diff = MIN_Y-backgroundCoordinates_lastY;

                        for(DefaultBitmap bitmap : mDefaultBitmaps)
                        {
                            bitmap.mCoordinates.lastY = bitmap.mCoordinates.lastY + diff;
                            bitmap.mCoordinates.backupy = bitmap.mCoordinates.lastY;
                        }
                        for(UserWorkerBitmap bitmap : mUserWorkerBitmaps.values())
                        {
                            bitmap.mCoordinates.lastY = bitmap.mCoordinates.lastY + diff;
                            bitmap.mCoordinates.backupy = bitmap.mCoordinates.lastY;
                        }


                        mTouchCoordinates.y = new_y;
                    }
                    else
                    {
                        for(DefaultBitmap bitmap : mDefaultBitmaps)
                        {
                            bitmap.mCoordinates.lastY = bitmap.mCoordinates.backupy + mTouchCoordinates.dy;
                        }
                        for(UserWorkerBitmap bitmap : mUserWorkerBitmaps.values())
                        {
                            bitmap.mCoordinates.lastY = bitmap.mCoordinates.backupy + mTouchCoordinates.dy;
                        }
                    }
                    /* CalculateMatrix(true); */
                }
                break;
            case MotionEvent.ACTION_UP:
                /* if the drag/move of the touch is small enough then consider it a touch */
                if((mTouchCoordinates.dx < 10 && mTouchCoordinates.dy < 10) &&
                        (mTouchCoordinates.dx > -10 && mTouchCoordinates.dy > -10))
                {
                    Log.v(TAG, "WE GOT A TOUCH");
                    x = event.getX(); y = event.getY();
                    Log.v(TAG, "tapped at: " + x + ", " + y);
//                    touch(x,y);
                }


                /* clear dx/dy and mode */
                mTouchCoordinates.setdxdy(0,0);
                mMode = InteractionMode.NONE;
                break;
            }
        mScaleDetector.onTouchEvent(event);
        return true;
    }


    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
//            mScaleFactor *= detector.getScaleFactor();
//            mScaleFactor = Math.max(0.557f, Math.min(mScaleFactor, 1.75f));
            invalidate();
            return true;
        }
    }
}
