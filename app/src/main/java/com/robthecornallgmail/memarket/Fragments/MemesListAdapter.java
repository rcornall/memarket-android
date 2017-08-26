package com.robthecornallgmail.memarket.Fragments;

import android.graphics.Typeface;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.robthecornallgmail.memarket.Activities.MainActivity;
import com.robthecornallgmail.memarket.Fragments.ListMemesFragment.OnListFragmentInteractionListener;
import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.MemeRow;
import com.squareup.picasso.Picasso;


import org.apache.commons.lang3.text.WordUtils;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MemeRow} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MemesListAdapter extends RecyclerView.Adapter<MemesListAdapter.ViewHolder> {

    private final List<MemeRow> mRows;
    public List<MemeRow> mItemsCopy;
    private final OnListFragmentInteractionListener mListener;
    private Typeface pixelStartFont;


    public MemesListAdapter(List<MemeRow> items, OnListFragmentInteractionListener listener, Typeface pixelStartFont) {
        mRows = items;
        mItemsCopy = new ArrayList<>();
        mListener = listener;
        this.pixelStartFont = pixelStartFont;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_meme_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if( position%2 == 0) {
            holder.mView.setBackgroundColor(holder.mView.getResources().getColor(R.color.colorMySlightlyBetweenGreylight));
        }
        else {
            holder.mView.setBackgroundColor(holder.mView.getResources().getColor(R.color.colorMySlightlyBetweenGreyDark));
        }

        DecimalFormat df = new DecimalFormat("#0.0#");
        holder.mItem = mRows.get(position);
        String name = mRows.get(position).getName();
        String nameToDisplay = name.replace("meme", "");
        nameToDisplay = WordUtils.capitalize(nameToDisplay);
        holder.mNameView.setText(nameToDisplay);
        holder.mPriceView.setText("$" + mRows.get(position).getPrice().toString());
        float priceDiff = mRows.get(position).getPrice() - mRows.get(position).getLastPrice();
        if(priceDiff != 0)
        {
            priceDiff = (priceDiff/(float)mRows.get(position).getPrice())*100;
        }

        if(priceDiff>=0)
        {
            holder.mPriceDifferenceView.setText("+" + df.format(priceDiff) + "%");
            holder.mUpDownArrowView.setBackgroundResource(R.mipmap.ic_action_arrow_drop_up);
            holder.mPriceDifferenceView.setTextColor(holder.mView.getResources().getColor(R.color.accent));
            holder.mPriceView.setTextColor(holder.mView.getResources().getColor(R.color.accent));
        }
        else
        {
            holder.mPriceDifferenceView.setText(df.format(priceDiff) + "%");
            holder.mUpDownArrowView.setBackgroundResource(R.mipmap.ic_action_arrow_drop_down);
            holder.mPriceDifferenceView.setTextColor(holder.mView.getResources().getColor(R.color.myRed));
            holder.mPriceView.setTextColor(holder.mView.getResources().getColor(R.color.myRed));
        }
        String iconName = "icon_" + name.replaceAll(" ", "_").toLowerCase();
        int iconId = holder.mView.getResources().getIdentifier(iconName, "drawable", MainActivity.PACKAGE_NAME);
//        holder.mImageView.setImageResource(iconId);
        Picasso.with(holder.mView.getContext()).load(iconId).centerCrop().fit().into(holder.mImageView);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    public void filter(String text)
    {
        Log.v("memarket baby:", text);
        Log.v("memarket baby:", mRows.toString());
        Log.v("memarket baby:", mItemsCopy.toString());
        mRows.clear();
        if (text.isEmpty()) {
            Log.v("memarket baby:", "empty");
            mRows.addAll(mItemsCopy);
        } else {
            Log.v("memarket baby:", "not empty");
            text = text.toLowerCase();
            for (MemeRow item : mItemsCopy) {
                Log.v("memarket baby:", item.getName() + text);
                if (item.getName().toLowerCase().contains(text))
                    mRows.add(item);
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mRows.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mPriceView;
        public final TextView mPriceDifferenceView;
        public final ImageView mUpDownArrowView;
        public final AppCompatImageButton mImageView;
        public MemeRow mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.MemeName);
//            mNameView.setTypeface(pixelStartFont);
            mPriceView = (TextView) view.findViewById(R.id.MemePrice);
            mPriceDifferenceView = (TextView) view.findViewById(R.id.MemePriceDifference);
//            mPriceView.setTypeface(pixelStartFont);
            mImageView = (AppCompatImageButton) view.findViewById(R.id.MemeImage);
            mUpDownArrowView = (ImageView) view.findViewById(R.id.UpDownArrow);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPriceView.getText() + "'";
        }
    }



}
