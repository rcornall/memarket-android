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

import com.robthecornallgmail.memarket.Activities.MainActivity;
import com.robthecornallgmail.memarket.Canvas.DefaultBitmap;
import com.robthecornallgmail.memarket.Canvas.HouseCanvasDrawer;
import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Threads.HouseThread;
import com.robthecornallgmail.memarket.Util.Defines;
import com.robthecornallgmail.memarket.Util.InteractionMode;
import com.robthecornallgmail.memarket.Util.ItemObject;
import com.robthecornallgmail.memarket.Util.MyApplication;
import com.robthecornallgmail.memarket.Util.MyHelper;
import com.robthecornallgmail.memarket.Util.UserBitmap;
import com.robthecornallgmail.memarket.Util.UserItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.robthecornallgmail.memarket.Util.MyHelper.decodeSampledBitmapFromResourceByWidth;
import static java.lang.Thread.sleep;

/**
 * Created by rob on 03/03/17.
 */

public class HouseSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    static String TAG = "HouseSurfaceView";
    MyApplication mApplication;
    int mTextColor;
    public static int SCREEN_WIDTH,SCREEN_HEIGHT,CANVAS_HEIGHT, MAP_SECTION_WIDTH;

    int SKY_SIZE_X, SKY_SIZE_Y;
    int MAX_X, MIN_X, MAX_Y, MIN_Y;

    boolean mFirst = true;
    static boolean mFreed = false;

    InteractionMode mMode; // for touchevents
    Matrix mMatrixSky = new Matrix(); // for calculating where to put image after pan/zoom
    Matrix mMatrixGround = new Matrix(); // for calculating where to put image after pan/zoom
    Matrix mMatrixGuy = new Matrix();

    SurfaceHolder mHolder;
    HouseCanvasDrawer mHouseCanvasDrawer;
    HouseThread mHouseThread;

    static int GROUND_HEIGHT, GUY_HEIGHT, SILHOUETTE_HEIGHT, CLOUD_HEIGHT;
    static float GROUND_SCALE_FACTOR, GUY_SCALE_FACTOR, SILHOUETTE_SCALE_FACTOR, CLOUD_SCALE_FACTOR;


    /* list of bitmaps and their coordinates, types, etc. */
    static HashMap<Integer,UserBitmap> mUserBitmaps = new HashMap<>();
    /* default bitmaps are things like sky, clouds, ground etc. */
    static ArrayList<DefaultBitmap> mDefaultBitmaps = new ArrayList<>();
    static Bitmap mSilhouette;


    ScaleGestureDetector mScaleDetector;
    float mScaleFactor = 1.f;

    movementCoordinates mTouchCoordinates;


    public void updateBitmaps(HashMap<Integer, UserItem> itemsIDtoUserItems, HashMap<Integer, ItemObject> itemsIdToObject) {
        /*might have to stop/start thread here?*/

        /* update list of users bitmaps & coordinates retrieved from server */
        for (Map.Entry<Integer, UserItem> itemIDtoUser : itemsIDtoUserItems.entrySet()) {
            Integer itemID = itemIDtoUser.getValue().mItemID;
            ItemObject itemObj = itemsIdToObject.get(itemID);
            /* only draw if building(for now) & equipped */
            if(itemObj.mType == Defines.ITEM_TYPE.BUILDING)
            {
                if(itemIDtoUser.getValue().mIsEquipped)
                {
                    String name = itemObj.mName;
                    Integer type = itemObj.mType;
                    Integer subType = itemObj.mSubtype;
                    Integer width = itemObj.mWidth;
                    Integer xCoord = itemIDtoUser.getValue().mXCoordinate;
                    mUserBitmaps.put(itemIDtoUser.getKey(),new UserBitmap(itemID,name,xCoord,width,type,subType));
                }
            }
        }
        LoadBitmaps loadBitmaps = new LoadBitmaps(getResources());
        loadBitmaps.execute((Void)null);
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

    public HouseSurfaceView(Context context, AttributeSet attrs) {
        // If we scale everything to screen size of users phone,
        // then sprites and map size should all be scaled relatively to each other
        // but if screen size is not 16:9 then sprites may be stretched, which is ok.
        super(context, attrs);
        Log.v(TAG, "surface view constructor, sleeping to let previous activity destroy");
        mApplication = (MyApplication) context.getApplicationContext();
        mTextColor = context.getResources().getColor(R.color.monokaiBetweenGreen);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        SCREEN_WIDTH = displaymetrics.widthPixels;
        SCREEN_HEIGHT = displaymetrics.heightPixels;

        /* size of sections for bitmaps to be placed at coordinates 1-36 */
        MAP_SECTION_WIDTH = SCREEN_WIDTH/9;

        SKY_SIZE_X = (int)(SCREEN_WIDTH*4);
        SKY_SIZE_Y = (int)(SCREEN_HEIGHT*1.4);
        MAX_X = 0;
        MIN_X = -(SKY_SIZE_X - SCREEN_WIDTH);
        MAX_Y = 0;
        MIN_Y = -(SKY_SIZE_Y - SCREEN_HEIGHT);

        mHolder = getHolder();
        mHolder.addCallback(this);

        setFocusable(true);



//        CalculateMatrix(true);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mFreed = false;
        CANVAS_HEIGHT = this.getHeight();

        mHouseThread = new HouseThread(holder, this);

        /* for the first time loading bitmaps is called from MenuActivity once requests are in */
        if(!mFirst)
        {
            Log.e(TAG, "SDLKFJSDLKFJSDLKFJ");
            LoadBitmaps reloadBitmaps = new LoadBitmaps(getResources());
            reloadBitmaps.execute((Void)null);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}



    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {


        Log.v(TAG, "SURFACE DESTROYED");
        boolean retry = true;
        mHouseThread.setRunning(false);
        while(retry) {
            try {
                mHouseThread.join();
                mHouseThread = null;
                Log.v(TAG, "thread has joined");
                retry = false;
            } catch (InterruptedException e) {
                Log.e(TAG, "failed to stop thread?");
                e.printStackTrace();
            }
        }
        free();
    }

    public void draw(Canvas canvas) {
        if (canvas != null) {
            canvas.save();
            canvas.scale(mScaleFactor, mScaleFactor);
            mHouseCanvasDrawer.onDraw(canvas, mScaleFactor);
            canvas.restore();
        }
    }

    void CalculateMatrix(boolean invalidate) {
        mMatrixSky.reset(); mMatrixGround.reset(); mMatrixGuy.reset();
//        mMatrixSky.postTranslate(backgroundCoordinates.lastX, backgroundCoordinates.lastY);
//        mMatrixGround.postTranslate(groundCoordinates.lastX, groundCoordinates.lastY);
//        mMatrixGuy.postTranslate(guyCoordinates.lastX, guyCoordinates.lastY);

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

            /* Load and add default bitmaps into list */

                /* scale the background to 1.25 times the height, this makes for a scrollable up/down screen */
                Bitmap background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mResources, R.drawable.sky_gradient4), SKY_SIZE_X/100, SKY_SIZE_Y, true);
//                Log.v(TAG, "byteCount: " + background.getByteCount() + ", allocBytes: " + background.getAllocationByteCount() + ", height + width: " + background.getHeight() + " " + background.getWidth());

                /* reading a scaled Bitmap reduces means reduced memory, since you dont decode the original full bitmap */
                GROUND_HEIGHT = SCREEN_HEIGHT/16;
                Bitmap ground = MyHelper.decodeSampledBitmapFromResourceByHeight(mResources, R.drawable.ground2, HouseSurfaceView.GROUND_HEIGHT);
                GROUND_SCALE_FACTOR = (float)HouseSurfaceView.GROUND_HEIGHT/(float)ground.getHeight();
                ground = Bitmap.createScaledBitmap(ground,(int)((float)ground.getWidth()*GROUND_SCALE_FACTOR), GROUND_HEIGHT, true);

                GUY_HEIGHT = (int)((float)SCREEN_HEIGHT/10);
                Bitmap guy = MyHelper.decodeSampledBitmapFromResourceByHeight(mResources, R.drawable.ground_frog, GUY_HEIGHT);
                GUY_SCALE_FACTOR = (float)GUY_HEIGHT/(float)guy.getHeight();
                guy = Bitmap.createScaledBitmap(guy,(int)((float)guy.getWidth()*GUY_SCALE_FACTOR), GUY_HEIGHT, true);

                SILHOUETTE_HEIGHT = MAP_SECTION_WIDTH/2;
                mSilhouette = MyHelper.decodeSampledBitmapFromResourceByHeight(mResources, R.drawable.silouette, SILHOUETTE_HEIGHT);
                SILHOUETTE_SCALE_FACTOR = (float)SILHOUETTE_HEIGHT/(float) mSilhouette.getHeight();
                mSilhouette = Bitmap.createScaledBitmap(mSilhouette,(int)((float) mSilhouette.getWidth()*SILHOUETTE_SCALE_FACTOR), SILHOUETTE_HEIGHT, true);

                CLOUD_HEIGHT = SCREEN_HEIGHT/12;
                Bitmap cloud1 = MyHelper.decodeSampledBitmapFromResourceByHeight(mResources, R.drawable.cloud_houseview, CLOUD_HEIGHT);
                CLOUD_SCALE_FACTOR = (float)CLOUD_HEIGHT/(float)cloud1.getHeight();
                cloud1 = Bitmap.createScaledBitmap(cloud1,(int)((float)cloud1.getWidth()*CLOUD_SCALE_FACTOR), CLOUD_HEIGHT, true);

                mDefaultBitmaps.add(new DefaultBitmap(background, Defines.DEFAULT_TYPE.BACKGROUND, - (float) ( (SKY_SIZE_X - SCREEN_WIDTH)/2 ), - (float) (SKY_SIZE_Y - CANVAS_HEIGHT) ));
                mDefaultBitmaps.add(new DefaultBitmap(ground, Defines.DEFAULT_TYPE.GROUND, - (float) ( (SKY_SIZE_X - HouseSurfaceView.SCREEN_WIDTH)/2 ), CANVAS_HEIGHT - ground.getHeight()));
                mDefaultBitmaps.add(new DefaultBitmap(cloud1, Defines.DEFAULT_TYPE.CLOUD1, SCREEN_WIDTH-SCREEN_WIDTH/3, SCREEN_HEIGHT/18));
                mDefaultBitmaps.add(new DefaultBitmap(guy, Defines.DEFAULT_TYPE.GUY, (mUserBitmaps.get(4).mXCoordinate - 14)*(SCREEN_WIDTH/9), CANVAS_HEIGHT - ground.getHeight() - guy.getHeight() + ground.getHeight() / 12));

            /* load appropriate bitmaps which the user owns */
                for (UserBitmap entry : mUserBitmaps.values()) {
                    Log.v(TAG,"ENTRY mName: " + entry.mName);
                    String name = entry.mName;
                    name = "bitmap_" + name.replaceAll(" ", "_").toLowerCase();
                    int bitmapWidth =  entry.mWidth*MAP_SECTION_WIDTH;
                    entry.mBitmap = decodeSampledBitmapFromResourceByWidth(mResources, mResources.getIdentifier(name, "drawable", MainActivity.PACKAGE_NAME), bitmapWidth);
                    float scaleFactor = (float)bitmapWidth/(float)entry.mBitmap.getWidth();
                    entry.mBitmap = Bitmap.createScaledBitmap(entry.mBitmap, bitmapWidth, (int)((float)entry.mBitmap.getHeight()*scaleFactor), true);

                    /* set coordinates using 1-36 section x coordinates, and placing at appropriate y coordinate */
                    entry.mCoordinates.setLastXY((entry.mXCoordinate-14)*MAP_SECTION_WIDTH, CANVAS_HEIGHT-ground.getHeight()-entry.mBitmap.getHeight());
                }


                mTouchCoordinates = new movementCoordinates();


                Log.v(TAG, "HouseSurface constructor finished (loading bitmaps)");
            }
            else {
                if (mDefaultBitmaps.get(Defines.DEFAULT_TYPE.BACKGROUND).mBitmap == null) {
                    Log.v(TAG, "bitmaps are getting reloaded");
                    mDefaultBitmaps.get(Defines.DEFAULT_TYPE.BACKGROUND).mBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mResources, R.drawable.sky_gradient4), SKY_SIZE_X / 100, SKY_SIZE_Y, true);
                }
                if (mDefaultBitmaps.get(Defines.DEFAULT_TYPE.GROUND).mBitmap == null) {
                    mDefaultBitmaps.get(Defines.DEFAULT_TYPE.GROUND).mBitmap = MyHelper.decodeSampledBitmapFromResourceByHeight(mResources, R.drawable.ground2, GROUND_HEIGHT);
                    mDefaultBitmaps.get(Defines.DEFAULT_TYPE.GROUND).mBitmap = Bitmap.createScaledBitmap(mDefaultBitmaps.get(Defines.DEFAULT_TYPE.GROUND).mBitmap,
                            (int) ((float) mDefaultBitmaps.get(Defines.DEFAULT_TYPE.GROUND).mBitmap.getWidth() * GROUND_SCALE_FACTOR), GROUND_HEIGHT, true);
                }
                if (mDefaultBitmaps.get(Defines.DEFAULT_TYPE.CLOUD1).mBitmap == null) {
                    mDefaultBitmaps.get(Defines.DEFAULT_TYPE.CLOUD1).mBitmap = MyHelper.decodeSampledBitmapFromResourceByHeight(mResources, R.drawable.cloud_houseview, CLOUD_HEIGHT);
                    mDefaultBitmaps.get(Defines.DEFAULT_TYPE.CLOUD1).mBitmap = Bitmap.createScaledBitmap(mDefaultBitmaps.get(Defines.DEFAULT_TYPE.CLOUD1).mBitmap,
                            (int) ((float) mDefaultBitmaps.get(Defines.DEFAULT_TYPE.CLOUD1).mBitmap.getWidth() * CLOUD_SCALE_FACTOR), CLOUD_HEIGHT, true);
                }
                if (mDefaultBitmaps.get(Defines.DEFAULT_TYPE.GUY).mBitmap == null) {
                    mDefaultBitmaps.get(Defines.DEFAULT_TYPE.GUY).mBitmap = MyHelper.decodeSampledBitmapFromResourceByHeight(mResources, R.drawable.ground_frog, GUY_HEIGHT);
                    mDefaultBitmaps.get(Defines.DEFAULT_TYPE.GUY).mBitmap = Bitmap.createScaledBitmap(mDefaultBitmaps.get(Defines.DEFAULT_TYPE.GUY).mBitmap,
                            (int) ((float) mDefaultBitmaps.get(Defines.DEFAULT_TYPE.GUY).mBitmap.getWidth() * GUY_SCALE_FACTOR), GUY_HEIGHT, true);
                }
                if (mSilhouette == null) {
                    mSilhouette = MyHelper.decodeSampledBitmapFromResourceByHeight(mResources, R.drawable.silouette, SILHOUETTE_HEIGHT);
                    mSilhouette = Bitmap.createScaledBitmap(mSilhouette,(int)((float) mSilhouette.getWidth()*SILHOUETTE_SCALE_FACTOR), SILHOUETTE_HEIGHT, true);
                }

                for (UserBitmap bitmap : mUserBitmaps.values()) {
                    if(bitmap.mBitmap == null)
                    {
                        String name = bitmap.mName;
                        name = "bitmap_" + name.replaceAll(" ", "_").toLowerCase();
                        int bitmapWidth =  bitmap.mWidth*MAP_SECTION_WIDTH;
                        bitmap.mBitmap = decodeSampledBitmapFromResourceByWidth(mResources, mResources.getIdentifier(name, "drawable", MainActivity.PACKAGE_NAME), bitmapWidth);
                        float scaleFactor = (float)bitmapWidth/(float)bitmap.mBitmap.getWidth();
                        bitmap.mBitmap = Bitmap.createScaledBitmap(bitmap.mBitmap, bitmapWidth, (int)((float)bitmap.mBitmap.getHeight()*scaleFactor), true);
                    }

                }


                Log.v(TAG, "Done reloading bitmaps into ram");
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            /* check we havent freed bitmaps (they arent null) before proceeding */
            if(!mFreed)
            {
                mHouseCanvasDrawer = new HouseCanvasDrawer(  mUserBitmaps, mDefaultBitmaps, mSilhouette,
                        SCREEN_WIDTH,CANVAS_HEIGHT,
                        mApplication.pixelFont, mTextColor
                );
                if(mHouseThread != null)
                {
                    if(!mHouseThread.isRunning())
                    {
                        mHouseThread.start();
                        Log.v(TAG, "Thread has started");
                        mHouseThread.setRunning(true);
                    }
                }
                else
                {
                    Log.v(TAG, "mHouseThread is null, probably left houseview previously");
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x, y;
        float backgroundCoordinates_backupX, backgroundCoordinates_backupY;
        float backgroundCoordinates_lastX, backgroundCoordinates_lastY;
        backgroundCoordinates_backupX = mDefaultBitmaps.get(Defines.DEFAULT_TYPE.BACKGROUND).mCoordinates.backupx;
        backgroundCoordinates_backupY = mDefaultBitmaps.get(Defines.DEFAULT_TYPE.BACKGROUND).mCoordinates.backupy;
        backgroundCoordinates_lastX = mDefaultBitmaps.get(Defines.DEFAULT_TYPE.BACKGROUND).mCoordinates.lastX;
        backgroundCoordinates_lastY = mDefaultBitmaps.get(Defines.DEFAULT_TYPE.BACKGROUND).mCoordinates.lastY;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX(); y = event.getY();
                mTouchCoordinates.setXY(x, y);

                /* remember the last coordinates */
                for(UserBitmap userBitmap : mUserBitmaps.values())
                {
                    userBitmap.mCoordinates.setBackupXY();
                }
                for(DefaultBitmap bitmap : mDefaultBitmaps)
                {
                    bitmap.mCoordinates.setBackupXY();
                }
                mMode = InteractionMode.PAN;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == InteractionMode.PAN) {
                    float new_x = event.getX(); float new_y = event.getY();
//                    Log.v(TAG, String.format("backgroundCoordinates.x and y are: %f, %f. \nnewxy are: %f, %f", mTouchCoordinates.x, mTouchCoordinates.x, new_x, new_y));
                    mTouchCoordinates.setdxdy( new_x - mTouchCoordinates.x, new_y - mTouchCoordinates.y);

                    /* calculate new MIN_X/MIN_Y (skyarea max/min) based on scaling. */
                    MIN_Y = (int)-(SKY_SIZE_Y - CANVAS_HEIGHT/mScaleFactor);
                    MIN_X = (int)-(SKY_SIZE_X - SCREEN_WIDTH/mScaleFactor);

//                    Log.v(TAG, String.format("x and y are: %f, %f. \ndx and dy are: %f, %f", new_x, new_y, mTouchCoordinates.dx, mTouchCoordinates.dy));
//                    Log.v(TAG, String.format("scaleFactor is: %f", mScaleFactor));
//                    Log.v(TAG, String.format("NEW MIN_Y is: %d", MIN_Y));
//                    Log.v(TAG, String.format("MAXY*scaleFactor is: %d", MAX_Y));
//                    Log.v(TAG, String.format("backgroundCoordinates.backupy+mTouchCoordinates.dy: %f", backgroundCoordinates.backupy+mTouchCoordinates.dy));

                    //lock the surfaceview area to move around the size of the sky width and height.
                    if (backgroundCoordinates_backupX+ mTouchCoordinates.dx > MAX_X)
                    {
                        float diff = MAX_X-backgroundCoordinates_lastX;

                        for(DefaultBitmap bitmap : mDefaultBitmaps)
                        {
                            bitmap.mCoordinates.lastX = bitmap.mCoordinates.lastX + diff;
                            bitmap.mCoordinates.backupx = bitmap.mCoordinates.lastX;
                        }
                        for(UserBitmap userBitmap : mUserBitmaps.values())
                        {
                            userBitmap.mCoordinates.lastX = userBitmap.mCoordinates.lastX + diff;
                            userBitmap.mCoordinates.backupx = userBitmap.mCoordinates.lastX;
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
                        for(UserBitmap userBitmap : mUserBitmaps.values())
                        {
                            userBitmap.mCoordinates.lastX = userBitmap.mCoordinates.lastX + diff;
                            userBitmap.mCoordinates.backupx = userBitmap.mCoordinates.lastX;
                        }

                        mTouchCoordinates.x = new_x;
                    }
                    else
                    {
                        for(UserBitmap userBitmap : mUserBitmaps.values())
                        {
                            userBitmap.mCoordinates.lastX = userBitmap.mCoordinates.backupx + mTouchCoordinates.dx;
                        }
                        for(DefaultBitmap bitmap : mDefaultBitmaps)
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
                        for(UserBitmap userBitmap : mUserBitmaps.values())
                        {
                            userBitmap.mCoordinates.lastY = userBitmap.mCoordinates.lastY + diff;
                            userBitmap.mCoordinates.backupy = userBitmap.mCoordinates.lastY;
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
                        for(UserBitmap userBitmap : mUserBitmaps.values())
                        {
                            userBitmap.mCoordinates.lastY = userBitmap.mCoordinates.lastY + diff;
                            userBitmap.mCoordinates.backupy = userBitmap.mCoordinates.lastY;
                        }

                        mTouchCoordinates.y = new_y;
                    }
                    else
                    {
                        for(DefaultBitmap bitmap : mDefaultBitmaps)
                        {
                            bitmap.mCoordinates.lastY = bitmap.mCoordinates.backupy + mTouchCoordinates.dy;
                        }
                        for(UserBitmap userBitmap : mUserBitmaps.values())
                        {
                            userBitmap.mCoordinates.lastY = userBitmap.mCoordinates.backupy + mTouchCoordinates.dy;
                        }
                    }
                    /* CalculateMatrix(true); */
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
    static public void free() {
        // fix memory leak issues..
        mFreed = true;

        Log.v(TAG, "free() bitmaps called");
        for (UserBitmap entry : mUserBitmaps.values()) {
            if(entry.mBitmap != null)
            {
                entry.mBitmap.recycle();
                entry.mBitmap=null;
            }
        }
        for (DefaultBitmap bitmap : mDefaultBitmaps) {
            if(bitmap.mBitmap != null)
            {
                bitmap.mBitmap.recycle();
                bitmap.mBitmap=null;
            }
        }
        if(mSilhouette != null)
        {
            mSilhouette.recycle();
            mSilhouette=null;
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
