package com.robthecornallgmail.memarket.Fragments;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.robthecornallgmail.memarket.Activities.MainActivity;
import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.ActiveRow;
import com.robthecornallgmail.memarket.Util.OrderRow;
import com.squareup.picasso.Picasso;

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


    public ActiveListingsAdapter(List<ActiveRow> items, ActiveListingsFragment.OnActiveListingsInteractionListener listener) {
        mListener = listener;
        mRows = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_row, parent, false);
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

        holder.mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onActiveListingsCancel(mRows.get(position));
                }
            }
        });

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
            mIcon = (ImageView) view.findViewById(R.id.active_row_icon);
            mBuying = (TextView) view.findViewById(R.id.active_row_buying);
            mMemeName = (TextView) view.findViewById(R.id.active_row_memename);
            mAmt = (TextView) view.findViewById(R.id.active_row_amount);
            mPrice = (TextView) view.findViewById(R.id.active_row_price);
            mDate = (TextView) view.findViewById(R.id.active_row_date);
            mCancel = (ImageButton) view.findViewById(R.id.active_row_cancel);
        }
    }
}
