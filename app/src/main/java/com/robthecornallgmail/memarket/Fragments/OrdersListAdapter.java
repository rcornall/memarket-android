package com.robthecornallgmail.memarket.Fragments;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.OrderRow;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

/**
 * Created by rob on 12/09/17.
 */

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.ViewHolder> {
    final String TAG = "OrdersListAdapter";
    final List<OrderRow> mRows;
    final OrdersListFragment.OnOrdersListFragmentInteractionListener mListener;

    Boolean mIsBuy = true;

    public OrdersListAdapter(List<OrderRow> items, Boolean isBuy, OrdersListFragment.OnOrdersListFragmentInteractionListener listener) {
        mListener = listener;
        mIsBuy = isBuy;
        mRows = items;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_order_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if( position%2 == 0) {
            holder.mView.setBackgroundColor(holder.mView.getResources().getColor(R.color.colorMySlightlyBetweenGreylight));
        }
        else {
            holder.mView.setBackgroundColor(holder.mView.getResources().getColor(R.color.colorMySlightlyBetweenGreyDark));
        }
        holder.mUsernameView.setText(mRows.get(position).mName);
        holder.mAmount.setText(mRows.get(position).mAmount.toString());
        holder.mPrice.setText("$ " + mRows.get(position).mPrice.toString());
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

    public void setBuyOrSell(Boolean isBuy) {
        mIsBuy = isBuy;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView mUsernameView;
        public final TextView mAmount;
        public final TextView mPrice;
        public final Button mBuyNowButton;

        public final View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mUsernameView = (TextView) view.findViewById(R.id.order_user_name);
            mAmount = (TextView) view.findViewById(R.id.order_amount);
            mPrice = (TextView) view.findViewById(R.id.order_price);
            mBuyNowButton = (Button) view.findViewById(R.id.orders_buy_now_button);
        }

    }
}
