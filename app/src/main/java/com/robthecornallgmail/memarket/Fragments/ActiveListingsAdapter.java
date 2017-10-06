package com.robthecornallgmail.memarket.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.robthecornallgmail.memarket.Activities.MainActivity;
import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.ActiveRow;
import com.robthecornallgmail.memarket.Util.OrderRow;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by rob on 30/09/17.
 */

public class ActiveListingsAdapter extends RecyclerView.Adapter<ActiveListingsAdapter.ViewHolder> {
    private final String TAG = "ActiveListingsAdapter";
    private final ActiveListingsFragment.OnActiveListingsInteractionListener mListener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy h:mma");

    private final List<ActiveRow> mRows;
    private View mClassView;
    private ProgressDialog mProgressDialog;
    private Dialog mConfirmOrderAD;
    private AlertDialog.Builder mDialogBuilder;
    private AlertDialog mResponseAD;

    private String mSelectedMeme;


    public ActiveListingsAdapter(List<ActiveRow> items, ActiveListingsFragment.OnActiveListingsInteractionListener listener) {
        mListener = listener;
        mRows = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_row, parent, false);
        mDialogBuilder = new AlertDialog.Builder(view.getContext());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ActiveRow row = mRows.get(position);
        if(row.mIsBuy) {
            holder.mBuying.setText("BUYING");
            holder.mBuying.setTextColor(holder.mView.getResources().getColor(R.color.accent));
        } else {
            holder.mBuying.setText("SELLING");
            holder.mBuying.setTextColor(holder.mView.getResources().getColor(R.color.myRed));
        }
        holder.mMemeName.setText(WordUtils.capitalize(row.mMemeName));
        holder.mAmt.setText(row.mAmount.toString());
        holder.mPrice.setText(String.format("$%s",row.mPrice.toString()));
        holder.mDate.setText(sdf.format(row.mDate));

        String iconName = "icon_" + row.mMemeName.replaceAll(" ", "_").toLowerCase();
        int iconId = holder.mView.getResources().getIdentifier(iconName, "drawable", MainActivity.PACKAGE_NAME);
        Picasso.with(holder.mView.getContext()).load(iconId).centerCrop().fit().into(holder.mIcon);

    }

    @Override
    public int getItemCount() {
        return mRows.size();
    }


    protected class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mIcon;
        public final TextView mBuying;
        public final TextView mMemeName;
        public final TextView mAmt;
        public final TextView mPrice;
        public final TextView mDate;
        public final ImageButton mCancel;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mClassView = mView;
            mIcon = (ImageView) view.findViewById(R.id.active_row_icon);
            mBuying = (TextView) view.findViewById(R.id.active_row_buying);
            mMemeName = (TextView) view.findViewById(R.id.active_row_memename);
            mAmt = (TextView) view.findViewById(R.id.active_row_amount);
            mPrice = (TextView) view.findViewById(R.id.active_row_price);
            mDate = (TextView) view.findViewById(R.id.active_row_date);
            mCancel = (ImageButton) view.findViewById(R.id.active_row_cancel);

            /* set cancel button callback */
            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        String memeName = mRows.get(getAdapterPosition()).mMemeName;
                        mSelectedMeme = memeName;
                        Integer amount = mRows.get(getAdapterPosition()).mAmount;
                        Integer price = mRows.get(getAdapterPosition()).mPrice;
                        // make a confirmation dialog, then callback to activity if confirmed
                        LayoutInflater inflater = (LayoutInflater)mView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View dialogView = inflater.inflate(R.layout.confirm_cancellation, null);
                        mDialogBuilder.setView(dialogView);

                        mConfirmOrderAD = mDialogBuilder.create();
                        mConfirmOrderAD.show();

                        TextView memeText = (TextView) dialogView.findViewById(R.id.cancel_memeText);
                        memeText.setText(WordUtils.capitalize(memeName));

                        if(mRows.get(getAdapterPosition()).mIsBuy) {
                            TextView sharesText = (TextView) dialogView.findViewById(R.id.sharesText);
                            TextView priceText = (TextView) dialogView.findViewById(R.id.priceText);
                            TextView costText = (TextView) dialogView.findViewById(R.id.costText);
                            sharesText.setText(String.format("%d shares", amount));
                            priceText.setText(String.format("$%d", price));
                            costText.setText(String.format("$%d",amount*price));
                        } else {
                            LinearLayout buyLayout = (LinearLayout) dialogView.findViewById(R.id.cancel_refund_sentence);
                            buyLayout.setVisibility(View.GONE);
                            TextView costText = (TextView) dialogView.findViewById(R.id.costText);
                            costText.setText(String.format("%d shares", amount));
                        }

                        Button confirm = (Button) dialogView.findViewById(R.id.confirm_order_button);
                        confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setProgressDialog(true, mView);

                                mListener.onActiveListingsCancel(mRows.get(getAdapterPosition()));
                            }
                        });
                        Button cancel = (Button) dialogView.findViewById(R.id.cancel_order_button);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mConfirmOrderAD.dismiss();
                            }
                        });



                    }
                }
            });
        }
    }

    /* order response from direct order from user */
    public void orderResponse(Integer moneyDiff, Integer stocksDiff) {
        setProgressDialog(false, mClassView);
        mConfirmOrderAD.dismiss();

        LayoutInflater infltr = (LayoutInflater)mClassView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dlogView = infltr.inflate(R.layout.cancel_order_response, null);
        mDialogBuilder.setView(dlogView);

        mResponseAD = mDialogBuilder.create();
        mResponseAD.show();

        TextView respAmount= (TextView) dlogView.findViewById(R.id.cancel_response_refund_amount);

        if(moneyDiff == null){
            respAmount.setText(String.format("%d %s stocks.", stocksDiff  ,mSelectedMeme));
        } else {
            respAmount.setText(String.format("$%d", moneyDiff));
        }
        Button ok = (Button) dlogView.findViewById(R.id.response_ok_btn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResponseAD.dismiss();
            }
        });
        mSelectedMeme = null;

        mListener.refreshList();
    }

    public void orderFailed() {
        setProgressDialog(false, mClassView);
        mConfirmOrderAD.dismiss();

        LayoutInflater infltr = (LayoutInflater)mClassView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dlogView = infltr.inflate(R.layout.cancel_order_response, null);
        mDialogBuilder.setView(dlogView);

        mResponseAD = mDialogBuilder.create();
        mResponseAD.show();

        TextView respSuccess = (TextView) dlogView.findViewById(R.id.cancel_response_success);
        TextView resporders = (TextView) dlogView.findViewById(R.id.cancel_response);
        TextView cancelSent = (TextView) dlogView.findViewById(R.id.cancel_sentence);
        cancelSent.setVisibility(View.GONE);

        respSuccess.setText("Error");
        resporders.setText(String.format("Failed to cancel order.. try again later."));

        Button ok = (Button) dlogView.findViewById(R.id.response_ok_btn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResponseAD.dismiss();
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
