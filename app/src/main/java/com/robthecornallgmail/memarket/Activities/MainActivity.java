package com.robthecornallgmail.memarket.Activities;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Sprites.Clouds;
import com.robthecornallgmail.memarket.Threads.MainThread;
import com.robthecornallgmail.memarket.Views.OurView;

public class MainActivity extends AppCompatActivity
{
    public static String PACKAGE_NAME;
    OurView surfaceView;
    Bitmap movingClouds;
    Clouds spriteClouds;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        OurView ov = new OurView(this);
//        setContentView(ov);
        AssetManager am = getApplicationContext().getAssets();
        TextView title = (TextView) findViewById(R.id.titleText);
        Typeface pixelFont = Typeface.createFromAsset(getAssets(), "fonts/ARCADECLASSIC.TTF");

        Typeface pixelStartFont = Typeface.createFromAsset(getAssets(), "fonts/PressStart2P-Regular.ttf");
        title.setTypeface(pixelFont);
        Float alpha = 0.8f;
        title.setAlpha(0.8f);

//        final ImageView walkingFrog = (ImageView) findViewById(R.id.mainFrogWalking);
//        walkingFrog.bringToFront();
//        walkingFrog.post(new Runnable() {
//            @Override
//            public void run() {
//                // start the blinking animation..
//                ((AnimationDrawable) walkingFrog.getBackground()).start();
//            }
//        });
        PACKAGE_NAME = getApplicationContext().getPackageName();

        Button button = (Button) findViewById(R.id.startButton);
        button.bringToFront();
        button.setTypeface(pixelStartFont);
        button.getBackground().setAlpha(129);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent myIntent = new Intent(MainActivity.this,
                LoginActivity.class);
                startActivity(myIntent);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Used to load the 'native-lib' library on application startup.
    static
    {
        System.loadLibrary("native-lib");
    }


}
