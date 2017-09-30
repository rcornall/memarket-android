package com.robthecornallgmail.memarket.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.Defines;
import com.robthecornallgmail.memarket.Util.MyApplication;
import com.robthecornallgmail.memarket.Util.MyHelper;
import com.robthecornallgmail.memarket.Util.TaskCallback;
import com.robthecornallgmail.memarket.Views.MainSurfaceView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements TaskCallback
{
    public static String PACKAGE_NAME;
    private static final String TAG = "MainActivity";

    private MainActivity.UserLoginTask mAuthTask = null;
    private MyApplication mApplication;

    private Button mStartButton, mLoginButton, mRegisterButton;
    private EditText mEmail, mPassword;
    private View mProgressView;

    private MediaPlayer mp3Player;

//    View mBlackFog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //start the dank

        //hide song till happy with it TODO 07092017
//        mp3Player = MediaPlayer.create(this, R.raw.roccow_pumped);
//        mp3Player.setLooping(true);
//        mp3Player.start();

        setContentView(R.layout.activity_main);

        //hide surface view till work on it later.. TODO 07092017
//        MainSurfaceView msv = (MainSurfaceView) findViewById(R.id.mainSurfaceView);
//        msv.setVisibility(View.GONE);


//        mBlackFog = findViewById(R.id.black_fog);
//        OurView ov = new OurView(this);
//        setContentView(ov);
        mApplication = (MyApplication) getApplicationContext();
        mApplication.pixelFont  = Typeface.createFromAsset(getAssets(), "fonts/ARCADECLASSIC.TTF");
        mApplication.pixelStartFont = Typeface.createFromAsset(getAssets(), "fonts/PressStart2P-Regular.ttf");

        AssetManager am = getApplicationContext().getAssets();
        TextView title = (TextView) findViewById(R.id.titleText);
//        Typeface pixelFont = Typeface.createFromAsset(getAssets(), "fonts/ARCADECLASSIC.TTF");
//
//        Typeface pixelStartFont = Typeface.createFromAsset(getAssets(), "fonts/PressStart2P-Regular.ttf");
        title.setTypeface(mApplication.pixelFont);
        Float alpha = 0.8f;
        PACKAGE_NAME = getApplicationContext().getPackageName();

        mStartButton = (Button) findViewById(R.id.startButton);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mEmail = (EditText) findViewById(R.id.login_email);
        mPassword = (EditText) findViewById(R.id.login_password);

        mLoginButton.setVisibility(View.GONE);
        mEmail.setVisibility(View.GONE);
        mPassword.setVisibility(View.GONE);

        mLoginButton.setTypeface(mApplication.pixelStartFont);
        mLoginButton.getBackground().setAlpha(129);
        mStartButton.setTypeface(mApplication.pixelStartFont);
        mStartButton.getBackground().setAlpha(129);
        mEmail.getBackground().setAlpha(129);
        mPassword.getBackground().setAlpha(129);

        mEmail.setText("test@gmail.com");
        mPassword.setText("swagger");

        mStartButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mStartButton.setVisibility(View.GONE);
                mLoginButton.setVisibility(View.VISIBLE);
                mEmail.setVisibility(View.VISIBLE);
                mPassword.setVisibility(View.VISIBLE);

            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                view.startAnimation(Defines.buttonClick);
                attemptLogin();
            }
        });

        //get rid of error box if touched.
        mEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmail.setError(null);
            }
        });
        mPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmail.setError(null);
            }
        });

        mProgressView = findViewById(R.id.login_progress);

    }



    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mp3Player.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mp3Player.start();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "MainActivity onDestroy()");
//        mp3Player.release(); //prob redundant
//        MainSurfaceView msv = (MainSurfaceView) findViewById(R.id.mainSurfaceView);
//        msv.surfaceDestroyed();
//        msv.free();

        super.onDestroy();
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
        // automatically handle clicks on the Home/Up startButton, so long
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


    private boolean isEmailValid(String email)
    {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password)
    {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void attemptLogin()
    {
        if (mAuthTask != null)
        {
            return;
        }

        // Reset errors.
        mEmail.setError(null);
        mPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email))
        {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        }
        else if (!isEmailValid(email))
        {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        else if (TextUtils.isEmpty(password))
        {
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        }
        else if (!isPasswordValid(password))
        {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            mBlackFog.setVisibility(View.VISIBLE);
            mProgressView.setVisibility(View.VISIBLE);
            int shortAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);
            mProgressView.animate().setDuration(6000).alpha(1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(View.GONE);
//                    mBlackFog.setVisibility(View.GONE);
                }
            });
            mAuthTask = new UserLoginTask(email, password, this);
            mAuthTask.execute((Void) null);
        }
    }

    @Override
    public void done() {
        Log.v(TAG, "Done() called, finish called");
//        MainSurfaceView msv = (MainSurfaceView) findViewById(R.id.mainSurfaceView);
//        msv.free();
        this.finish();
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, MyHelper.results>
    {
        private TaskCallback mCallback;
        private final String mEmailString;
        private final String mPasswordString;

        private String serverResponse;

        private MyHelper.results Result;


        UserLoginTask(String email, String password, TaskCallback callback)
        {
            mEmailString = email;
            mPasswordString = password;
            mCallback = callback;
        }

        @Override
        protected MyHelper.results doInBackground(Void... params)
        {
            // attempt authentication against a network service.
            String charset = "UTF-8";
            Result = new MyHelper.results();
            // hardcoded user for testing purposes
            Log.v(TAG,mEmailString);
            if (mEmailString.equalsIgnoreCase("@@@"))
            {
                Log.v(TAG, "@@@ logged in");
                Result.success = true; Result.httpResponse = MyHelper.HttpResponses.SUCCESS; return Result;
            }
            URL url;
            HttpURLConnection urlConnection;
            int responseCode;
            try {
                url = new URL(Defines.SERVER_ADDRESS + "/loginUser.php");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(6000);
                urlConnection.setReadTimeout(6000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
                OutputStream out = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(out, "UTF-8"));

                String query = String.format("Content-Type: application/json&charset=%s&password=%s&email=%s",
                        URLEncoder.encode(charset, charset),
                        URLEncoder.encode(mPasswordString, charset),
                        URLEncoder.encode(mEmailString, charset));
                writer.write(query);

                writer.flush();
                writer.close();
                out.close();
                Log.v(TAG, "did we make it here.");
                try {
                    responseCode = urlConnection.getResponseCode();
                } catch (IOException e)
                {
                    responseCode = urlConnection.getResponseCode();
                }

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.v(TAG, "we got 200");
                    Result.httpResponse = MyHelper.HttpResponses.SUCCESS;
                    serverResponse = MyHelper.readHttpStream(urlConnection.getInputStream());
                    Log.v(TAG, serverResponse);
                } else {
                    Result.httpResponse = MyHelper.HttpResponses.FAILURE;
                    Result.success = false;
//                    serverResponse = MyHelper.readHttpStream(urlConnection.getInputStream());
                    Log.v(TAG, "Server request failed, Response (" + responseCode+")");
                    Result.response = "Server request failed, Response (" + responseCode+")";
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                Log.e(TAG, "http timeout.. probably no internet:" + e);
                Result.httpResponse = MyHelper.HttpResponses.TIMEOUT;
                Result.success = false;
                Result.response = e.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e(TAG, "malformed url exception: " + e);
                Result.httpResponse = MyHelper.HttpResponses.FAILURE;
                Result.success = false;
                Result.response = e.toString();
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e);
                e.printStackTrace();
                Result.httpResponse = MyHelper.HttpResponses.FAILURE;
                Result.success = false;
                Result.response = e.toString();
            }
            if (Result.httpResponse != MyHelper.HttpResponses.SUCCESS)
            {
                Log.e(TAG, "response was a failure");
                return Result;
            }
            String result = null;
            String username = null;
            try {
                JSONObject jsonObject = new JSONObject(serverResponse);
                Log.v(TAG, jsonObject.getString("result"));
                result = jsonObject.getString("result");
                if (result.equals("true"))
                {
                    mApplication.userData.setID(jsonObject.getInt("id"));
                    mApplication.userData.setUsername(jsonObject.getString("username"));
                    mApplication.userData.setEmail(jsonObject.getString("email"));
                    mApplication.userData.setMoney(jsonObject.getInt("money"));
                    Log.e(TAG, "memarket  " + jsonObject.getInt("money") + jsonObject.getInt("id"));
                    Result.success = true;
                    Result.response = jsonObject.toString();
                }
                else
                {
                    Result.success = false;
                    Result.response = jsonObject.getString("error");
                    Result.code = jsonObject.getInt("code");
                }

            } catch (JSONException e) {
                Log.e(TAG, "Error parsing data " + e.toString());
                e.printStackTrace();
                Result.success = false;
                Result.response = e.toString();
            }

            if (!Result.success)
            {
                return Result;
            }
            else if (result.equals("true"))
            {
                Result.success = true;
            }
            else // (result == null || result == "false")
            {
                Result.success = false;
            }
            return Result;
        }

        @Override
        protected void onPostExecute(final MyHelper.results Result)
        {
            mAuthTask = null;



            Log.v(TAG,"Response is: " + Result.response);
            if (Result.success)
            {
                Toast.makeText(getBaseContext(), "Success - hi " + mApplication.userData.getUsername() +"!",
                        Toast.LENGTH_SHORT).show();
                Log.v(TAG, "About to start a new activity MenuActivity");
                // Start NewActivity.class
                Intent myIntent = new Intent(MainActivity.this, MenuActivity.class);
                // these flags would clear the task stack..
                // myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(myIntent);
                Log.v(TAG, "Hello? next thing to do is call finish after starting menuactivity");
//                MainSurfaceView msv = (MainSurfaceView) findViewById(R.id.mainSurfaceView);
//                msv.free();
                mCallback.done();
            }
            else if (Result.httpResponse == MyHelper.HttpResponses.TIMEOUT)
            {
                mPassword.setError(getString(R.string.error_http_timeout));
                MyHelper.AlertBox(MainActivity.this, "Timed out, check your Internet Connection.\n 600ms timeout ERROR (101)");
            }
            else
            {
                try {
                    if (Result.code.equals(401))
                    {
                        mEmail.setError(Result.response);
                        mEmail.requestFocus();
                    }
                    else if(Result.code.equals(403))
                    {
                        mPassword.setError(Result.response);
                        mPassword.requestFocus();
                    }
                    else
                    {
                        MyHelper.AlertBox(MainActivity.this, Result.response);
                    }

                } catch (NullPointerException e) {
                    MyHelper.AlertBox(MainActivity.this, Result.response);
                }
            }
//            mBlackFog.setVisibility(View.GONE);
            mProgressView.setVisibility(View.GONE);
        }

        @Override
        protected void onCancelled()
        {
//            mAuthTask = null;
////            mBlackFog.setVisibility(View.GONE);
//            mProgressView.setVisibility(View.GONE);
        }
    }






}
