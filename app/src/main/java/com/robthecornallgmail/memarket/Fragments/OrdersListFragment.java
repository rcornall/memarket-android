package com.robthecornallgmail.memarket.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.OrderRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rob on 12/09/17.
 */

public class OrdersListFragment  extends Fragment {
    List<OrderRow> mOrderRows;
    OrdersListAdapter mAdapter;
    OnOrdersListFragmentInteractionListener mListener;

    public OrdersListFragment() {
        mOrderRows = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new OrdersListAdapter(mOrderRows, mListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leaderboard, container, false);
        view.setBackgroundColor(getResources().getColor(R.color.colorMyGrey));

        RecyclerView rview = (RecyclerView) view.findViewById(R.id.leaderboard_recycler_view);
        rview.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rview.setAdapter(mAdapter);

        return view;
    }


    public interface OnOrdersListFragmentInteractionListener {
        void onOrdersListFragmentInteraction(OrderRow orderRow);
    }
}
