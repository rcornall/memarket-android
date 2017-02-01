package com.robthecornallgmail.memarket.Fragments;

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


import java.util.ArrayList;
import java.util.List;
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

    ListMemesFragment mListMemesFragment;

    public MemesListAdapter(List<MemeRow> items, OnListFragmentInteractionListener listener) {
        mRows = items;
        mItemsCopy = new ArrayList<>();
        mListener = listener;
        mListMemesFragment = new ListMemesFragment();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_meme_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mRows.get(position);
        String name = mRows.get(position).getName();
        holder.mNameView.setText(mRows.get(position).getName());
        holder.mPriceView.setText("$" + mRows.get(position).getPrice().toString());
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
        public final AppCompatImageButton mImageView;
        public MemeRow mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.MemeName);
            mPriceView = (TextView) view.findViewById(R.id.MemePrice);
            mImageView = (AppCompatImageButton) view.findViewById(R.id.MemeImage);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPriceView.getText() + "'";
        }
    }


    public void updateListFragment(Map<String, Integer> map)
    {
        mListMemesFragment.updateList(map);
    }

}
