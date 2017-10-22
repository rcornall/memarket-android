package com.robthecornallgmail.memarket.Activities;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.robthecornallgmail.memarket.Fragments.ActiveListingsFragment;
import com.robthecornallgmail.memarket.Fragments.BagGridFragment;
import com.robthecornallgmail.memarket.Fragments.ListMemesFragment;
import com.robthecornallgmail.memarket.Fragments.MemeDetailsFragment;
import com.robthecornallgmail.memarket.Fragments.NewMemeFragment;
import com.robthecornallgmail.memarket.Fragments.OrdersListFragment;
import com.robthecornallgmail.memarket.Util.ActiveRow;
import com.robthecornallgmail.memarket.Util.BagGrid;
import com.robthecornallgmail.memarket.Util.ItemObject;
import com.robthecornallgmail.memarket.Util.MemeObject;
import com.robthecornallgmail.memarket.Util.MemePastData;
import com.robthecornallgmail.memarket.Util.MemeRow;
import com.robthecornallgmail.memarket.Util.MyApplication;
import com.robthecornallgmail.memarket.Util.MyHelper;
import com.robthecornallgmail.memarket.R;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.robthecornallgmail.memarket.Util.Defines;
import com.robthecornallgmail.memarket.Util.OrderRow;
import com.robthecornallgmail.memarket.Util.UserBuildingBitmap;
import com.robthecornallgmail.memarket.Util.UserItem;
import com.robthecornallgmail.memarket.Util.UserRow;
import com.robthecornallgmail.memarket.Util.UserWorkerBitmap;
import com.robthecornallgmail.memarket.Views.HouseSurfaceView;
import com.robthecornallgmail.memarket.Fragments.LeaderboardDialogFragment;
import com.robthecornallgmail.memarket.Views.InsideHouseSurfaceView;
import com.squareup.picasso.Picasso;

import static java.lang.Thread.sleep;

public class MenuActivity extends AppCompatActivity implements ListMemesFragment.OnListFragmentInteractionListener,
        MemeDetailsFragment.OnFragmentInteractionListener,
        LeaderboardDialogFragment.OnLeaderboardFragmentInteractionListener,
        OrdersListFragment.OnOrdersListNewOrderListener,
        BagGridFragment.OnBagGridInteractionListener,
        ActiveListingsFragment.OnActiveListingsInteractionListener,

        HouseSurfaceView.OnBuildingInteractionListener
{
    private static final String TAG = "Menu";
    public  static MyApplication mApplication;

    private Calendar calendar;
    private  long timeTillUpdate = 900000;
    private static CountDownTimer mFifteenMinTimer;

    private ProgressDialog mProgressDialog;

    private Button mSettingsWheelButton, mLeaderboardButton;
    private RelativeLayout mAddMemeLayout;
    private FloatingActionButton mAddMemeButton;
    /*graph stuff*/
    private GraphView mGraphView;
    private GetGraphData mGetGraphTask;

    private DateRange mDateRange = DateRange.DAY;
    /*maps holding various data*/
    private HashMap<Integer, MemeObject> mMemeIdtoObject = new LinkedHashMap<>();
    private List<OrderRow> mOrderRows = new ArrayList<>();
    private List<ActiveRow> mActiveOrdersList;
    private Map<Integer, LineGraphSeries<DataPoint>> mMemeIDtoSeriesMap = new HashMap<>();
    private Map<String,Integer> mLeaderboardUsersToMoneyMap = new LinkedHashMap<>(); //preserves ordering in HashMap
    private HashMap<Integer, ItemObject> mItemsIdToObject = new HashMap<>();

    private HashMap<Integer, UserItem> mUserItemIdToUsersItems = new HashMap<>();
    /*selected meme for list/detail fragments*/
    private String mSelectedName;

    private Integer mSelectedMemeID;

    private RelativeLayout mSearchView;
    private EditText mSearchMemes;

    /*fragments*/
    private ListMemesFragment mMemeListFragment;
    private ActiveListingsFragment mActiveListingsFragment;
    private MemeDetailsFragment mMemeDetailsFragment;
    private NewMemeFragment mNewMemeFragment;
    private OrdersListFragment mOrdersListFragment;
    private LeaderboardDialogFragment mLeaderboardDialogFragment;
    private BagGridFragment mBagGridFragment;

    private FrameLayout mMemeListFrameLayout, mMemeDetailsFrameLayout, mMemeOrdersFrameLayout;

    private HouseSurfaceView mHouseSurfaceView;
    private InsideHouseSurfaceView mInsideHouseSurfaceView;
    // map meme IDs to their respective past Data from server.
    private Map<Integer, MemePastData> mGraphDataObjectMap = new HashMap<>();
    private TextView mMoney;
    private Button mActiveListingBtn;
    private Button mFreshMemesBtn;
    private View mSearchMemesHighlight;
    private View mActiveListingHighlight;
    private View mFreshMemesHighlight;


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
        mNewMemeFragment = new NewMemeFragment();
        mActiveListingsFragment = new ActiveListingsFragment();
        mOrdersListFragment = new OrdersListFragment();
        mBagGridFragment = new BagGridFragment();


//        mLeaderboardDialogFragment = new LeaderboardDialogFragment();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mGraphView = (GraphView) findViewById(R.id.graph);
        mInsideHouseSurfaceView = (InsideHouseSurfaceView) findViewById(R.id.inside_house_surface_view);
        mInsideHouseSurfaceView.setVisibility(View.GONE);
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

        mAddMemeLayout = (RelativeLayout) findViewById(R.id.add_meme_layout);
        mAddMemeLayout.setVisibility(View.GONE);
        mAddMemeButton = (FloatingActionButton) findViewById(R.id.add_meme_fab);
        mAddMemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*TODO: fragment for adding a new meme submission*/
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.new_meme_fragment, mNewMemeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
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

        mMoney = (TextView) findViewById(R.id.money_text);
        mMoney.setText("$" + mApplication.userData.getMoney().toString());

        mSearchView = (RelativeLayout) findViewById(R.id.TopBarSeperator);
        mSearchMemes = (EditText) findViewById(R.id.search_meme_edittext);
//        mSearchView.getBackground().setColorFilter(getResources().getColor(R.color.colorMyOffGrey), PorterDuff.Mode.SRC_IN);
        mSearchMemes.setBackgroundColor(getResources().getColor(R.color.backgroundGrey));
//        mSearchView.setVisibility(View.GONE);
        final RelativeLayout searchBar = (RelativeLayout) findViewById(R.id.TopBarSeperator);
        final RelativeLayout dragDownButton = (RelativeLayout) findViewById(R.id.drag_down_line);
        final LinearLayout linearLayoutTabButtons = (LinearLayout) findViewById(R.id.linearLayoutTabButtons);

        dragDownButton.setAlpha(0.95f);
        searchBar.setBackgroundColor(getResources().getColor(R.color.backgroundGrey));
        searchBar.setVisibility(View.GONE);

        linearLayoutTabButtons.setBackgroundColor(getResources().getColor(R.color.backgroundGrey));

        /* get framelayouts for fragments*/
        mMemeListFrameLayout = (FrameLayout) findViewById(R.id.meme_list_fragment);
        mMemeDetailsFrameLayout = (FrameLayout) findViewById(R.id.meme_details_fragment);
        mMemeOrdersFrameLayout = (FrameLayout) findViewById(R.id.orders_list_fragment);

        /* add list fragment and hide it first (for home view to show) */
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.meme_list_fragment, mMemeListFragment);
        transaction.addToBackStack(null);
        /* add bag grid fragment (first to show as default) */
        transaction.add(R.id.inventory_bag_fragment, mBagGridFragment);
        transaction.commit();
        mMemeListFrameLayout.setVisibility(View.GONE);


        /* Setup highlights for bottom tab buttons e.g. home find store*/
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

                /* Manually freeing Bitmaps because java */
                mHouseSurfaceView.free();

                mHouseSurfaceView.setVisibility(View.INVISIBLE);
                mInsideHouseSurfaceView.setVisibility(View.INVISIBLE);
                homeHighlight.setVisibility(View.INVISIBLE);
                storeHighlight.setVisibility(View.INVISIBLE);
                findMemesHighlight.setVisibility(View.VISIBLE);
                Log.v(TAG, "SET TO INVISIBLE DONE");
//                mSearchView.setVisibility(View.VISIBLE);
                searchBar.setVisibility(View.VISIBLE);
                mMemeOrdersFrameLayout.setVisibility(View.VISIBLE);
                mMemeListFrameLayout.setVisibility(View.VISIBLE);
                mMemeDetailsFrameLayout.setVisibility(View.VISIBLE);
                if(getSupportFragmentManager().getBackStackEntryCount() == 1){
                    mAddMemeLayout.setVisibility(View.VISIBLE);
                }
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

                mMemeDetailsFrameLayout.setVisibility(View.GONE);
                mMemeListFrameLayout.setVisibility(View.GONE);
                mMemeOrdersFrameLayout.setVisibility(View.GONE);
//                mSearchView.setVisibility(View.GONE);
                searchBar.setVisibility(View.GONE);
                mAddMemeLayout.setVisibility(View.GONE);
                mHouseSurfaceView.setVisibility(View.VISIBLE);
                mInsideHouseSurfaceView.setVisibility(View.INVISIBLE);

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

        /* setup search memes, activing listings, fresh memes layout */

        /* Setup highlights for bottom tab buttons e.g. home find store*/
        mSearchMemesHighlight = findViewById(R.id.search_memes_highlight);
        mActiveListingHighlight = findViewById(R.id.active_listing_highlight);
        mFreshMemesHighlight = findViewById(R.id.fresh_memes_highlight);
        mActiveListingHighlight.setVisibility(View.INVISIBLE);
        mFreshMemesHighlight.setVisibility(View.INVISIBLE);

        mActiveListingBtn = (Button) findViewById(R.id.active_listing_button);
        mFreshMemesBtn = (Button) findViewById(R.id.fresh_memes_button);

        mSearchMemes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSearchMemesHighlight.setVisibility(View.VISIBLE);
                mActiveListingHighlight.setVisibility(View.INVISIBLE);
                mFreshMemesHighlight.setVisibility(View.INVISIBLE);

                mSearchMemes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_white_24dp, 0,0,0);

                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() >= 2) {
                    Log.i("MainActivity", "popping backstack  > 2");
                    fm.popBackStackImmediate();
                }
                return false;
            }
        });
        mActiveListingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchMemesHighlight.setVisibility(View.INVISIBLE);
                mActiveListingHighlight.setVisibility(View.VISIBLE);
                mFreshMemesHighlight.setVisibility(View.INVISIBLE);

                mSearchMemes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_back_white_24dp, 0,0,0);
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() == 2) {
                    return;
                }

                /* fetch and inflate active listings fragment, on top of search. hide fresh memes fragment*/
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.active_listings_fragment, mActiveListingsFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                new GetDataFromServer().execute(Defines.SERVER_ADDRESS + "/getActiveOrders.php?user=" + mApplication.userData.getID(), "GETTING_ACTIVE_ORDERS");

            }
        });
        mFreshMemesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchMemesHighlight.setVisibility(View.INVISIBLE);
                mActiveListingHighlight.setVisibility(View.INVISIBLE);
                mFreshMemesHighlight.setVisibility(View.VISIBLE);

                mSearchMemes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_back_white_24dp, 0,0,0);

                /*TODO: inflate fresh memes fragment, on top of search. hide active listings fragment*/
            }
        });




        mSearchMemes.addTextChangedListener(new TextWatcher() {
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
                    new GetGraphData(mSelectedMemeID).execute(Defines.SERVER_ADDRESS + "/getPast2Days.php?");
                } catch (NullPointerException e) {
                    Log.v(TAG, e.toString());
                }

                //clear old graph data so we forced to get new ones
                try {
                    for(MemePastData it : mGraphDataObjectMap.values()){
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
                    new GetGraphData(mSelectedMemeID).execute(Defines.SERVER_ADDRESS + "/getPast2Days.php?");
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


        new GetDataFromServer().execute
                (Defines.SERVER_ADDRESS + "/getData.php?action", "GETTING_DATA");
        new GetDataFromServer().execute
                (Defines.SERVER_ADDRESS + "/getUserStocks.php?user=" + mApplication.userData.getID(), "GETTING_USER_STOCKS");
        new GetDataFromServer().execute
                (Defines.SERVER_ADDRESS + "/getUserItems.php?user=" + mApplication.userData.getID(), "GETTING_USER_ITEMS");
        new GetDataFromServer().execute
                (Defines.SERVER_ADDRESS + "/getItemTypes.php?", "GETTING_ITEM_TYPES");

    }

    @Override
    public void onListFragmentInteraction(MemeRow row)
    {
        mOrdersListFragment.clearList();
        mSelectedMemeID = row.mID;
        Integer mAmountOwned = 0;
        try {
            mAmountOwned = mMemeIdtoObject.get(mSelectedMemeID).mSharesHeld;
            if(mAmountOwned == null)
            {
                throw new NullPointerException("sldk");
            }
        } catch (NullPointerException e) {
            Log.v(TAG, "couldntget sharesheld: " + e.toString());
            e.printStackTrace();
        }


        if(mMemeDetailsFragment != null) {
            mMemeDetailsFragment = null;
//            System.gc();
        }
        mMemeDetailsFragment = MemeDetailsFragment.newInstance(mSelectedMemeID, mMemeIdtoObject.get(mSelectedMemeID), mAmountOwned);
        Log.v(TAG, "onListFragmentInteraction called" + mMemeIdtoObject.get(mSelectedMemeID).mName);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_bottom, 0);
        transaction.addToBackStack(null);
        transaction.add(R.id.meme_details_fragment, mMemeDetailsFragment);
        transaction.commit();
        mSearchView.setVisibility(View.GONE);
        mAddMemeLayout.setVisibility(View.GONE);

        mMemeDetailsFragment.setGraphProgressDialog(true);

        try {
            if (!mGraphDataObjectMap.get(mSelectedMemeID).isDoneAlready()) {
                Log.v(TAG, "done already? " + mGraphDataObjectMap.get(mSelectedMemeID).isDoneAlready().toString());
                mGetGraphTask = new GetGraphData(mSelectedMemeID);
                mGetGraphTask.execute(Defines.SERVER_ADDRESS + "/getPast2Days.php?");
            }
            else
            {
                Log.v(TAG, "AlreadyDone is true, adding existing series to graph");
                //still need to refresh graph, need to use a new thread so that this UI thread can finish.
                updateGraph ug = new updateGraph(mMemeIDtoSeriesMap.get(mSelectedMemeID),mDateRange,mMemeDetailsFragment);
                ug.execute("");

            }
        } catch (NullPointerException e) {
            Log.v(TAG, "exception, getting graph data");
            mGetGraphTask = new GetGraphData(mSelectedMemeID);
            mGetGraphTask.execute(Defines.SERVER_ADDRESS + "/getPast2Days.php?");
        } catch (Exception e) {Log.e(TAG, e.toString());}
    }


    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 2) {
            Log.i("MainActivity", "popping backstack  > 2");
            fm.popBackStackImmediate();
        } else if (fm.getBackStackEntryCount() == 2) {
            Log.i("MainActivity", "popping backstack == 2");
            fm.popBackStackImmediate();

            mSearchView.setVisibility(View.VISIBLE);
            mSearchMemesHighlight.setVisibility(View.VISIBLE);
            mActiveListingHighlight.setVisibility(View.INVISIBLE);
            mFreshMemesHighlight.setVisibility(View.INVISIBLE);

            mSearchMemes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_white_24dp, 0,0,0);

            mAddMemeLayout.setVisibility(View.VISIBLE);
        } else {
            /* hide insidehouse view if visible */
            if(mInsideHouseSurfaceView.getVisibility() == View.VISIBLE)
            {
                mInsideHouseSurfaceView.setVisibility(View.GONE);
                mHouseSurfaceView.setVisibility(View.VISIBLE);
            }
            else
            {
                moveTaskToBack(true);
            }
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
            Log.v(TAG, "buy called" + mMemeIdtoObject.get(mSelectedMemeID).mName);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_bottom, 0);
            transaction.addToBackStack(null);
            transaction.add(R.id.orders_list_fragment, mOrdersListFragment);
            transaction.commit();
            new GetDataFromServer().execute(Defines.SERVER_ADDRESS + String.format("/getOrders.php?meme=%d&sell=true", mSelectedMemeID), "GETTING_SELL_ORDERS");
        }
        else
        {
            Log.v(TAG, "sell called" + mMemeIdtoObject.get(mSelectedMemeID).mName);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_bottom, 0);
            transaction.addToBackStack(null);
            transaction.add(R.id.orders_list_fragment, mOrdersListFragment);
            transaction.commit();

            new GetDataFromServer().execute(Defines.SERVER_ADDRESS + String.format("/getOrders.php?meme=%d&buy=true", mSelectedMemeID), "GETTING_BUY_ORDERS");
        }
    }

    @Override
    public void onLeaderboardFragmentInteraction(UserRow userRow) {
        Log.v(TAG, "onLeaderboardFragmentInteractioncalled: ");
    }

    @Override
    public void OnBagGridInteraction(BagGrid row) {
        Log.v(TAG, "OnBagGridInteraction called ");
    }

    /* create Inside Building view with users building type, employees */
    @Override
    public void OnBuildingInteraction(Map.Entry<Integer, UserBuildingBitmap> userBuildingBitmap, HashMap<Integer, UserWorkerBitmap> userWorkers){
        Log.v(TAG, "OnBuildingInteraction() : " + userBuildingBitmap.getValue().mName.toString());
        mHouseSurfaceView.setVisibility(View.GONE);
        mInsideHouseSurfaceView.updateUserBitmap(userBuildingBitmap, userWorkers);
        mInsideHouseSurfaceView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onOrdersListNewOrder(Integer memeId, Integer amount, Integer price, Boolean isBuy, String buy) {
        /* clicking new order */
        new PlaceOrder(mApplication.userData.getID(), memeId, 0, amount, price)
            .execute(Defines.SERVER_ADDRESS + String.format("/new%sOrder.php",buy), "NEW_ORDER");
    }
    @Override
    public void onOrdersListDirectOrder(Integer memeID, Integer orderID, Integer amount, Boolean isBuy, String buy) {
        /* clicking direct buy */
        new PlaceOrder(mApplication.userData.getID(), memeID, orderID, amount, 0)
                .execute(Defines.SERVER_ADDRESS + String.format("/%sOrder.php", buy.toLowerCase()), "ORDER_FROM_USER");
    }

    @Override
    public void refreshList(Boolean isBuy) {
        if(isBuy) {
            new GetDataFromServer().execute(Defines.SERVER_ADDRESS + "/getOrders.php?sell=true", "GETTING_SELL_ORDERS");
        } else {
            new GetDataFromServer().execute(Defines.SERVER_ADDRESS + "/getOrders.php?buy=true", "GETTING_BUY_ORDERS");
        }
    }
    @Override
    public void onActiveListingsCancel(ActiveRow row) {
        /* active orders callback for cancelling an order */
        new PlaceOrder(mApplication.userData.getID(), null, row.mOrderID, null, null)
                .execute(Defines.SERVER_ADDRESS + String.format("/cancelOrder.php"), row.mIsBuy ? "CANCEL_BUY_ORDER" : "CANCEL_SELL_ORDER");
        return;
    }

    @Override
    public void refreshList() {
        new GetDataFromServer().execute(Defines.SERVER_ADDRESS + "/getActiveOrders.php?user=" + mApplication.userData.getID(), "GETTING_ACTIVE_ORDERS");
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
                if (strings[1].equals("GETTING_BUY_ORDERS") || strings[1].equals("GETTING_SELL_ORDERS")) {
                    mOrderRows.clear(); /*refresh with new values*/
                }
                if (mRequest.equals("GETTING_ACTIVE_ORDERS")) {
                    JSONObject jsonObject = new JSONObject(serverResponse);
                    JSONArray buyOrders = jsonObject.getJSONArray("buy_orders");
                    JSONArray sellOrders = jsonObject.getJSONArray("sell_orders");
                    /* merge+sort both arrays into 1 array, order by date desc */
                    mActiveOrdersList = mergeAndSort(buyOrders, sellOrders);
                    Result.success = true;
                } else {
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
                            String LINK = "http://knowyourmeme.com";
                            try{
                                LINK = jsonObject.getString("KYM_URL");
                            } catch (JSONException e) {
                                Log.v(TAG, "no knowyourmeme url for this meme: " + e.toString());
                            }
                            if (mMemeIdtoObject.get(ID) == null)
                            {
                                mMemeIdtoObject.put(ID, new MemeObject(NAME, CURRENT_STOCK,LAST_STOCK,LINK));
                            }
                            else
                            {
                                mMemeIdtoObject.get(ID).mPrice = CURRENT_STOCK;
                                mMemeIdtoObject.get(ID).mLastPrice = LAST_STOCK;
                            }
                        } else if (strings[1].equals("GETTING_USER_STOCKS")) {
                            Integer MEME_ID = jsonObject.getInt("MEME_ID");
                            Integer AMOUNT = jsonObject.getInt("AMOUNT");
                            mMemeIdtoObject.get(MEME_ID).mSharesHeld = AMOUNT;
                        } else if (strings[1].equals("GETTING_LEADERBOARD")) {
                            String NAME = jsonObject.getString("USER_DISPLAY_NAME");
                            Integer MONEY = jsonObject.getInt("MONEY");
                            mLeaderboardUsersToMoneyMap.put(NAME, MONEY);
                        } else if (strings[1].equals("GETTING_ITEM_TYPES")) {
                            Integer ID = jsonObject.getInt("ITEM_ID");
                            String NAME = jsonObject.getString("ITEM_NAME");
                            String ITEM_DESCRIPTION = jsonObject.getString("ITEM_DESCRIPTION");
                            Integer ITEM_PRICE = jsonObject.getInt("ITEM_PRICE");
                            Integer ITEM_MAX_AMOUNT = jsonObject.getInt("ITEM_MAX_AMOUNT");
                            Integer ITEM_TYPE = jsonObject.getInt("ITEM_TYPE");
                            Integer ITEM_SUBTYPE = jsonObject.getInt("ITEM_SUBTYPE");
                            Integer ITEM_WIDTH = jsonObject.getInt("ITEM_WIDTH");
                            mItemsIdToObject.put(ID, new ItemObject(NAME, ITEM_DESCRIPTION, ITEM_PRICE,
                                    ITEM_MAX_AMOUNT,ITEM_TYPE,ITEM_SUBTYPE, ITEM_WIDTH));
                        } else if (strings[1].equals("GETTING_USER_ITEMS")) {
                            Integer USER_ITEM_ID = jsonObject.getInt("USER_ITEM_ID");
                            Integer ITEM_ID = jsonObject.getInt("ITEM_ID");
                            Integer ITEM_AMOUNT = jsonObject.getInt("ITEM_AMOUNT");
                            Integer EQUIPPED = 0;
                            Integer X_COORDINATE = 0;
                            Integer WORKING_AT = 0;
                            Integer WORKING_POSITION = 0;
                            Integer WORKER_LEVEL = 0;
                            try{
                                EQUIPPED = jsonObject.getInt("EQUIPPED");
                                X_COORDINATE = jsonObject.getInt("X_COORDINATE");
                                WORKING_AT = jsonObject.getInt("WORKING_AT");
                                WORKING_POSITION = jsonObject.getInt("WORKING_POSITION");
                                WORKER_LEVEL = jsonObject.getInt("WORKER_LEVEL");
                            } catch (JSONException e) {
                                Log.v(TAG, e.toString());
                            }
                            mUserItemIdToUsersItems.put(USER_ITEM_ID, new UserItem(ITEM_ID,ITEM_AMOUNT,EQUIPPED,X_COORDINATE,WORKING_AT,WORKING_POSITION, WORKER_LEVEL));
                        } else if (strings[1].equals("GETTING_BUY_ORDERS") || strings[1].equals("GETTING_SELL_ORDERS")) {
                        /*TODO: make new buy order button work*/

                            Integer ORDER_ID;
                            if(strings[1].equals("GETTING_BUY_ORDERS")) {
                                ORDER_ID = jsonObject.getInt("buy_order_id");
                            } else {
                                ORDER_ID = jsonObject.getInt("sell_order_id");
                            }
                            Integer USER_ID = jsonObject.getInt("user_id");
                            Integer MEME_ID = jsonObject.getInt("meme_id");
                            String USER_NAME = jsonObject.getString("user_name");
                            Integer AMOUNT = jsonObject.getInt("amount");
                            Integer PRICE = jsonObject.getInt("price");
                            String DATE = jsonObject.getString("date");
                            mOrderRows.add(new OrderRow(ORDER_ID, USER_ID,USER_NAME,MEME_ID,AMOUNT,PRICE,DATE));
                        } else {
                            Result.success = false;
                            Log.e(TAG, "params wrong..");
                            return  Result;
                        }
                    }
                }
                for(OrderRow order  : mOrderRows) {
                    Log.v(TAG, "order: " + order.mAmount);
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
                    Log.e(TAG, "error cant get server response:"  + e2.toString());
                    Result.response = "Server Connection Issues, Try again later";
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing dates----- " + e.toString());
                e.printStackTrace();
            }
            return Result;
        }

        private List<ActiveRow> mergeAndSort(JSONArray a, JSONArray b) throws JSONException, ParseException {
            int i = 0, j = 0, k = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<ActiveRow> ret = new ArrayList<>();
            while (i < a.length() && j < b.length()) {
                JSONObject jsonA = a.getJSONObject(i);
                JSONObject jsonB = b.getJSONObject(j);
                Date dateA = sdf.parse(jsonA.getString("date"));
                Date dateB = sdf.parse(jsonB.getString("date"));
                if (dateA.after(dateB)) {
                    Integer ORDER_ID = jsonA.getInt("buy_order_id");
                    Integer MEME_ID = jsonA.getInt("meme_id");
                    String MEME_NAME = mMemeIdtoObject.get(MEME_ID).mName;
                    Integer AMOUNT = jsonA.getInt("amount");
                    Integer PRICE = jsonA.getInt("price");
                    ret.add(new ActiveRow(true, ORDER_ID, MEME_ID, MEME_NAME, AMOUNT, PRICE, dateA));
                    i++;
                } else {
                    Integer ORDER_ID = jsonB.getInt("sell_order_id");
                    Integer MEME_ID = jsonB.getInt("meme_id");
                    String MEME_NAME = mMemeIdtoObject.get(MEME_ID).mName;
                    Integer AMOUNT = jsonB.getInt("amount");
                    Integer PRICE = jsonB.getInt("price");
                    ret.add(new ActiveRow(false, ORDER_ID, MEME_ID, MEME_NAME, AMOUNT, PRICE, dateB));
                    j++;
                }
                k++;
            }
            while (i < a.length())
            {
                JSONObject jsonA = a.getJSONObject(i);
                Date dateA = sdf.parse(jsonA.getString("date"));
                Integer ORDER_ID = jsonA.getInt("buy_order_id");
                Integer MEME_ID = jsonA.getInt("meme_id");
                String MEME_NAME = mMemeIdtoObject.get(MEME_ID).mName;
                Integer AMOUNT = jsonA.getInt("amount");
                Integer PRICE = jsonA.getInt("price");
                ret.add(new ActiveRow(true, ORDER_ID, MEME_ID, MEME_NAME, AMOUNT, PRICE, dateA));
                i++;
                k++;
            }

            while (j < b.length())
            {
                JSONObject jsonB = b.getJSONObject(j);
                Date dateB = sdf.parse(jsonB.getString("date"));
                Integer ORDER_ID = jsonB.getInt("sell_order_id");
                Integer MEME_ID = jsonB.getInt("meme_id");
                String MEME_NAME = mMemeIdtoObject.get(MEME_ID).mName;
                Integer AMOUNT = jsonB.getInt("amount");
                Integer PRICE = jsonB.getInt("price");
                ret.add(new ActiveRow(false, ORDER_ID, MEME_ID, MEME_NAME, AMOUNT, PRICE, dateB));
                j++;
                k++;
            }
            return ret;
        }

        @Override
        protected void onPostExecute(MyHelper.results Result)
        {

//            TextView newTextView = (TextView) findViewById(R.id.dataFromServer);
//            newTextView.setText(mMemeNametoStockMap.get("10 guy") + " --- ");

            Log.v(TAG,"Response is: " + Result.response);

            if (Result.success) {
//                mFraLgment = (ListMemesFragment) getSupportFragmentManager().findFragmentById(R.id.meme_list_fragment);
                if (mRequest.equals("GETTING_DATA")) {
                    try {
                        mMemeListFragment.updateList(mMemeIdtoObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try{
                        mMemeDetailsFragment.updateStockPrice(mMemeIdtoObject.get(mSelectedMemeID).mPrice);
                    } catch (NullPointerException e) {
                        Log.v(TAG, e.toString());
                    }
                } else if(mRequest.equals("GETTING_ITEM_TYPES")) {
                    try {
                        mHouseSurfaceView.updateBitmaps(mUserItemIdToUsersItems, mItemsIdToObject);
                        mBagGridFragment.updateList(mUserItemIdToUsersItems, mItemsIdToObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(mRequest.equals("GETTING_LEADERBOARD")) {
                    try {
                        mLeaderboardDialogFragment.updateList(mLeaderboardUsersToMoneyMap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(mRequest.equals("GETTING_USER_STOCKS")){
//                    mMemeDetailsFragment.updateOwned(mMemeIdtoObject.get(mSelectedMemeID).mSharesHeld);
                } else if(mRequest.equals("GETTING_BUY_ORDERS")) {
                    Log.v(TAG, "getting  buy orders");
                    mOrdersListFragment.updateList(mOrderRows, mSelectedMemeID, mMemeIdtoObject.get(mSelectedMemeID), "Buy");
                } else if(mRequest.equals("GETTING_SELL_ORDERS")) {
                    Log.v(TAG, "getting  sell orders");
                    mOrdersListFragment.updateList(mOrderRows, mSelectedMemeID, mMemeIdtoObject.get(mSelectedMemeID), "Sell");
                } else if(mRequest.equals("GETTING_ACTIVE_ORDERS")) {
                    Log.v(TAG, "updating active orders");
                    mActiveListingsFragment.updateList(mActiveOrdersList);
                } else {
                    Log.v(TAG, "NOT SPECIFIED REQUEST TYPE");
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

    // TODO: 03/08/17 NEED TO MAKE SURE ONLY ONE INSTANCE OF THIS THREAD IS RUNNING AT ONCE BEFORE STARTING A NEW ONE

    public class PlaceOrder extends AsyncTask<String,Void,Boolean>
    {
        private String serverResponse;
        private final Integer mUserID;
        private final Integer mOrderID;
        private final Integer mMemeID;
        private final Integer mAmount;
        private final Integer mPrice;
        private Integer mNewMoney;
        private Integer mNewStocks;
        Integer mMoneyDiff, mStocksDiff, mNoOrders;
        String mOrderCompleted;
        private String mRequest;

        public PlaceOrder(Integer user_id, Integer meme_id, Integer order_id, Integer amount, Integer price) {
            mUserID = user_id;
            mMemeID = meme_id;
            mAmount = amount;
            mPrice = price;
            mOrderID = order_id;
        }

        @Override
        protected Boolean doInBackground(String... strings)
        {
            mRequest = strings[1];
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
                String query;
                if (mRequest.equals("ORDER_FROM_USER")) {
                    query = String.format("Content-Type: application/json&charset=%s&user=%d&order_id=%d&amount=%d",
                            URLEncoder.encode(charset, charset),
                            mUserID, mOrderID, mAmount);
                } else if (mRequest.equals("NEW_ORDER")) {
                    query = String.format("Content-Type: application/json&charset=%s&user=%d&meme=%d&amount=%d&price=%d",
                            URLEncoder.encode(charset, charset),
                            mUserID, mMemeID, mAmount, mPrice);
                } else if (mRequest.equals("CANCEL_BUY_ORDER")){
                    query = String.format("Content-Type: application/json&charset=%s&user=%d&buy_order_id=%d",
                            URLEncoder.encode(charset, charset),
                            mUserID, mOrderID);
                } else if (mRequest.equals("CANCEL_SELL_ORDER")){
                    query = String.format("Content-Type: application/json&charset=%s&user=%d&sell_order_id=%d",
                            URLEncoder.encode(charset, charset),
                            mUserID, mOrderID);
                } else {
                    return false;
                }

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
                    Log.v(TAG, "git purchase: we got 200");
                    serverResponse = MyHelper.readHttpStream(urlConnection.getInputStream());
                    Log.v(TAG, serverResponse);
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
                    if(mRequest.equals("ORDER_FROM_USER")){
                        mNewMoney = jsonObject.getInt("new_money");
                        mNewStocks = jsonObject.getInt("new_stocks");
                        mMemeIdtoObject.get(mMemeID).mSharesHeld = mNewStocks;
                        return true;
                    } else if(mRequest.equals("NEW_ORDER")) {
                        mNewMoney = jsonObject.getInt("new_money");
                        mNewStocks = jsonObject.getInt("new_stocks");
                        mMoneyDiff = jsonObject.getInt("money_diff");
                        mStocksDiff = jsonObject.getInt("stocks_diff");
                        mNoOrders = jsonObject.getInt("no_orders");
                        mOrderCompleted = jsonObject.getString("completed");
                        mMemeIdtoObject.get(mMemeID).mSharesHeld = mNewStocks;
                        return true;
                    } else if(mRequest.equals("CANCEL_BUY_ORDER")) {
                        mNewMoney = jsonObject.getInt("new_money");
                        mMoneyDiff = jsonObject.getInt("money_diff");
                    } else if(mRequest.equals("CANCEL_SELL_ORDER")) {
                        mNewStocks = jsonObject.getInt("new_stocks");
                        mStocksDiff = jsonObject.getInt("stocks_diff");
                    } else {
                        return  false;
                    }
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
            if (Result) {
                Log.v(TAG, "newStocks:" + mNewStocks);
//                Animation shrinkAnim = AnimationUtils.loadAnimation(MenuActivity.this, R.anim.shrink_money);
//                Animation growAnim = AnimationUtils.loadAnimation(MenuActivity.this, R.anim.shrink_money);
//
//                moneyTextView.clearAnimation();
//                moneyTextView.startAnimation(shrinkAnim);
//                moneyTextView.setText("$" + mNewMoney.toString());
//                moneyTextView.startAnimation(growAnim);
//
//                TextView sharesOwnedView = (TextView) findViewById(R.id.stocks_owned);
//                sharesOwnedView.setText(mNewStocks.toString());


                if(mRequest.equals("ORDER_FROM_USER")) {
                    mMemeIdtoObject.get(mMemeID).mSharesHeld = mNewStocks;
                    mApplication.userData.setMoney(mNewMoney);
                    mMoney.setText(String.format("$%s", mNewMoney));
                    mMemeDetailsFragment.updateStocksOwned(mNewStocks);
                    mOrdersListFragment.orderResponseDirect(mNewMoney, mNewStocks, mAmount);
                } else if(mRequest.equals("NEW_ORDER")) {
                    mMemeIdtoObject.get(mMemeID).mSharesHeld = mNewStocks;
                    mApplication.userData.setMoney(mNewMoney);
                    mMoney.setText(String.format("$%s", mNewMoney));
                    mMemeDetailsFragment.updateStocksOwned(mNewStocks);
                    mOrdersListFragment.orderResponse(mNewMoney, mNewStocks, mMoneyDiff, mStocksDiff,
                            mNoOrders, mOrderCompleted);
                } else if(mRequest.equals("CANCEL_BUY_ORDER")) {
                    mApplication.userData.setMoney(mNewMoney);
                    mMoney.setText(String.format("$%s", mNewMoney));
                    mActiveListingsFragment.cancelOrderResponse(mMoneyDiff, mStocksDiff);
                } else if(mRequest.equals("CANCEL_SELL_ORDER")) {
                    mMemeIdtoObject.get(mMemeID).mSharesHeld = mNewStocks;
                    mMemeDetailsFragment.updateStocksOwned(mNewStocks);
                    mActiveListingsFragment.cancelOrderResponse(mMoneyDiff, mStocksDiff);
                } else {
                    Log.e(TAG, "placeOrder success but wrong request type");
                }



            }
            else {
                if(mRequest.equals("ORDER_FROM_USER")) {
                    mOrdersListFragment.orderDirectFailed();
                } else if(mRequest.equals("NEW_ORDER")) {
                    mOrdersListFragment.orderFailed();
                } else if(mRequest.equals("CANCEL_BUY_ORDER") || mRequest.equals("CANCEL_SELL_ORDER")) {
                    mActiveListingsFragment.cancelOrderFailed();
                } else {
                    Log.e(TAG, "placeOrder failed wrong request type");
                }

            }
            return;
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
                mGetGraphTask = new GetGraphData(mSelectedMemeID);
                mGetGraphTask.execute(Defines.SERVER_ADDRESS + "/getPast2Days.php?");
            }
        }
    }

    private void setProgressDialog(boolean wait) {

        if (wait) {
            mProgressDialog= ProgressDialog.show(this,null,null);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setContentView(new ProgressBar(this));
        } else {
            mProgressDialog.dismiss();
        }

    }



}
