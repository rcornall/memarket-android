package com.robthecornallgmail.memarket.Activities;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import android.util.Log;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.robthecornallgmail.memarket.Fragments.ListMemesFragment;
import com.robthecornallgmail.memarket.Fragments.MemeDetailsFragment;
import com.robthecornallgmail.memarket.Util.MemePastData;
import com.robthecornallgmail.memarket.Util.MemeRow;
import com.robthecornallgmail.memarket.Util.MyApplication;
import com.robthecornallgmail.memarket.Util.MyHelper;
import com.robthecornallgmail.memarket.R;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.robthecornallgmail.memarket.Util.Defines;
import com.robthecornallgmail.memarket.Util.UserRow;
import com.robthecornallgmail.memarket.Views.HouseSurfaceView;
import com.robthecornallgmail.memarket.Fragments.LeaderboardDialogFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import static java.lang.Thread.sleep;

public class MenuActivity extends AppCompatActivity implements ListMemesFragment.OnListFragmentInteractionListener,
        MemeDetailsFragment.OnFragmentInteractionListener,
        LeaderboardDialogFragment.OnLeaderboardFragmentInteractionListener
{
    private static long timeTillUpdate = 900000;
    private static CountDownTimer mFifteenMinTimer;
    private MyApplication mApplication;
    private static Calendar calendar;

    private static final String TAG = "Menu";

    private Button mSettingsWheelButton, mLeaderboardButton;

    private GraphView mGraphView;
    private GetGraphData mGetGraphTask;
    private DateRange mDateRange = DateRange.DAY;
    private Map<String, Integer> mMemeNametoIDMap = new HashMap<>();
    private Map<Integer, LineGraphSeries<DataPoint>> mMemeIDtoSeriesMap = new HashMap<>();
    private Map<String, Integer> mMemeNametoStockMap = new HashMap<>();
    private Map<Integer, Integer> mMemeIDtoAmountHeld = new HashMap<>();
    private Map<String, Integer> mMemeNametoLastStockMap= new HashMap<>();
    private Map<String,Integer> mLeaderboardUsersToMoneyMap = new LinkedHashMap<>(); //preserves ordering in HashMap

    private String mSelectedName;
    private Integer mSelectedMemeID;

    EditText mSearchView;
    private ListMemesFragment mMemeListFragment;
    private LeaderboardDialogFragment mLeaderboardDialogFragment;

    MemeDetailsFragment mMemeDetailsFragment;

    HouseSurfaceView mHouseSurfaceView;
    // map meme IDs to their respective past Data from server.
    private Map<Integer, MemePastData> mGraphDataObjectMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.v(TAG, "MenuActivity onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mApplication = (MyApplication) getApplicationContext();
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        Log.v(TAG, "memoryClass: " + Integer.toString(memoryClass));

        mMemeListFragment = new ListMemesFragment();
//        mLeaderboardDialogFragment = new LeaderboardDialogFragment();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mGraphView = (GraphView) findViewById(R.id.graph);
        mHouseSurfaceView = (HouseSurfaceView) findViewById(R.id.house_surface_view);

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
                                mHouseSurfaceView.free();
                                Intent myIntent = new Intent(MenuActivity.this, MainActivity.class);
                                startActivity(myIntent);
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
        mLeaderboardButton = (Button) findViewById(R.id.leaderboard_button);
        mLeaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeaderboardUsersToMoneyMap.clear();
                new GetDataFromServer().execute(Defines.SERVER_ADDRESS + "/getLeaderboard.php?action", "GETTING_LEADERBOARD");
                mLeaderboardDialogFragment = new LeaderboardDialogFragment();
                mLeaderboardDialogFragment.show(getFragmentManager(), TAG);
            }
        });


        final ImageView characterIcon = (ImageView) findViewById(R.id.character_icon);
        characterIcon.post(new Runnable() {
            @Override
            public void run() {
                // start the blinking animation..
                ((AnimationDrawable) characterIcon.getBackground()).start();
            }
        });

        TextView username = (TextView) findViewById(R.id.username_text);
        username.setText(mApplication.userData.getUsername());

        final TextView money = (TextView) findViewById(R.id.money_text);
        money.setText("$" + mApplication.userData.getMoney().toString());

        mSearchView = (EditText) findViewById(R.id.search_meme_view);
//        mSearchView.getBackground().setColorFilter(getResources().getColor(R.color.colorMyOffGrey), PorterDuff.Mode.SRC_IN);
        mSearchView.setBackgroundColor(getResources().getColor(R.color.backgroundGrey));
//        mSearchView.setVisibility(View.GONE);
        final RelativeLayout searchBar = (RelativeLayout) findViewById(R.id.TopBarSeperator);
        final RelativeLayout dragDownButton = (RelativeLayout) findViewById(R.id.drag_down_line);
        final LinearLayout linearLayoutTabButtons = (LinearLayout) findViewById(R.id.linearLayoutTabButtons);

        dragDownButton.setAlpha(0.95f);
        searchBar.setBackgroundColor(getResources().getColor(R.color.backgroundGrey));
        searchBar.setVisibility(View.GONE);

        linearLayoutTabButtons.setBackgroundColor(getResources().getColor(R.color.backgroundGrey));

        final FrameLayout flListFragment = (FrameLayout) findViewById(R.id.meme_list_fragment);
        final FrameLayout flDetailsFragment = (FrameLayout) findViewById(R.id.meme_details_fragment);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.meme_list_fragment, mMemeListFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        flListFragment.setVisibility(View.GONE);


        final View findMemesHighlight =(View) findViewById(R.id.find_memes_highlight);
        final View homeHighlight =(View) findViewById(R.id.home_highlight);
        final View storeHighlight =(View) findViewById(R.id.store_highlight);
        storeHighlight.setVisibility(View.INVISIBLE);
        findMemesHighlight.setVisibility(View.INVISIBLE);

        Button findMemesButton = (Button) findViewById(R.id.find_memes_button);
        findMemesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "FIND MEMES PRESSED");

                /* Manually freeing Bitmaps because java sucks? */
                mHouseSurfaceView.free();

                mHouseSurfaceView.setVisibility(View.INVISIBLE);
                homeHighlight.setVisibility(View.INVISIBLE);
                storeHighlight.setVisibility(View.INVISIBLE);
                findMemesHighlight.setVisibility(View.VISIBLE);
                Log.v(TAG, "SET TO INVISIBLE DONE");
//                mSearchView.setVisibility(View.VISIBLE);
                searchBar.setVisibility(View.VISIBLE);
                flListFragment.setVisibility(View.VISIBLE);
                flDetailsFragment.setVisibility(View.VISIBLE);
                dragDownButton.setAlpha(0.5f);
//                try {
//                    mMemeListFragment.updateList(mMemeNametoStockMap);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });
        Button homeButton = (Button) findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "HOME PRESSED");
                homeHighlight.setVisibility(View.VISIBLE);
                storeHighlight.setVisibility(View.INVISIBLE);
                findMemesHighlight.setVisibility(View.INVISIBLE);

                flDetailsFragment.setVisibility(View.GONE);
                flListFragment.setVisibility(View.GONE);
//                mSearchView.setVisibility(View.GONE);
                searchBar.setVisibility(View.GONE);
                mHouseSurfaceView.setVisibility(View.VISIBLE);
                dragDownButton.setAlpha(0.95f);


            }
        });
        Button storeButton = (Button) findViewById(R.id.store_button);
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeHighlight.setVisibility(View.INVISIBLE);
                findMemesHighlight.setVisibility(View.INVISIBLE);
                storeHighlight.setVisibility(View.VISIBLE);
            }
        });

        mSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mMemeListFragment.filter(s.toString());
            }
        });

        final TextView countdownTimer = (TextView) findViewById(R.id.timer_text);
        final String FORMAT = "%2d:%02d";

        mFifteenMinTimer = new CountDownTimer(900000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownTimer.setText(String.format(FORMAT,TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            @Override
            public void onFinish() {
                //refresh stock values:
                new GetDataFromServer().execute(Defines.SERVER_ADDRESS + "/getData.php?action", "GETTING_DATA");
                try{
                    new GetGraphData(mMemeNametoIDMap.get(mSelectedName)).execute(Defines.SERVER_ADDRESS + "/getPast2Days.php?");
                } catch (NullPointerException e) {
                    Log.v(TAG, e.toString());
                }

                //clear old graph data so we forced to get new ones
                try {
                    for(MemePastData it : mGraphDataObjectMap.values())
                    {
                        it.setNotDone();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                this.cancel();
                this.start();
            }
        };
        //countdown timer, figure out when next 15 min interval is
        calendar = Calendar.getInstance();
        long minutes = calendar.get(Calendar.MINUTE);
        long seconds = calendar.get(Calendar.SECOND);
        Log.v(TAG, "mins is: " + minutes + " seconds: " + seconds);
        long minsRemain = (15 - minutes%15) - 1;
        long secsRemain = (60 - seconds);
        Log.v(TAG, "minsrenain is: " + minsRemain+ " seconds: " + secsRemain);

        timeTillUpdate = (minsRemain*60 + secsRemain)*1000;
        new CountDownTimer(timeTillUpdate, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                countdownTimer.setText(String.format(FORMAT,TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            @Override
            public void onFinish() {
                //refresh stock values:
                new GetDataFromServer().execute(Defines.SERVER_ADDRESS + "/getData.php?action", "GETTING_DATA");
                try{
                    new GetGraphData(mMemeNametoIDMap.get(mSelectedName)).execute(Defines.SERVER_ADDRESS + "/getPast2Days.php?");
                } catch (NullPointerException e) {
                    Log.v(TAG, e.toString());
                }
                //clear old graph data so we forced to get new ones
                try {
                    for(MemePastData it : mGraphDataObjectMap.values())
                    {
                        it.setNotDone();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                this.cancel();
                MenuActivity.mFifteenMinTimer.start();

            }
        }.start();



        /*                    load character view in slidingdrawer layout                    */
        ImageView userCharacter = (ImageView) findViewById(R.id.character_full);
        Picasso.with(getApplicationContext()).load(R.drawable.character_full).fit().centerInside().into(userCharacter);

        ImageView invBag, invOffice, invPeople, invMemes, invFinancials;
        invBag = (ImageView) findViewById(R.id.inventory_bag);
        invOffice = (ImageView) findViewById(R.id.inventory_office);
        invPeople= (ImageView) findViewById(R.id.inventory_people);
        invMemes = (ImageView) findViewById(R.id.inventory_memes);
        invFinancials= (ImageView) findViewById(R.id.inventory_financials);

        final View bagHighlight, officeHighlight, peopleHighlight, memesHighlight, financialsHighlight;
        final View bagHighlightView, officeHighlightView, peopleHighlightView, memesHighlightView, financialsHighlightView;

        bagHighlight = findViewById(R.id.inventory_bag_highlight);
        officeHighlight = findViewById(R.id.inventory_office_highlight);
        peopleHighlight = findViewById(R.id.inventory_people_highlight);
        memesHighlight = findViewById(R.id.inventory_memes_highlight);
        financialsHighlight  = findViewById(R.id.inventory_financials_highlight);

        bagHighlightView = findViewById(R.id.bag_highlight_view);
        officeHighlightView = findViewById(R.id.office_highlight_view);
        peopleHighlightView = findViewById(R.id.people_highlight_view);
        memesHighlightView = findViewById(R.id.memes_highlight_view);
        financialsHighlightView  = findViewById(R.id.financials_highlight_view);

        Picasso.with(getApplicationContext()).load(R.drawable.bag).fit().centerInside().into(invBag);
        Picasso.with(getApplicationContext()).load(R.drawable.office_inventory).fit().centerInside().into(invOffice);
        Picasso.with(getApplicationContext()).load(R.drawable.people_inventory).fit().centerInside().into(invPeople);
        Picasso.with(getApplicationContext()).load(R.drawable.memes_inventory).fit().centerInside().into(invMemes);
        Picasso.with(getApplicationContext()).load(R.drawable.financials_inventory).fit().centerInside().into(invFinancials);


        memesHighlight.setVisibility(View.INVISIBLE);
        officeHighlight.setVisibility(View.INVISIBLE);
        peopleHighlight.setVisibility(View.INVISIBLE);
        financialsHighlight.setVisibility(View.INVISIBLE);

        memesHighlightView.setVisibility(View.INVISIBLE);
        officeHighlightView.setVisibility(View.INVISIBLE);
        peopleHighlightView.setVisibility(View.INVISIBLE);
        financialsHighlightView.setVisibility(View.INVISIBLE);

        invBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bagHighlight.setVisibility(View.VISIBLE);
                bagHighlightView.setVisibility(View.VISIBLE);
                officeHighlight.setVisibility(View.INVISIBLE);
                officeHighlightView.setVisibility(View.INVISIBLE);
                peopleHighlight.setVisibility(View.INVISIBLE);
                peopleHighlightView.setVisibility(View.INVISIBLE);
                memesHighlight.setVisibility(View.INVISIBLE);
                memesHighlightView.setVisibility(View.INVISIBLE);
                financialsHighlight.setVisibility(View.INVISIBLE);
                financialsHighlightView.setVisibility(View.INVISIBLE);
            }
        });
        invOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bagHighlight.setVisibility(View.INVISIBLE);
                bagHighlightView.setVisibility(View.INVISIBLE);
                officeHighlight.setVisibility(View.VISIBLE);
                officeHighlightView.setVisibility(View.VISIBLE);
                peopleHighlight.setVisibility(View.INVISIBLE);
                peopleHighlightView.setVisibility(View.INVISIBLE);
                memesHighlight.setVisibility(View.INVISIBLE);
                memesHighlightView.setVisibility(View.INVISIBLE);
                financialsHighlight.setVisibility(View.INVISIBLE);
                financialsHighlightView.setVisibility(View.INVISIBLE);
            }
        });
        invPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bagHighlight.setVisibility(View.INVISIBLE);
                bagHighlightView.setVisibility(View.INVISIBLE);
                officeHighlight.setVisibility(View.INVISIBLE);
                officeHighlightView.setVisibility(View.INVISIBLE);
                peopleHighlight.setVisibility(View.VISIBLE);
                peopleHighlightView.setVisibility(View.VISIBLE);
                memesHighlight.setVisibility(View.INVISIBLE);
                memesHighlightView.setVisibility(View.INVISIBLE);
                financialsHighlight.setVisibility(View.INVISIBLE);
                financialsHighlightView.setVisibility(View.INVISIBLE);
            }
        });
        invMemes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bagHighlight.setVisibility(View.INVISIBLE);
                bagHighlightView.setVisibility(View.INVISIBLE);
                officeHighlight.setVisibility(View.INVISIBLE);
                officeHighlightView.setVisibility(View.INVISIBLE);
                peopleHighlight.setVisibility(View.INVISIBLE);
                peopleHighlightView.setVisibility(View.INVISIBLE);
                memesHighlight.setVisibility(View.VISIBLE);
                memesHighlightView.setVisibility(View.VISIBLE);
                financialsHighlight.setVisibility(View.INVISIBLE);
                financialsHighlightView.setVisibility(View.INVISIBLE);
            }
        });
        invFinancials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bagHighlight.setVisibility(View.INVISIBLE);
                bagHighlightView.setVisibility(View.INVISIBLE);
                officeHighlight.setVisibility(View.INVISIBLE);
                officeHighlightView.setVisibility(View.INVISIBLE);
                peopleHighlight.setVisibility(View.INVISIBLE);
                peopleHighlightView.setVisibility(View.INVISIBLE);
                memesHighlight.setVisibility(View.INVISIBLE);
                memesHighlightView.setVisibility(View.INVISIBLE);
                financialsHighlight.setVisibility(View.VISIBLE);
                financialsHighlightView.setVisibility(View.VISIBLE);
            }
        });


        new GetDataFromServer().execute(Defines.SERVER_ADDRESS + "/getData.php?action", "GETTING_DATA");
        new GetDataFromServer().execute(Defines.SERVER_ADDRESS + "/getUserStocks.php?user=" + mApplication.userData.getID(), "GETTING_USER_STOCKS");

    }






    @Override
    public void onListFragmentInteraction(MemeRow row)
    {
        mSelectedName = row.getName();
        Integer amountOwned = 0;
        try {
            mSelectedMemeID = mMemeNametoIDMap.get(mSelectedName);
            amountOwned = mMemeIDtoAmountHeld.get(mSelectedMemeID);
        } catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        if(mMemeDetailsFragment != null) {
            mMemeDetailsFragment = null;
            System.gc();
        }
        mMemeDetailsFragment = MemeDetailsFragment.newInstance(mSelectedName, row.getPrice(), (amountOwned != null ? amountOwned:0));
        Log.v(TAG, "onListFragmentInteraction called" + mSelectedName);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_bottom, 0);
        transaction.addToBackStack(null);
        transaction.add(R.id.meme_details_fragment, mMemeDetailsFragment);
        transaction.commit();
        mSearchView.setVisibility(View.GONE);

        try {
            if (!mGraphDataObjectMap.get(mMemeNametoIDMap.get(mSelectedName)).isDoneAlready()) {
                Log.v(TAG, "done already? " + mGraphDataObjectMap.get(mMemeNametoIDMap.get(mSelectedName)).isDoneAlready().toString());
                mGetGraphTask = new GetGraphData(mMemeNametoIDMap.get(mSelectedName));
                mGetGraphTask.execute(Defines.SERVER_ADDRESS + "/getPast2Days.php?");
            }
            else
            {
                Log.v(TAG, "AlreadyDone is true, adding existing series to graph");
                //still need to refresh graph, need to use a new thread so that this UI thread can finish.
                updateGraph ug = new updateGraph(mMemeIDtoSeriesMap.get(mMemeNametoIDMap.get(mSelectedName)),mDateRange,mMemeDetailsFragment);
                ug.execute("");

            }
        } catch (NullPointerException e) {
            Log.v(TAG, "exception, getting graph data");
            mGetGraphTask = new GetGraphData(mMemeNametoIDMap.get(mSelectedName));
            mGetGraphTask.execute(Defines.SERVER_ADDRESS + "/getPast2Days.php?");
        } catch (Exception e) {Log.e(TAG, e.toString());}
    }




    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 2) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStackImmediate();
            mSearchView.setVisibility(View.VISIBLE);
        } else {
            moveTaskToBack(true);
        }
    }

//    @Override
//    protected void onPause() {
//        Log.v(TAG,"ONPAUSE");
//        mHouseSurfaceView.setVisibility(View.GONE);
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        mHouseSurfaceView.setVisibility(View.VISIBLE);
//        super.onResume();
//    }

    @Override
    public void onMemeDetailsFragmentInteraction(String arg)
    {

        Log.v(TAG, "onListFragmentInteraction called: " + arg);
        if (arg.equals("buy"))
        {
            if (mApplication.userData.getMoney() < mMemeNametoStockMap.get(mSelectedName) + 5)
            {
                Toast.makeText(getBaseContext(), "Not enough money!",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                new MenuActivity.PurchaseStockFromServer(mApplication.userData.getID(), mMemeNametoIDMap.get(mSelectedName), 1).execute(Defines.SERVER_ADDRESS + "/purchaseStock.php");
            }
        }
        else
        {
            if (mMemeIDtoAmountHeld.get(mSelectedMemeID) == null)
                Toast.makeText(getBaseContext(), "Still loading, Try again",
                        Toast.LENGTH_SHORT).show();
            if (mMemeIDtoAmountHeld.get(mSelectedMemeID) == 0)
            {
                Toast.makeText(getBaseContext(), "Not enough Stocks!",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                new MenuActivity.PurchaseStockFromServer(mApplication.userData.getID(), mMemeNametoIDMap.get(mSelectedName), 1).execute(Defines.SERVER_ADDRESS + "/sellStock.php");
            }
        }
    }

    @Override
    public void onLeaderboardFragmentInteraction(UserRow userRow) {
        Log.v(TAG, "onLeaderboardFragmentInteractioncalled: ");
    }


// TODO: 03/08/17 NEED TO MAKE SURE ONLY ONE INSTANCE OF THIS THREAD IS RUNNING AT ONCE BEFORE STARTING A NEW ONE 

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
                Log.v(TAG, "newStocks:" + mNewStocks);
                TextView moneyTextView = (TextView) findViewById(R.id.money_text);
                Animation shrinkAnim = AnimationUtils.loadAnimation(MenuActivity.this, R.anim.shrink_money);
                Animation growAnim = AnimationUtils.loadAnimation(MenuActivity.this, R.anim.shrink_money);

                moneyTextView.clearAnimation();
                moneyTextView.startAnimation(shrinkAnim);
                moneyTextView.setText("$" + mNewMoney.toString());
                moneyTextView.startAnimation(growAnim);

                TextView sharesOwnedView = (TextView) findViewById(R.id.stocks_owned);
                sharesOwnedView.setText(mNewStocks.toString());

                mMemeIDtoAmountHeld.put(mMemeID, mNewStocks);
                mApplication.userData.setMoney(mNewMoney);

            }
            return;
        }
    }
    public class GetDataFromServer extends AsyncTask<String , Void , MyHelper.results>
    {
        private String serverResponse;
        private MyHelper.results Result;
        private String mRequest;

        @Override
        protected MyHelper.results doInBackground(String... strings)
        {
            mRequest = strings[1];
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
                        Integer LAST_STOCK = jsonObject.getInt("LAST_STOCK");
                        mMemeNametoIDMap.put(NAME, ID);
                        mMemeNametoStockMap.put(NAME, CURRENT_STOCK);
                        mMemeNametoLastStockMap.put(NAME, LAST_STOCK);
                    } else if (strings[1].equals("GETTING_USER_STOCKS")) {
                        Integer MEME_ID = jsonObject.getInt("MEME_ID");
                        Integer AMOUNT = jsonObject.getInt("AMOUNT");
                        mMemeIDtoAmountHeld.put(MEME_ID, AMOUNT);
                    } else if (strings[1].equals("GETTING_LEADERBOARD")) {
                        String NAME = jsonObject.getString("USER_DISPLAY_NAME");
                        Integer MONEY = jsonObject.getInt("MONEY");
                        mLeaderboardUsersToMoneyMap.put(NAME, MONEY);
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
            Log.v(TAG, mMemeNametoIDMap.toString());

            if (Result.success) {
//                mFraLgment = (ListMemesFragment) getSupportFragmentManager().findFragmentById(R.id.meme_list_fragment);
                if (mRequest == "GETTING_DATA") {
                    try {
                        mMemeListFragment.updateList(mMemeNametoStockMap, mMemeNametoLastStockMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try{
                        mMemeDetailsFragment.updateStockPrice(mMemeNametoStockMap.get(mSelectedName));
                    } catch (NullPointerException e) {
                        Log.v(TAG, e.toString());
                    }
                } else if(mRequest == "GETTING_LEADERBOARD") {
                    try {
                        mLeaderboardDialogFragment.updateList(mLeaderboardUsersToMoneyMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.v(TAG, "getting user stocks?? not updating any lists");
                }
            }
            else
            {
//                Log.e(TAG, "failed to get data");
//                TextView stockTextView = (TextView) findViewById(R.id.dataFromServer);
//                String error = String.format(Locale.US, "Error retrieving data from server:\n %s", Result.response);
//                stockTextView.setText(error);
//
//                AlertDialog alertDialog = new AlertDialog.Builder(MenuActivity.this, android.R.style.Theme_Holo_Dialog).create();
//                alertDialog.setTitle("Alert");
//                alertDialog.setMessage(Result.response);
//                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                alertDialog.show();
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
            } catch (NullPointerException e) {
                Log.v(TAG, "cant get graph data, meme not selected yet");
                Log.v(TAG, e.toString());
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

            Log.v(TAG,"Response is: " + Result.response);
            if (Result.success)
            {
                try {
                    mMemeIDtoSeriesMap.put(mMemeID, new LineGraphSeries<DataPoint>());
                    for(Map.Entry<Date, Integer> entry : mGraphDataObjectMap.get(mMemeID).pastYearData.entrySet()) {
                        mMemeIDtoSeriesMap.get(mMemeID).appendData(new DataPoint(entry.getKey(),entry.getValue()), true, 35040);
                    }
                    mMemeIDtoSeriesMap.get(mMemeID).setColor(ContextCompat.getColor(MenuActivity.this, R.color.monokaiBlue));
                    mMemeIDtoSeriesMap.get(mMemeID).setDrawDataPoints(true);
                    mMemeIDtoSeriesMap.get(mMemeID).setDataPointsRadius(10);
                    mMemeIDtoSeriesMap.get(mMemeID).setThickness(8);
                    mMemeDetailsFragment.updateGraph(mMemeIDtoSeriesMap.get(mMemeID),mDateRange);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    e.printStackTrace();
                }
            }
        }
    }

    private class updateGraph extends AsyncTask<String, Void, Boolean>
    {
        private LineGraphSeries<DataPoint> mDataPointLineGraphSeries;
        private DateRange mDateRange;
        private MemeDetailsFragment mMemeDetailsFragment;
        public updateGraph(LineGraphSeries<DataPoint> dplgs, DateRange dr, MemeDetailsFragment mdf) {
            this.mDataPointLineGraphSeries = dplgs;
            this.mDateRange = dr;
            this.mMemeDetailsFragment = mdf;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            int i = 0;
            while(true)
            {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    /* keep trying to update the graphView if its not null, otherwise request the data */
                    boolean res = this.mMemeDetailsFragment.updateGraph(this.mDataPointLineGraphSeries, this.mDateRange);
                    if (!res)
                    {
                        if(i >= 4)
                        {
                            return false;
                        }
                        i++;
                        continue;
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    if(i >= 2)
                    {
                        return false;
                    }
                    i++;
                    continue;
                }
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(!result)
            {
                mGetGraphTask = new GetGraphData(mMemeNametoIDMap.get(mSelectedName));
                mGetGraphTask.execute(Defines.SERVER_ADDRESS + "/getPast2Days.php?");
            }
        }
    }

}
