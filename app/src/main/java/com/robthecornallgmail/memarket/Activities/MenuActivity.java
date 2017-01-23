package com.robthecornallgmail.memarket.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.net.HttpURLConnection;
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
                Log.v("Spinner", "onItemSelected was called........");
//                if (initialDisplay == true)
//                {
//                    Log.v("Spinner", "onItemSelected was called INITIALLYYYYYYYY........");
//                    initialDisplay = false;
//                    return;
//                }
                String name = parentView.getItemAtPosition(position).toString();
                Log.v("Spinner", "test:10 guy. " + "real:" + name + ".");
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


    public class GetDataFromServer extends AsyncTask<String , Void ,String>
    {
        private String serverResponse;


        @Override
        protected String doInBackground(String... strings)
        {

            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    serverResponse = MyHelper.readHttpStream(urlConnection.getInputStream());
                    Log.v("HTTP Response", serverResponse);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                JSONArray jsonArray = new JSONArray(serverResponse);
                Log.v("Json Parser", "did it work>>>");
                for(int i=0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    memeMap.put(jsonObject.getString("NAME"), jsonObject.getInt("CURRENT_STOCK"));
                }
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {

//            TextView newTextView = (TextView) findViewById(R.id.dataFromServer);
//            newTextView.setText(memeMap.get("10 guy") + " --- ");

            populateSpinner();
            Log.v("Spinner", "Spinner was populated.......");
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
