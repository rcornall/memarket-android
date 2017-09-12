package com.robthecornallgmail.memarket.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.robthecornallgmail.memarket.Util.MemeObject;
import com.robthecornallgmail.memarket.Util.MyApplication;
import com.robthecornallgmail.memarket.Util.MyHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import static java.lang.Thread.sleep;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MemeDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MemeDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemeDetailsFragment extends Fragment {
    private static final String TAG = "Details";
    private MyApplication mApplication;
    private static String mName;


    static MemeObject mMemeObject;

    private View mView;
    private TextView mMemeTitleView;
    private TextView mPriceView;
    private ImageButton mImageView;
    private Integer mIconId;
    private TextView mDifference;
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

    private ProgressDialog mProgressDialog;
    private ImageView mUpDownArrowView;
    private TextView mKYMURL;

    public MemeDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @param price
     * @param mAmountOwned
     * @return A new instance of fragment MemeDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemeDetailsFragment newInstance(Integer ID, MemeObject selectedMemeObject, Integer amountOwned) {
        MemeDetailsFragment fragment = new MemeDetailsFragment();
        mMemeObject = selectedMemeObject;
        mMemeObject.mSharesHeld = amountOwned;


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
        Log.e(TAG, "memename: " + mMemeObject.mPrice.toString());

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

            final LinearLayout detailsLinearLayout = (LinearLayout) mView.findViewById(R.id.details_linear_layout);

            mApplication = (MyApplication) getActivity().getApplicationContext();
            mMemeTitleView = (TextView) mView.findViewById(R.id.detail_meme_title);
            mPriceView = (TextView) mView.findViewById(R.id.detail_meme_price);
            mDifference = (TextView) mView.findViewById(R.id.detail_meme_price_difference);
            mKYMURL = (TextView) mView.findViewById(R.id.kym_url);
            mUpDownArrowView = (ImageView) mView.findViewById(R.id.detail_updown_arrow);
            mImageView = (ImageButton) mView.findViewById(R.id.memeIcon);
            mStocksOwnedView = (TextView) mView.findViewById(R.id.stocks_owned);
            mSellButton = (Button) mView.findViewById(R.id.sell_stock_button);
            mBuyButton = (Button) mView.findViewById(R.id.buy_stock_button);
            mGraphTitle = (TextView) mView.findViewById(R.id.graphTitle);
            mGraphView = (GraphView) mView.findViewById(R.id.graph);
            mDayButton = (ToggleButton) mView.findViewById(R.id.DayButton);
            mWeekButton = (ToggleButton) mView.findViewById(R.id.WeekButton);
            mMonthButton = (ToggleButton) mView.findViewById(R.id.MonthButton);
            mYearButton = (ToggleButton) mView.findViewById(R.id.YearButton);
            final ImageView expandedImageView = (ImageView) mView.findViewById(R.id.expanded_image);

            String nameToDisplay = mMemeObject.mName.replace("meme", "");
            nameToDisplay = WordUtils.capitalize(nameToDisplay);
            mMemeTitleView.setText(nameToDisplay);
            mPriceView.setText("$"+mMemeObject.mPrice.toString());

            DecimalFormat df = new DecimalFormat("#0.0#");
            float priceDiff = mMemeObject.mPrice - mMemeObject.mLastPrice;
            if(priceDiff != 0)
            {
                priceDiff = (priceDiff/(float)mMemeObject.mPrice)*100;
            }
            if(priceDiff>=0)
            {
                mDifference.setText("+" + df.format(priceDiff) + "%");
                mUpDownArrowView.setBackgroundResource(R.mipmap.ic_action_arrow_drop_up);
                mDifference.setTextColor(mView.getResources().getColor(R.color.accent));
            }
            else
            {
                mDifference.setText(df.format(priceDiff) + "%");
                mUpDownArrowView.setBackgroundResource(R.mipmap.ic_action_arrow_drop_down);
                mDifference.setTextColor(mView.getResources().getColor(R.color.myRed));
            }

            try{
                mStocksOwnedView.setText(mMemeObject.mSharesHeld.toString());
            } catch (NullPointerException e ) {
                Log.v(TAG, e.toString());
            }
            mGraphTitle.setText(mMemeObject.mName + "'s stock trend" );

            mKYMURL.setText(Html.fromHtml(String.format("<a href=\"%s\">[KnowYourMeme.com]</a>",mMemeObject.mLink)));
            mKYMURL.setMovementMethod(LinkMovementMethod.getInstance());


            String iconName = "icon_" + mMemeObject.mName.replaceAll(" ", "_").toLowerCase();
            mIconId = getResources().getIdentifier(iconName, "drawable", MainActivity.PACKAGE_NAME);
            Log.e(TAG, mImageView.toString());
            Picasso.with(mView.getContext()).load(mIconId).fit().centerCrop().into(mImageView);

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Load the high-resolution "zoomed-in" image.

                    detailsLinearLayout.setAlpha(0.4f);
                    detailsLinearLayout.setBackground(new ColorDrawable(0xE6000000));

                    setProgressDialog(true);
                    Picasso.with(mView.getContext()).load(mIconId).fit().centerInside().into(expandedImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            setProgressDialog(false);
                            MyHelper.zoomImageFromThumb(mImageView, expandedImageView, mView, detailsLinearLayout);
                        }

                        @Override
                        public void onError() {
                            mProgressDialog.dismiss();
                        }
                    });
                }
            });

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
            mBuyButton.setBackgroundColor(getResources().getColor(R.color.colorMySlightlyBetweenGreylight));
            mBuyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Buying Stock");
                    mListener.onMemeDetailsFragmentInteraction("buy");
                }
            });

            mSellButton.setBackgroundColor(getResources().getColor(R.color.colorMySlightlyBetweenGreylight));
            mSellButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Selling Stock");
                    mListener.onMemeDetailsFragmentInteraction("sell");
                }
            });
            return mView;
        } catch (Exception e) {
            Log.e(TAG , e.toString());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Picasso.with(mView.getContext()).load(mIconId).fit().centerCrop().into(mImageView);
            mGraphView.removeAllSeries();
            mGraphView.addSeries(mCurrentSeries);
            mGraphView.getGridLabelRenderer().setNumHorizontalLabels(6); //spacing
            Calendar cal = Calendar.getInstance();
            Date start = cal.getTime();
            cal.add(Calendar.HOUR, -17);
            Date end = cal.getTime();
            mGraphView.getViewport().setMaxX(start.getTime());
            mGraphView.getViewport().setMinX(end.getTime());
            // refresh graph (its glitchy)
            mGraphView.dispatchTouchEvent(mMotionEvent);
        } catch (Exception e){
            Log.v(TAG, e.toString());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // hide that annoying keyboard
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
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

    public Boolean updateGraph(LineGraphSeries<DataPoint> dataPointLineGraphSeries, DateRange dateRange) {
        // remove old mMemeIDtoSeriesMap.get(mMemeID)(line)
//        try {
//            sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//ih
        mDateRange = dateRange;
        if(mGraphView == null)
        {
            return false;
        }
        try {
            mGraphView.removeAllSeries();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }
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
        cal.add(Calendar.HOUR, -17);
        Date end = cal.getTime();
        mGraphView.getViewport().setMaxX(start.getTime());
        mGraphView.getViewport().setMinX(end.getTime());

        mGraphView.getViewport().setBackgroundColor(Color.argb(11, 230, 255, 255));

        mGraphView.dispatchTouchEvent(mMotionEvent);
        return true;
    }

    public void updateOwned(Integer owned) {
        mMemeObject.mSharesHeld = owned;
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

    public void updateStockPrice(Integer price) {
        mPriceView.setText("$" + price.toString());
    }

    public void setGraphProgressDialog(boolean wait) {

        /*TODO: graphview loading*/
        return;

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
        void onMemeDetailsFragmentInteraction(String arg);
    }


    private void setProgressDialog(boolean wait) {

        if (wait) {
            mProgressDialog=ProgressDialog.show(this.getContext(),null,null);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setContentView(new ProgressBar(this.getContext()));
        } else {
            mProgressDialog.dismiss();
        }

    }
}
