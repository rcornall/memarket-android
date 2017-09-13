package com.robthecornallgmail.memarket.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robthecornallgmail.memarket.R;
import com.robthecornallgmail.memarket.Util.OrderRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rob on 12/09/17.
 */

public class OrdersListFragment  extends Fragment {
    String TAG = "OrdersListFragment";
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
        View view = inflater.inflate(R.layout.fragment_orders_list, container, false);
//        view.setBackgroundColor(getResources().getColor(R.color.colorMyGrey));

        RecyclerView rview = (RecyclerView) view.findViewById(R.id.orders_list_rv);
        rview.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rview.setAdapter(mAdapter);

        return view;
    }

    public void updateList(HashMap<Integer, OrderRow> orderIDtoRow) {
        mOrderRows.clear();
        Log.v(TAG, "updateListCalled");
        for(Map.Entry<Integer, OrderRow> order: orderIDtoRow.entrySet()) {
            order.getValue().mOrderID = order.getKey();
            mOrderRows.add(order.getValue());
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOrdersListFragmentInteractionListener) {
            mListener = (OnOrdersListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnOrdersListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnOrdersListFragmentInteractionListener {
        void onOrdersListFragmentInteraction(OrderRow orderRow);
    }
}
