package com.robthecornallgmail.memarket.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.robthecornallgmail.memarket.Activities.MainActivity;
import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.MemeObject;
import com.robthecornallgmail.memarket.Util.MyApplication;
import com.robthecornallgmail.memarket.Util.OrderRow;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.robthecornallgmail.memarket.Fragments.MemeDetailsFragment.mMemeObject;

/**
 * Created by rob on 12/09/17.
 */

public class OrdersListFragment  extends Fragment {
    private String TAG = "OrdersListFragment";
    private MyApplication mApplication;
    private List<OrderRow> mOrderRows;
    private OrdersListAdapter mAdapter;
    private OnOrdersListNewOrderListener mListener;
    private TextView mTitle, mOrderMaker, mNothingHere;
    private ImageView mIcon;
    private Button mPlaceBuyOrder;
    private Boolean mIsBuy;
    private LinearLayout mLayoutRView;
    private boolean mReady=false;
    private String mSubTitle;
    private String mBuying;
    private Integer mSelectedMemeID;
    private String mBuy;
    private AlertDialog mMakeOrderAD, mConfirmOrderAD, mResponseAD;
    private ProgressDialog mProgressDialog;
    private String mName;
    private String mCostingYou;
    private String mBought;
    private String mFrom;

    public OrdersListFragment() {
        mOrderRows = new ArrayList<>();
        mIsBuy = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new OrdersListAdapter(mOrderRows, mListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_orders_list, container, false);
//        view.setBackgroundColor(getResources().getColor(R.color.colorMyGrey)); /* covers up drop down arrow */
        mApplication = (MyApplication) getActivity().getApplicationContext();

        mTitle = (TextView) view.findViewById(R.id.orders_text_title);
        mOrderMaker = (TextView) view.findViewById(R.id.order_maker);
        mIcon = (ImageView) view.findViewById(R.id.orders_meme_icon);
        mPlaceBuyOrder = (Button) view.findViewById(R.id.new_buy_order_button);
        mNothingHere = (TextView) view.findViewById(R.id.orders_nothing_here);
        mLayoutRView = (LinearLayout) view.findViewById(R.id.orders_list_recycler_layout);
        RecyclerView rview = (RecyclerView) view.findViewById(R.id.orders_list_rv);
        rview.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rview.setAdapter(mAdapter);

        mPlaceBuyOrder.setOnClickListener(new View.OnClickListener() {
            public Integer mAmnt, mPrice;
            public long mTotal;

            @Override
            public void onClick(View v) {
                if(!mReady)
                    return;
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.new_order_layout, null);
                dialogBuilder.setView(dialogView);

                mMakeOrderAD = dialogBuilder.create();
                mMakeOrderAD.show();
                TextView subTitle = (TextView) dialogView.findViewById(R.id.new_order_subtitle);
                final TextView sentBuying = (TextView) dialogView.findViewById(R.id.pricing_sentence_buying);
                final TextView sentBuyingN = (TextView) dialogView.findViewById(R.id.pricing_sentence_buying_n);
                final TextView sentPriceN = (TextView) dialogView.findViewById(R.id.pricing_sentence_price_n);
                final TextView sentTotalN = (TextView) dialogView.findViewById(R.id.pricing_sentence_total_n);
                final Button placeOrder = (Button) dialogView.findViewById(R.id.place_new_order);

                subTitle.setText(mSubTitle);
                sentBuying.setText(mBuying);
                final NumberPicker numPickerAmt = (NumberPicker) dialogView.findViewById(R.id.number_picker_amount);
                final NumberPicker numPickerPrice = (NumberPicker) dialogView.findViewById(R.id.number_picker_price);
                numPickerAmt.setMinValue(1);
                if(mBuy.toUpperCase().equals("SELL")) {
                    Log.v(TAG, "SDLKFJS:DLFKSDKFJ: " + mMemeObject.mSharesHeld.toString());
                    numPickerAmt.setMaxValue(mMemeObject.mSharesHeld);
                    numPickerAmt.setValue(mMemeObject.mSharesHeld);
                } else {
                    numPickerAmt.setMaxValue(999999);
                    numPickerAmt.setValue(10);
                }
                numPickerPrice.setMinValue(1);numPickerPrice.setMaxValue(99999);
                numPickerPrice.setValue(mMemeObject.mPrice);
                sentBuyingN.setText(String.format("%d", numPickerAmt.getValue()));
                sentPriceN.setText(String.format("$%d", numPickerPrice.getValue()));
                mAmnt = numPickerAmt.getValue();
                mPrice = numPickerPrice.getValue();
                mTotal = numPickerAmt.getValue()*numPickerPrice.getValue();
                sentTotalN.setText(String.format("$%d", mTotal));

                numPickerAmt.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        mAmnt = newVal;
                        sentBuyingN.setText(String.format("%d", mAmnt));
                        mTotal = mAmnt*numPickerPrice.getValue();
                        sentTotalN.setText(String.format("$%d", mTotal));
                        if(mBuy.toUpperCase().equals("BUY")) {
                            if(mTotal > mApplication.userData.getMoney()) {
                                if(sentTotalN.getCurrentTextColor() != Color.RED)
                                    sentTotalN.setTextColor(Color.RED);
                            } else if(sentTotalN.getCurrentTextColor() == Color.RED) {
                                sentTotalN.setTextColor(Color.WHITE);
                            }
                        }
                    }
                });

                numPickerPrice.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        mPrice = newVal;
                        sentPriceN.setText(String.format("$%d", mPrice));
                        mTotal = mPrice*numPickerAmt.getValue();
                        sentTotalN.setText(String.format("$%d", mTotal));
                        if(mBuy.toUpperCase().equals("BUY")) {
                            if(mTotal > mApplication.userData.getMoney()) {
                                if(sentTotalN.getCurrentTextColor() != Color.RED)
                                    sentTotalN.setTextColor(Color.RED);
                            } else if(sentTotalN.getCurrentTextColor() == Color.RED) {
                                sentTotalN.setTextColor(Color.WHITE);
                            }
                        }
                    }
                });

                placeOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* try placing the order */
                        AlertDialog.Builder dBuilder = new AlertDialog.Builder(getActivity());

                        LayoutInflater infltr = getActivity().getLayoutInflater();
                        if(mBuy.toUpperCase().equals("BUY") && mTotal > mApplication.userData.getMoney()) {
                            View dlogView = infltr.inflate(R.layout.invalid_order, null);
                            dBuilder.setView(dlogView);
                            mResponseAD = dBuilder.create();
                            mResponseAD.show();
                            Button ok = (Button) dlogView.findViewById(R.id.response_ok_btn);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mResponseAD.dismiss();
                                }
                            });
                            return;
                        } else if(mBuy.toUpperCase().equals("SELL") && mAmnt > mMemeObject.mSharesHeld) {
                            View dlogView = infltr.inflate(R.layout.invalid_order, null);
                            dBuilder.setView(dlogView);
                            mResponseAD = dBuilder.create();
                            mResponseAD.show();
                            TextView sent = (TextView) dlogView.findViewById(R.id.invalid_order_sent);
                            sent.setText("Not enough Stocks to sell!");
                            Button ok = (Button) dlogView.findViewById(R.id.response_ok_btn);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mResponseAD.dismiss();
                                }
                            });
                            return;
                        } else if (mTotal == 0) {
                            View dlogView = infltr.inflate(R.layout.invalid_order, null);
                            dBuilder.setView(dlogView);
                            mResponseAD = dBuilder.create();
                            mResponseAD.show();
                            TextView sent = (TextView) dlogView.findViewById(R.id.invalid_order_sent);
                            sent.setText("Can not " + mBuy + " $0 of stock!");
                            Button ok = (Button) dlogView.findViewById(R.id.response_ok_btn);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mResponseAD.dismiss();
                                }
                            });
                            return;
                        }
                        View dlogView = infltr.inflate(R.layout.confirm_new_order, null);
                        dBuilder.setView(dlogView);

                        mConfirmOrderAD = dBuilder.create();
                        mConfirmOrderAD.show();

                        TextView meme = (TextView) dlogView.findViewById(R.id.memeText);
                        TextView buyingText = (TextView) dlogView.findViewById(R.id.buyingText);
                        TextView sharesText = (TextView) dlogView.findViewById(R.id.sharesText);
                        TextView priceText = (TextView) dlogView.findViewById(R.id.priceText);
                        TextView costing_you_text = (TextView) dlogView.findViewById(R.id.costing_you_text);
                        TextView costText = (TextView) dlogView.findViewById(R.id.costText);
                        meme.setText(mName);
                        buyingText.setText(mBuying);
                        sharesText.setText(String.format("$%d", mAmnt));
                        priceText.setText(String.format("$%d", mPrice));
                        costing_you_text.setText(mCostingYou);
                        costText.setText(String.format("$%d", mTotal));


                        Button confirm = (Button) dlogView.findViewById(R.id.confirm_order_button);
                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setProgressDialog(true);


                                mListener.onOrdersListNewOrder(mSelectedMemeID, mAmnt, mPrice, mIsBuy, mBuy);
//                                new MenuActivity.PlaceOrder(mApplication.userData.getID(), mSelectedMemeID, 0, mAmnt, mPrice)
//                                        .execute(Defines.SERVER_ADDRESS + String.format("/new%sOrder.php",mBuy), "NEW_ORDER");

                            }
                        });
                        Button cancel = (Button) dlogView.findViewById(R.id.cancel_order_button);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mConfirmOrderAD.dismiss();
                            }
                        });
                    }
                });
            }
        });

//        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
//        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
//        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
//        p.y = 50;
//        getDialog().getWindow().setAttributes(p);

        return view;
    }

    public void updateList(List<OrderRow> orderRows, Integer selectedMemeID, MemeObject memeObject, String sell) {
        mOrderRows.clear();
        mAdapter.setReady(false);
        mSelectedMemeID = selectedMemeID;
        mName = WordUtils.capitalize(memeObject.mName);
        mTitle.setText(sell + " orders for " + mName);
        if(sell.toUpperCase().equals("SELL")) {
            mPlaceBuyOrder.setText("PLACE BUY ORDER");
            mOrderMaker.setText("SELLER");
            mSubTitle = String.format("Buying %s shares", mName);
            mCostingYou = "Costing you ";
            mBuying = "Buying ";
            mBuy = "Buy";
            mFrom = " from ";
            mBought = "Bought";
            mIsBuy = true;
        } else {
            mPlaceBuyOrder.setText("PLACE SELL ORDER");
            mOrderMaker.setText("BUYER");
            mSubTitle = String.format("Selling %s shares", mName);
            mCostingYou = "For a minimum profit of ";
            mBuying = "Selling ";
            mBuy = "Sell";
            mFrom = " to ";
            mBought = "Sold";
            mIsBuy = false;
        }
        mAdapter.updateBuyInfo(mIsBuy, mSelectedMemeID, mName, mBuy, mBuying, mBought, mFrom, mSubTitle, mCostingYou);
        mAdapter.setReady(true);
        String iconName = "icon_" + memeObject.mName.replaceAll(" ", "_").toLowerCase();


        try {
            int iconId = getContext().getResources().getIdentifier(iconName, "drawable", MainActivity.PACKAGE_NAME);
            Picasso.with(getContext()).load(iconId).centerCrop().fit().into(mIcon);
        } catch (Exception e ) {
            Log.v(TAG,e.toString());
        }

        Log.v(TAG, "updateListCalled");
        for(OrderRow row : orderRows) {
            mOrderRows.add(row);
        }

        if(mOrderRows.isEmpty())
        {
            mNothingHere.setText("No orders right now...");
            mLayoutRView.setVisibility(View.GONE);
        }
        else
        {
            mLayoutRView.setVisibility(View.VISIBLE);
            mNothingHere.setText(null);
        }
        mAdapter.notifyDataSetChanged();
        mReady = true;
    }

    public void clearList() {
        mOrderRows.clear();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOrdersListNewOrderListener) {
            mListener = (OnOrdersListNewOrderListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnOrdersListNewOrderListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /* response from creating a new order */
    public void orderResponse(Integer newMoney, Integer newStocks, Integer moneyDiff,
                              Integer stocksDiff, Integer noOrders, String orderCompleted) {
        setProgressDialog(false);
        mConfirmOrderAD.dismiss();
        AlertDialog.Builder dBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater infltr = getActivity().getLayoutInflater();
        View dlogView = infltr.inflate(R.layout.order_response, null);
        dBuilder.setView(dlogView);

        mResponseAD = dBuilder.create();
        mResponseAD.show();

        TextView resp = (TextView) dlogView.findViewById(R.id.response_sentence_status);
        TextView resporders = (TextView) dlogView.findViewById(R.id.response_orders);
        TextView respshares_n = (TextView) dlogView.findViewById(R.id.response_shares_n);
        TextView respshares = (TextView) dlogView.findViewById(R.id.response_shares);
        TextView resporders_n = (TextView) dlogView.findViewById(R.id.response_orders_n);
        TextView resptransactions = (TextView) dlogView.findViewById(R.id.response_transactions);
        TextView respViewtransactions = (TextView) dlogView.findViewById(R.id.response_view_transactions);
        TextView respRestOfOrder = (TextView) dlogView.findViewById(R.id.response_rest_of_order);

        if(noOrders.equals(0)) {
            respViewtransactions.setVisibility(View.GONE);
            respRestOfOrder.setVisibility(View.GONE);
        } else if (orderCompleted.equals("t")) {
            resp.setVisibility(View.GONE);
            resporders.setText(String.format("You %s ", mBought));
            respshares_n.setText(String.format("%d",Math.abs(stocksDiff)));
            respshares.setText(" shares in ");
            resporders_n.setText(String.format("%d ", noOrders));
            resptransactions.setText("transaction(s).");
            respRestOfOrder.setVisibility(View.GONE);

        } else {
            resp.setVisibility(View.GONE);
            resporders.setText(String.format("You %s ", mBought));
            respshares_n.setText(String.format("%d",Math.abs(stocksDiff)));
            respshares.setText(" shares in ");
            resporders_n.setText(String.format("%d ", noOrders));
            resptransactions.setText("transaction(s).");


        }

        Button ok = (Button) dlogView.findViewById(R.id.response_ok_btn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResponseAD.dismiss();
                mMakeOrderAD.dismiss();
            }
        });

        /* refresh list */
        mListener.refreshList(mIsBuy);
    }

    public void orderResponseDirect(Integer mNewMoney, Integer mNewStocks, Integer mAmount) {
        mAdapter.orderResponse(mNewMoney, mNewStocks, mAmount);
    }

    public void orderFailed() {
        setProgressDialog(false);
        mConfirmOrderAD.dismiss();
        AlertDialog.Builder dBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater infltr = getActivity().getLayoutInflater();
        View dlogView = infltr.inflate(R.layout.order_response_direct, null);
        dBuilder.setView(dlogView);

        mResponseAD = dBuilder.create();
        mResponseAD.show();

        TextView resporders = (TextView) dlogView.findViewById(R.id.response_orders);

        resporders.setText(String.format("Failed to complete order.. try again later."));

        Button ok = (Button) dlogView.findViewById(R.id.response_ok_btn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResponseAD.dismiss();
                mMakeOrderAD.dismiss();
            }
        });
    }

    public void orderDirectFailed() {
        mAdapter.orderFailed();
    }


    public interface OnOrdersListNewOrderListener {
        void onOrdersListNewOrder(Integer memeID, Integer amount, Integer price, Boolean isBuy, String buy);

        void onOrdersListDirectOrder(Integer memeID, Integer orderID, Integer amount, Boolean isBuy, String buy);

        void refreshList(Boolean isBuy);
    }

    private void setProgressDialog(boolean wait) {

        if (wait) {
            mProgressDialog= ProgressDialog.show(this.getContext(),null,null);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setContentView(new ProgressBar(this.getContext()));
        } else {
            mProgressDialog.dismiss();
        }

    }
}
