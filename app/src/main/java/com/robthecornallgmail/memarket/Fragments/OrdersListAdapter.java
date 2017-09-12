package com.robthecornallgmail.memarket.Fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.OrderRow;

import java.util.List;

/**
 * Created by rob on 12/09/17.
 */

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.ViewHolder> {
    final String TAG = "OrdersListAdapter";
    final List<OrderRow> mRows;
    final OrdersListFragment.OnOrdersListFragmentInteractionListener mListener;

    public OrdersListAdapter(List<OrderRow> items, OrdersListFragment.OnOrdersListFragmentInteractionListener listener) {
        mListener = listener;
        mRows = items;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

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
