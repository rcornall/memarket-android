package com.robthecornallgmail.memarket.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

import com.robthecornallgmail.memarket.Util.MyHelper;
import com.robthecornallgmail.memarket.R;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.robthecornallgmail.memarket.Util.Defines;


public class MenuActivity extends AppCompatActivity
{
    private static final String TAG = "Menu";
    Boolean initialDisplay = true;
    private Spinner memeChoicesSpinner;
    private Map<String, Integer> memeMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        memeChoicesSpinner = (Spinner) findViewById(R.id.memeChoicesSpinner);


        new GetDataFromServer().execute(Defines.SERVER_ADDRESS + "/getData.php?action");

        memeChoicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
//                Log.v(TAG, "wut");
//                if (initialDisplay == true)
//                {
//                    Log.v(TAG",TAGcalled INITIALLYYYYYYYY........");
//                    initialDisplay = false;
//                    return;
//                }
                String name = parentView.getItemAtPosition(position).toString();
                Log.v(TAG, "test:10 guy. " + "real:" + name + ".");
                TextView stockTextView = (TextView) findViewById(R.id.dataFromServer);
                String stockView = name + String.format(Locale.US, "'s current stock is %d", memeMap.get(name));
                stockTextView.setText(stockView);

                // get name of icon jpg or png
                String iconName = "icon_" + name.replaceAll(" ", "_").toLowerCase();
                int iconId = getResources().getIdentifier(iconName, "drawable", getPackageName());
                AppCompatImageButton memeIcon = (AppCompatImageButton) findViewById(R.id.memeIcon);
                memeIcon.setImageResource(iconId);

                return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                return;
            }

        });
    }


    public class GetDataFromServer extends AsyncTask<String , Void , MyHelper.results>
    {
        private String serverResponse;
        private MyHelper.results Result;

        @Override
        protected MyHelper.results doInBackground(String... strings)
        {

            URL url;
            HttpURLConnection urlConnection;
            Result = new MyHelper.results();
            int responseCode;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(6000);
                urlConnection.setReadTimeout(6000);
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

            try {
                JSONArray jsonArray = new JSONArray(serverResponse);
                for(int i=0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    memeMap.put(jsonObject.getString("NAME"), jsonObject.getInt("CURRENT_STOCK"));
                }
                Result.success = true;
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing data " + e.toString());
                e.printStackTrace();
                Result.success = false;
                String result;
                try {
                    JSONObject jsonObject = new JSONObject(serverResponse);
                    result = jsonObject.getString("result");
                    Log.v(TAG, "result from server = " + result);
                    if (result == "false")
                    {
                        Result.success = false;
                        Result.response = jsonObject.getString("error");
                    }
                } catch (JSONException e2) {
                    Log.e(TAG, "error cant get server response:"  + e2);
                    Result.response = "Server Connection Issues, Try again later";
                }
            }
            return Result;
        }

        @Override
        protected void onPostExecute(MyHelper.results Result)
        {

//            TextView newTextView = (TextView) findViewById(R.id.dataFromServer);
//            newTextView.setText(memeMap.get("10 guy") + " --- ");

            Log.v(TAG,"Response is: " + Result.response);

            if (Result.success)
            {
                populateSpinner();
                Log.v(TAG, "Spinner was populated.......");
            }
            else
            {
                Log.e(TAG, "failed to get data");
                TextView stockTextView = (TextView) findViewById(R.id.dataFromServer);
                String error = String.format(Locale.US, "Error retrieving data from server:\n %s", Result.response);
                stockTextView.setText(error);

                AlertDialog alertDialog = new AlertDialog.Builder(MenuActivity.this, android.R.style.Theme_Holo_Dialog).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage(Result.response);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }


    }
    private void populateSpinner()
    {
        List<String> spinnerLables = new ArrayList<>();
        for (String key : memeMap.keySet()) {
            spinnerLables.add(key);
        }

        //            Spinner memeChoicesSpinner = (Spinner) findViewById(R.id.memeChoicesSpinner);
        // Creating adapter for spinner
        Collections.sort(spinnerLables);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerLables);
        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        memeChoicesSpinner.setAdapter(spinnerAdapter);
    }

}
