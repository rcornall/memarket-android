package com.robthecornallgmail.memarket.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.robthecornallgmail.memarket.Activities.DateRange;
import com.robthecornallgmail.memarket.Activities.MainActivity;
import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.MyApplication;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MemeDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MemeDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemeDetailsFragment extends Fragment {
    private MyApplication mApplication;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String TAG = "Details";
    // TODO: Rename and change types of parameters
    private View mView;
    private String mMemeName;
    private Integer mMemeID;
    private Integer mMemePrice;
    private Integer mStocksOwned;
    private TextView mMemeTitleView;
    private TextView mMoneyView;
    private AppCompatImageButton mImageView;
    private TextView mStocksOwnedView;
    private Button mSellButton;
    private Button mBuyButton;
    private TextView mGraphTitle;
    private GraphView mGraphView;
    private ToggleButton mDayButton;
    private ToggleButton mWeekButton;
    private ToggleButton mMonthButton;
    private ToggleButton mYearButton;

    private DateRange mDateRange;
    private LineGraphSeries<DataPoint> mCurrentSeries;

    private Map<String, Integer> mMemeNametoIDMap = new HashMap<>();
    private Map<Integer, Integer> mMemeIDtoAmountHeld = new HashMap<>();


    private OnFragmentInteractionListener mListener;

    private  MotionEvent mMotionEvent;

    public MemeDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MemeDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemeDetailsFragment newInstance(String name, Integer price, Integer owned) {
        MemeDetailsFragment fragment = new MemeDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        args.putInt(ARG_PARAM2, price);
        args.putInt(ARG_PARAM3, owned);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        Log.v(TAG, "SWAGER STARTED");
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMemeName = getArguments().getString(ARG_PARAM1);
            mMemePrice = getArguments().getInt(ARG_PARAM2);
            mStocksOwned = getArguments().getInt(ARG_PARAM3);
            Log.e(TAG, "memename: " + mMemeName);
            Log.e(TAG, "memename: " + mMemePrice.toString());
        }

        //create motion event for hacky fix graphview bug
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 50;
        float x = 2.0f;
        float y = 30f;
        int metaState = 0;
        mMotionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_MOVE,
                x,
                y,
                metaState
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the  layout for this fragment
        try {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            mView = inflater.inflate(R.layout.fragment_meme_details, container, false);
            mApplication = (MyApplication) getActivity().getApplicationContext();
            mMemeTitleView = (TextView) mView.findViewById(R.id.detail_meme_title);
            mMoneyView = (TextView) mView.findViewById(R.id.detail_meme_price);
            mImageView = (AppCompatImageButton) mView.findViewById(R.id.memeIcon);
            mStocksOwnedView = (TextView) mView.findViewById(R.id.stocks_owned);
            mSellButton = (Button) mView.findViewById(R.id.sell_stock_button);
            mBuyButton = (Button) mView.findViewById(R.id.buy_stock_button);
            mGraphTitle = (TextView) mView.findViewById(R.id.graphTitle);
            mGraphView = (GraphView) mView.findViewById(R.id.graph);
            mDayButton = (ToggleButton) mView.findViewById(R.id.DayButton);
            mWeekButton = (ToggleButton) mView.findViewById(R.id.WeekButton);
            mMonthButton = (ToggleButton) mView.findViewById(R.id.MonthButton);
            mYearButton = (ToggleButton) mView.findViewById(R.id.YearButton);

            String nameToDisplay = mMemeName.replace("meme", "");
            nameToDisplay = WordUtils.capitalize(nameToDisplay);
            mMemeTitleView.setText(nameToDisplay);
            mMoneyView.setText("$"+mMemePrice.toString());

            mStocksOwnedView.setText(mStocksOwned.toString());
            mGraphTitle.setText(mMemeName + "'s stock trend" );

            String iconName = "icon_" + mMemeName.replaceAll(" ", "_").toLowerCase();
            int iconId = getResources().getIdentifier(iconName, "drawable", MainActivity.PACKAGE_NAME);
            Log.e(TAG, mImageView.toString());
//            mImageView.setImageResource(iconId);
            // use picasso to scale down image, saves ram
            Picasso.with(mView.getContext()).load(iconId).centerCrop().fit().into(mImageView);

            mDayButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mWeekButton.setChecked(false);
                        mMonthButton.setChecked(false);
                        mYearButton.setChecked(false);
                        mDateRange = com.robthecornallgmail.memarket.Activities.DateRange.DAY;

                        mGraphView.removeAllSeries();
                        mGraphView.addSeries(mCurrentSeries);
                        mGraphView.getGridLabelRenderer().setNumHorizontalLabels(6); //spacing
                        Calendar cal = Calendar.getInstance();
                        Date start = cal.getTime();
                        cal.add(Calendar.HOUR, -24);
                        Date end = cal.getTime();
                        mGraphView.getViewport().setMaxX(start.getTime());
                        mGraphView.getViewport().setMinX(end.getTime());
                        // refresh graph (its glitchy)
                        mGraphView.dispatchTouchEvent(mMotionEvent);
                    } else {

                    }
                }
            });
            mWeekButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mDayButton.setChecked(false);
                        mMonthButton.setChecked(false);
                        mYearButton.setChecked(false);
                        mDateRange = com.robthecornallgmail.memarket.Activities.DateRange.WEEK;

                        mGraphView.removeAllSeries();
                        mGraphView.addSeries(mCurrentSeries);
                        mGraphView.getGridLabelRenderer().setNumHorizontalLabels(4); //spacing
                        Calendar cal = Calendar.getInstance();
                        Date start = cal.getTime();
                        cal.add(Calendar.DAY_OF_YEAR, -7);
                        Date end = cal.getTime();
                        mGraphView.getViewport().setMaxX(start.getTime());
                        mGraphView.getViewport().setMinX(end.getTime());
                        // refresh graph (its glitchy)
                        mGraphView.dispatchTouchEvent(mMotionEvent);


                    } else {

                    }
                }
            });
            mMonthButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mDayButton.setChecked(false);
                        mWeekButton.setChecked(false);
                        mYearButton.setChecked(false);
                        mDateRange = com.robthecornallgmail.memarket.Activities.DateRange.MONTH;

                        mGraphView.removeAllSeries();
                        mGraphView.addSeries(mCurrentSeries);
                        mGraphView.getGridLabelRenderer().setNumHorizontalLabels(4); //spacing
                        Calendar cal = Calendar.getInstance();
                        Date start = cal.getTime();
                        cal.add(Calendar.DAY_OF_YEAR, -30);
                        Date end = cal.getTime();
                        mGraphView.getViewport().setMaxX(start.getTime());
                        mGraphView.getViewport().setMinX(end.getTime());
                        // refresh graph (its glitchy)
                        mGraphView.dispatchTouchEvent(mMotionEvent);
                    } else {

                    }
                }
            });
            mYearButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mDayButton.setChecked(false);
                        mWeekButton.setChecked(false);
                        mMonthButton.setChecked(false);
                        mDateRange = com.robthecornallgmail.memarket.Activities.DateRange.YEAR;

                        mGraphView.removeAllSeries();
                        mGraphView.addSeries(mCurrentSeries);
                        mGraphView.getGridLabelRenderer().setNumHorizontalLabels(4); //
                        Calendar cal = Calendar.getInstance();
                        Date start = cal.getTime();
                        cal.add(Calendar.YEAR, -1);
                        Date end = cal.getTime();
                        mGraphView.getViewport().setMaxX(start.getTime());
                        mGraphView.getViewport().setMinX(end.getTime());
                        // refresh graph (its glitchy)
                        mGraphView.dispatchTouchEvent(mMotionEvent);
                    } else {

                    }
                }
            });
//            mBuyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (mApplication.userData.getMoney() < mMemeNametoStockMap.get(mSelectedName) + 5)
//                {
//                    Toast.makeText(getBaseContext(), "Not enough money!",
//                            Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    new PurchaseStockFromServer(mApplication.userData.getID(), mMemeNametoIDMap.get(mSelectedName), 1).execute(Defines.SERVER_ADDRESS + "/purchaseStock.php");
//                }
//
//            }
//            });
            return mView;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // hide that annoying keyboard
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
    }
    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateGraph(LineGraphSeries<DataPoint> dataPointLineGraphSeries, DateRange dateRange) {
        // remove old mMemeIDtoSeriesMap.get(mMemeID)(line)
        mDateRange = dateRange;
        mGraphView.removeAllSeries();
        mCurrentSeries = dataPointLineGraphSeries;
        mGraphView.addSeries(dataPointLineGraphSeries);
        // set date label formatter

        mGraphView.getViewport().setScalable(true);
        mGraphView.getViewport().setScrollable(true);



        mGraphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getContext()));
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

        mGraphView.getViewport().setBackgroundColor(Color.argb(11, 230, 255, 255));

        mGraphView.dispatchTouchEvent(mMotionEvent);
    }

    public void updateOwned(Map<Integer, Integer> owned, Map<String , Integer> nametoID) {
        mMemeIDtoAmountHeld = owned;
        mMemeNametoIDMap = nametoID;
    }

    public void updateStocksOwned(Integer owned) {
        mStocksOwnedView.setText(owned.toString());
    }

    @Override
    public void onStop() {
        Log.v(TAG, "SWAGGER STOPED");
        //free sum of that juicy memory
        mImageView.setImageResource(0);
        mGraphView.removeAllSeries();

        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMemeDetailsFragmentInteraction(Uri uri);
    }
}
