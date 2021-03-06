package com.robthecornallgmail.memarket.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.OrderRow;

import java.util.List;

import static com.robthecornallgmail.memarket.Activities.MenuActivity.mApplication;
import static com.robthecornallgmail.memarket.Fragments.MemeDetailsFragment.mMemeObject;
import static com.robthecornallgmail.memarket.Util.MyHelper.setNumberPickerTextColor;

/**
 * Created by rob on 12/09/17.
 */

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.ViewHolder> {
    private final String TAG = "OrdersListAdapter";
    private final List<OrderRow> mRows;
    private final OrdersListFragment.OnOrdersListNewOrderListener mListener;
    private View mClassView;
    private Boolean mIsBuy = true;

    private Integer mAmnt;
    private Integer mPrice;
    private Integer mTotal;


    private  AlertDialog mResponseAD;
    private static AlertDialog mMakeOrderAD;
    private AlertDialog mConfirmOrderAD;
    private  ProgressDialog mProgressDialog;

    private String mCostingYou;
    private String mBuy;
    private String mFrom;
    private String mSubTitle;
    private String mBuying;
    private String mMemeName;
    private Integer mSelectedMemeID;
    private String mBought;
    private Boolean mReady;
    private AlertDialog.Builder mDialogBuilder;


    public OrdersListAdapter(List<OrderRow> items, OrdersListFragment.OnOrdersListNewOrderListener listener) {
        mListener = listener;
        mRows = items;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_order_row, parent, false);
        mDialogBuilder = new AlertDialog.Builder(view.getContext());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if( position%2 == 0) {
            holder.mView.setBackgroundColor(holder.mView.getResources().getColor(R.color.colorMySlightlyBetweenGreylight));
        }
        else {
            holder.mView.setBackgroundColor(holder.mView.getResources().getColor(R.color.colorMySlightlyBetweenGreyDark));
        }
        holder.mUsernameView.setText(mRows.get(position).mName);
        holder.mAmountView.setText(mRows.get(position).mAmount.toString());
        holder.mPriceView.setText("$ " + mRows.get(position).mPrice.toString());
        if(mIsBuy)
        {
            holder.mBuyNowButton.setText("BUY NOW");
        }
        else
        {
            holder.mBuyNowButton.setText("SELL NOW");
        }

    }

    @Override
    public int getItemCount() {
        return mRows.size();
    }


    public void updateBuyInfo(Boolean mIsBuy, Integer mSelectedMemeID, String mName, String mBuy, String mBuying, String mBought, String mFrom, String mSubTitle, String mCostingYou) {
        this.mIsBuy = mIsBuy;
        this.mSelectedMemeID = mSelectedMemeID;
        this.mMemeName = mName;
        this.mBuy = mBuy;
        this.mBuying = mBuying;
        this.mBought = mBought;
        this.mFrom = mFrom;
        this.mSubTitle = mSubTitle;
        this.mCostingYou = mCostingYou;
    }

    public void setReady(boolean ready) {
        this.mReady = ready;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView mUsernameView;

        public final TextView mAmountView;
        public final TextView mPriceView;
        public final Button mBuyNowButton;
        public final View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mClassView = mView;
            mUsernameView = (TextView) view.findViewById(R.id.order_user_name);
            mAmountView = (TextView) view.findViewById(R.id.order_amount);
            mPriceView = (TextView) view.findViewById(R.id.order_price);
            mBuyNowButton = (Button) view.findViewById(R.id.orders_buy_now_button);

            /* handle on click events with dialogs */
            mBuyNowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if(!mReady)
                {
                    return; /*avoid race conditions*/
                }

                LayoutInflater inflater = (LayoutInflater)mView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.new_order_layout, null);
                mDialogBuilder.setView(dialogView);

                mMakeOrderAD = mDialogBuilder.create();
                mMakeOrderAD.show();
                TextView title =(TextView) dialogView.findViewById(R.id.new_order_title);
                TextView subTitle = (TextView) dialogView.findViewById(R.id.new_order_subtitle);
                final TextView sentBuying = (TextView) dialogView.findViewById(R.id.pricing_sentence_buying);
                final TextView sentBuyingN = (TextView) dialogView.findViewById(R.id.pricing_sentence_buying_n);
                final TextView sentPriceN = (TextView) dialogView.findViewById(R.id.pricing_sentence_price_n);
                final TextView sentTotalN = (TextView) dialogView.findViewById(R.id.pricing_sentence_total_n);
                final Button placeOrder = (Button) dialogView.findViewById(R.id.place_new_order);

                title.setText(mBuy + mFrom + mRows.get(getAdapterPosition()).mName);
                subTitle.setText(mSubTitle);
                sentBuying.setText(mBuying);
                final NumberPicker numPickerAmt = (NumberPicker) dialogView.findViewById(R.id.number_picker_amount);
                final NumberPicker numPickerPrice = (NumberPicker) dialogView.findViewById(R.id.number_picker_price);

                numPickerAmt.setMinValue(1);
                numPickerAmt.setMaxValue(mRows.get(getAdapterPosition()).mAmount);
                numPickerAmt.setValue(mRows.get(getAdapterPosition()).mAmount);

                numPickerPrice.setMinValue(mRows.get(getAdapterPosition()).mPrice);
                numPickerPrice.setMaxValue(mRows.get(getAdapterPosition()).mPrice);
                numPickerPrice.setValue(mRows.get(getAdapterPosition()).mPrice);
                setNumberPickerTextColor(numPickerPrice, mView.getContext().getResources().getColor(R.color.greenWhiteFaded));

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

                placeOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    /* try placing the order */
                        LayoutInflater infltr = (LayoutInflater)mView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        if (mBuy.toUpperCase().equals("BUY") && mTotal > mApplication.userData.getMoney()) {
                            View dlogView = infltr.inflate(R.layout.invalid_order, null);
                            mDialogBuilder.setView(dlogView);
                            mResponseAD = mDialogBuilder.create();
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
                            mDialogBuilder.setView(dlogView);
                            mResponseAD = mDialogBuilder.create();
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
                            mDialogBuilder.setView(dlogView);
                            mResponseAD = mDialogBuilder.create();
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
                        mDialogBuilder.setView(dlogView);

                        mConfirmOrderAD = mDialogBuilder.create();
                        mConfirmOrderAD.show();

                        TextView meme = (TextView) dlogView.findViewById(R.id.memeText);
                        TextView buyingText = (TextView) dlogView.findViewById(R.id.buyingText);
                        TextView sharesText = (TextView) dlogView.findViewById(R.id.sharesText);
                        TextView priceText = (TextView) dlogView.findViewById(R.id.priceText);
                        TextView costing_you_text = (TextView) dlogView.findViewById(R.id.costing_you_text);
                        TextView costText = (TextView) dlogView.findViewById(R.id.costText);
                        meme.setText(mMemeName);
                        buyingText.setText(mBuying);
                        sharesText.setText(String.format("$%d", mAmnt));
                        priceText.setText(String.format("$%d", mPrice));
                        costing_you_text.setText(mCostingYou);
                        costText.setText(String.format("$%d", mTotal));


                        Button confirm = (Button) dlogView.findViewById(R.id.confirm_order_button);
                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setProgressDialog(true, mView);

                                mListener.onOrdersListDirectOrder(mSelectedMemeID, mRows.get(getAdapterPosition()).mOrderID, mAmnt, mIsBuy, mBuy);
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
        }

    }
    /* order response from direct order from user */
    public void orderResponse(Integer mNewMoney, Integer mNewStocks, Integer stocksDiff) {
        setProgressDialog(false, mClassView);
        mConfirmOrderAD.dismiss();

        LayoutInflater infltr = (LayoutInflater)mClassView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dlogView = infltr.inflate(R.layout.order_response_direct, null);
        mDialogBuilder.setView(dlogView);

        mResponseAD = mDialogBuilder.create();
        mResponseAD.show();

        TextView resporders = (TextView) dlogView.findViewById(R.id.response_orders);
        TextView respshares_n = (TextView) dlogView.findViewById(R.id.response_shares_n);
        TextView respshares = (TextView) dlogView.findViewById(R.id.response_shares);

        resporders.setText(String.format("You %s ", mBought));
        respshares_n.setText(String.format("%d",Math.abs(stocksDiff)));
        respshares.setText(" shares.");

        Button ok = (Button) dlogView.findViewById(R.id.response_ok_btn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResponseAD.dismiss();
                mMakeOrderAD.dismiss();
            }
        });

        mListener.refreshList(mIsBuy);
    }

    public void orderFailed() {
        setProgressDialog(false, mClassView);
        mConfirmOrderAD.dismiss();

        LayoutInflater infltr = (LayoutInflater)mClassView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dlogView = infltr.inflate(R.layout.order_response_direct, null);
        mDialogBuilder.setView(dlogView);

        mResponseAD = mDialogBuilder.create();
        mResponseAD.show();

        TextView respSuccess = (TextView) dlogView.findViewById(R.id.response_success);
        TextView resporders = (TextView) dlogView.findViewById(R.id.response_orders);

        respSuccess.setText("Error");
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

    private void setProgressDialog(boolean wait, View view) {

        if (wait) {
            mProgressDialog= ProgressDialog.show(view.getContext(),null,null);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setContentView(new ProgressBar(view.getContext()));
        } else {
            mProgressDialog.dismiss();
        }

    }

}
