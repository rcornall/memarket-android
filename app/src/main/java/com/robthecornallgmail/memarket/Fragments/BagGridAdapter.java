package com.robthecornallgmail.memarket.Fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.robthecornallgmail.memarket.Activities.MainActivity;
import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.BagGrid;
import com.robthecornallgmail.memarket.Util.MemeRow;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rob on 26/08/17.
 */

public class BagGridAdapter extends RecyclerView.Adapter<BagGridAdapter.ViewHolder> {
String TAG = "BagGridAdapter";
    private final List<BagGrid> mGrids;
    public List<BagGrid> mItemsCopy;
    private final BagGridFragment.OnBagGridInteractionListener mListener;

    public BagGridAdapter( List<BagGrid> items, BagGridFragment.OnBagGridInteractionListener listener) {
        mGrids = items;
        mItemsCopy = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_bag_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mGrids.get(position);
        String name = holder.mItem.mName;
        name = "icon_" + name.replaceAll(" ", "_").toLowerCase();
        String description = holder.mItem.mDescription;
        Boolean isEquipped = holder.mItem.mIsEquipped;
        Integer amount = holder.mItem.mAmount;
        Log.v(TAG, "TESTTTTTTTT name is: " + name + "descrip: " + description);

        int iconId = holder.mView.getResources().getIdentifier(name, "drawable", MainActivity.PACKAGE_NAME);

        if(name.equals("icon_blank"))
        {
            return;
        }
        Picasso.with(holder.mView.getContext()).load(iconId).centerCrop().fit().into(holder.mImage);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null)
                {
                    mListener.OnBagGridInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGrids.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView mImage;
        BagGrid mItem;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mImage = (ImageView) itemView.findViewById(R.id.bag_grid_icon);
        }
    }
}
