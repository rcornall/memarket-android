package com.robthecornallgmail.memarket.Fragments;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.UserRow;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by rob on 06/08/17.
 */

public class LeaderboardListAdapter extends RecyclerView.Adapter<LeaderboardListAdapter.ViewHolder> {
    private final String TAG = "leaderboardAdapter";
    private final List<UserRow> mRows;
    public List<UserRow> mItemsCopy;
    private final LeaderboardDialogFragment.OnLeaderboardFragmentInteractionListener mListener;

    public LeaderboardListAdapter(List<UserRow> items, LeaderboardDialogFragment.OnLeaderboardFragmentInteractionListener listener) {
        mListener = listener;
        mRows = items;
        mItemsCopy = new ArrayList<>();
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ImageView iv = (ImageView) holder.mView.findViewById(R.id.face_icon);
        iv.setBackgroundColor(holder.mView.getResources().getColor(R.color.colorMyBetweenGrey));
        Log.v(TAG, "position: " + String.format("%d",position));
        if( position%2 == 0) {
            Log.v(TAG, "%2   position: " + String.format("%d",position));

            holder.mView.setBackgroundColor(holder.mView.getResources().getColor(R.color.colorMyOffGrey));
        }

        String name = mRows.get(position).getName();
        Log.v(TAG, "name is: " + name);
        holder.mUsernameView.setText(name);
        holder.mMoneyView.setText("$"+mRows.get(position).getMoney().toString());
        holder.mPositionView.setText(String.format(Locale.CANADA,"%d", position+1) + ".");
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onLeaderboardFragmentInteraction(mRows.get(position));
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mRows.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView mUsernameView;
        public final TextView mMoneyView;
        public final TextView mPositionView;

        public final View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mUsernameView = (TextView) view.findViewById(R.id.leaderboard_username);
            mMoneyView = (TextView) view.findViewById(R.id.leaderboard_money);
            mPositionView = (TextView) view.findViewById(R.id.leaderboard_position);
        }

    }
}
