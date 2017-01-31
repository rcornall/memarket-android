package com.robthecornallgmail.memarket.Activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.robthecornallgmail.memarket.Util.MemePastData;
import com.robthecornallgmail.memarket.Util.MyApplication;
import com.robthecornallgmail.memarket.Util.MyHelper;
import com.robthecornallgmail.memarket.R;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import com.robthecornallgmail.memarket.Util.Defines;

public class MenuActivity extends AppCompatActivity
{
    private MyApplication mApplication;

    private static final String TAG = "Menu";
    Boolean initialDisplay = true;
    private Spinner memeChoicesSpinner;
    private Button mSettingsWheelButton;
    private GraphView mGraphView;
    private GetGraphData mGetGraphTask;
    private DateRange mDateRange = DateRange.DAY;
    private Map<String, Integer> mMemeNametoIDMap = new HashMap<>();
    private Map<Integer, LineGraphSeries<DataPoint>> mMemeIDtoSeriesMap = new HashMap<>();
    private Map<String, Integer> mMemeNametoStockMap = new HashMap<>();
    private Map<Integer, Integer> mMemeIDtoAmountHeld = new HashMap<>();

    private String mSelectedName;
    private Integer mSelectedMemeID;

    // map meme IDs to their respective past Data from server.
    private Map<Integer, MemePastData> mGraphDataObjectMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mApplication = (MyApplication) getApplicationContext();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mGraphView = (GraphView) findViewById(R.id.graph);

        mSettingsWheelButton = (Button) findViewById(R.id.settings_button);
        mSettingsWheelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(MenuActivity.this, android.R.style.Theme_DeviceDefault_Dialog_Alert).create();
                alertDialog.setTitle("Settings");
                alertDialog.setMessage("Would you like to Logout?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        TextView username = (TextView) findViewById(R.id.username_text);
        username.setText(mApplication.userData.getUsername());

        final TextView money = (TextView) findViewById(R.id.money_text);
        money.setText("$" + mApplication.userData.getMoney().toString());


        memeChoicesSpinner = (Spinner) findViewById(R.id.memeChoicesSpinner);
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
                mSelectedName = parentView.getItemAtPosition(position).toString();
                try {
                    mSelectedMemeID = mMemeNametoIDMap.get(mSelectedName);
                } catch (NullPointerException e)
                {
                    e.printStackTrace();
                }
                Log.v(TAG, "test:10 guy. " + "real:" + mSelectedName + ".");
                TextView stockTextView = (TextView) findViewById(R.id.dataFromServer);
                String stockView = mSelectedName + String.format(Locale.US, "'s current stock is %d", mMemeNametoStockMap.get(mSelectedName));
                stockTextView.setText(stockView);

                TextView circleStockView = (TextView) findViewById(R.id.circle_stock_thing);
                circleStockView.setText("$" + mMemeNametoStockMap.get(mSelectedName).toString());

                TextView sharesOwnedView = (TextView) findViewById(R.id.shares_owned);
                try {
                    sharesOwnedView.setText("Shares Currently Held = " + mMemeIDtoAmountHeld.get(mSelectedMemeID).toString());
                } catch (NullPointerException e) {
                    sharesOwnedView.setText("Shares Currently Held = 0");
                            e.printStackTrace();
                }

                TextView graphTitle = (TextView) findViewById(R.id.graphTitle);
                graphTitle.setText(mSelectedName);

                // get mSelectedName of icon jpg or png
                String iconName = "icon_" + mSelectedName.replaceAll(" ", "_").toLowerCase();
                int iconId = getResources().getIdentifier(iconName, "drawable", getPackageName());
                AppCompatImageButton memeIcon = (AppCompatImageButton) findViewById(R.id.memeIcon);
                memeIcon.setImageResource(iconId);

                // fill graph with data of past (day, month?)
                try {
                    if (!mGraphDataObjectMap.get(mMemeNametoIDMap.get(mSelectedName)).isDoneAlready()) {
                        mGetGraphTask = new GetGraphData(mMemeNametoIDMap.get(mSelectedName));
                        mGetGraphTask.execute(Defines.SERVER_ADDRESS + "/getPast2Days.php?");
                    }
                    else
                    {
                        //still need to refresh graph with newly selected meme's data previously stored
                        mGraphView.removeAllSeries();
                        mGraphView.addSeries(mMemeIDtoSeriesMap.get(mMemeNametoIDMap.get(mSelectedName)));
                    }
                } catch (NullPointerException e) {
                    //we didnt load data into that mMemeNametoIDMap.get(mSelectedName) yet.
                    mGetGraphTask = new GetGraphData(mMemeNametoIDMap.get(mSelectedName));
                    mGetGraphTask.execute(Defines.SERVER_ADDRESS + "/getPast2Days.php?");
                }

//                mGraphDataObjectMap.get(3).pastMonthData

                return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                return;
            }
        });

        final ToggleButton dayButton = (ToggleButton) findViewById(R.id.DayButton);
        final ToggleButton weekButton = (ToggleButton) findViewById(R.id.WeekButton);
        final ToggleButton monthButton = (ToggleButton) findViewById(R.id.MonthButton);
        final ToggleButton yearButton = (ToggleButton) findViewById(R.id.YearButton);
        dayButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    weekButton.setChecked(false);
                    monthButton.setChecked(false);
                    yearButton.setChecked(false);
                    mDateRange = com.robthecornallgmail.memarket.Activities.DateRange.DAY;

                    mGraphView.removeAllSeries();
                    mGraphView.addSeries(mMemeIDtoSeriesMap.get(mMemeNametoIDMap.get(mSelectedName)));
                    mGraphView.getGridLabelRenderer().setNumHorizontalLabels(6); //spacing
                    Calendar cal = Calendar.getInstance();
                    Date start = cal.getTime();
                    cal.add(Calendar.HOUR, -24);
                    Date end = cal.getTime();
                    mGraphView.getViewport().setMaxX(start.getTime());
                    mGraphView.getViewport().setMinX(end.getTime());
                    // refresh graph (its glitchy)
                    mGraphView.refreshDrawableState();
                    mGraphView.removeAllSeries();
                    mGraphView.addSeries(mMemeIDtoSeriesMap.get(mMemeNametoIDMap.get(mSelectedName)));
                    mGraphView.refreshDrawableState();
                } else {

                }
            }
        });
        weekButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dayButton.setChecked(false);
                    monthButton.setChecked(false);
                    yearButton.setChecked(false);
                    mDateRange = com.robthecornallgmail.memarket.Activities.DateRange.WEEK;

                    mGraphView.removeAllSeries();
                    mGraphView.addSeries(mMemeIDtoSeriesMap.get(mMemeNametoIDMap.get(mSelectedName)));
                    mGraphView.getGridLabelRenderer().setNumHorizontalLabels(4); //spacing
                    Calendar cal = Calendar.getInstance();
                    Date start = cal.getTime();
                    cal.add(Calendar.DAY_OF_YEAR, -7);
                    Date end = cal.getTime();
                    mGraphView.getViewport().setMaxX(start.getTime());
                    mGraphView.getViewport().setMinX(end.getTime());
                    // refresh graph (its glitchy)
                    mGraphView.refreshDrawableState();
                    mGraphView.removeAllSeries();
                    mGraphView.addSeries(mMemeIDtoSeriesMap.get(mMemeNametoIDMap.get(mSelectedName)));
                    mGraphView.refreshDrawableState();


                } else {

                }
            }
        });
        monthButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dayButton.setChecked(false);
                    weekButton.setChecked(false);
                    yearButton.setChecked(false);
                    mDateRange = com.robthecornallgmail.memarket.Activities.DateRange.MONTH;

                    mGraphView.removeAllSeries();
                    mGraphView.addSeries(mMemeIDtoSeriesMap.get(mMemeNametoIDMap.get(mSelectedName)));
                    mGraphView.getGridLabelRenderer().setNumHorizontalLabels(4); //spacing
                    Calendar cal = Calendar.getInstance();
                    Date start = cal.getTime();
                    cal.add(Calendar.DAY_OF_YEAR, -30);
                    Date end = cal.getTime();
                    mGraphView.getViewport().setMaxX(start.getTime());
                    mGraphView.getViewport().setMinX(end.getTime());
                    // refresh graph (its glitchy)
                    mGraphView.refreshDrawableState();
                    mGraphView.removeAllSeries();
                    mGraphView.addSeries(mMemeIDtoSeriesMap.get(mMemeNametoIDMap.get(mSelectedName)));
                    mGraphView.refreshDrawableState();
                } else {

                }
            }
        });
        yearButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dayButton.setChecked(false);
                    weekButton.setChecked(false);
                    monthButton.setChecked(false);
                    mDateRange = com.robthecornallgmail.memarket.Activities.DateRange.YEAR;

                    mGraphView.removeAllSeries();
                    mGraphView.addSeries(mMemeIDtoSeriesMap.get(mMemeNametoIDMap.get(mSelectedName)));
                    mGraphView.getGridLabelRenderer().setNumHorizontalLabels(4); //
                    Calendar cal = Calendar.getInstance();
                    Date start = cal.getTime();
                    cal.add(Calendar.YEAR, -1);
                    Date end = cal.getTime();
                    mGraphView.getViewport().setMaxX(start.getTime());
                    mGraphView.getViewport().setMinX(end.getTime());
                    // refresh graph (its glitchy)
                    mGraphView.refreshDrawableState();
                    mGraphView.removeAllSeries();
                    mGraphView.addSeries(mMemeIDtoSeriesMap.get(mMemeNametoIDMap.get(mSelectedName)));
                    mGraphView.refreshDrawableState();
                } else {

                }
            }
        });

        Button buyStock = (Button) findViewById(R.id.buy_stock_button);
        buyStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(Defines.buttonClick);
                if (mApplication.userData.getMoney() < mMemeNametoStockMap.get(mSelectedName) + 5)
                {
                    Toast.makeText(getBaseContext(), "Not enough money!",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    new PurchaseStockFromServer(mApplication.userData.getID(), mMemeNametoIDMap.get(mSelectedName), 1).execute(Defines.SERVER_ADDRESS + "/purchaseStock.php");
                }

            }
        });
        Button sellStock = (Button) findViewById(R.id.sell_stock_button);
        sellStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer memeID = mMemeNametoIDMap.get(mSelectedName);
                v.startAnimation(Defines.buttonClick);
                if (mApplication.userData.getMoney() < mMemeNametoStockMap.get(mSelectedName) + 5)
                {
                    Toast.makeText(getBaseContext(), "Not enough money!",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try {
                        Integer currentAmount = mMemeIDtoAmountHeld.get(memeID);
                        if (currentAmount > 0)
                            new PurchaseStockFromServer(mApplication.userData.getID(), memeID, 1).execute(Defines.SERVER_ADDRESS + "/sellStock.php");
                    } catch (NullPointerException e) {
                        // cannot sell if the user doesnt own any stock to sell.
                        e.printStackTrace();
                    }
                }

            }
        });

        new GetDataFromServer().execute(Defines.SERVER_ADDRESS + "/getData.php?action", "GETTING_DATA");

        new GetDataFromServer().execute(Defines.SERVER_ADDRESS + "/getUserStocks.php?user=" + mApplication.userData.getID(), "GETTING_USER_STOCKS");

    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    public class PurchaseStockFromServer extends AsyncTask<String,Void,Boolean>
    {
        private String serverResponse;
        private final Integer mUserID;
        private final Integer mMemeID;
        private final Integer mAmount;
        private Integer mNewMoney;
        private Integer mNewStocks;
        PurchaseStockFromServer(Integer user_id, Integer meme_id, Integer amount)
        {
            mUserID = user_id;
            mMemeID = meme_id;
            mAmount = amount;
        }
        @Override
        protected Boolean doInBackground(String... strings)
        {
            URL url;
            HttpURLConnection urlConnection;
            String charset = "UTF-8";
            int responseCode;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(7000);
                urlConnection.setReadTimeout(7000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
                OutputStream out = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(out, "UTF-8"));

                String query = String.format("Content-Type: application/json&charset=%s&user=%d&meme=%d&amount=%d",
                        URLEncoder.encode(charset, charset),
                        mUserID,mMemeID,mAmount);
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
                    Log.v(TAG, "purchase: we got 200");
                    serverResponse = MyHelper.readHttpStream(urlConnection.getInputStream());
                } else {
                    Log.v(TAG, "purchase we got " + responseCode);
                    return false;
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                Log.e(TAG, "http timeout on purchase.. probably no internet:" + e);
                return false;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e(TAG, "malformed url exception: " + e);
                return false;
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e);
                e.printStackTrace();
                return false;
            }

            try {
                JSONObject jsonObject = new JSONObject(serverResponse);
                Log.v(TAG, jsonObject.getString("result"));
                String result = jsonObject.getString("result");
                if (result.equals("true"))
                {
                    mNewMoney = jsonObject.getInt("new_money");
                    mNewStocks = jsonObject.getInt("new_stocks");
                    mMemeIDtoAmountHeld.put(mMemeID, mNewStocks);
                    return true;
                }
                else
                {
                    String err = jsonObject.getString("error");
                    Integer code = jsonObject.getInt("code");
                    Log.e(TAG, "Error purchasing stock: " + code + ": " + err);
                    return false;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing data " + e.toString());
                e.printStackTrace();
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean Result)
        {
            if (Result)
            {
                TextView moneyTextView = (TextView) findViewById(R.id.money_text);
                Animation shrinkAnim = AnimationUtils.loadAnimation(MenuActivity.this, R.anim.shrink_money);
                shrinkAnim.reset();
                Animation growAnim = AnimationUtils.loadAnimation(MenuActivity.this, R.anim.shrink_money);
                growAnim.reset();

                moneyTextView.clearAnimation();
                moneyTextView.startAnimation(shrinkAnim);
                moneyTextView.setText("$" + mNewMoney.toString());
                moneyTextView.clearAnimation();
                moneyTextView.startAnimation(growAnim);

                TextView sharesOwnedView = (TextView) findViewById(R.id.shares_owned);
                sharesOwnedView.setText("Shares Currently Held = " + mNewStocks);
            }
            return;
        }
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
                    if (strings[1].equals("GETTING_DATA"))
                    {
                        Integer ID = jsonObject.getInt("ID");
                        String NAME = jsonObject.getString("NAME");
                        Integer CURRENT_STOCK = jsonObject.getInt("CURRENT_STOCK");
                        mMemeNametoIDMap.put(NAME, ID);
                        mMemeNametoStockMap.put(NAME, CURRENT_STOCK);
                    } else if (strings[1].equals("GETTING_USER_STOCKS")) {
                        Integer MEME_ID = jsonObject.getInt("MEME_ID");
                        Integer AMOUNT = jsonObject.getInt("AMOUNT");
                        mMemeIDtoAmountHeld.put(MEME_ID, AMOUNT);
                    } else {
                        Result.success = false;
                        Log.e(TAG, "params wrong..");
                        return  Result;
                    }
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
                    if (result.equals("false"))
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
//            newTextView.setText(mMemeNametoStockMap.get("10 guy") + " --- ");

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
    public class GetGraphData extends AsyncTask<String , Void , MyHelper.results>
    {
        private String serverResponse;
        private MyHelper.results Result;
        private Integer mMemeID;
        GetGraphData(Integer memeID)
        {
            mMemeID = memeID;
        }

        @Override
        protected MyHelper.results doInBackground(String... strings)
        {

            URL url;
            HttpURLConnection urlConnection;
            Result = new MyHelper.results();
            int responseCode;
//            Integer range;
//            if (mDateRange == DateRange.DAY) {
//                range = 1;
//            } else if (mDateRange == DateRange.WEEK) {
//                range = 7;
//            } else if (mDateRange == DateRange.MONTH) {
//                range = 30;
//            } else if (mDateRange == DateRange.YEAR) {
//                range = 365;
//            }

            try {
                url = new URL(strings[0] + "meme=" + mMemeID.toString());
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
//                    Log.v(TAG, serverResponse);
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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // need to initialize with an empty MemePastData object.
                mGraphDataObjectMap.put(mMemeID, new MemePastData());
                for(int i=0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Date date = sdf.parse(jsonObject.getString("Date"));
                    Integer stock = jsonObject.getInt("stock");
                    mGraphDataObjectMap.get(mMemeID).pastYearData.put(date,stock);
                }
                mGraphDataObjectMap.get(mMemeID).setDoneAlready();
                Result.response = "we put at " + mMemeID.toString();
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
                    if (result.equals("false"))
                    {
                        Result.success = false;
                        Result.response = jsonObject.getString("error");
                    }
                } catch (JSONException e2) {
                    Log.e(TAG, "error cant get server response:"  + e2);
                    Result.response = "Server Connection Issues, Try again later";
                }
            } catch (java.text.ParseException e) {
                Log.e(TAG, "Error parsing data " + e.toString());
                e.printStackTrace();
                Result.success = false;
                Result.response = "App parsing Issues, Try again later";
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
                Result.success = false;
                Result.response = "unknown error.";
            }
            return Result;
        }

        @Override
        protected void onPostExecute(MyHelper.results Result)
        {

//            TextView newTextView = (TextView) findViewById(R.id.dataFromServer);
//            newTextView.setText(mMemeNametoStockMap.get("10 guy") + " --- ");

            Log.v(TAG,"Response is: " + Result.response);

            if (Result.success)
            {
                mMemeIDtoSeriesMap.put(mMemeID, new LineGraphSeries<DataPoint>());
//                populateSpinner();
                Log.v(TAG, "graph data was got??.......");

                for(Map.Entry<Date, Integer> entry : mGraphDataObjectMap.get(mMemeID).pastYearData.entrySet()) {
                    mMemeIDtoSeriesMap.get(mMemeID).appendData(new DataPoint(entry.getKey(),entry.getValue()), true, 35040);
                    Log.v(TAG , entry.getKey().toString());
                    Log.v(TAG , entry.getValue().toString());
                }
                mMemeIDtoSeriesMap.get(mMemeID).setColor(ContextCompat.getColor(MenuActivity.this, R.color.monokaiBlue));
                mMemeIDtoSeriesMap.get(mMemeID).setDrawDataPoints(true);
                mMemeIDtoSeriesMap.get(mMemeID).setDataPointsRadius(10);
                mMemeIDtoSeriesMap.get(mMemeID).setThickness(8);
                // remove old mMemeIDtoSeriesMap.get(mMemeID)(line)
                mGraphView.removeAllSeries();
                mGraphView.addSeries(mMemeIDtoSeriesMap.get(mMemeID));
                // set date label formatter

                mGraphView.getViewport().setScalable(true);
                mGraphView.getViewport().setScrollable(true);

                mGraphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(MenuActivity.this));
                mGraphView.getGridLabelRenderer().setNumHorizontalLabels(6); // only 4 because of the space
                mGraphView.getGridLabelRenderer().setTextSize(33);
                mGraphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        // TODO Auto-generated method stub
                        if (isValueX) {
                            Date date = new Date((long) (value));
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            Log.v(TAG, date.toString());
                            SimpleDateFormat sdf = new SimpleDateFormat();
                            if (mDateRange == DateRange.DAY) {
                                sdf.applyPattern("h:mma");
                            } else if (mDateRange == DateRange.WEEK) {
                                sdf.applyPattern("E h:mma");
                            } else if (mDateRange == DateRange.MONTH) {
                                sdf.applyPattern("MMM dd, ha");
                            } else /*(mDateRange == DateRange.YEAR)*/ {
                                sdf.applyPattern("MMM d, ''yy");
                            }
                            return (sdf.format(date));
                        } else {
                            return "$" + (int) value;
                        }
                    }
                });
                Calendar cal = Calendar.getInstance();
                Date start = cal.getTime();
                cal.add(Calendar.HOUR, -24);
                Date end = cal.getTime();
                mGraphView.getViewport().setMaxX(start.getTime());
                mGraphView.getViewport().setMinX(end.getTime());

            }
            else
            {
                Log.e(TAG, "failed to get data for graph");

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
        for (String key : mMemeNametoStockMap.keySet()) {
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
