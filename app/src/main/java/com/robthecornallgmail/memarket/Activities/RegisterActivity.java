package com.robthecornallgmail.memarket.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.Defines;
import com.robthecornallgmail.memarket.Util.MyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final String TAG = "Register";
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mUsernameView;
    private View mProgressView;
    private View mRegisterFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.v(TAG, "after_set");
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.register_email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.register_password);
        mUsernameView = (EditText) findViewById(R.id.register_user_name);
//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.register || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });
        Log.v(TAG, "AFTER other");
        Button mRegisterAccountButton = (Button) findViewById(R.id.register_account_button);
        mRegisterAccountButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mUsernameView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username, if the user entered one.
        if (TextUtils.isEmpty(username))
        {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }
        else if (!isUsernameValid(username))
        {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }
        // Check for a valid email address.
        else if (TextUtils.isEmpty(email))
        {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        else if (!isEmailValid(email))
        {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        else if (TextUtils.isEmpty(password))
        {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        else if (!isPasswordValid(password))
        {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            MyHelper.showProgress(true, mProgressView, mRegisterFormView, getResources());
            Log.v(TAG,  "username: " + username);
            mAuthTask = new UserRegisterTask(username, email, password);
            mAuthTask.execute((Void) null);
        }
    }
    private boolean isUsernameValid(String username)
    {
        return username.length() > 2;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, MyHelper.results> {

        private final String mUsername;
        private final String mEmail;
        private final String mPassword;
        private final String mDate;
        private String serverResponse;

        private MyHelper.results Result;

        UserRegisterTask(String username, String email, String password) {
            mUsername = username;
            mEmail = email;
            mPassword = password;

            DateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            mDate = date.format(new Date());

        }

        @Override
        protected MyHelper.results doInBackground(Void... params) {
            // attempt registering against a network service.

            Boolean ret = false;
            String charset = "UTF-8";
            Result = new MyHelper.results();
            // get time
            Log.v(TAG, "Date: " + mDate);
            URL url;
            HttpURLConnection urlConnection;
            int responseCode;
            try {
                url = new URL(Defines.SERVER_ADDRESS + "/registerUser.php");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(7000);
                urlConnection.setReadTimeout(7000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
                OutputStream out = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(out, "UTF-8"));

                String query = String.format("Content-Type: application/json&charset=%s&name=%s&password=%s&email=%s&date=%s",
                        URLEncoder.encode(charset, charset),
                        URLEncoder.encode(mUsername, charset),
                        URLEncoder.encode(mPassword, charset),
                        URLEncoder.encode(mEmail, charset),
                        URLEncoder.encode(mDate, charset));
                Log.v(TAG, "query is: " + query);
                writer.write(query);

                writer.flush();
                writer.close();
                out.close();
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
                    // serverResponse = MyHelper.readHttpStream(urlConnection.getInputStream());
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
            }

            return Result;
        }

        @Override
        protected void onPostExecute(final MyHelper.results Result) {
            mAuthTask = null;
            MyHelper.showProgress(false, mProgressView, mRegisterFormView, getResources());

            Log.v(TAG,"Response is: " + Result.response);
            if (Result.success)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this, android.R.style.Theme_DeviceDefault_Dialog_Alert).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Successfully registered " + mUsername + "! continue to login");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent myIntent = new Intent(RegisterActivity.this, MenuActivity.class);
                                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                //instead just override onBackPressed in MenuActivity to close app.
                                // and add logout button ..
                                startActivity(myIntent);
                            }
                        });
                alertDialog.show();

            }
            else if (Result.httpResponse == MyHelper.HttpResponses.TIMEOUT)
            {
                mPasswordView.setError(getString(R.string.error_http_timeout));
                MyHelper.AlertBox(RegisterActivity.this, "Timed out, check your Internet Connection.\n 600ms timeout ERROR (101)");
            }
            else
            {
                try {
                    if (Result.code.equals(400))
                    {
                        mUsernameView.setError(Result.response);
                        mUsernameView.requestFocus();
                    }
                    else if (Result.code.equals(401))
                    {
                        mEmailView.setError(Result.response);
                        mEmailView.requestFocus();
                    }
                    else if(Result.code.equals(403))
                    {
                        mPasswordView.setError(Result.response);
                        mPasswordView.requestFocus();
                    }
                    else
                    {
                        MyHelper.AlertBox(RegisterActivity.this, Result.response);
                    }

                } catch (NullPointerException e) {
                    MyHelper.AlertBox(RegisterActivity.this, Result.response);
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            MyHelper.showProgress(false, mProgressView, mRegisterFormView, getResources());
        }
    }
}

